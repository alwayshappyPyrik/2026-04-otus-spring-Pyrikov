package ru.otus.hw.dto;

import java.util.Set;
import java.util.stream.Collectors;

public record BookResponseDto(
        Long id,
        String title,
        AuthorResponseDto author,
        Set<GenreResponseDto> genres
) {
    @Override
    public String toString() {
        var genresString = genres.stream()
                .map(GenreResponseDto::toString)
                .collect(Collectors.joining(","));

        return "Id: %d, title: %s, author: {%s}, genres: [%s]".formatted(
                id,
                title,
                author,
                genresString
        );
    }
}