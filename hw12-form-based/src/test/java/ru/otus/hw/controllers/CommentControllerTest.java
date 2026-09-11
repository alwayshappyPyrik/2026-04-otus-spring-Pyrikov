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
import ru.otus.hw.dto.*;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Контроллер для работы с комментариями")
@WebMvcTest(CommentController.class)
@WithMockUser
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private CommentMapper commentMapper;

    private CommentResponseDto commentResponseDto;
    private CommentRequestDto commentRequestDto;
    private BookResponseDto bookResponseDto;
    private BookRequestDto bookRequestDto;
    private List<CommentResponseDto> commentList;

    @BeforeEach
    void setUp() {
        bookRequestDto = BookRequestDto.builder()
                .id(1L)
                .build();

        bookResponseDto = BookResponseDto.builder()
                .id(1L)
                .title("Test Book")
                .build();

        commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .text("Test Comment")
                .book(bookResponseDto)
                .build();

        commentRequestDto = CommentRequestDto.builder()
                .id(1L)
                .text("Test Comment")
                .book(bookRequestDto)
                .build();

        commentList = List.of(commentResponseDto);
    }

    @Test
    @DisplayName("должен возвращать страницу с деталями комментария по ID")
    void shouldReturnCommentDetailPage() throws Exception {
        Long commentId = 1L;

        BookResponseDto book = BookResponseDto.builder()
                .id(1L)
                .title("Test Book")
                .build();

        CommentResponseDto comment = CommentResponseDto.builder()
                .id(commentId)
                .text("Test Comment")
                .book(book)
                .build();

        when(commentService.findById(any(CommentRequestDto.class))).thenReturn(comment);

        mockMvc.perform(get("/comments/{id}", commentId))
                .andExpect(status().isOk())
                .andExpect(view().name("comments/detail"));
    }

    @Test
    @DisplayName("должен возвращать страницу со списком комментариев по ID книги")
    void shouldReturnCommentsListByBookId() throws Exception {
        Long bookId = 1L;

        when(bookService.findById(any(BookRequestDto.class))).thenReturn(bookResponseDto);
        when(commentService.findAllByBookId(any(CommentRequestDto.class))).thenReturn(commentList);

        mockMvc.perform(get("/comments/book/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(view().name("comments/list"));
    }

    @Test
    @DisplayName("должен показывать форму создания нового комментария")
    void shouldShowCreateForm() throws Exception {
        Long bookId = 1L;

        when(bookService.findById(any(BookRequestDto.class))).thenReturn(bookResponseDto);

        mockMvc.perform(get("/comments/book/{bookId}/new", bookId))
                .andExpect(status().isOk())
                .andExpect(view().name("comments/create"));
    }

    @Test
    @DisplayName("должен создавать новый комментарий")
    void shouldCreateComment() throws Exception {
        Long bookId = 1L;

        when(commentService.insert(any(CommentRequestDto.class))).thenReturn(commentResponseDto);

        mockMvc.perform(post("/comments/book/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("text", "Test Comment")
                        .flashAttr("comment", commentRequestDto));
    }

    @Test
    @DisplayName("должен показывать форму редактирования комментария")
    void shouldShowEditForm() throws Exception {
        Long commentId = 1L;

        when(commentService.findById(any(CommentRequestDto.class))).thenReturn(commentResponseDto);
        when(commentMapper.toRequestDto(any(CommentResponseDto.class))).thenReturn(commentRequestDto);

        mockMvc.perform(get("/comments/{id}/edit", commentId))
                .andExpect(status().isOk())
                .andExpect(view().name("comments/edit"))
                .andExpect(model().attributeExists("comment"))
                .andExpect(model().attributeExists("bookId"));
    }

    @Test
    @DisplayName("должен обновлять комментарий и перенаправлять на список комментариев книги")
    void shouldUpdateComment() throws Exception {
        Long commentId = 1L;

        when(commentService.update(any(CommentRequestDto.class))).thenReturn(commentResponseDto);

        mockMvc.perform(put("/comments/{id}", commentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("text", "Updated Comment"));
    }

    @Test
    @DisplayName("должен удалять комментарий и перенаправлять на список комментариев книги")
    void shouldDeleteComment() throws Exception {
        Long commentId = 1L;

        when(commentService.findById(any(CommentRequestDto.class))).thenReturn(commentResponseDto);

        mockMvc.perform(delete("/comments/{id}", commentId));
    }
}