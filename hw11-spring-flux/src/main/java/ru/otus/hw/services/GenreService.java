package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenresRequestDto;
import ru.otus.hw.dto.GenreResponseDto;

public interface GenreService {
    Flux<GenreResponseDto> findAll();

    Flux<GenreResponseDto> findAllByIds(GenresRequestDto genresRequestDto);
}