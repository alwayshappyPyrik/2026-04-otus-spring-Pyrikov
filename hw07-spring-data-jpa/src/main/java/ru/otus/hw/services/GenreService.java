package ru.otus.hw.services;

import ru.otus.hw.dto.GenresRequestDto;
import ru.otus.hw.dto.GenreResponseDto;

import java.util.List;

public interface GenreService {
    List<GenreResponseDto> findAll();

    List<GenreResponseDto> findAllByIds(GenresRequestDto genresRequestDto);
}