package ru.otus.hw.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.mapper.CommentMapperImpl;
import ru.otus.hw.mapper.BookMapperImpl;
import ru.otus.hw.mapper.AuthorMapperImpl;
import ru.otus.hw.mapper.GenreMapperImpl;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@DisplayName("Интеграционные тесты сервиса комментария ")
@DataJpaTest
@Import({CommentServiceImpl.class,
        CommentMapperImpl.class,
        JpaCommentRepository.class,
        BookMapperImpl.class,
        JpaBookRepository.class,
        AuthorMapperImpl.class,
        GenreMapperImpl.class})
@Transactional(propagation = Propagation.NEVER)
public class CommentServiceImplTest {

    private static final long BOOK_ID = 1L;
    private static final long COMMENT_ID = 1L;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentMapper commentMapper;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("должен загружать комментарий по id без LazyInitializationException")
    void shouldFindCommentByIdWithoutLazyException() {
        var entityGraph = em.getEntityGraph("Comment.withBookAndAuthorAndGenres");
        var comment = em.createQuery(
                        "SELECT c FROM Comment c " +
                            "WHERE c.id = :id",
                        Comment.class)
                .setParameter("id", COMMENT_ID)
                .setHint(FETCH.getKey(), entityGraph)
                .getSingleResult();

        var expectedComment = commentMapper.toDto(comment);

        var actualComment = commentService.findById(COMMENT_ID);

        assertThat(actualComment)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedComment);
    }

    @Test
    @DisplayName("должен загружать все комментарии по id книги без LazyInitializationException")
    void shouldFindAllCommentsByBookIdWithoutLazyException() {
        var entityGraph = em.getEntityGraph("Comment.withBookAndAuthorAndGenres");
        var comments = em.createQuery(
                        "SELECT c FROM Comment c " +
                            "WHERE c.book.id = :bookId",
                        Comment.class)
                .setParameter("bookId", BOOK_ID)
                .setHint(FETCH.getKey(), entityGraph)
                .getResultList();

        var expectedComments = comments.stream()
                .map(commentMapper::toDto)
                .toList();

        var actualComments = commentService.findAllByBookId(BOOK_ID);

        assertThat(actualComments)
                .usingRecursiveComparison()
                .isEqualTo(expectedComments);
    }

    @Test
    @DisplayName("должен вставлять новый комментарий без LazyInitializationException")
    void shouldInsertNewCommentWithoutLazyException() {
        var text = "newComment";

        var savedComment = commentService.insert(text, BOOK_ID);

        assertThat(savedComment)
                .isNotNull()
                .matches(c -> c.id() > 0)
                .matches(c -> c.text().equals(text))
                .matches(c -> c.book() != null);

        var entityGraph = em.getEntityGraph("Comment.withBookAndAuthorAndGenres");
        var comment = em.createQuery(
                        "SELECT c FROM Comment c " +
                            "WHERE c.id = :id",
                        Comment.class)
                .setParameter("id", savedComment.id())
                .setHint(FETCH.getKey(), entityGraph)
                .getSingleResult();

        var expectedComment = commentMapper.toDto(comment);

        assertThat(savedComment)
                .usingRecursiveComparison()
                .isEqualTo(expectedComment);
    }

    @Test
    @DisplayName("должен обновлять комментарий без LazyInitializationException")
    void shouldUpdateCommentWithoutLazyException() {
        var text = "editedComment";

        var updatedComment = commentService.update(COMMENT_ID, text, BOOK_ID);

        assertThat(updatedComment)
                .isNotNull()
                .matches(c -> c.id() > 0)
                .matches(c -> c.text().equals(text))
                .matches(c -> c.book() != null);

        var entityGraph = em.getEntityGraph("Comment.withBookAndAuthorAndGenres");
        var comment = em.createQuery(
                        "SELECT c FROM Comment c " +
                            "WHERE c.id = :id",
                        Comment.class)
                .setParameter("id", updatedComment.id())
                .setHint(FETCH.getKey(), entityGraph)
                .getSingleResult();

        var expectedComment = commentMapper.toDto(comment);

        assertThat(updatedComment)
                .usingRecursiveComparison()
                .isEqualTo(expectedComment);
    }

    @Test
    @DisplayName("должен удалять комментарий без LazyInitializationException")
    void shouldDeleteCommentWithoutLazyException() {
        assertThat(commentService.findById(COMMENT_ID)).isPresent();

        commentService.deleteById(COMMENT_ID);

        var afterDelete = commentService.findById(COMMENT_ID);
        assertThat(afterDelete).isEmpty();
    }
}