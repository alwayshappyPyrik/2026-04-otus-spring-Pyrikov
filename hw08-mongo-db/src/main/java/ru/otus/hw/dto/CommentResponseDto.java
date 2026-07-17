package ru.otus.hw.dto;

public record CommentResponseDto(
        String id,
        String text) {
    @Override
    public String toString() {
        return "Id: %s, Text: %s".formatted(id, text);
    }
}