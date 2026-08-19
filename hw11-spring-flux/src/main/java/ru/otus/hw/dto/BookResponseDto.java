package ru.otus.hw.dto;

import lombok.Builder;
import ru.otus.hw.models.Book;

import java.util.Set;
import java.util.stream.Collectors;

@Builder(toBuilder = true)
public record BookResponseDto(
        Long id,
        String title,
        AuthorResponseDto author,
        Set<GenreResponseDto> genres
) {
    public static BookResponseDto fromBook(Book book) {
        return new BookResponseDto(
                book.getId(),
                book.getTitle(),
                new AuthorResponseDto(
                        book.getAuthorId(),
                        book.getAuthorFullName()
                ),
                book.getGenres().stream()
                        .map(g -> new GenreResponseDto(g.getId(), g.getName()))
                        .collect(Collectors.toSet())
        );
    }
}
