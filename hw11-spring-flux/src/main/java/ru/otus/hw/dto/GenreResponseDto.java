package ru.otus.hw.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record GenreResponseDto(
        Long id,
        String name
) {
    @Override
    public String toString() {
        return "Id: %d, Name: %s".formatted(id, name);
    }
}