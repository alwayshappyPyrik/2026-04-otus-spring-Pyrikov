package ru.otus.hw.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Интеграционные тесты сервиса комментария ")
@SpringBootTest
@Transactional
public class CommentServiceImplTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentMapper commentMapper;

    @PersistenceContext
    private EntityManager em;

    private Book testBook;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        testBook = em.find(Book.class, 1L);
        testComment = em.find(Comment.class, 1L);
    }

    @Test
    @DisplayName("должен загружать комментарий по id без LazyInitializationException")
    void shouldFindCommentByIdWithoutLazyException() {
        Comment commentEntity = Comment.builder()
                .id(testComment.getId())
                .text(testComment.getText())
                .book(testBook)
                .build();

        CommentDto expectedComment = commentMapper.toDto(commentEntity);

        var optionalCommentDto = commentService.findById(testComment.getId());

        assertThat(optionalCommentDto)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedComment);
    }

    @Test
    @DisplayName("должен загружать список всех комментариев по книжке без LazyInitializationException")
    void shouldFindAllCommentsByBookWithoutLazyException() {
        Comment commentEntity = Comment.builder()
                .id(testComment.getId())
                .text(testComment.getText())
                .book(testBook)
                .build();

        CommentDto expectedComment = commentMapper.toDto(commentEntity);
        List<CommentDto> expectedComments = List.of(expectedComment);

        List<CommentDto> actualComments = commentService.findAllByBookId(testBook.getId());

        assertThat(actualComments)
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(expectedComments);
    }

    @Test
    @DisplayName("должен вставлять новый комментарий без LazyInitializationException")
    void shouldInsertCommentWithoutLazyException() {
        String newText = "newComment";

        Comment commentToInsert = Comment.builder()
                .text(newText)
                .book(testBook)
                .build();

        CommentDto expectedComment = commentMapper.toDto(commentToInsert);

        CommentDto savedComment = commentService.insert(newText, testBook.getId());

        assertThat(savedComment)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedComment);
    }

    @Test
    @DisplayName("должен обновлять комментарий без LazyInitializationException")
    void shouldUpdateCommentWithoutLazyException() {
        long commentId = testComment.getId();
        String updatedText = "editedComment";

        Comment commentEntity = Comment.builder()
                .id(commentId)
                .text(updatedText)
                .book(testBook)
                .build();

        CommentDto expectedComment = commentMapper.toDto(commentEntity);

        CommentDto updatedComment = commentService.update(commentId, updatedText, testBook.getId());

        assertThat(updatedComment)
                .usingRecursiveComparison()
                .isEqualTo(expectedComment);
    }

    @Test
    @DisplayName("должен удалять комментарий без LazyInitializationException")
    void shouldDeleteCommentWithoutLazyException() {
        long commentId = testComment.getId();

        commentService.deleteById(commentId);

        var commentAfterDelete = commentService.findById(commentId);
        assertThat(commentAfterDelete).isEmpty();
    }
}