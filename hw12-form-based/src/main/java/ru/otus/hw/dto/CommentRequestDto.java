package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record CommentRequestDto(
        Long id,

        BookRequestDto book,

        @NotBlank(message = "Текст комментария обязателен")
        String text
) {}
