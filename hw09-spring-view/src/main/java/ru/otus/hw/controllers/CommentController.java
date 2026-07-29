package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.CommentRequestDto;
import ru.otus.hw.dto.CommentResponseDto;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    private final BookService bookService;

    private final CommentMapper commentMapper;

    @GetMapping("/{id}")
    public String findCommentById(@PathVariable Long id, Model model) {
        CommentRequestDto request = CommentRequestDto.builder()
                .id(id)
                .build();

        CommentResponseDto comment = commentService.findById(request);

        model.addAttribute("comment", comment);
        return "comments/detail";
    }

    @GetMapping("/book/{bookId}")
    public String findCommentsByBookId(@PathVariable Long bookId, Model model) {
        BookRequestDto bookRequest = BookRequestDto.builder()
                .id(bookId)
                .build();

        BookResponseDto book = bookService.findById(bookRequest);

        CommentRequestDto commentRequest = CommentRequestDto.builder()
                .book(bookRequest)
                .build();

        List<CommentResponseDto> comments = commentService.findAllByBookId(commentRequest);

        model.addAttribute("book", book);
        model.addAttribute("comments", comments);
        return "comments/list";
    }

    @GetMapping("/book/{bookId}/new")
    public String showCreateForm(@PathVariable Long bookId, Model model) {
        BookRequestDto bookRequest = BookRequestDto.builder()
                .id(bookId)
                .build();

        BookResponseDto book = bookService.findById(bookRequest);

        CommentRequestDto commentForm = CommentRequestDto.builder()
                .book(bookRequest)
                .build();

        model.addAttribute("book", book);
        model.addAttribute("comment", commentForm);
        return "comments/create";
    }

    @PostMapping("/book/{bookId}")
    public String createComment(@PathVariable Long bookId,
                                @Valid @ModelAttribute("comment") CommentRequestDto commentRequestDto,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            BookRequestDto bookRequest = BookRequestDto.builder()
                    .id(bookId)
                    .build();
            model.addAttribute("book", bookService.findById(bookRequest));
            return "comments/create";
        }

        BookRequestDto bookRequest = BookRequestDto.builder()
                .id(bookId)
                .build();

        CommentRequestDto commentWithBook = CommentRequestDto.builder()
                .text(commentRequestDto.text())
                .book(bookRequest)
                .build();

        commentService.insert(commentWithBook);
        return "redirect:/comments/book/" + bookId;
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        CommentRequestDto request = CommentRequestDto.builder()
                .id(id)
                .build();

        CommentResponseDto comment = commentService.findById(request);

        CommentRequestDto commentForm = commentMapper.toRequestDto(comment);

        model.addAttribute("comment", commentForm);
        model.addAttribute("bookId", comment.book().id());
        return "comments/edit";
    }

    @PutMapping("/{id}")
    public String updateComment(@PathVariable Long id,
                                @Valid @ModelAttribute("comment") CommentRequestDto commentRequestDto,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bookId", commentRequestDto.book().id());
            return "comments/edit";
        }

        CommentRequestDto commentWithData = CommentRequestDto.builder()
                .id(id)
                .text(commentRequestDto.text())
                .book(commentRequestDto.book())
                .build();

        CommentResponseDto updatedComment = commentService.update(commentWithData);
        return "redirect:/comments/book/" + updatedComment.book().id();
    }

    @DeleteMapping("/{id}")
    public String deleteComment(@PathVariable Long id) {
        CommentRequestDto request = CommentRequestDto.builder()
                .id(id)
                .build();

        CommentResponseDto comment = commentService.findById(request);
        commentService.deleteById(request);

        return "redirect:/comments/book/" + comment.book().id();
    }
}
