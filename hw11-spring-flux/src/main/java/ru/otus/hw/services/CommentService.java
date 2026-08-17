package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentRequestDto;
import ru.otus.hw.dto.CommentResponseDto;

public interface CommentService {

    Mono<CommentResponseDto> findById(CommentRequestDto commentRequestDto);

    Flux<CommentResponseDto> findAllByBookId(CommentRequestDto commentRequestDto);

    Mono<CommentResponseDto> insert(CommentRequestDto commentRequestDto);

    Mono<CommentResponseDto> update(CommentRequestDto commentRequestDto);

    Mono<Void> deleteById(CommentRequestDto commentRequestDto);
}