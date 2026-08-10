package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.CommentRequestDto;
import ru.otus.hw.dto.CommentResponseDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDto> findCommentById(@PathVariable Long id) {
        CommentRequestDto request = CommentRequestDto.builder()
                .id(id)
                .build();

        CommentResponseDto comment = commentService.findById(request);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<CommentResponseDto>> findCommentsByBookId(@PathVariable Long bookId) {
        CommentRequestDto commentRequest = CommentRequestDto.builder()
                .book(BookRequestDto.builder().id(bookId).build())
                .build();

        List<CommentResponseDto> comments = commentService.findAllByBookId(commentRequest);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/book/{bookId}")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long bookId,
            @Valid @RequestBody CommentRequestDto commentRequestDto) {

        CommentRequestDto commentWithBook = CommentRequestDto.builder()
                .text(commentRequestDto.text())
                .book(BookRequestDto.builder().id(bookId).build())
                .build();

        CommentResponseDto createdComment = commentService.insert(commentWithBook);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequestDto commentRequestDto) {

        CommentRequestDto commentWithData = CommentRequestDto.builder()
                .id(id)
                .text(commentRequestDto.text())
                .book(commentRequestDto.book())
                .build();

        CommentResponseDto updatedComment = commentService.update(commentWithData);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        CommentRequestDto request = CommentRequestDto.builder()
                .id(id)
                .build();

        commentService.deleteById(request);
        return ResponseEntity.noContent().build();
    }
}