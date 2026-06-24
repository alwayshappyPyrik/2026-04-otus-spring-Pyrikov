package ru.otus.hw.dto;

import java.util.List;
import java.util.stream.Collectors;

public record BookDto(Long id, String title, AuthorDto author, List<GenreDto> genres) {
    @Override
    public String toString() {
        var genresString = genres.stream()
                .map(GenreDto::toString)
                .collect(Collectors.joining(","));

        return "Id: %d, title: %s, author: {%s}, genres: [%s]".formatted(
                id,
                title,
                author,
                genresString
        );
    }
}