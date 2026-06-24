package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с жанрами ")
@DataJpaTest
@Import(JpaGenreRepository.class)
public class JpaGenreRepositoryTest {

    private static final int EXPECTED_NUMBER_OF_GENRE = 6;

    @Autowired
    private JpaGenreRepository jpaGenreRepository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("должен загружать список всеx жанры с полной информацией о них")
    @Test
    void shouldReturnCorrectGenresListWithAllInfo() {
        var genres = jpaGenreRepository.findAll();
        assertThat(genres).isNotNull().hasSize(EXPECTED_NUMBER_OF_GENRE)
                .allMatch(g -> g.getId() > 0)
                .allMatch(g -> !g.getName().isEmpty());
    }

    @DisplayName("должен загружать список жанров по указанным айдишкам")
    @Test
    void shouldReturnCorrectGenresListWithSelectedIds() {
        Set<Long> genreIds = Set.of(1L, 3L, 5L);

        var actualGenres = jpaGenreRepository.findAllByIds(genreIds);

        var optionalActualGenres = List.of(
                em.find(Genre.class, 1L),
                em.find(Genre.class, 3L),
                em.find(Genre.class, 5L)
        );

        assertThat(actualGenres)
                .isNotNull()
                .hasSize(genreIds.size())
                .usingRecursiveComparison()
                .isEqualTo(optionalActualGenres);
    }

    @DisplayName("должен возвращать пустой список жанров, если переданы несуществующие id")
    @Test
    void shouldReturnEmptyListWhenIdsNotFound() {
        Set<Long> ids = Set.of(999L, 1000L);

        var actualGenres = jpaGenreRepository.findAllByIds(ids);

        assertThat(actualGenres)
                .isNotNull()
                .isEmpty();
    }
}