package ru.otus.hw.mapper;

import org.mapstruct.Mapper;
import ru.otus.hw.dto.UserResponseDto;
import ru.otus.hw.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(User user);
}
