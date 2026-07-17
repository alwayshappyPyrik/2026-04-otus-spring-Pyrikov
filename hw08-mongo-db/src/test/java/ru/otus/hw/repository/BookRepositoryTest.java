package ru.otus.hw.repository;

import io.mongock.test.springboot.junit5.MongockSpringbootJUnit5IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.config.TestMongockConfig;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Spring Data MongoDB для работы с книгами")
@DataMongoTest
@ActiveProfiles("test")
@Import(TestMongockConfig.class)
public class BookRepositoryTest extends MongockSpringbootJUnit5IntegrationTestBase {

    private static final int EXPECTED_NUMBER_OF_BOOKS = 3;
    private static final String FIRST_BOOK_TITLE = "BookTitle_1";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("authors");
        mongoTemplate.dropCollection("books");
        mongoTemplate.dropCollection("comments");
        mongoTemplate.dropCollection("mongockChangeLog");

        executeMongock();
    }

    private List<Author> dbAuthors;
    private Set<Genre> dbGenres;

    @DisplayName("должен загружать информацию о нужной книге по ее id")
    @Test
    void shouldFindExpectedBookById() {
        var allBooks = bookRepository.findAll();
        assertThat(allBooks).isNotEmpty();

        var bookId = allBooks.getFirst().getId();

        var actualBook = bookRepository.findById(bookId);

        assertThat(actualBook)
                .isPresent()
                .hasValueSatisfying(book -> {
                    assertThat(book.getId()).isEqualTo(bookId);
                    assertThat(book.getTitle()).isEqualTo(FIRST_BOOK_TITLE);
                    assertThat(book.getAuthor()).isNotNull();
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
                .allMatch(b -> !b.getId().isBlank())
                .allMatch(b -> !b.getTitle().isBlank())
                .allMatch(b -> b.getAuthor() != null && !b.getAuthor().getId().isBlank())
                .allMatch(b -> b.getAuthor().getFullName() != null && !b.getAuthor().getFullName().isBlank())
                .allMatch(b -> b.getGenres() != null && !b.getGenres().isEmpty());

        assertThat(books)
                .extracting(Book::getTitle)
                .containsExactly("BookTitle_1", "BookTitle_2", "BookTitle_3");
    }

    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteBook() {
        var allBooks = bookRepository.findAll();
        assertThat(allBooks).isNotEmpty();

        var bookId = allBooks.getFirst().getId();

        assertThat(bookRepository.findById(bookId));

        bookRepository.deleteById(bookId);

        assertThat(bookRepository.findById(bookId)).isEmpty();
    }

    @Nested
    @DisplayName("Тяжелые тесты для книг")
    class BookRepositoryHeavyTest {

        @BeforeEach
        void setUp() {
            dbAuthors = authorRepository.findAll();
            var genre1 = new Genre("Genre_1");
            var genre2 = new Genre("Genre_2");
            dbGenres = Set.of(genre1, genre2);
        }

        @DisplayName("должен сохранять новую книгу с полной информацией о ней")
        @Test
        void shouldSaveNewBook() {
            var expectedBook = new Book(
                    "New Book",
                    dbAuthors.getFirst(),
                    dbGenres
            );

            var returnedBook = bookRepository.save(expectedBook);

            assertThat(returnedBook)
                    .isNotNull()
                    .satisfies(book -> {
                        assertThat(book.getId()).isNotBlank();
                        assertThat(book.getTitle()).isEqualTo("New Book");
                        assertThat(book.getAuthor())
                                .isNotNull()
                                .satisfies(author -> {
                                    assertThat(author.getId()).isEqualTo(dbAuthors.getFirst().getId());
                                    assertThat(author.getFullName()).isEqualTo(dbAuthors.getFirst().getFullName());
                                });
                        assertThat(book.getGenres())
                                .isNotNull()
                                .hasSize(2)
                                .containsExactlyInAnyOrderElementsOf(dbGenres);
                    });

            var foundBook = bookRepository.findById(returnedBook.getId());
            assertThat(foundBook)
                    .isPresent()
                    .get()
                    .satisfies(book -> {
                        assertThat(book.getId()).isEqualTo(returnedBook.getId());
                        assertThat(book.getTitle()).isEqualTo(returnedBook.getTitle());
                        assertThat(book.getAuthor().getId()).isEqualTo(returnedBook.getAuthor().getId());
                        assertThat(book.getGenres())
                                .hasSize(returnedBook.getGenres().size())
                                .containsExactlyInAnyOrderElementsOf(returnedBook.getGenres());
                    });
        }

        @DisplayName("должен сохранять измененную книгу")
        @Test
        void shouldSaveUpdatedBook() {
            var allBooks = bookRepository.findAll();
            assertThat(allBooks).isNotEmpty();

            var bookId = allBooks.getFirst().getId();
            var bookToUpdate = bookRepository.findById(bookId).orElseThrow();

            var expectedBook = new Book(
                    bookToUpdate.getId(),
                    "Edited Book",
                    dbAuthors.get(2),
                    dbGenres
            );

            assertThat(bookRepository.findById(expectedBook.getId()))
                    .isPresent()
                    .get()
                    .satisfies(book -> {
                        assertThat(book.getTitle()).isNotEqualTo("Edited Book");
                        assertThat(book.getAuthor().getId()).isNotEqualTo(dbAuthors.get(2).getId());
                    });

            var returnedBook = bookRepository.save(expectedBook);

            assertThat(returnedBook)
                    .isNotNull()
                    .satisfies(book -> {
                        assertThat(book.getId()).isNotBlank();
                        assertThat(book.getId()).isEqualTo(expectedBook.getId());
                        assertThat(book.getTitle()).isEqualTo("Edited Book");
                        assertThat(book.getAuthor())
                                .isNotNull()
                                .satisfies(author -> {
                                    assertThat(author.getId()).isEqualTo(dbAuthors.get(2).getId());
                                    assertThat(author.getFullName()).isEqualTo(dbAuthors.get(2).getFullName());
                                });
                        assertThat(book.getGenres())
                                .isNotNull()
                                .hasSize(2)
                                .containsExactlyInAnyOrderElementsOf(dbGenres);
                    });

            var foundBook = bookRepository.findById(returnedBook.getId());
            assertThat(foundBook)
                    .isPresent()
                    .get()
                    .satisfies(book -> {
                        assertThat(book.getId()).isEqualTo(returnedBook.getId());
                        assertThat(book.getTitle()).isEqualTo("Edited Book");
                        assertThat(book.getAuthor().getId()).isEqualTo(dbAuthors.get(2).getId());
                        assertThat(book.getGenres())
                                .hasSize(dbGenres.size())
                                .containsExactlyInAnyOrderElementsOf(dbGenres);
                    });
        }
    }
}
