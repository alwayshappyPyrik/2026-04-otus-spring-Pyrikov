package ru.otus.hw.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.dto.GenresRequestDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@DisplayName("Контроллер для работы с жанрами")
@WebMvcTest(GenreController.class)
@WithMockUser
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    private GenreResponseDto genreResponseDto1;
    private GenreResponseDto genreResponseDto2;
    private List<GenreResponseDto> genreList;

    @BeforeEach
    void setUp() {
        genreResponseDto1 = GenreResponseDto.builder()
                .id(1L)
                .name("Test Genre 1")
                .build();

        genreResponseDto2 = GenreResponseDto.builder()
                .id(2L)
                .name("Test Genre 2")
                .build();

        genreList = List.of(genreResponseDto1, genreResponseDto2);
    }

    @Test
    @DisplayName("должен возвращать страницу со списком всех жанров")
    void shouldReturnGenresListPage() throws Exception {
        when(genreService.findAll()).thenReturn(genreList);

        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(view().name("genres/list"));

    }

    @Test
    @DisplayName("должен возвращать страницу со списком жанров по ID")
    void shouldReturnGenresListByIds() throws Exception {
        String ids = "1,2";

        when(genreService.findAllByIds(any(GenresRequestDto.class))).thenReturn(genreList);

        mockMvc.perform(get("/genres/search")
                        .param("ids", ids))
                .andExpect(status().isOk())
                .andExpect(view().name("genres/list"));
    }
}