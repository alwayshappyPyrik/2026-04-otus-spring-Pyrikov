package ru.otus.hw.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserRequestDto(
        Long id
) {}
