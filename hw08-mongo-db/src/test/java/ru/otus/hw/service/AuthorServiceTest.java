package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.dto.AuthorRequestDto;
import ru.otus.hw.dto.AuthorResponseDto;
import ru.otus.hw.mapper.AuthorMapperImpl;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.AuthorServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с авторами")
@DataMongoTest
@Import({AuthorServiceImpl.class,
        AuthorMapperImpl.class})
public class AuthorServiceTest {

    private static final int EXPECTED_NUMBER_OF_AUTHORS = 3;
    private static final String FIRST_AUTHOR_NAME = "Author_1";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("authors");

        List<Author> authors = List.of(
                new Author(null, "Author_1"),
                new Author(null, "Author_2"),
                new Author(null, "Author_3")
        );
        authors.forEach(author -> mongoTemplate.save(author, "authors"));
    }

    @DisplayName("должен загружать информацию о нужном авторе по его id")
    @Test
    void shouldFindExpectedAuthorById() {
        var allAuthors = authorRepository.findAll();
        assertThat(allAuthors).isNotEmpty();

        var author = allAuthors.getFirst();
        var authorId = author.getId();

        AuthorRequestDto request = AuthorRequestDto.builder()
                .id(authorId)
                .fullName(null)
                .build();

        Optional<AuthorResponseDto> optionalActualAuthor = authorService.findById(request);

        assertThat(optionalActualAuthor)
                .isPresent()
                .get()
                .satisfies(authorDto -> {
                    assertThat(authorDto.id()).isEqualTo(authorId);
                    assertThat(authorDto.fullName()).isEqualTo(FIRST_AUTHOR_NAME);
                });
    }

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnAllAuthors() {
        var authors = authorService.findAll();

        assertThat(authors)
                .isNotNull()
                .hasSize(EXPECTED_NUMBER_OF_AUTHORS)
                .allMatch(dto -> dto.id() != null && !dto.id().isBlank())
                .allMatch(dto -> dto.fullName() != null && !dto.fullName().isBlank())
                .extracting(AuthorResponseDto::fullName)
                .containsExactlyInAnyOrder("Author_1", "Author_2", "Author_3");
    }
}