package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookCreateRequestDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.BookUpdateRequestDto;

public interface BookService {
    Mono<BookResponseDto> findById(BookRequestDto bookRequestDto);

    Flux<BookResponseDto> findAll();

    Mono<BookResponseDto> insert(BookCreateRequestDto bookCreateRequestDto);

    Mono<BookResponseDto> update(BookUpdateRequestDto bookUpdateRequestDto);

    Mono<Void> deleteById(BookRequestDto bookRequestDto);
}