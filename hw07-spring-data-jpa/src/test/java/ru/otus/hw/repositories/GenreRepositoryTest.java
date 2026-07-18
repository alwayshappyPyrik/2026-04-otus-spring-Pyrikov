package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Genre;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Spring Data Jpa для работы с жанрами ")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class GenreRepositoryTest {

    private static final int EXPECTED_NUMBER_OF_GENRE = 6;
    private static final Set<Long> SELECTED_GENRE_IDS = Set.of(1L, 3L, 5L);

    @Autowired
    private GenreRepository genreRepository;

    @DisplayName("должен загружать список всех жанров с полной информацией о них")
    @Test
    void shouldReturnCorrectGenresListWithAllInfo() {
        var genres = genreRepository.findAll();

        assertThat(genres)
                .isNotNull()
                .hasSize(EXPECTED_NUMBER_OF_GENRE)
                .allMatch(g -> g.getId() > 0)
                .allMatch(g -> g.getName() != null && !g.getName().isEmpty());

        assertThat(genres)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder(
                        "Genre_1", "Genre_2", "Genre_3",
                        "Genre_4", "Genre_5", "Genre_6"
                );
    }

    @DisplayName("должен загружать список жанров по указанным айдишкам")
    @Test
    void shouldReturnCorrectGenresListWithSelectedIds() {
        var actualGenres = genreRepository.findAllByIds(SELECTED_GENRE_IDS);

        assertThat(actualGenres)
                .isNotNull()
                .hasSize(SELECTED_GENRE_IDS.size());

        assertThat(actualGenres)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(1L, 3L, 5L);

        assertThat(actualGenres)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder("Genre_1", "Genre_3", "Genre_5");

        assertThat(actualGenres)
                .allMatch(g -> SELECTED_GENRE_IDS.contains(g.getId()))
                .allMatch(g -> g.getName() != null && !g.getName().isEmpty());
    }

    @DisplayName("должен возвращать пустой список жанров, если переданы несуществующие id")
    @Test
    void shouldReturnEmptyListWhenIdsNotFound() {
        Set<Long> nonExistentIds = Set.of(999L, 1000L);

        var actualGenres = genreRepository.findAllByIds(nonExistentIds);

        assertThat(actualGenres)
                .isNotNull()
                .isEmpty();
    }
}