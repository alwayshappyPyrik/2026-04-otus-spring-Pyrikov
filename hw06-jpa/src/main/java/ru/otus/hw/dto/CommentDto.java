package ru.otus.hw.dto;

public record CommentDto(long id, BookDto book, String text) {
    @Override
    public String toString() {
        return "Id: %d, book: {%s}, Text: %s"
                .formatted(id, book, text);
    }
}