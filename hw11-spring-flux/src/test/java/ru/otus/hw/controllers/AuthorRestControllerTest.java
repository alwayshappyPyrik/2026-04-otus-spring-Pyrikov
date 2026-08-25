package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorRequestDto;
import ru.otus.hw.dto.AuthorResponseDto;
import ru.otus.hw.services.AuthorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("REST Контроллер для работы с авторами")
@WebFluxTest(AuthorRestController.class)
class AuthorRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AuthorService authorService;

    @Test
    @DisplayName("должен возвращать список всех авторов")
    void shouldReturnListOfAuthors() {
        AuthorResponseDto author1 = AuthorResponseDto.builder()
                .id(1L)
                .fullName("Test Author 1")
                .build();

        AuthorResponseDto author2 = AuthorResponseDto.builder()
                .id(2L)
                .fullName("Test Author 2")
                .build();

        when(authorService.findAll()).thenReturn(Flux.just(author1, author2));

        webTestClient.get()
                .uri("/api/v1/authors")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].fullName").isEqualTo("Test Author 1")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].fullName").isEqualTo("Test Author 2");
    }

    @Test
    @DisplayName("должен возвращать автора по айди")
    void shouldReturnAuthorById() {
        Long authorId = 1L;
        AuthorResponseDto author = AuthorResponseDto.builder()
                .id(authorId)
                .fullName("Test Author 1")
                .build();

        when(authorService.findById(any(AuthorRequestDto.class))).thenReturn(Mono.just(author));

        webTestClient.get()
                .uri("/api/v1/authors/{id}", authorId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.fullName").isEqualTo("Test Author 1");
    }
}