package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ru.otus.hw.security.SecurityConfig;
import ru.otus.hw.services.CustomUserDetailsServiceImpl;
import ru.otus.hw.services.UserService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@DisplayName("Тесты безопасности UserController")
public class UserControllerSecurityTest {

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
}
