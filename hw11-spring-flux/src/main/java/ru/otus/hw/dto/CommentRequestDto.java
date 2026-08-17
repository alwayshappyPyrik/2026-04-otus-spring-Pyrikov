package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder(toBuilder = true)
public record CommentRequestDto(
        Long id,

        @NotNull(message = "Книга не может быть null")
        BookRequestDto book,

        @NotBlank(message = "Текст комментария не может быть пустым")
        @Size(max = 1000, message = "Текст комментария не должен превышать 1000 символов")
        String text
) {}
