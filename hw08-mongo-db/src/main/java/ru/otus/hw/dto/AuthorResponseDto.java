package ru.otus.hw.dto;

public record AuthorResponseDto(
        String id,
        String fullName
) {
    @Override
    public String toString() {
        return "Id: %s, FullName: %s".formatted(id, fullName);
    }
}