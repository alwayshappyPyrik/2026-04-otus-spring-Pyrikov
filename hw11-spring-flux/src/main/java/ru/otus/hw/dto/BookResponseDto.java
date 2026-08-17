package ru.otus.hw.dto;

import lombok.Builder;

import java.util.Set;
import java.util.stream.Collectors;

@Builder(toBuilder = true)
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