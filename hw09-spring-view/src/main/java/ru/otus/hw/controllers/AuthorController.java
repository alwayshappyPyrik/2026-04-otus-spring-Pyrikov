package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.otus.hw.dto.AuthorRequestDto;
import ru.otus.hw.dto.AuthorResponseDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@Controller
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public String findAllAuthors(Model model) {
        List<AuthorResponseDto> authors = authorService.findAll();
        model.addAttribute("authors", authors);
        return "authors/list";
    }

    @GetMapping("/{id}")
    public String findAuthorById(@PathVariable Long id, Model model) {
        AuthorRequestDto authorRequestDto = AuthorRequestDto.builder()
                .id(id)
                .build();

        AuthorResponseDto author = authorService.findById(authorRequestDto)
                .orElseThrow(() -> new NotFoundException("Author not found"));
        model.addAttribute("author", author);
        return "authors/detail";
    }
}