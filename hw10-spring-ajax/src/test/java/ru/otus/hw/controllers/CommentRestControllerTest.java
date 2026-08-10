package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.CommentRequestDto;
import ru.otus.hw.dto.CommentResponseDto;
import ru.otus.hw.services.CommentService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("REST Controller для работы с комментариями")
@WebMvcTest(CommentRestController.class)
class CommentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    private static final String BASE_URL = "/api/v1/comments";
    private static final Long BOOK_ID = 1L;
    private static final Long COMMENT_ID = 1L;

    private CommentResponseDto commentResponseDto;
    private CommentRequestDto commentRequestDto;
    private List<CommentResponseDto> commentList;
    private BookResponseDto bookResponseDto;

    @BeforeEach
    void setUp() {
        bookResponseDto = BookResponseDto.builder()
                .id(BOOK_ID)
                .title("Test Book")
                .build();

        commentResponseDto = CommentResponseDto.builder()
                .id(COMMENT_ID)
                .text("Test comment")
                .book(bookResponseDto)
                .build();

        BookRequestDto bookRequest = BookRequestDto.builder()
                .id(BOOK_ID)
                .build();

        commentRequestDto = CommentRequestDto.builder()
                .text("Test comment")
                .book(bookRequest)
                .build();

        commentList = Arrays.asList(commentResponseDto);
    }

    @Test
    @DisplayName("должен возвращать комментарий по id")
    void shouldReturnComment() throws Exception {
        when(commentService.findById(any(CommentRequestDto.class))).thenReturn(commentResponseDto);

        mockMvc.perform(get(BASE_URL + "/{id}", COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(COMMENT_ID))
                .andExpect(jsonPath("$.text").value("Test comment"))
                .andExpect(jsonPath("$.book.id").value(BOOK_ID));

        verify(commentService, times(1)).findById(any(CommentRequestDto.class));
    }

    @Test
    @DisplayName("должен возвращать комментарии по id книги")
    void shouldReturnComments() throws Exception {
        when(commentService.findAllByBookId(any(CommentRequestDto.class))).thenReturn(commentList);

        mockMvc.perform(get(BASE_URL + "/book/{bookId}", BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(COMMENT_ID))
                .andExpect(jsonPath("$[0].text").value("Test comment"))
                .andExpect(jsonPath("$[0].book.id").value(BOOK_ID));

        verify(commentService, times(1)).findAllByBookId(any(CommentRequestDto.class));
    }

    @Test
    @DisplayName("должен создавать комментарий и возвращать статус CREATED")
    void shouldCreateComment() throws Exception {
        when(commentService.insert(any(CommentRequestDto.class))).thenReturn(commentResponseDto);

        mockMvc.perform(post(BASE_URL + "/book/{bookId}", BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(COMMENT_ID))
                .andExpect(jsonPath("$.text").value("Test comment"))
                .andExpect(jsonPath("$.book.id").value(BOOK_ID));

        verify(commentService, times(1)).insert(any(CommentRequestDto.class));
    }

    @Test
    @DisplayName("должен обновлять комментарий и возвращать обновленный комментарий")
    void shouldUpdateComment() throws Exception {
        when(commentService.update(any(CommentRequestDto.class))).thenReturn(commentResponseDto);

        mockMvc.perform(put(BASE_URL + "/{id}", COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(COMMENT_ID))
                .andExpect(jsonPath("$.text").value("Test comment"))
                .andExpect(jsonPath("$.book.id").value(BOOK_ID));

        verify(commentService, times(1)).update(any(CommentRequestDto.class));
    }

    @Test
    @DisplayName("должен удалять комментарий и возвращать статус NO_CONTENT")
    void shouldDeleteComment() throws Exception {
        doNothing().when(commentService).deleteById(any(CommentRequestDto.class));

        mockMvc.perform(delete(BASE_URL + "/{id}", COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteById(any(CommentRequestDto.class));
    }
}