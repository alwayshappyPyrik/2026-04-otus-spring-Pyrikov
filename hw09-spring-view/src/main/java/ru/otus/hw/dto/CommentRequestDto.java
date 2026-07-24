package ru.otus.hw.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record CommentRequestDto(
        Long id,
        BookRequestDto book,
        String text
) {}
