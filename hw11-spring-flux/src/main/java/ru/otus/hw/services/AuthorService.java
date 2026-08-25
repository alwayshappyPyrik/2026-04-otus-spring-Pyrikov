package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorRequestDto;
import ru.otus.hw.dto.AuthorResponseDto;

public interface AuthorService {
    Flux<AuthorResponseDto> findAll();

    Mono<AuthorResponseDto> findById(AuthorRequestDto authorRequestDto);
}