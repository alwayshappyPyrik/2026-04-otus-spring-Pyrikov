package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.UserCreateRequestDto;
import ru.otus.hw.dto.UserRequestDto;
import ru.otus.hw.dto.UserResponseDto;
import ru.otus.hw.dto.UserUpdateRequestDto;
import ru.otus.hw.exceptions.DuplicateResourceException;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.UserMapper;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findById(UserRequestDto request) {
        User user = userRepository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.id()));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto insert(UserCreateRequestDto createRequest) {
        if (userRepository.existsByLogin(createRequest.login())) {
            throw new DuplicateResourceException("User with login '" + createRequest.login() + "' already exists");
        }

        if (userRepository.existsByEmail(createRequest.email())) {
            throw new DuplicateResourceException("User with email '" + createRequest.email() + "' already exists");
        }

        User user = User.builder()
                .login(createRequest.login())
                .password(passwordEncoder.encode(createRequest.password()))
                .email(createRequest.email())
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserResponseDto update(UserUpdateRequestDto updateRequest) {
        User existingUser = userRepository.findById(updateRequest.id())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + updateRequest.id()));

        if (!existingUser.getLogin().equals(updateRequest.login()) &&
                userRepository.existsByLogin(updateRequest.login())) {
            throw new DuplicateResourceException("User with login '" + updateRequest.login() + "' already exists");
        }

        if (!existingUser.getEmail().equals(updateRequest.email()) &&
                userRepository.existsByEmail(updateRequest.email())) {
            throw new DuplicateResourceException("User with email '" + updateRequest.email() + "' already exists");
        }

        existingUser.setLogin(updateRequest.login());
        existingUser.setEmail(updateRequest.email());
        existingUser.setEnabled(updateRequest.enabled());

        if (updateRequest.password() != null && !updateRequest.password().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updateRequest.password()));
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteById(UserRequestDto request) {
        if (!userRepository.existsById(request.id())) {
            throw new EntityNotFoundException("User not found with id: " + request.id());
        }
        userRepository.deleteById(request.id());
    }
}

