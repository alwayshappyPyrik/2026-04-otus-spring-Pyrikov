package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.CommentRequestDto;
import ru.otus.hw.dto.CommentResponseDto;
import ru.otus.hw.services.CommentService;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;

    @GetMapping("/{id}")
    public Mono<CommentResponseDto> findCommentById(@PathVariable Long id) {
        CommentRequestDto request = CommentRequestDto.builder()
                .id(id)
                .build();

        return commentService.findById(request);
    }

    @GetMapping("/book/{bookId}")
    public Flux<CommentResponseDto> findCommentsByBookId(@PathVariable Long bookId) {
        CommentRequestDto commentRequest = CommentRequestDto.builder()
                .book(BookRequestDto.builder().id(bookId).build())
                .build();

        return commentService.findAllByBookId(commentRequest);
    }

    @PostMapping("/book/{bookId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CommentResponseDto> createComment(
            @PathVariable Long bookId,
            @Valid @RequestBody Mono<CommentRequestDto> commentRequestDtoMono) {

        return commentRequestDtoMono
                .map(request -> CommentRequestDto.builder()
                        .text(request.text())
                        .book(BookRequestDto.builder().id(bookId).build())
                        .build())
                .flatMap(commentService::insert);
    }

    @PutMapping("/{id}")
    public Mono<CommentResponseDto> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody Mono<CommentRequestDto> commentRequestDtoMono) {

        return commentRequestDtoMono
                .map(request -> CommentRequestDto.builder()
                        .id(id)
                        .text(request.text())
                        .book(request.book())
                        .build())
                .flatMap(commentService::update);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteComment(@PathVariable Long id) {
        CommentRequestDto request = CommentRequestDto.builder()
                .id(id)
                .build();

        return commentService.deleteById(request);
    }
}