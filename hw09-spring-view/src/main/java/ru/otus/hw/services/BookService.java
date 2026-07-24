package ru.otus.hw.services;

import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Optional<BookResponseDto> findById(BookRequestDto bookRequestDto);

    List<BookResponseDto> findAll();

    BookResponseDto insert(BookRequestDto bookRequestDto);

    BookResponseDto update(BookRequestDto bookRequestDto);

    void deleteById(BookRequestDto bookRequestDto);
}