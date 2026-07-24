package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.dto.AuthorRequestDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.mapper.AuthorMapperImpl;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.mapper.BookMapperImpl;
import ru.otus.hw.mapper.GenreMapperImpl;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.BookServiceImpl;
import ru.otus.hw.services.GenreServiceImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с книгами")
@DataMongoTest
@Import({
        BookServiceImpl.class,
        BookMapperImpl.class,
        AuthorMapperImpl.class,
        GenreMapperImpl.class
})
public class BookServiceTest {

    private static final int EXPECTED_NUMBER_OF_BOOKS = 3;
    private static final String FIRST_BOOK_TITLE = "BookTitle_1";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private BookService bookService;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("authors");
        mongoTemplate.dropCollection("books");
        mongoTemplate.dropCollection("genres");
        mongoTemplate.dropCollection("comments");

        List<Author> authors = List.of(
                new Author(null, "Author_1"),
                new Author(null, "Author_2"),
                new Author(null, "Author_3")
        );
        authors.forEach(author -> mongoTemplate.save(author, "authors"));

        List<Genre> genres = List.of(
                new Genre(null, "Genre_1"),
                new Genre(null, "Genre_2"),
                new Genre(null, "Genre_3"),
                new Genre(null, "Genre_4"),
                new Genre(null, "Genre_5"),
                new Genre(null, "Genre_6")
        );
        genres.forEach(genre -> mongoTemplate.save(genre, "genres"));

        List<Author> dbAuthors = authorRepository.findAll();
        List<Genre> dbGenres = genreRepository.findAll();

