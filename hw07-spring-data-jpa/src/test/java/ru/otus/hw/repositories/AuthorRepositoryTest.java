package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Author;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Spring Data Jpa для работы с авторами ")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AuthorRepositoryTest {

    private static final long FIRST_AUTHOR_ID = 1L;
    private static final int EXPECTED_NUMBER_OF_AUTHOR = 3;
    private static final String FIRST_AUTHOR_NAME = "Author_1";

    @Autowired
    private AuthorRepository authorRepository;

    @DisplayName("должен загружать информацию о нужном авторе по его id")
    @Test
    void shouldFindExpectedAuthorById() {
        var optionalActualAuthor = authorRepository.findById(FIRST_AUTHOR_ID);

        assertThat(optionalActualAuthor)
                .isPresent()
                .get()
                .satisfies(author -> {
                    assertThat(author.getId()).isEqualTo(FIRST_AUTHOR_ID);
                    assertThat(author.getFullName()).isEqualTo(FIRST_AUTHOR_NAME);
                });
    }

    @DisplayName("должен загружать список всех авторов с полной информацией о них")
    @Test
    void shouldReturnCorrectAuthorsListWithAllInfo() {
        var authors = authorRepository.findAll();

        assertThat(authors)
                .isNotNull()
                .hasSize(EXPECTED_NUMBER_OF_AUTHOR)
                .allMatch(a -> a.getId() > 0)
                .allMatch(a -> !a.getFullName().isEmpty())
                .extracting(Author::getFullName)
                .containsExactlyInAnyOrder("Author_1", "Author_2", "Author_3");
    }
}