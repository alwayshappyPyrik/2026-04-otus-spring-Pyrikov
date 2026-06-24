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
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("Интеграционные тесты сервиса комментария ")
@SpringBootTest
@Transactional
public class CommentServiceImplTest {

    @Autowired
    private CommentService commentService;

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
        assertThatCode(() -> {
            var optionalCommentDto = commentService.findById(testBook.getId());
            assertThat(optionalCommentDto).isPresent();

            CommentDto comment = optionalCommentDto.get();

            assertThat(comment.book().author().fullName()).isNotNull();
            assertThat(comment.book().genres()).isNotEmpty();
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("должен загружать список всех комментариев по книжке без LazyInitializationException")
    void shouldFindAllCommentsByBookWithoutLazyException() {
        assertThatCode(() -> {
            var comments = commentService.findAllByBookId(testBook.getId());
            assertThat(comments).isNotEmpty();

            comments.forEach(comment -> {
                assertThat(comment.book().author().fullName()).isNotNull();
                assertThat(comment.book().genres()).isNotEmpty();
            });
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("должен вставлять новый комментарий без LazyInitializationException")
    void shouldInsertCommentWithoutLazyException() {
        assertThatCode(() -> {
            CommentDto savedComment = commentService.insert(
                    "newComment",
                    testBook.getId()
            );

            assertThat(savedComment.book().author().fullName()).isNotNull();
            assertThat(savedComment.book().genres()).isNotEmpty();
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("должен обновлять комментарий без LazyInitializationException")
    void shouldUpdateCommentWithoutLazyException() {
        long commentId = testComment.getId();
        assertThatCode(() -> {
            CommentDto updateComment = commentService.update(
                    commentId,
                    "editedComment",
                    testBook.getId()
            );

            assertThat(updateComment.book().author().fullName()).isNotNull();
            assertThat(updateComment.book().genres()).isNotEmpty();
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("должен удалять комментарий без LazyInitializationException")
    void shouldDeleteCommentWithoutLazyException() {
        long commentId = testComment.getId();
        assertThatCode(() -> {
            var commentBeforeDelete = commentService.findById(commentId);
            assertThat(commentBeforeDelete).isPresent();

            commentService.deleteById(commentId);

            var commentAfterDelete = commentService.findById(commentId);
            assertThat(commentAfterDelete).isEmpty();
        }).doesNotThrowAnyException();
    }
}