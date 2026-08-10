package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorRequestDto;
import ru.otus.hw.dto.AuthorResponseDto;
import ru.otus.hw.services.AuthorService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@DisplayName("REST Контроллер для работы с авторами")
@WebMvcTest(AuthorRestController.class)
class AuthorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @Test
    @DisplayName("должен возвращать список всех авторов")
    void shouldReturnListOfAuthors() throws Exception {
        AuthorResponseDto author1 = AuthorResponseDto.builder()
                .id(1L)
                .fullName("Test Author 1")
                .build();

        AuthorResponseDto author2 = AuthorResponseDto.builder()
                .id(2L)
                .fullName("Test Author 2")
                .build();

        List<AuthorResponseDto> authors = Arrays.asList(author1, author2);

        when(authorService.findAll()).thenReturn(authors);

        mockMvc.perform(get("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].fullName").value("Test Author 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].fullName").value("Test Author 2"));
    }

    @Test
    @DisplayName("должен возвращать автора по айди")
    void shouldReturnAuthorById() throws Exception {
        Long authorId = 1L;
        AuthorResponseDto author = AuthorResponseDto.builder()
                .id(authorId)
                .fullName("Test Author 1")
                .build();

        when(authorService.findById(any(AuthorRequestDto.class))).thenReturn(author);

        mockMvc.perform(get("/api/v1/authors/{id}", authorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(authorId))
                .andExpect(jsonPath("$.fullName").value("Test Author 1"));
    }
}