package ru.otus.hw.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("Интеграционные тесты сервиса книг ")
@SpringBootTest
@Transactional
public class BookServiceImplTest {

    @Autowired
    private BookService bookService;

    @PersistenceContext
    private EntityManager em;

    private Author testAuthor;
    private Genre testGenre1;
    private Genre testGenre2;
    private Book testBook;

    @BeforeEach
    void setUp() {
        testAuthor = em.find(Author.class, 1L);
        testGenre1 = em.find(Genre.class, 1L);
        testGenre2 = em.find(Genre.class, 2L);
        testBook = em.find(Book.class, 1L);
    }

    @Test
    @DisplayName("должен загружать книгу по id без LazyInitializationException")
    void shouldFindBookByIdWithoutLazyException() {
        assertThatCode(() -> {
            var optionalBookDto = bookService.findById(testBook.getId());
            assertThat(optionalBookDto).isPresent();

            BookDto book = optionalBookDto.get();

            assertThat(book.author().fullName()).isNotNull();
            assertThat(book.genres()).isNotEmpty();
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("должен загружать список всех книг без LazyInitializationException")
    void shouldFindAllBooksWithoutLazyException() {
        assertThatCode(() -> {
            var books = bookService.findAll();
            assertThat(books).isNotEmpty();

            books.forEach(book -> {
                assertThat(book.author().fullName()).isNotNull();
                assertThat(book.genres()).isNotEmpty();
            });
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("должен вставлять новую книгу без LazyInitializationException")
    void shouldInsertNewBookWithoutLazyException() {
        assertThatCode(() -> {
            BookDto savedBook = bookService.insert(
                    "newBook",
                    testAuthor.getId(),
                    Set.of(testGenre1.getId(), testGenre2.getId())
            );

            assertThat(savedBook.author().fullName()).isNotNull();
            assertThat(savedBook.genres()).isNotEmpty();
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("должен обновлять книгу без LazyInitializationException")
    void shouldUpdateNewBookWithoutLazyException() {
        long bookId = testBook.getId();
        assertThatCode(() -> {
            BookDto savedBook = bookService.update(
                    bookId,
                    "newBook",
                    testAuthor.getId(),
                    Set.of(testGenre1.getId(), testGenre2.getId())
            );

            assertThat(savedBook.author().fullName()).isNotNull();
            assertThat(savedBook.genres()).isNotEmpty();
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("должен удалять книгу без LazyInitializationException")
    void shouldDeleteBookWithoutLazyException() {
        long bookId = testBook.getId();
        assertThatCode(() -> {
            var bookBeforeDelete = bookService.findById(bookId);
            assertThat(bookBeforeDelete).isPresent();

            bookService.deleteById(bookId);

            var bookAfterDelete = bookService.findById(bookId);
            assertThat(bookAfterDelete).isEmpty();
        }).doesNotThrowAnyException();
    }
}