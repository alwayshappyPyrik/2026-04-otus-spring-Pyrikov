package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorRequestDto;
import ru.otus.hw.dto.AuthorResponseDto;
import ru.otus.hw.services.AuthorService;

@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
public class AuthorRestController {

    private final AuthorService authorService;

    @GetMapping
    public Flux<AuthorResponseDto> findAllAuthors() {
        return authorService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<AuthorResponseDto> findAuthorById(@PathVariable Long id) {
        AuthorRequestDto authorRequestDto = AuthorRequestDto.builder()
                .id(id)
                .build();

        return authorService.findById(authorRequestDto);
    }
}