package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.CommentRequestDto;
import ru.otus.hw.dto.CommentResponseDto;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;

    @ShellMethod(value = "Find by id comment", key = "cbid")
    public String findCommentById(String id) {
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .id(id)
                .build();

        return commentService.findById(commentRequestDto)
                .map(CommentResponseDto::toString)
                .orElse("Comment with id %s not found".formatted(id));
    }

    @ShellMethod(value = "Find comments by book id", key = "cbbid")
    public String findCommentsByBookId(String id) {
        BookRequestDto bookRequestDto = BookRequestDto.builder()
                .id(id)
                .build();

        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .book(bookRequestDto)
                .build();

        return commentService.findAllByBookId(commentRequestDto).stream()
                .map(CommentResponseDto::toString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Insert comment (comma-separated, e.g.: cins newComment book_id)", key = "cins")
    public String insertComment(String text, String bookId) {
        BookRequestDto bookRequestDto = BookRequestDto.builder()
                .id(bookId)
                .build();

        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .text(text)
                .book(bookRequestDto)
                .build();

        var savedComment = commentService.insert(commentRequestDto);
        return savedComment.toString();
    }

    @ShellMethod(value = "Update comment (comma-separated, e.g.: cupd comment_id editedComment)", key = "cupd")
    public String updateComment(String id, String text) {
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .id(id)
                .text(text)
                .build();

        var savedComment = commentService.update(commentRequestDto);
        return savedComment.toString();
    }

    @ShellMethod(value = "Delete comment by id (comma-separated, e.g.: cdel 4)", key = "cdel")
    public void deleteComment(String id) {
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .id(id)
                .build();

        commentService.deleteById(commentRequestDto);
    }
}