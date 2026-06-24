package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;

    @ShellMethod(value = "Find by id comment", key = "cbid")
    public String findCommentById(long id) {
        return commentService.findById(id)
                .map(CommentDto::toString)
                .orElse("Comment with id %d not found".formatted(id));
    }

    @ShellMethod(value = "Find comments by book id", key = "cbbid")
    public String findCommentsByBookId(long id) {
        return commentService.findAllByBookId(id).stream()
                .map(CommentDto::toString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Insert comment (comma-separated, e.g.: cins newComment 1)", key = "cins")
    public String insertComment(String text, long bookId) {
        var savedComment = commentService.insert(text, bookId);
        return savedComment.toString();
    }

    @ShellMethod(value = "Update comment (comma-separated, e.g.: cupd 4 editedComment 1)", key = "cupd")
    public String updateComment(long id, String text, long bookId) {
        var savedComment = commentService.update(id, text, bookId);
        return savedComment.toString();
    }

    @ShellMethod(value = "Delete comment by id (comma-separated, e.g.: bdel 4)", key = "cdel")
    public void deleteComment(long id) {
        commentService.deleteById(id);
    }
}