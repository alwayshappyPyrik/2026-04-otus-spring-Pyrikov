package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.dto.GenreRequestDto;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.mapper.GenreMapperImpl;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.GenreServiceImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с жанрами")
@DataMongoTest
@Import({
        GenreServiceImpl.class,
        GenreMapperImpl.class
})
public class GenreServiceTest {

    private static final int EXPECTED_NUMBER_OF_GENRES = 6;
    private static final String FIRST_GENRE_NAME = "Genre_1";
    private static final String SECOND_GENRE_NAME = "Genre_2";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private GenreService genreService;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("genres");
        mongoTemplate.dropCollection("books");
        mongoTemplate.dropCollection("authors");
        mongoTemplate.dropCollection("comments");

        List<Genre> genres = List.of(
                new Genre(null, "Genre_1"),
                new Genre(null, "Genre_2"),
                new Genre(null, "Genre_3"),
                new Genre(null, "Genre_4"),
                new Genre(null, "Genre_5"),
                new Genre(null, "Genre_6")
        );
        genres.forEach(genre -> mongoTemplate.save(genre, "genres"));
    }


    @DisplayName("должен загружать список всех жанров с полной информацией о них")
    @Test
    void shouldReturnCorrectGenresListWithAllInfo() {
        var genres = genreService.findAll();

        assertThat(genres)
                .isNotNull()
                .hasSize(EXPECTED_NUMBER_OF_GENRES)
                .allMatch(g -> !g.id().isBlank())
                .allMatch(g -> g.name() != null && !g.name().isBlank())
                .extracting(GenreResponseDto::name)
                .containsExactlyInAnyOrder(
                        "Genre_1", "Genre_2", "Genre_3",
                        "Genre_4", "Genre_5", "Genre_6"
                );
    }

    @DisplayName("должен загружать жанры по списку id через GenreRequestDto")
    @Test
    void shouldFindGenresByIds() {
        var allGenres = genreRepository.findAll();
        assertThat(allGenres).hasSize(EXPECTED_NUMBER_OF_GENRES);

        Set<String> genreIds = allGenres.stream()
                .limit(2)
                .map(Genre::getId)
                .collect(Collectors.toSet());

        GenreRequestDto request = GenreRequestDto.builder()
                .id(genreIds)
                .build();

        List<GenreResponseDto> foundGenres = genreService.findAllByIds(request);

        assertThat(foundGenres)
                .isNotNull()
                .hasSize(2)
                .extracting(GenreResponseDto::name)
                .containsExactlyInAnyOrder(FIRST_GENRE_NAME, SECOND_GENRE_NAME);
    }
}