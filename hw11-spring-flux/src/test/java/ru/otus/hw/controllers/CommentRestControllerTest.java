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
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.CommentRequestDto;
import ru.otus.hw.dto.CommentResponseDto;
import ru.otus.hw.services.CommentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("REST Controller для работы с комментариями")
@WebFluxTest(CommentRestController.class)
class CommentRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CommentService commentService;

    private static final String BASE_URL = "/api/v1/comments";
    private static final Long BOOK_ID = 1L;
    private static final Long COMMENT_ID = 1L;

    private CommentResponseDto commentResponseDto;
    private CommentRequestDto commentRequestDto;
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
    }

    @Test
    @DisplayName("должен возвращать комментарий по id")
    void shouldReturnComment() {
        when(commentService.findById(any(CommentRequestDto.class))).thenReturn(Mono.just(commentResponseDto));

        webTestClient.get()
                .uri(BASE_URL + "/{id}", COMMENT_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(COMMENT_ID)
                .jsonPath("$.text").isEqualTo("Test comment")
                .jsonPath("$.book.id").isEqualTo(BOOK_ID);

        verify(commentService, times(1)).findById(any(CommentRequestDto.class));
    }

    @Test
    @DisplayName("должен возвращать комментарии по id книги")
    void shouldReturnComments() {
        when(commentService.findAllByBookId(any(CommentRequestDto.class))).thenReturn(Flux.just(commentResponseDto));

        webTestClient.get()
                .uri(BASE_URL + "/book/{bookId}", BOOK_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(COMMENT_ID)
                .jsonPath("$[0].text").isEqualTo("Test comment")
                .jsonPath("$[0].book.id").isEqualTo(BOOK_ID);

        verify(commentService, times(1)).findAllByBookId(any(CommentRequestDto.class));
    }

    @Test
    @DisplayName("должен создавать комментарий и возвращать статус CREATED")
    void shouldCreateComment() {
        when(commentService.insert(any(CommentRequestDto.class))).thenReturn(Mono.just(commentResponseDto));

        webTestClient.post()
                .uri(BASE_URL + "/book/{bookId}", BOOK_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(commentRequestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(COMMENT_ID)
                .jsonPath("$.text").isEqualTo("Test comment")
                .jsonPath("$.book.id").isEqualTo(BOOK_ID);

        verify(commentService, times(1)).insert(any(CommentRequestDto.class));
    }

    @Test
    @DisplayName("должен обновлять комментарий и возвращать обновленный комментарий")
    void shouldUpdateComment() {
        when(commentService.update(any(CommentRequestDto.class))).thenReturn(Mono.just(commentResponseDto));

        webTestClient.put()
                .uri(BASE_URL + "/{id}", COMMENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(commentRequestDto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(COMMENT_ID)
                .jsonPath("$.text").isEqualTo("Test comment")
                .jsonPath("$.book.id").isEqualTo(BOOK_ID);

        verify(commentService, times(1)).update(any(CommentRequestDto.class));
    }

    @Test
    @DisplayName("должен удалять комментарий и возвращать статус NO_CONTENT")
    void shouldDeleteComment() {
        when(commentService.deleteById(any(CommentRequestDto.class))).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(BASE_URL + "/{id}", COMMENT_ID)
                .exchange()
                .expectStatus().isNoContent();

        verify(commentService, times(1)).deleteById(any(CommentRequestDto.class));
    }
}
