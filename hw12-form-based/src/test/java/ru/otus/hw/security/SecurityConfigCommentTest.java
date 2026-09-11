package ru.otus.hw.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ru.otus.hw.controllers.CommentController;
import ru.otus.hw.dto.AuthorResponseDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.CommentRequestDto;
import ru.otus.hw.dto.CommentResponseDto;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.CustomUserDetailsServiceImpl;

import java.util.List;
import java.util.Set;

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

@WebMvcTest(CommentController.class)
@Import(SecurityConfig.class)
@DisplayName("Тесты безопасности CommentController")
public class SecurityConfigCommentTest {

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

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("GET /comments/{id} - авторизованный пользователь должен получить доступ к комментарию по ID")
    public void shouldAllowAccessToCommentByIdWhenAuthenticated() throws Exception {
        BookResponseDto book = BookResponseDto.builder()
                .id(1L)
                .title("Test Book")
                .author(new AuthorResponseDto(1L, "Test Author"))
                .genres(Set.of(new GenreResponseDto(1L, "Test Genre")))
                .build();

        CommentResponseDto comment = CommentResponseDto.builder()
                .id(1L)
                .text("Test Comment")
                .book(book)
                .build();

        when(commentService.findById(any(CommentRequestDto.class))).thenReturn(comment);

        mockMvc.perform(get("/comments/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("GET /comments/book/{bookId} - авторизованный пользователь должен получить доступ к комментариям по ID книги")
    public void shouldAllowAccessToCommentsByBookIdWhenAuthenticated() throws Exception {
        BookResponseDto book = BookResponseDto.builder()
                .id(1L)
                .title("Test Book")
                .author(new AuthorResponseDto(1L, "Test Author"))
                .genres(Set.of(new GenreResponseDto(1L, "Test Genre")))
                .build();

        CommentResponseDto comment1 = CommentResponseDto.builder()
                .id(1L)
                .text("Comment 1")
                .book(book)
                .build();

        CommentResponseDto comment2 = CommentResponseDto.builder()
                .id(2L)
                .text("Comment 2")
                .book(book)
                .build();

        List<CommentResponseDto> comments = List.of(comment1, comment2);

        when(commentService.findAllByBookId(any(CommentRequestDto.class))).thenReturn(comments);
        when(bookService.findById(any(BookRequestDto.class))).thenReturn(book);

        mockMvc.perform(get("/comments/book/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("GET /comments/book/{bookId}/new - авторизованный пользователь должен получить доступ к форме создания комментария")
    public void shouldAllowAccessToCreateCommentFormWhenAuthenticated() throws Exception {
        BookResponseDto book = BookResponseDto.builder()
                .id(1L)
                .title("Test Book")
                .author(new AuthorResponseDto(1L, "Test Author"))
                .genres(Set.of(new GenreResponseDto(1L, "Test Genre")))
                .build();

        when(bookService.findById(any(BookRequestDto.class))).thenReturn(book);

        mockMvc.perform(get("/comments/book/1/new"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("GET /comments/{id}/edit - авторизованный пользователь должен получить доступ к форме редактирования комментария")
    public void shouldAllowAccessToEditCommentFormWhenAuthenticated() throws Exception {
        BookResponseDto book = BookResponseDto.builder()
                .id(1L)
                .title("Test Book")
                .author(new AuthorResponseDto(1L, "Test Author"))
                .genres(Set.of(new GenreResponseDto(1L, "Test Genre")))
                .build();

        CommentResponseDto comment = CommentResponseDto.builder()
                .id(1L)
                .text("Test Comment")
                .book(book)
                .build();

        when(commentService.findById(any(CommentRequestDto.class))).thenReturn(comment);

        mockMvc.perform(get("/comments/1/edit"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("POST /comments/book/{bookId} - авторизованный пользователь должен иметь возможность создать комментарий")
    public void shouldAllowCreatingCommentWhenAuthenticated() throws Exception {
        BookResponseDto book = BookResponseDto.builder()
                .id(1L)
                .title("New Book")
                .author(new AuthorResponseDto(1L, "Test Author"))
                .genres(Set.of(new GenreResponseDto(1L, "Test Genre")))
                .build();

        CommentResponseDto comment = CommentResponseDto.builder()
                .id(1L)
                .text("New Comment")
                .book(book)
                .build();

        when(commentService.insert(any(CommentRequestDto.class))).thenReturn(comment);

        mockMvc.perform(post("/comments/book/1")
                        .with(csrf())
                        .param("text", "New Comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/comments/book/1"));
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("PUT /comments/{id} - авторизованный пользователь должен иметь возможность обновить комментарий")
    public void shouldAllowUpdatingCommentWhenAuthenticated() throws Exception {
        BookResponseDto book = BookResponseDto.builder()
                .id(1L)
                .title("Updated Book")
                .author(new AuthorResponseDto(1L, "Test Author"))
                .genres(Set.of(new GenreResponseDto(1L, "Test Genre")))
                .build();

        CommentResponseDto comment = CommentResponseDto.builder()
                .id(1L)
                .text("Updated Comment")
                .book(book)
                .build();

        when(commentService.update(any(CommentRequestDto.class))).thenReturn(comment);

        mockMvc.perform(put("/comments/1")
                        .with(csrf())
                        .param("text", "Updated Comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/comments/book/1"));
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("DELETE /comments/{id} - авторизованный пользователь должен иметь возможность удалить комментарий")
    public void shouldAllowDeletingCommentWhenAuthenticated() throws Exception {
        BookResponseDto book = BookResponseDto.builder()
                .id(1L)
                .title("Test Book")
                .author(new AuthorResponseDto(1L, "Test Author"))
                .genres(Set.of(new GenreResponseDto(1L, "Test Genre")))
                .build();

        CommentResponseDto comment = CommentResponseDto.builder()
                .id(1L)
                .text("Test Comment")
                .book(book)
                .build();

        when(commentService.findById(any(CommentRequestDto.class))).thenReturn(comment);

        mockMvc.perform(delete("/comments/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/comments/book/1"));
    }
}
