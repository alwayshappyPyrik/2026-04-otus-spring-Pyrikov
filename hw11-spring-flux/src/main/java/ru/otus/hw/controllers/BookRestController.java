package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookCreateRequestDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.BookUpdateRequestDto;
import ru.otus.hw.services.BookService;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookRestController {
    private final BookService bookService;

    @GetMapping
    public Flux<BookResponseDto> findAll() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<BookResponseDto> findBookById(@PathVariable Long id) {
        BookRequestDto bookRequestDto = BookRequestDto.builder()
                .id(id)
                .build();

        return bookService.findById(bookRequestDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookResponseDto> createBook(@Valid @RequestBody Mono<BookCreateRequestDto> bookCreateRequestDtoMono) {
        return bookCreateRequestDtoMono
                .flatMap(bookService::insert);
    }

    @PutMapping("/{id}")
    public Mono<BookResponseDto> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody Mono<BookUpdateRequestDto> bookUpdateRequestDtoMono) {

        return bookUpdateRequestDtoMono
                .map(request -> request.toBuilder()
                        .id(id)
                        .build())
                .flatMap(bookService::update);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBook(@PathVariable Long id) {
        BookRequestDto bookRequestDto = BookRequestDto.builder()
                .id(id)
                .build();

        return bookService.deleteById(bookRequestDto);
    }
}