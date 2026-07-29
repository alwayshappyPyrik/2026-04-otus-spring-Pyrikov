package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

@Builder(toBuilder = true)
public record BookUpdateRequestDto(
        Long id,

        @NotBlank(message = "Название книги обязательно")
        @Size(min = 1, max = 255, message = "Название должно быть от 1 до 255 символов")
        String title,

        Long authorId,

        @NotEmpty(message = "Должен быть выбран хотя бы один жанр")
        Set<Long> genreIds
) {}
