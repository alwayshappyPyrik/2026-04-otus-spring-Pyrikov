package ru.otus.hw.dto;

import lombok.Builder;

import java.util.Set;

@Builder
public record GenreRequestDto(
        Set<String> id
) {
}
