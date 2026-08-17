package ru.otus.hw.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.BookGenre;

@Repository
public interface BookGenreRepository extends ReactiveCrudRepository<BookGenre, Long> {

    Flux<BookGenre> findByBookId(Long bookId);

    Flux<BookGenre> findByGenreId(Long genreId);

    Mono<Object> deleteByBookId(Long id);
}
