package ru.otus.hw.services;

import ru.otus.hw.dto.UserCreateRequestDto;
import ru.otus.hw.dto.UserRequestDto;
import ru.otus.hw.dto.UserResponseDto;
import ru.otus.hw.dto.UserUpdateRequestDto;

import java.util.List;

public interface UserService {

    List<UserResponseDto> findAll();

    UserResponseDto findById(UserRequestDto request);

    UserResponseDto insert(UserCreateRequestDto createRequest);

    UserResponseDto update(UserUpdateRequestDto updateRequest);

    void deleteById(UserRequestDto request);
    
}
