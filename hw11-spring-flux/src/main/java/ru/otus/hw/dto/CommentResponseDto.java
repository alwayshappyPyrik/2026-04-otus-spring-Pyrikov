package ru.otus.hw.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record CommentResponseDto(
        Long id,
        String text,
        BookResponseDto book) {
    @Override
    public String toString() {
        return "Id: %d, Text: %s".formatted(id, text);
    }
}