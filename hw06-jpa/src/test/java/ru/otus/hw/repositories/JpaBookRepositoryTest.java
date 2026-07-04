package ru.otus.hw.repositories;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с книгами ")
@DataJpaTest
@Import(JpaBookRepository.class)
class JpaBookRepositoryTest {

    private static final long FIRST_BOOK_ID = 1L;
    private static final int EXPECTED_NUMBER_OF_BOOKS = 3;

    private static final long EXPECTED_QUERIES_COUNT = 2L;

    @Autowired
    private JpaBookRepository jpaBookRepository;

    @Autowired
    private TestEntityManager em;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    @DisplayName("должен загружать информацию о нужной книге по ее id")
    @Test
    void shouldFindExpectedBookById() {
        var optionalActualBook = jpaBookRepository.findById(FIRST_BOOK_ID);
        var expectedBook = em.find(Book.class, FIRST_BOOK_ID);
        assertThat(optionalActualBook).isPresent().get()
                .usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг с полной информацией о них")
    @Test
    void shouldReturnCorrectBooksListWithAllInfo() {
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);

        var books = jpaBookRepository.findAll();
        assertThat(books).isNotNull().hasSize(EXPECTED_NUMBER_OF_BOOKS)
                .allMatch(b -> b.getId() > 0)
                .allMatch(b -> !b.getTitle().isEmpty())
                .allMatch(b -> b.getAuthor().getId() > 0)
                .allMatch(b -> b.getAuthor().getFullName() != null)
                .allMatch(b -> b.getGenres() != null && !b.getGenres().isEmpty());

        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(EXPECTED_QUERIES_COUNT);
    }

    @DisplayName(" должен удалять книгу по id")
    @Test
    void shouldDeleteBook() {
        assertThat(jpaBookRepository.findById(1L)).isPresent();
        jpaBookRepository.deleteById(1L);
        assertThat(jpaBookRepository.findById(1L)).isEmpty();
    }

    @Nested
    public class JpaBookRepositoryHeavyTest {

        @BeforeEach
        void setUp() {
            dbAuthors = getDbAuthors();
            dbGenres = getDbGenres();
        }

        @DisplayName("должен сохранять новую книгу c полной информации о ней")
        @Test
        void shouldSaveNewBook() {
            var expectedBook = new Book(
                    "newBook",
                    dbAuthors.getFirst(),
                    List.of(dbGenres.get(0), dbGenres.get(2))
            );

            var returnedBook = jpaBookRepository.save(expectedBook);

            assertThat(returnedBook).isNotNull()
                    .matches(book -> book.getId() > 0)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expectedBook);

            var foundBook = jpaBookRepository.findById(returnedBook.getId());
            assertThat(foundBook)
                    .isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .isEqualTo(returnedBook);
        }

        @DisplayName("должен сохранять измененную книгу")
        @Test
        void shouldSaveUpdatedBook() {
            var expectedBook = new Book(1L, "editedBook", dbAuthors.get(2),
                    List.of(dbGenres.get(4), dbGenres.get(5)));

            assertThat(jpaBookRepository.findById(expectedBook.getId()))
                    .isPresent()
                    .get()
                    .isNotEqualTo(expectedBook);

            var returnedBook = jpaBookRepository.save(expectedBook);
            assertThat(returnedBook).isNotNull()
                    .matches(book -> book.getId() > 0)
                    .usingRecursiveComparison().isEqualTo(expectedBook);

            assertThat(jpaBookRepository.findById(returnedBook.getId()))
                    .isPresent()
                    .get()
                    .isEqualTo(returnedBook);
        }
    }

    private List<Author> getDbAuthors() {
        return List.of(
                em.find(Author.class, 1L),
                em.find(Author.class, 2L),
                em.find(Author.class, 3L)
        );
    }

    private List<Genre> getDbGenres() {
        return List.of(
                em.find(Genre.class, 1L),
                em.find(Genre.class, 2L),
                em.find(Genre.class, 3L),
                em.find(Genre.class, 4L),
                em.find(Genre.class, 5L),
                em.find(Genre.class, 6L)
        );
    }
}