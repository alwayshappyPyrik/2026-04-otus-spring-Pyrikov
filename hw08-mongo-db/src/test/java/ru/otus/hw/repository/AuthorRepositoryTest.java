package ru.otus.hw.repository;

import io.mongock.test.springboot.junit5.MongockSpringbootJUnit5IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.config.TestMongockConfig;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Spring Data MongoDB для работы с авторами")
@DataMongoTest
@ActiveProfiles("test")
@Import(TestMongockConfig.class)
public class AuthorRepositoryTest extends MongockSpringbootJUnit5IntegrationTestBase {

    private static final int EXPECTED_NUMBER_OF_AUTHOR = 3;
    private static final String FIRST_AUTHOR_NAME = "Author_1";

    @Autowired
    private MongoTemplate mongoTemplate;

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

    @DisplayName("должен загружать информацию о нужном авторе по его id")
    @Test
    void shouldFindExpectedAuthorById() {
        var allAuthors = authorRepository.findAll();
        assertThat(allAuthors).isNotEmpty();

        var authorId = allAuthors.getFirst().getId();

        var optionalActualAuthor = authorRepository.findById(authorId);

        assertThat(optionalActualAuthor)
                .isPresent()
                .get()
                .satisfies(author -> {
                    assertThat(author.getId()).isEqualTo(authorId);
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
                .allMatch(a -> a.getId() != null && !a.getId().isBlank())
                .allMatch(a -> a.getFullName() != null && !a.getFullName().isBlank())
                .extracting(Author::getFullName)
                .containsExactlyInAnyOrder("Author_1", "Author_2", "Author_3");
    }
}
