package ru.otus.hw.dto;

import java.util.Set;

public record BookResponseDto(
        String id,
        String title,
        AuthorResponseDto author,
        Set<GenreResponseDto> genres
) {
    @Override
    public String toString() {
        return "Id: %s, title: %s, author: {%s}, genres: [%s]".formatted(
                id,
                title,
                author,
                genres
        );
    }
}