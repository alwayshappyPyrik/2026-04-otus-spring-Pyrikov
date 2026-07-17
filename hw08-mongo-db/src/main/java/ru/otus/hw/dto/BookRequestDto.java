package ru.otus.hw.dto;

import lombok.Builder;
import ru.otus.hw.models.Genre;

import java.util.Set;

@Builder(toBuilder = true)
public record BookRequestDto(
        String id,
        String title,
        AuthorRequestDto author,
        Set<Genre> genres
) {}