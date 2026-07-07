package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с книгами ")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class BookRepositoryTest {

    private static final long FIRST_BOOK_ID = 1L;
    private static final int EXPECTED_NUMBER_OF_BOOKS = 3;
    private static final String FIRST_BOOK_TITLE = "BookTitle_1";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    private List<Author> dbAuthors;
    private List<Genre> dbGenres;

    @DisplayName("должен загружать информацию о нужной книге по ее id")
    @Test
    void shouldFindExpectedBookById() {
        var actualBook = bookRepository.findById(FIRST_BOOK_ID);

        assertThat(actualBook)
                .isPresent()
                .hasValueSatisfying(book -> {
                    assertThat(book.getId()).isEqualTo(FIRST_BOOK_ID);
                    assertThat(book.getTitle()).isEqualTo(FIRST_BOOK_TITLE);
                    assertThat(book.getAuthor()).isNotNull();
                    assertThat(book.getAuthor().getId()).isGreaterThan(0);
                    assertThat(book.getGenres()).isNotEmpty();
                });
    }

    @DisplayName("должен загружать список всех книг с полной информацией о них")
    @Test
    void shouldReturnCorrectBooksListWithAllInfo() {
        var books = bookRepository.findAll();

        assertThat(books)
                .isNotNull()
                .hasSize(EXPECTED_NUMBER_OF_BOOKS)
                .allMatch(b -> b.getId() > 0)
                .allMatch(b -> !b.getTitle().isEmpty())
                .allMatch(b -> b.getAuthor() != null && b.getAuthor().getId() > 0)
                .allMatch(b -> b.getAuthor().getFullName() != null && !b.getAuthor().getFullName().isEmpty())
                .allMatch(b -> b.getGenres() != null && !b.getGenres().isEmpty())
                .allMatch(b -> b.getGenres().stream().allMatch(g -> g.getId() > 0));

        assertThat(books)
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder("BookTitle_1", "BookTitle_2", "BookTitle_3");
    }

    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteBook() {
        assertThat(bookRepository.findById(FIRST_BOOK_ID)).isPresent();

        bookRepository.deleteById(FIRST_BOOK_ID);

        assertThat(bookRepository.findById(FIRST_BOOK_ID)).isEmpty();
    }

    @Nested
    @DisplayName("Тяжелые тесты для книг")
    class BookRepositoryHeavyTest {

        @BeforeEach
        void setUp() {
            dbAuthors = authorRepository.findAll();
            dbGenres = genreRepository.findAll();
        }

        @DisplayName("должен сохранять новую книгу с полной информацией о ней")
        @Test
        void shouldSaveNewBook() {
            var expectedBook = new Book(
                    "New Book",
                    dbAuthors.getFirst(),
                    List.of(dbGenres.get(0), dbGenres.get(2))
            );

            var returnedBook = bookRepository.save(expectedBook);

            assertThat(returnedBook)
                    .isNotNull()
                    .matches(book -> book.getId() > 0)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expectedBook);

            var foundBook = bookRepository.findById(returnedBook.getId());
            assertThat(foundBook)
                    .isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .isEqualTo(returnedBook);
        }

        @DisplayName("должен сохранять измененную книгу")
        @Test
        void shouldSaveUpdatedBook() {
            var bookToUpdate = bookRepository.findById(1L).orElseThrow();

            var expectedBook = new Book(
                    bookToUpdate.getId(),
                    "Edited Book",
                    dbAuthors.get(2),
                    List.of(dbGenres.get(4), dbGenres.get(5))
            );

            assertThat(bookRepository.findById(expectedBook.getId()))
                    .isPresent()
                    .get()
                    .isNotEqualTo(expectedBook);

            var returnedBook = bookRepository.save(expectedBook);

            assertThat(returnedBook)
                    .isNotNull()
                    .matches(book -> book.getId() > 0)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedBook);

            assertThat(bookRepository.findById(returnedBook.getId()))
                    .isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .isEqualTo(returnedBook);
        }
    }
}