package ru.otus.hw.services;

import ru.otus.hw.dto.CommentRequestDto;
import ru.otus.hw.dto.CommentResponseDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    Optional<CommentResponseDto> findById(CommentRequestDto commentRequestDto);

    List<CommentResponseDto> findAllByBookId(CommentRequestDto commentRequestDto);

    CommentResponseDto insert(CommentRequestDto commentRequestDto);

    CommentResponseDto update(CommentRequestDto commentRequestDto);

    void deleteById(CommentRequestDto commentRequestDto);
}