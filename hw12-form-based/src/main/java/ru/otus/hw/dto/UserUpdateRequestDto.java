package ru.otus.hw.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder(toBuilder = true)
public record UserUpdateRequestDto(
        @NotNull
        Long id,

        @NotBlank(message = "Логин не должен быть пустым")
        String login,

        @NotBlank(message = "Пароль не должен быть пустым")
        @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
        String password,

        @NotBlank(message = "Емайл не должен быть пустым")
        @Email(message = "Емайл должен быть валидным")
        @Size(max = 100, message = "Емайл должен содержать не более 100 символов")
        String email,

        Boolean enabled
) {}
