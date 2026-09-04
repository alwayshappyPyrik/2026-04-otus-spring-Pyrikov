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
import ru.otus.hw.services.GenreService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreController.class)
@Import(SecurityConfig.class)
@DisplayName("Тесты безопасности GenreController")
public class GenreControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private CustomUserDetailsServiceImpl customUserDetailsService;

    @Test
    @DisplayName("GET /genres - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenAccessingGenresList() throws Exception {
        mockMvc.perform(get("/genres"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("GET /genres/search - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenSearchingGenres() throws Exception {
        mockMvc.perform(get("/genres/search")
                        .param("ids", "1,2,3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("GET /genres/search с пустыми ids - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenSearchingGenresWithEmptyIds() throws Exception {
        mockMvc.perform(get("/genres/search"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
