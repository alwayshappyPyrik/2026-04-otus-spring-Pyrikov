package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.CommentRequestDto;
import ru.otus.hw.dto.CommentResponseDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    private final BookService bookService;

    @GetMapping("/book/{bookId}")
    public String findCommentsByBookId(@PathVariable Long bookId, Model model) {
        var bookRequestDto = BookRequestDto.builder()
                .id(bookId)
                .build();

        var book = bookService.findById(bookRequestDto)
                .orElseThrow(() -> new NotFoundException("Book with id " + bookId + " not found"));

        var commentRequestDto = CommentRequestDto.builder()
                .book(bookRequestDto)
                .build();

        List<CommentResponseDto> comments = commentService.findAllByBookId(commentRequestDto);
        model.addAttribute("book", book);
        model.addAttribute("comments", comments);
        return "comments/list";
    }

    @GetMapping("/{id}")
    public String findCommentById(@PathVariable Long id, Model model) {
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .id(id)
                .build();

        CommentResponseDto comment = commentService.findById(commentRequestDto)
                .orElseThrow(() -> new NotFoundException("Comment with id " + id + " not found"));

        model.addAttribute("comment", comment);
        return "comments/detail";
    }

    @GetMapping("/book/{bookId}/new")
    public String showCreateForm(@PathVariable Long bookId, Model model) {
        var bookRequestDto = BookRequestDto.builder()
                .id(bookId)
                .build();

        var book = bookService.findById(bookRequestDto)
                .orElseThrow(() -> new NotFoundException("Book with id " + bookId + " not found"));

        model.addAttribute("book", book);
        model.addAttribute("comment", CommentRequestDto.builder().build());
        return "comments/create";
    }

    @PostMapping("/book/{bookId}")
    public String createComment(@PathVariable Long bookId,
                                @ModelAttribute CommentRequestDto commentRequestDto) {
        var bookRequestDto = BookRequestDto.builder()
                .id(bookId)
                .build();

        var commentWithBook = CommentRequestDto.builder()
                .text(commentRequestDto.text())
                .book(bookRequestDto)
                .build();

        commentService.insert(commentWithBook);
        return "redirect:/comments/book/" + bookId;
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .id(id)
                .build();

        CommentResponseDto comment = commentService.findById(commentRequestDto)
                .orElseThrow(() -> new NotFoundException("Comment with id " + id + " not found"));

        var commentForm = CommentRequestDto.builder()
                .id(comment.id())
                .text(comment.text())
                .build();

        model.addAttribute("comment", commentForm);
        model.addAttribute("bookId", comment.book().id());
        return "comments/edit";
    }

    @PutMapping("/{id}")
    public String updateComment(@PathVariable Long id,
                                @ModelAttribute CommentRequestDto commentRequestDto,
                                @RequestParam Long bookId) {
        var commentWithData = CommentRequestDto.builder()
                .id(id)
                .text(commentRequestDto.text())
                .book(BookRequestDto.builder().id(bookId).build())
                .build();

        commentService.update(commentWithData);
        return "redirect:/comments/book/" + bookId;
    }

    @DeleteMapping("/{id}")
    public String deleteComment(@PathVariable Long id, @RequestParam Long bookId) {
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .id(id)
                .build();

        commentService.deleteById(commentRequestDto);
        return "redirect:/comments/book/" + bookId;
    }
}