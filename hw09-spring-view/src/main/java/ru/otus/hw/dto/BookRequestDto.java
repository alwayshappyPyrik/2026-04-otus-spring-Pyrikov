package ru.otus.hw.dto;

import lombok.Builder;

import java.util.Set;

@Builder(toBuilder = true)
public record BookRequestDto(
        Long id,
        String title,
        Long authorId,
        Set<Long> genreIds
) {}
