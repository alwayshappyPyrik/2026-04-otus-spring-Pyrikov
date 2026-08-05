package ru.otus.hw.services;

import ru.otus.hw.dto.AuthorRequestDto;
import ru.otus.hw.dto.AuthorResponseDto;

import java.util.List;

public interface AuthorService {
    List<AuthorResponseDto> findAll();

    AuthorResponseDto findById(AuthorRequestDto authorRequestDto);
}