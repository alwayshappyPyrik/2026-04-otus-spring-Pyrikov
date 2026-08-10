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
import ru.otus.hw.dto.BookCreateRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.AuthorResponseDto;
import ru.otus.hw.dto.BookUpdateRequestDto;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.any;
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

@DisplayName("REST Контроллер для работы с книжками")
@WebMvcTest(BookRestController.class)
class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    private BookResponseDto bookResponseDto;
    private BookCreateRequestDto bookCreateRequestDto;
    private BookUpdateRequestDto bookUpdateRequestDto;
    private AuthorResponseDto authorResponseDto;
    private GenreResponseDto genreResponseDto1;
    private GenreResponseDto genreResponseDto2;
    private List<BookResponseDto> bookList;

    @BeforeEach
    void setUp() {
        authorResponseDto = AuthorResponseDto.builder()
                .id(1L)
                .fullName("Test Author")
                .build();

        genreResponseDto1 = GenreResponseDto.builder()
                .id(1L)
                .name("Test Genre 1")
                .build();

        genreResponseDto2 = GenreResponseDto.builder()
                .id(2L)
                .name("Test Genre 2")
                .build();

        bookResponseDto = BookResponseDto.builder()
                .id(1L)
                .title("Test Book")
                .author(authorResponseDto)
                .genres(Set.of(genreResponseDto1, genreResponseDto2))
                .build();

        bookCreateRequestDto = BookCreateRequestDto.builder()
                .title("Test Book")
                .authorId(1L)
                .genreIds(Set.of(1L, 2L))
                .build();

        bookUpdateRequestDto = BookUpdateRequestDto.builder()
                .id(1L)
                .title("Test Book")
                .authorId(1L)
                .genreIds(Set.of(1L, 2L))
                .build();

        bookList = List.of(bookResponseDto);
    }

    @Test
    @DisplayName("должен возвращать список всех книг")
    void shouldReturnBookList() throws Exception {
        when(bookService.findAll()).thenReturn(bookList);

        mockMvc.perform(get("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].author.id").value(1L))
                .andExpect(jsonPath("$[0].author.fullName").value("Test Author"))
                .andExpect(jsonPath("$[0].genres").isArray())
                .andExpect(jsonPath("$[0].genres.length()").value(2));

        verify(bookService, times(1)).findAll();
    }

    @Test
    @DisplayName("должен возвращать книгу по id")
    void shouldReturnBookById() throws Exception {
        when(bookService.findById(any(BookRequestDto.class))).thenReturn(bookResponseDto);

        mockMvc.perform(get("/api/v1/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    @DisplayName("должен создавать книгу и возвращать созданную книгу")
    void shouldCreateBook() throws Exception {
        when(bookService.insert(any(BookCreateRequestDto.class))).thenReturn(bookResponseDto);

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCreateRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author.id").value(1L))
                .andExpect(jsonPath("$.author.fullName").value("Test Author"));

        verify(bookService, times(1)).insert(any(BookCreateRequestDto.class));
    }

    @Test
    @DisplayName("должен обновить книгу и возвращать обновленную книгу")
    void shouldReturnUpdatedBook() throws Exception {
        when(bookService.update(any(BookUpdateRequestDto.class))).thenReturn(bookResponseDto);

        mockMvc.perform(put("/api/v1/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author.id").value(1L))
                .andExpect(jsonPath("$.author.fullName").value("Test Author"));

        verify(bookService, times(1)).update(any(BookUpdateRequestDto.class));
    }

    @Test
    @DisplayName("должен удалить книгу")
    void shouldDeleteBook() throws Exception {
        Long bookId = 1L;
        BookRequestDto requestDto = BookRequestDto.builder().id(bookId).build();

        doNothing().when(bookService).deleteById(requestDto);

        mockMvc.perform(delete("/api/v1/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteById(requestDto);
    }
}