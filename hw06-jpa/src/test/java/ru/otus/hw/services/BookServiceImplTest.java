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
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Интеграционные тесты сервиса книг ")
@SpringBootTest
@Transactional
public class BookServiceImplTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookMapper bookMapper;

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
        Set<Long> genreIds = Set.of(testGenre1.getId(), testGenre2.getId());

        List<Genre> sortedGenres = genreIds.stream()
                .map(id -> em.find(Genre.class, id))
                .sorted(Comparator.comparing(Genre::getId))
                .toList();

        Book bookEntity = Book.builder()
                .id(testBook.getId())
                .title(testBook.getTitle())
                .author(testAuthor)
                .genres(sortedGenres)
                .build();

        BookDto expectedBookDto = bookMapper.toDto(bookEntity);

        var optionalBookDto = bookService.findById(testBook.getId());

        assertThat(optionalBookDto)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedBookDto);
    }

    @Test
    @DisplayName("должен загружать список всех книг без LazyInitializationException")
    void shouldFindAllBooksWithoutLazyException() {
        List<BookDto> expectedBooks = bookService.findAll().stream()
                .map(dto -> bookMapper.toEntity(dto))
                .map(bookMapper::toDto).toList();

        List<BookDto> actualBooks = bookService.findAll();

        assertThat(actualBooks)
                .usingRecursiveComparison()
                .isEqualTo(expectedBooks);
    }

    @Test
    @DisplayName("должен вставлять новую книгу без LazyInitializationException")
    void shouldInsertNewBookWithoutLazyException() {
        String newTitle = "newBook";
        Set<Long> genreIds = Set.of(testGenre1.getId(), testGenre2.getId());

        List<Genre> sortedGenres = genreIds.stream()
                .map(id -> em.find(Genre.class, id))
                .sorted(Comparator.comparing(Genre::getId))
                .toList();

        Book bookEntity = Book.builder()
                .title(newTitle)
                .author(testAuthor)
                .genres(sortedGenres)
                .build();

        BookDto expected = bookMapper.toDto(bookEntity);

        BookDto savedBook = bookService.insert(newTitle, testAuthor.getId(), genreIds);

        assertThat(savedBook)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("должен обновлять книгу без LazyInitializationException")
    void shouldUpdateBookWithoutLazyException() {
        long bookId = testBook.getId();
        String updatedTitle = "editedBook";
        Set<Long> genreIds = Set.of(testGenre1.getId());

        List<Genre> sortedGenres = genreIds.stream()
                .map(id -> em.find(Genre.class, id))
                .sorted(Comparator.comparing(Genre::getId))
                .toList();

        Book bookEntity = Book.builder()
                .id(bookId)
                .title(updatedTitle)
                .author(testAuthor)
                .genres(sortedGenres)
                .build();

        BookDto expected = bookMapper.toDto(bookEntity);

        BookDto updatedBook = bookService.update(bookId, updatedTitle, testAuthor.getId(), genreIds);

        assertThat(updatedBook)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("должен удалять книгу без LazyInitializationException")
    void shouldDeleteBookWithoutLazyException() {
        long bookId = testBook.getId();

        bookService.deleteById(bookId);

        var bookAfterDelete = bookService.findById(bookId);
        assertThat(bookAfterDelete).isEmpty();
    }
}