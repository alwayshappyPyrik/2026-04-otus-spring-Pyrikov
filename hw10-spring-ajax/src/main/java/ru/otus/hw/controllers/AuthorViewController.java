package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorViewController {

    @GetMapping
    public String listAuthors() {
        return "authors/list";
    }

    @GetMapping("/{id}")
    public String showAuthorDetails(@PathVariable String id) {
        return "authors/detail";
    }
}
