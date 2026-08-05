package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.dto.GenresRequestDto;
import ru.otus.hw.services.GenreService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@DisplayName("REST Контроллер для работы с жанрами")
@WebMvcTest(GenreRestController.class)
class GenreRestControllerTest {

    private static final String BASE_URL = "/api/v1/genres";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    @Test
    @DisplayName("должен возвращать список всех жанров")
    void shouldReturnAllGenres() throws Exception {
        GenreResponseDto genre1 = GenreResponseDto.builder()
                .id(1L)
                .name("Test Genre 1")
                .build();

        GenreResponseDto genre2 = GenreResponseDto.builder()
                .id(2L)
                .name("Test Genre 2")
                .build();

        List<GenreResponseDto> genres = Arrays.asList(genre1, genre2);

        when(genreService.findAll()).thenReturn(genres);

        mockMvc.perform(get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Genre 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Test Genre 2"));
    }

    @Test
    @DisplayName("должен возвращать айди заданных жанров")
    void shouldReturnSpecifiedGenres() throws Exception {

        String ids = "1";

        GenreResponseDto genre = GenreResponseDto.builder()
                .id(1L)
                .name("Test Genre 1")
                .build();

        List<GenreResponseDto> genres = List.of(genre);

        when(genreService.findAllByIds(any(GenresRequestDto.class))).thenReturn(genres);

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("ids", ids)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Genre 1"));
    }
}