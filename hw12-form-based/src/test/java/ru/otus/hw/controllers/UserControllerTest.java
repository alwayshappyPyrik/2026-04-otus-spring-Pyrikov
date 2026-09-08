package ru.otus.hw.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.UserCreateRequestDto;
import ru.otus.hw.dto.UserRequestDto;
import ru.otus.hw.dto.UserResponseDto;
import ru.otus.hw.dto.UserUpdateRequestDto;
import ru.otus.hw.services.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Контроллер для работы с пользователями")
@WebMvcTest(UserController.class)
@WithMockUser
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private UserResponseDto userResponseDto;
    private UserCreateRequestDto userCreateRequestDto;
    private UserUpdateRequestDto userUpdateRequestDto;
    private List<UserResponseDto> userList;

    @BeforeEach
    void setUp() {
        userResponseDto = UserResponseDto.builder()
                .id(1L)
                .login("testuser")
                .email("test@example.com")
                .enabled(true)
                .build();

        userCreateRequestDto = UserCreateRequestDto.builder()
                .login("testuser")
                .password("password123")
                .email("test@example.com")
                .build();

        userUpdateRequestDto = UserUpdateRequestDto.builder()
                .id(1L)
                .login("testuser")
                .email("test@example.com")
                .enabled(true)
                .build();

        userList = List.of(userResponseDto);
    }

    @Test
    @DisplayName("должен возвращать страницу со списком всех пользователей")
    void shouldReturnUsersListPage() throws Exception {
        when(userService.findAll()).thenReturn(userList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/list"));
    }

    @Test
    @DisplayName("должен возвращать страницу с деталями пользователя по ID")
    void shouldReturnUserDetailPage() throws Exception {
        Long userId = 1L;

        when(userService.findById(any(UserRequestDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("users/detail"));
    }

    @Test
    @DisplayName("должен показывать форму создания нового пользователя")
    void shouldShowCreateForm() throws Exception {
        mockMvc.perform(get("/users/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/new"));
    }

    @Test
    @DisplayName("должен создавать нового пользователя")
    void shouldCreateUser() throws Exception {
        when(userService.insert(any(UserCreateRequestDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("login", "testuser")
                        .param("password", "password123")
                        .param("email", "test@example.com")
                        .param("enabled", "true")
                        .flashAttr("user", userCreateRequestDto));
    }

    @Test
    @DisplayName("должен показывать форму редактирования пользователя")
    void shouldShowEditForm() throws Exception {
        Long userId = 1L;

        when(userService.findById(any(UserRequestDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(get("/users/{id}/edit", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("users/edit"));
    }

    @Test
    @DisplayName("должен обновлять пользователя")
    void shouldUpdateUser() throws Exception {
        Long userId = 1L;

        when(userService.update(any(UserUpdateRequestDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("login", "testuser")
                        .param("email", "test@example.com")
                        .param("enabled", "true"));
    }


    @Test
    @DisplayName("должен удалять пользователя и перенаправлять на список")
    void shouldDeleteUser() throws Exception {
        Long userId = 1L;

        doNothing().when(userService).deleteById(any(UserRequestDto.class));

        mockMvc.perform(delete("/users/{id}", userId));
    }
}