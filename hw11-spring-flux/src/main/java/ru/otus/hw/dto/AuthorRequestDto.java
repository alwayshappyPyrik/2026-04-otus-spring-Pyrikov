package ru.otus.hw.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record AuthorRequestDto(
        Long id,
        String fullName
) {}
