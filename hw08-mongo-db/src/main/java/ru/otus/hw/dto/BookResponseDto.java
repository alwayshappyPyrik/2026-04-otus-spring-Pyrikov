package ru.otus.hw.dto;

import ru.otus.hw.dto.AuthorResponseDto;
import ru.otus.hw.models.Genre;

import java.util.Set;
import java.util.stream.Collectors;

public record BookResponseDto(
        String id,
        String title,
        AuthorResponseDto author,
        Set<Genre> genres
) {
    @Override
    public String toString() {
        String genresNames = genres.stream()
                .map(Genre::getName)
                .collect(Collectors.joining(", "));

        return "Id: %s, title: %s, author: {%s}, genres: [%s]".formatted(
                id,
                title,
                author,
                genresNames
        );
    }
}