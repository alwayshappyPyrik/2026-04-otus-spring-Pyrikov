package ru.otus.hw.services;

import ru.otus.hw.dto.AuthorRequestDto;
import ru.otus.hw.dto.AuthorResponseDto;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    List<AuthorResponseDto> findAll();

    Optional<AuthorResponseDto> findById(AuthorRequestDto authorRequestDto);
}