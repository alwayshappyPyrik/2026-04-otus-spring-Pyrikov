package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookViewController {

    @GetMapping
    public String findAll() {
        return "books/list";
    }

    @GetMapping("/{id}")
    public String findBookById(@PathVariable Long id, Model model) {
        model.addAttribute("bookId", id);
        return "books/detail";
    }

    @GetMapping("/new")
    public String showCreateForm() {
        return "books/new";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("bookId", id);
        return "books/edit";
    }
}
