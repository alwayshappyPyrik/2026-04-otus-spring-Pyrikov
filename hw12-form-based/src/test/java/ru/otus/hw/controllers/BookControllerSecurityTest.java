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
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CustomUserDetailsServiceImpl;
import ru.otus.hw.services.GenreService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import(SecurityConfig.class)
@DisplayName("Тесты безопасности BookController")
public class BookControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private CustomUserDetailsServiceImpl customUserDetailsService;

    @Test
    @DisplayName("GET /books - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenAccessingBooksList() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("GET /books/{id} - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenAccessingBookById() throws Exception {
        mockMvc.perform(get("/books/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("GET /books/new - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenAccessingCreateBookForm() throws Exception {
        mockMvc.perform(get("/books/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("POST /books - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenCreatingBook() throws Exception {
        mockMvc.perform(post("/books")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("GET /books/{id}/edit - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenAccessingEditBookForm() throws Exception {
        mockMvc.perform(get("/books/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("PUT /books/{id} - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenUpdatingBook() throws Exception {
        mockMvc.perform(put("/books/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("DELETE /books/{id} - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenDeletingBook() throws Exception {
        mockMvc.perform(delete("/books/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}