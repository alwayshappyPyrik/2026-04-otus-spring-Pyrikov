package ru.otus.hw.dto;

public record GenreResponseDto(
        Long id,
        String name
) {
    @Override
    public String toString() {
        return "Id: %d, Name: %s".formatted(id, name);
    }
}