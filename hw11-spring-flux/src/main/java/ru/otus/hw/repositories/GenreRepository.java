package ru.otus.hw.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Genre;

import java.util.Set;

@Repository
public interface GenreRepository extends ReactiveCrudRepository<Genre, Long> {
    @Query("SELECT * FROM Genres WHERE id IN (:ids)")
    Flux<Genre> findAllByIds(Set<Long> ids);
}