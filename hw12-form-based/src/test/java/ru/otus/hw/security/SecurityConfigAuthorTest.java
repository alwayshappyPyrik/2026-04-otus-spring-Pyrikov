package ru.otus.hw.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.AuthorController;
import ru.otus.hw.dto.AuthorRequestDto;
import ru.otus.hw.dto.AuthorResponseDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.CustomUserDetailsServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
@Import(SecurityConfig.class)
@DisplayName("Тесты безопасности AuthorController")
public class SecurityConfigAuthorTest {

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

    @Test
    @WithMockUser(username = "user")
    @DisplayName("GET /authors - авторизованный пользователь должен получить доступ к списку авторов")
    public void shouldAllowAccessToAuthorsListWhenAuthenticated() throws Exception {
        AuthorResponseDto author1 = new AuthorResponseDto(1L, "Author 1");
        AuthorResponseDto author2 = new AuthorResponseDto(2L, "Author 2");
        List<AuthorResponseDto> authors = List.of(author1, author2);

        when(authorService.findAll()).thenReturn(authors);

        mockMvc.perform(get("/authors"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("GET /authors/{id} - авторизованный пользователь должен получить доступ к автору по ID")
    public void shouldAllowAccessToAuthorByIdWhenAuthenticated() throws Exception {
        AuthorResponseDto author = new AuthorResponseDto(1L, "Test Author");

        when(authorService.findById(any(AuthorRequestDto.class))).thenReturn(Optional.of(author));

        mockMvc.perform(get("/authors/1"))
                .andExpect(status().isOk());
    }
}
