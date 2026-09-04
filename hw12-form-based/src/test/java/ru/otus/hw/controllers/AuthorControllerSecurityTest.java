package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.security.SecurityConfig;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.CustomUserDetailsServiceImpl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
@Import(SecurityConfig.class)
@DisplayName("Тесты безопасности AuthorController")
public class AuthorControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private CustomUserDetailsServiceImpl customUserDetailsService;

    @DisplayName("GET /authors - должен перенаправлять на страницу логина")
    @Test
    public void shouldRedirectToLoginWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/authors"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @DisplayName("GET /authors/{id} - должен перенаправлять на страницу логина")
    @Test
    public void shouldRedirectToLoginWhenAccessingAuthorById() throws Exception {
        mockMvc.perform(get("/authors/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
