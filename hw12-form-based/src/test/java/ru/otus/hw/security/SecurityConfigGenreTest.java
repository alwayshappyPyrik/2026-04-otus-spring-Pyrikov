package ru.otus.hw.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ru.otus.hw.controllers.GenreController;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.services.CustomUserDetailsServiceImpl;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreController.class)
@Import(SecurityConfig.class)
@DisplayName("Тесты безопасности GenreController")
public class SecurityConfigGenreTest {

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
    @WithMockUser(username = "global_admin")
    @DisplayName("GET /genres - авторизованный пользователь должен получить доступ к списку жанров")
    public void shouldAllowAccessToGenresListWhenAuthenticated() throws Exception {
        GenreResponseDto genre1 = new GenreResponseDto(1L, "Genre 1");
        GenreResponseDto genre2 = new GenreResponseDto(2L, "Genre 2");
        List<GenreResponseDto> genres = List.of(genre1, genre2);

        when(genreService.findAll()).thenReturn(genres);

        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("GET /genres/search - авторизованный пользователь должен получить доступ к поиску жанров по ID")
    public void shouldAllowAccessToSearchGenresWhenAuthenticated() throws Exception {
        GenreResponseDto genre1 = new GenreResponseDto(1L, "Genre 1");
        GenreResponseDto genre2 = new GenreResponseDto(2L, "Genre 2");
        GenreResponseDto genre3 = new GenreResponseDto(3L, "Genre 3");
        List<GenreResponseDto> genres = List.of(genre1, genre2, genre3);

        when(genreService.findAllByIds(any())).thenReturn(genres);

        mockMvc.perform(get("/genres/search")
                        .param("ids", "1,2,3"))
                .andExpect(status().isOk());
    }
}
