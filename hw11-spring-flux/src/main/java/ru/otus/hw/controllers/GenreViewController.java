package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreViewController {

    @GetMapping
    public String findAllGenres() {
        return "genres/list";
    }
}