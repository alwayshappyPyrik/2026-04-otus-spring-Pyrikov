package ru.otus.hw.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record AuthorRequestDto(
        String id,
        String fullName
) {}
