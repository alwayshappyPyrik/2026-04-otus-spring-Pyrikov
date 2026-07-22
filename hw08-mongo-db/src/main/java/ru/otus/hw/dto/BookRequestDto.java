package ru.otus.hw.dto;

import lombok.Builder;

import java.util.Set;

@Builder(toBuilder = true)
public record BookRequestDto(
        String id,
        String title,
        AuthorRequestDto author,
        Set<String> genreIds
) {}