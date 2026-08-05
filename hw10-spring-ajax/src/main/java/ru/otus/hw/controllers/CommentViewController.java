package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentViewController {

    @GetMapping("/{id}")
    public String findCommentById(@PathVariable Long id, Model model) {
        model.addAttribute("commentId", id);
        return "comments/detail";
    }

    @GetMapping("/book/{bookId}")
    public String findCommentsByBookId(@PathVariable Long bookId, Model model) {
        model.addAttribute("bookId", bookId);
        return "comments/list";
    }

    @GetMapping("/book/{bookId}/new")
    public String showCreateForm(@PathVariable Long bookId, Model model) {
        model.addAttribute("bookId", bookId);
        return "comments/new";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("commentId", id);
        return "comments/edit";
    }
}