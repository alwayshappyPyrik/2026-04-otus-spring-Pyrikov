package ru.otus.hw.dto;

public record GenreResponseDto(
        String id,
        String name
) {
    @Override
    public String toString() {
        return "Id: %s, Name: %s".formatted(id, name);
    }
}
