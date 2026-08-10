package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.AuthorRequestDto;
import ru.otus.hw.dto.AuthorResponseDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
public class AuthorRestController {

    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<List<AuthorResponseDto>> findAllAuthors() {
        List<AuthorResponseDto> authors = authorService.findAll();
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDto> findAuthorById(@PathVariable Long id) {
        AuthorRequestDto authorRequestDto = AuthorRequestDto.builder()
                .id(id)
                .build();

        AuthorResponseDto author = authorService.findById(authorRequestDto);
        return ResponseEntity.ok(author);
    }
}