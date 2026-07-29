package ru.otus.hw.services;

import ru.otus.hw.dto.BookCreateRequestDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.BookUpdateRequestDto;

import java.util.List;

public interface BookService {
    BookResponseDto findById(BookRequestDto bookRequestDto);

    List<BookResponseDto> findAll();

    BookResponseDto insert(BookCreateRequestDto bookCreateRequestDto);

    BookResponseDto update(BookUpdateRequestDto bookUpdateRequestDto);

    void deleteById(BookRequestDto bookRequestDto);
}