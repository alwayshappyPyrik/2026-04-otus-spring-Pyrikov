package ru.otus.hw.dto;

public record CommentResponseDto(
        Long id,
        String text) {
    @Override
    public String toString() {
        return "Id: %d, Text: %s".formatted(id, text);
    }
}