        List<Book> books = List.of(
                new Book(null, "BookTitle_1", dbAuthors.get(0), List.of(dbGenres.get(0), dbGenres.get(1))),
                new Book(null, "BookTitle_2", dbAuthors.get(1), List.of(dbGenres.get(2), dbGenres.get(3))),
                new Book(null, "BookTitle_3", dbAuthors.get(2), List.of(dbGenres.get(4), dbGenres.get(5)))
        );
        books.forEach(book -> mongoTemplate.save(book, "books"));
    }

    @DisplayName("должен загружать информацию о нужной книге по ее id")
    @Test
    void shouldFindExpectedBookById() {
        var allBooks = bookRepository.findAll();
        assertThat(allBooks).isNotEmpty();

        var bookId = allBooks.getFirst().getId();

        BookRequestDto request = BookRequestDto.builder()
                .id(bookId)
                .build();

        var actualBook = bookService.findById(request);

        assertThat(actualBook)
                .isPresent()
                .hasValueSatisfying(bookDto -> {
                    assertThat(bookDto.id()).isEqualTo(bookId);
                    assertThat(bookDto.title()).isEqualTo(FIRST_BOOK_TITLE);
                    assertThat(bookDto.author()).isNotNull();
                    assertThat(bookDto.genres()).isNotEmpty();
                });
    }

    @DisplayName("должен загружать список всех книг с полной информацией о них")
    @Test
    void shouldReturnCorrectBooksListWithAllInfo() {
        var books = bookService.findAll();

        assertThat(books)
                .isNotNull()
                .hasSize(EXPECTED_NUMBER_OF_BOOKS)
                .allMatch(b -> !b.id().isBlank())
                .allMatch(b -> !b.title().isBlank())
                .allMatch(b -> b.author() != null && !b.author().id().isBlank())
                .allMatch(b -> b.author().fullName() != null && !b.author().fullName().isBlank())
                .allMatch(b -> b.genres() != null && !b.genres().isEmpty());

        assertThat(books)
                .extracting(BookResponseDto::title)
                .containsExactly("BookTitle_1", "BookTitle_2", "BookTitle_3");
    }

    @Nested
    @DisplayName("Тяжелые тесты для книг")
    class BookServiceHeavyTest {

        private List<Author> dbAuthors;
        private Set<String> dbGenreIds;

        @BeforeEach
        void setUp() {
            dbAuthors = authorRepository.findAll();
            dbGenreIds = genreRepository.findAll().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
        }

        @DisplayName("должен создавать новую книгу с полной информацией о ней")
        @Test
        void shouldCreateNewBook() {
            var authorId = dbAuthors.getFirst().getId();
            var genreIds = dbGenreIds.stream()
                    .limit(2)
                    .collect(Collectors.toSet());

            AuthorRequestDto authorRequestDto = AuthorRequestDto.builder()
                    .id(authorId)
                    .build();

            BookRequestDto bookRequestDto = BookRequestDto.builder()
                    .title("New Book")
                    .author(authorRequestDto)
                    .genreIds(genreIds)
                    .build();

            var createdBook = bookService.insert(bookRequestDto);

            assertThat(createdBook)
                    .isNotNull()
                    .satisfies(bookDto -> {
                        assertThat(bookDto.id()).isNotBlank();
                        assertThat(bookDto.title()).isEqualTo("New Book");
                        assertThat(bookDto.author())
                                .isNotNull()
                                .satisfies(author -> {
                                    assertThat(author.id()).isEqualTo(authorId);
                                    assertThat(author.fullName()).isEqualTo(dbAuthors.getFirst().getFullName());
                                });
                        assertThat(bookDto.genres())
                                .isNotNull()
                                .hasSize(2);
                    });

            var foundBook = bookRepository.findById(createdBook.id());
            assertThat(foundBook)
                    .isPresent()
                    .get()
                    .satisfies(book -> {
                        assertThat(book.getId()).isEqualTo(createdBook.id());
                        assertThat(book.getTitle()).isEqualTo(createdBook.title());
                        assertThat(book.getAuthor().getId()).isEqualTo(createdBook.author().id());
                        assertThat(book.getGenres())
                                .hasSize(createdBook.genres().size());
                    });
        }

        @DisplayName("должен обновлять существующую книгу")
        @Test
        void shouldUpdateBook() {
            var allBooks = bookRepository.findAll();
            assertThat(allBooks).isNotEmpty();

            var bookId = allBooks.getFirst().getId();
            var authorId = dbAuthors.get(2).getId();
            var genreIds = dbGenreIds.stream()
                    .limit(2)
                    .collect(Collectors.toSet());

            AuthorRequestDto authorRequestDto = AuthorRequestDto.builder()
                    .id(authorId)
                    .build();

            BookRequestDto bookRequestDto = BookRequestDto.builder()
                    .id(bookId)
                    .title("Edited Book")
                    .author(authorRequestDto)
                    .genreIds(genreIds)
                    .build();

            var updatedBook = bookService.update(bookRequestDto);

            assertThat(updatedBook)
                    .isNotNull()
                    .satisfies(bookDto -> {
                        assertThat(bookDto.id()).isEqualTo(bookId);
                        assertThat(bookDto.title()).isEqualTo("Edited Book");
                        assertThat(bookDto.author())
                                .isNotNull()
                                .satisfies(author -> {
                                    assertThat(author.id()).isEqualTo(authorId);
                                    assertThat(author.fullName()).isEqualTo(dbAuthors.get(2).getFullName());
                                });
                        assertThat(bookDto.genres())
                                .isNotNull()
                                .hasSize(2);
                    });

            var foundBook = bookRepository.findById(bookId);
            assertThat(foundBook)
                    .isPresent()
                    .get()
                    .satisfies(book -> {
                        assertThat(book.getTitle()).isEqualTo("Edited Book");
                        assertThat(book.getAuthor().getId()).isEqualTo(authorId);
                        assertThat(book.getGenres())
                                .hasSize(genreIds.size());
                    });
        }

        @DisplayName("должен удалять книгу по id")
        @Test
        void shouldDeleteBook() {
            var allBooks = bookRepository.findAll();
            assertThat(allBooks).isNotEmpty();

            var bookId = allBooks.getFirst().getId();

            assertThat(bookRepository.findById(bookId)).isPresent();

            BookRequestDto bookRequestDto = BookRequestDto.builder()
                    .id(bookId)
                    .build();

            bookService.deleteById(bookRequestDto);

            assertThat(bookRepository.findById(bookId)).isEmpty();
        }
    }
}