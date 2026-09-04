package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.security.SecurityConfig;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.CustomUserDetailsServiceImpl;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@Import(SecurityConfig.class)
@DisplayName("Тесты безопасности CommentController")
public class CommentControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private CommentMapper commentMapper;

    @MockitoBean
    private CustomUserDetailsServiceImpl customUserDetailsService;

    @Test
    @DisplayName("GET /comments/{id} - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenAccessingCommentById() throws Exception {
        mockMvc.perform(get("/comments/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("GET /comments/book/{bookId} - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenAccessingCommentsByBookId() throws Exception {
        mockMvc.perform(get("/comments/book/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("GET /comments/book/{bookId}/new - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenAccessingCreateCommentForm() throws Exception {
        mockMvc.perform(get("/comments/book/1/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("GET /comments/{id}/edit - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenAccessingEditCommentForm() throws Exception {
        mockMvc.perform(get("/comments/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("POST /comments/book/{bookId} - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenCreatingComment() throws Exception {
        mockMvc.perform(post("/comments/book/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("PUT /comments/{id} - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenUpdatingComment() throws Exception {
        mockMvc.perform(put("/comments/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("DELETE /comments/{id} - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenDeletingComment() throws Exception {
        mockMvc.perform(delete("/comments/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
