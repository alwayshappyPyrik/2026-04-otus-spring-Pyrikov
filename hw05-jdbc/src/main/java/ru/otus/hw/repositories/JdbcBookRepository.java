package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Optional<Book> findById(long id) {
        String sql = "SELECT id, title, author_id FROM books WHERE id = :id";

        return Optional.ofNullable(jdbc.query(sql,
                Collections.singletonMap("id", id),
                new BookResultSetExtractor(jdbc)));
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var books = getAllBooksWithoutGenres();
        var relations = getAllGenreRelations();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM books WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);

        jdbc.update(sql, params);
    }

    private List<Book> getAllBooksWithoutGenres() {
        String sql = """
        SELECT
            b.id, 
            b.title, 
            b.author_id,
            a.full_name 
        FROM books b
        JOIN authors a ON b.author_id = a.id
        ORDER BY b.id
        """;

        return jdbc.query(sql, (rs, rowNum) -> {
            long id = rs.getLong("id");
            String title = rs.getString("title");
            long authorId = rs.getLong("author_id");
            String fullName = rs.getString("full_name");

            Author author = new Author(authorId, fullName);
            return new Book(id, title, author);
        });
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        String sql = """
        SELECT book_id, genre_id 
        FROM books_genres 
        ORDER BY book_id, genre_id
        """;

        return jdbc.query(sql, (rs, rowNum) ->
                new BookGenreRelation(
                        rs.getLong("book_id"),
                        rs.getLong("genre_id")
                )
        );
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        Map<Long, Genre> genreMap = genres.stream()
                .collect(Collectors.toMap(Genre::getId, Function.identity()));

        Map<Long, List<Long>> bookGenresMap = relations.stream()
                .collect(Collectors.groupingBy(
                        BookGenreRelation::bookId,
                        Collectors.mapping(BookGenreRelation::genreId, Collectors.toList())
                ));

        for (Book book : booksWithoutGenres) {
            List<Long> genreIds = bookGenresMap.getOrDefault(book.getId(), Collections.emptyList());
            List<Genre> bookGenres = genreIds.stream()
                    .map(genreMap::get)
                    .toList();
            book.setGenres(bookGenres);
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();

        String sql = """
        INSERT INTO books (title, author_id)
        VALUES (:title, :authorId)
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("authorId", book.getAuthor().getId());

        jdbc.update(sql, params, keyHolder, new String[]{"id"});

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));

        batchInsertGenresRelationsFor(book);

        return book;
    }

    private Book update(Book book) {
        String sql = """
        UPDATE books 
        SET title = :title, 
            author_id = :authorId 
        WHERE id = :id
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("authorId", book.getAuthor().getId())
                .addValue("id", book.getId());

        int updatedRows = jdbc.update(sql, params);

        if (updatedRows == 0) {
            throw new EntityNotFoundException("Book with id " + book.getId() + " not found");
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        String sql = """
        INSERT INTO books_genres (book_id, genre_id)
        VALUES (:bookId, :genreId)
        """;

        List<MapSqlParameterSource> batchParams = book.getGenres().stream()
                .map(genre -> new MapSqlParameterSource()
                        .addValue("bookId", book.getId())
                        .addValue("genreId", genre.getId()))
                .toList();

        jdbc.batchUpdate(sql, batchParams.toArray(new MapSqlParameterSource[0]));
    }

    private void removeGenresRelationsFor(Book book) {
        String sql = "DELETE FROM books_genres WHERE book_id = :bookId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("bookId", book.getId());

        jdbc.update(sql, params);
    }

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

       private final NamedParameterJdbcOperations jdbc;

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
                long id = rs.getLong("id");
                String title = rs.getString("title");
                long authorId = rs.getLong("author_id");

                Author author = loadAuthorForBook(authorId);

                List<Genre> genres = loadGenresForBook(id);

                Book book = new Book(id, title, author, genres);
                book.setGenres(genres);

                return book;
            }
            return null;
        }

        private Author loadAuthorForBook(long authorId) {
            String sql = "SELECT id, full_name FROM authors WHERE id = :id";

            return jdbc.queryForObject(sql, Collections.singletonMap("id", authorId),
                    (rs, rowNum) -> new Author(
                            rs.getLong("id"),
                            rs.getString("full_name")));
        }

        private List<Genre> loadGenresForBook(long bookId) {
            String sql = """
            SELECT g.id, g.name 
            FROM genres g
            JOIN books_genres bg ON g.id = bg.genre_id
            WHERE bg.book_id = :bookId
            """;

            return jdbc.query(sql,
                    Collections.singletonMap("bookId", bookId),
                    (rs, rowNum) -> new Genre(
                            rs.getLong("id"),
                            rs.getString("name")));
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}