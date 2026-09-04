package ru.otus.hw.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserResponseDto(
        Long id,
        String login,
        String email,
        Boolean enabled
) {}
