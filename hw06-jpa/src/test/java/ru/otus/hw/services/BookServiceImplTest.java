package ru.otus.hw.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.mapper.BookMapperImpl;
import ru.otus.hw.mapper.AuthorMapperImpl;
import ru.otus.hw.mapper.GenreMapperImpl;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaGenreRepository;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@DisplayName("Интеграционные тесты сервиса книг ")
@DataJpaTest
@Import({BookServiceImpl.class,
        BookMapperImpl.class,
        JpaBookRepository.class,
        JpaAuthorRepository.class,
        JpaGenreRepository.class,
        AuthorMapperImpl.class,
        GenreMapperImpl.class})
@Transactional(propagation = Propagation.NEVER)
public class BookServiceImplTest {

    private static final long BOOK_ID = 1L;
    private static final long AUTHOR_ID = 1L;
    private static final Set<Long> GENRE_IDS = Set.of(1L, 2L);

    @Autowired
    private BookService bookService;

    @Autowired
    private BookMapper bookMapper;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("должен загружать книгу по id без LazyInitializationException")
    void shouldFindBookByIdWithoutLazyException() {
        var entityGraph = em.getEntityGraph("Book.withAuthorAndGenres");
        var book = em.createQuery(
                        "SELECT b FROM Book b " +
                            "WHERE b.id = :id",
                        Book.class)
                .setParameter("id", BOOK_ID)
                .setHint(FETCH.getKey(), entityGraph)
                .getSingleResult();

        var expectedBook = bookMapper.toDto(book);

        var actualBook = bookService.findById(BOOK_ID);

        assertThat(actualBook)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedBook);
    }

    @Test
    @DisplayName("должен загружать список всех книг без LazyInitializationException")
    void shouldFindAllBooksWithoutLazyException() {
        var entityGraph = em.getEntityGraph("Book.withAuthorAndGenres");
        var books = em.createQuery(
                        "SELECT b FROM Book b",
                        Book.class)
                .setHint(FETCH.getKey(), entityGraph)
                .getResultList();

        var expectedBooks = books.stream()
                .map(bookMapper::toDto)
                .toList();

        var actualBooks = bookService.findAll();

        assertThat(actualBooks)
                .usingRecursiveComparison()
                .isEqualTo(expectedBooks);
    }

    @Test
    @DisplayName("должен вставлять новую книгу без LazyInitializationException")
    void shouldInsertNewBookWithoutLazyException() {
        var title = "newBook";

        var savedBook = bookService.insert(title, AUTHOR_ID, GENRE_IDS);

        assertThat(savedBook)
                .isNotNull()
                .matches(b -> b.id() > 0)
                .matches(b -> !b.title().isEmpty())
                .matches(b -> b.author() != null)
                .matches(b -> b.genres() != null);

        var entityGraph = em.getEntityGraph("Book.withAuthorAndGenres");
        var book = em.createQuery(
                        "SELECT b FROM Book b " +
                            "WHERE b.id = :id",
                        Book.class)
                .setParameter("id", savedBook.id())
                .setHint(FETCH.getKey(), entityGraph)
                .getSingleResult();

        var expectedBook = bookMapper.toDto(book);

        assertThat(savedBook)
                .usingRecursiveComparison()
                .isEqualTo(expectedBook);
    }

    @Test
    @DisplayName("должен обновлять книгу без LazyInitializationException")
    void shouldUpdateBookWithoutLazyException() {
        var title = "editedBook";

        var updatedBook = bookService.update(BOOK_ID, title, AUTHOR_ID, GENRE_IDS);

        assertThat(updatedBook)
                .isNotNull()
                .matches(b -> b.id() > 0)
                .matches(b -> !b.title().isEmpty())
                .matches(b -> b.author() != null)
                .matches(b -> b.genres() != null);

        var entityGraph = em.getEntityGraph("Book.withAuthorAndGenres");
        var book = em.createQuery(
                        "SELECT b FROM Book b " +
                            "WHERE b.id = :id",
                        Book.class)
                .setParameter("id", updatedBook.id())
                .setHint(FETCH.getKey(), entityGraph)
                .getSingleResult();

        var expectedBook = bookMapper.toDto(book);

        assertThat(updatedBook)
                .usingRecursiveComparison()
                .isEqualTo(expectedBook);
    }

    @Test
    @DisplayName("должен удалять книгу без LazyInitializationException")
    void shouldDeleteBookWithoutLazyException() {
        assertThat(bookService.findById(BOOK_ID)).isPresent();

        bookService.deleteById(BOOK_ID);

        var bookAfterDelete = bookService.findById(BOOK_ID);
        assertThat(bookAfterDelete).isEmpty();
    }
}