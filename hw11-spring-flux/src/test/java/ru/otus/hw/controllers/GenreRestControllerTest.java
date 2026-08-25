package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.dto.GenresRequestDto;
import ru.otus.hw.services.GenreService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("REST Контроллер для работы с жанрами")
@WebFluxTest(GenreRestController.class)
class GenreRestControllerTest {

    private static final String BASE_URL = "/api/v1/genres";

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GenreService genreService;

    @Test
    @DisplayName("должен возвращать список всех жанров")
    void shouldReturnAllGenres() {
        GenreResponseDto genre1 = GenreResponseDto.builder()
                .id(1L)
                .name("Test Genre 1")
                .build();

        GenreResponseDto genre2 = GenreResponseDto.builder()
                .id(2L)
                .name("Test Genre 2")
                .build();

        when(genreService.findAll()).thenReturn(Flux.just(genre1, genre2));

        webTestClient.get()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("Test Genre 1")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].name").isEqualTo("Test Genre 2");
    }

    @Test
    @DisplayName("должен возвращать айди заданных жанров")
    void shouldReturnSpecifiedGenres() {
        String ids = "1";

        GenreResponseDto genre = GenreResponseDto.builder()
                .id(1L)
                .name("Test Genre 1")
                .build();

        when(genreService.findAllByIds(any(GenresRequestDto.class))).thenReturn(Flux.just(genre));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URL + "/search")
                        .queryParam("ids", ids)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("Test Genre 1");
    }
}