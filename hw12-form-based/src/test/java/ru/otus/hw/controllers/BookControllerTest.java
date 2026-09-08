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
import ru.otus.hw.dto.AuthorResponseDto;
import ru.otus.hw.dto.BookCreateRequestDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.BookUpdateRequestDto;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Контроллер для работы с книжками")
@WebMvcTest(BookController.class)
@WithMockUser
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    private List<AuthorResponseDto> authorList;
    private List<GenreResponseDto> genreList;

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
        authorList = List.of(authorResponseDto);
        genreList = List.of(genreResponseDto1, genreResponseDto2);
    }

    @Test
    @DisplayName("должен возвращать страницу со списком всех книг")
    void shouldReturnBooksListPage() throws Exception {
        when(bookService.findAll()).thenReturn(bookList);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/list"));
    }

    @Test
    @DisplayName("должен возвращать страницу с деталями книги по ID")
    void shouldReturnBookDetailPage() throws Exception {
        Long bookId = 1L;

        when(bookService.findById(any(BookRequestDto.class))).thenReturn(bookResponseDto);

        mockMvc.perform(get("/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(view().name("books/detail"));
    }

    @Test
    @DisplayName("должен показывать форму создания новой книги")
    void shouldShowCreateForm() throws Exception {
        when(authorService.findAll()).thenReturn(authorList);
        when(genreService.findAll()).thenReturn(genreList);

        mockMvc.perform(get("/books/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/new"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"));
    }

    @Test
    @DisplayName("должен создавать новую книгу")
    void shouldCreateBook() throws Exception {
        when(bookService.insert(any(BookCreateRequestDto.class))).thenReturn(bookResponseDto);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Test Book")
                        .param("authorId", "1")
                        .param("genreIds", "1")
                        .param("genreIds", "2"));
    }

    @Test
    @DisplayName("должен показывать форму редактирования книги")
    void shouldShowEditForm() throws Exception {
        Long bookId = 1L;

        when(bookService.findById(any(BookRequestDto.class))).thenReturn(bookResponseDto);
        when(authorService.findAll()).thenReturn(authorList);
        when(genreService.findAll()).thenReturn(genreList);

        mockMvc.perform(get("/books/{id}/edit", bookId))
                .andExpect(status().isOk())
                .andExpect(view().name("books/edit"));
    }

    @Test
    @DisplayName("должен обновлять книгу")
    void shouldUpdateBook() throws Exception {
        Long bookId = 1L;

        when(bookService.update(any(BookUpdateRequestDto.class))).thenReturn(bookResponseDto);

        mockMvc.perform(put("/books/{id}", bookId)
                        .param("title", "Test Book")
                        .param("authorId", "1")
                        .param("genreIds", "1", "2"));
    }

    @Test
    @DisplayName("должен удалять книгу")
    void shouldDeleteBook() throws Exception {
        Long bookId = 1L;

        doNothing().when(bookService).deleteById(any(BookRequestDto.class));

        mockMvc.perform(delete("/books/{id}", bookId));
    }
}