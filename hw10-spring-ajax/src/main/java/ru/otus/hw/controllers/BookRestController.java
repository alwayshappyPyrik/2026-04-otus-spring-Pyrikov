package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.BookCreateRequestDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.BookUpdateRequestDto;
import ru.otus.hw.services.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookRestController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookResponseDto>> findAll() {
        List<BookResponseDto> books = bookService.findAll();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> findBookById(@PathVariable Long id) {
        BookRequestDto request = BookRequestDto.builder().id(id).build();
        BookResponseDto book = bookService.findById(request);
        return ResponseEntity.ok(book);
    }

    @PostMapping
    public ResponseEntity<BookResponseDto> createBook(@Valid @RequestBody BookCreateRequestDto createRequest) {
        BookResponseDto createdBook = bookService.insert(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDto> updateBook(@PathVariable Long id,
                                                      @Valid @RequestBody BookUpdateRequestDto updateRequest) {

        BookUpdateRequestDto request = updateRequest.toBuilder()
                .id(id)
                .build();

        BookResponseDto updatedBook = bookService.update(request);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        BookRequestDto request = BookRequestDto.builder()
                .id(id)
                .build();

        bookService.deleteById(request);
        return ResponseEntity.noContent().build();
    }
}