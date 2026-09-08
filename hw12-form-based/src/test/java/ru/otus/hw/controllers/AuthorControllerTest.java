package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorRequestDto;
import ru.otus.hw.dto.AuthorResponseDto;
import ru.otus.hw.services.AuthorService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@DisplayName("Контроллер для работы с авторами")
@WebMvcTest(AuthorController.class)
@WithMockUser
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @Test
    @DisplayName("должен возвращать страницу со списком всех авторов")
    void shouldReturnAuthorsListPage() throws Exception {
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

        mockMvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(view().name("authors/list"));
    }

    @Test
    @DisplayName("должен возвращать страницу с деталями автора по ID")
    void shouldReturnAuthorDetailPage() throws Exception {
        Long authorId = 1L;
        AuthorResponseDto author = AuthorResponseDto.builder()
                .id(authorId)
                .fullName("Test Author 1")
                .build();

        when(authorService.findById(any(AuthorRequestDto.class))).thenReturn(Optional.of(author));

        mockMvc.perform(get("/authors/{id}", authorId))
                .andExpect(status().isOk())
                .andExpect(view().name("authors/detail"));
    }
}