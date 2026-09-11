package ru.otus.hw.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ru.otus.hw.controllers.UserController;
import ru.otus.hw.dto.UserCreateRequestDto;
import ru.otus.hw.dto.UserRequestDto;
import ru.otus.hw.dto.UserResponseDto;
import ru.otus.hw.dto.UserUpdateRequestDto;
import ru.otus.hw.services.CustomUserDetailsServiceImpl;
import ru.otus.hw.services.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@DisplayName("Тесты безопасности UserController")
public class SecurityConfigUserTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsServiceImpl customUserDetailsService;

    @Test
    @DisplayName("GET /users - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenAccessingUsersList() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("GET /users/{id} - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenAccessingUserById() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("GET /users/new - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenAccessingCreateUserForm() throws Exception {
        mockMvc.perform(get("/users/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("GET /users/{id}/edit - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenAccessingEditUserForm() throws Exception {
        mockMvc.perform(get("/users/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("POST /users - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenCreatingUser() throws Exception {
        mockMvc.perform(post("/users")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("PUT /users/{id} - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenUpdatingUser() throws Exception {
        mockMvc.perform(put("/users/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("DELETE /users/{id} - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenDeletingUser() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("GET /users - авторизованный пользователь должен получить доступ к списку пользователей")
    public void shouldAllowAccessToUsersListWhenAuthenticated() throws Exception {
        UserResponseDto user1 = new UserResponseDto(1L, "User 1", "user1@mail.com", true);
        UserResponseDto user2 = new UserResponseDto(2L, "User 2", "user2@mail.com", true);
        List<UserResponseDto> users = List.of(user1, user2);

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("GET /users/{id} - авторизованный пользователь должен получить доступ к пользователю по ID")
    public void shouldAllowAccessToUserByIdWhenAuthenticated() throws Exception {
        UserResponseDto user = new UserResponseDto(1L, "Test User", "test@mail.com", true);

        when(userService.findById(any(UserRequestDto.class))).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("GET /users/new - авторизованный пользователь должен получить доступ к форме создания пользователя")
    public void shouldAllowAccessToCreateUserFormWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/users/new"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("POST /users - авторизованный пользователь должен иметь возможность создать пользователя")
    public void shouldAllowCreatingUserWhenAuthenticated() throws Exception {
        UserResponseDto user = new UserResponseDto(1L, "New User", "new@mail.com", true);

        when(userService.insert(any(UserCreateRequestDto.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .param("username", "New User")
                        .param("email", "new@mail.com"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("GET /users/{id}/edit - авторизованный пользователь должен получить доступ к форме редактирования пользователя")
    public void shouldAllowAccessToEditUserFormWhenAuthenticated() throws Exception {
        UserResponseDto user = new UserResponseDto(1L, "Test User", "test@mail.com", true);

        when(userService.findById(any(UserRequestDto.class))).thenReturn(user);

        mockMvc.perform(get("/users/1/edit"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("PUT /users/{id} - авторизованный пользователь должен иметь возможность обновить пользователя")
    public void shouldAllowUpdatingUserWhenAuthenticated() throws Exception {
        UserResponseDto user = new UserResponseDto(1L, "Updated User", "updated@mail.com", true);

        when(userService.update(any(UserUpdateRequestDto.class))).thenReturn(user);

        mockMvc.perform(put("/users/1")
                        .with(csrf())
                        .param("username", "Updated User")
                        .param("email", "updated@mail.com"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("DELETE /users/{id} - авторизованный пользователь должен иметь возможность удалить пользователя")
    public void shouldAllowDeletingUserWhenAuthenticated() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }
}
