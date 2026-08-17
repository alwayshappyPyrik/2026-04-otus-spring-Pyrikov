package ru.otus.hw.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookCreateRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.AuthorResponseDto;
import ru.otus.hw.dto.BookUpdateRequestDto;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.services.BookService;

import java.util.Set;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("REST Контроллер для работы с книжками")
@WebFluxTest(BookRestController.class)
class BookRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BookService bookService;

    private BookResponseDto bookResponseDto;
    private BookCreateRequestDto bookCreateRequestDto;
    private BookUpdateRequestDto bookUpdateRequestDto;
    private AuthorResponseDto authorResponseDto;
    private GenreResponseDto genreResponseDto1;
    private GenreResponseDto genreResponseDto2;

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
    }

    @Test
    @DisplayName("должен возвращать список всех книг")
    void shouldReturnBookList() {
        when(bookService.findAll()).thenReturn(Flux.just(bookResponseDto));

        webTestClient.get()
                .uri("/api/v1/books")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].title").isEqualTo("Test Book")
                .jsonPath("$[0].author.id").isEqualTo(1)
                .jsonPath("$[0].author.fullName").isEqualTo("Test Author")
                .jsonPath("$[0].genres").isArray()
                .jsonPath("$[0].genres.length()").isEqualTo(2);

        verify(bookService, times(1)).findAll();
    }

    @Test
    @DisplayName("должен возвращать книгу по id")
    void shouldReturnBookById() {
        Long bookId = 1L;

        when(bookService.findById(any(BookRequestDto.class))).thenReturn(Mono.just(bookResponseDto));

        webTestClient.get()
                .uri("/api/v1/books/{id}", bookId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.title").isEqualTo("Test Book");
    }

    @Test
    @DisplayName("должен создавать книгу и возвращать созданную книгу")
    void shouldCreateBook() {
        when(bookService.insert(any(BookCreateRequestDto.class))).thenReturn(Mono.just(bookResponseDto));

        webTestClient.post()
                .uri("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookCreateRequestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.title").isEqualTo("Test Book")
                .jsonPath("$.author.id").isEqualTo(1)
                .jsonPath("$.author.fullName").isEqualTo("Test Author");

        verify(bookService, times(1)).insert(any(BookCreateRequestDto.class));
    }

    @Test
    @DisplayName("должен обновить книгу и возвращать обновленную книгу")
    void shouldReturnUpdatedBook() {
        Long bookId = 1L;

        when(bookService.update(any(BookUpdateRequestDto.class))).thenReturn(Mono.just(bookResponseDto));

        webTestClient.put()
                .uri("/api/v1/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookUpdateRequestDto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.title").isEqualTo("Test Book")
                .jsonPath("$.author.id").isEqualTo(1)
                .jsonPath("$.author.fullName").isEqualTo("Test Author");

        verify(bookService, times(1)).update(any(BookUpdateRequestDto.class));
    }

    @Test
    @DisplayName("должен удалить книгу")
    void shouldDeleteBook() {
        Long bookId = 1L;
        BookRequestDto requestDto = BookRequestDto.builder().id(bookId).build();

        when(bookService.deleteById(requestDto)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/books/{id}", bookId)
                .exchange()
                .expectStatus().isNoContent();

        verify(bookService, times(1)).deleteById(requestDto);
    }
}