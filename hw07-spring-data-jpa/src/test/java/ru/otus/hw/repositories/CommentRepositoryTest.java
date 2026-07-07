package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями ")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CommentRepositoryTest {

    private static final long FIRST_COMMENT_ID = 1L;
    private static final int EXPECTED_NUMBER_OF_COMMENTS = 1;
    private static final String FIRST_COMMENT_TEXT = "Book 1 is interesting and exciting";

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookRepository bookRepository;

    private List<Book> dbBooks;

    @DisplayName("должен загружать информацию о нужном комментарии по его id")
    @Test
    void shouldFindExpectedCommentById() {
        var actualComment = commentRepository.findById(FIRST_COMMENT_ID);

        assertThat(actualComment)
                .isPresent()
                .hasValueSatisfying(comment -> {
                    assertThat(comment.getId()).isEqualTo(FIRST_COMMENT_ID);
                    assertThat(comment.getText()).isEqualTo(FIRST_COMMENT_TEXT);
                    assertThat(comment.getBook()).isNotNull();
                    assertThat(comment.getBook().getId()).isGreaterThan(0);
                });
    }

    @DisplayName("должен удалять комментарий по id")
    @Test
    void shouldDeleteComment() {
        assertThat(commentRepository.findById(FIRST_COMMENT_ID)).isPresent();

        commentRepository.deleteById(FIRST_COMMENT_ID);

        assertThat(commentRepository.findById(FIRST_COMMENT_ID)).isEmpty();
    }

    @Nested
    @DisplayName("Тяжелые тесты для комментариев")
    class CommentRepositoryHeavyTest {

        @BeforeEach
        void setUp() {
            dbBooks = bookRepository.findAll();
        }

        @DisplayName("должен загружать список всех комментариев по заданной книге")
        @Test
        void shouldReturnCorrectCommentsBySelectedBookIds() {
            Book expectedBook = dbBooks.getFirst();

            var comments = commentRepository.findAllByBookId(expectedBook.getId());

            assertThat(comments)
                    .isNotNull()
                    .hasSize(EXPECTED_NUMBER_OF_COMMENTS)
                    .allMatch(c -> c.getId() > 0)
                    .allMatch(c -> c.getBook() != null)
                    .allMatch(c -> c.getBook().getId() == expectedBook.getId())
                    .allMatch(c -> c.getText() != null && !c.getText().isEmpty());

            assertThat(comments)
                    .allMatch(c -> c.getBook().getId() == expectedBook.getId());

            assertThat(comments)
                    .extracting(Comment::getText)
                    .containsExactly("Book 1 is interesting and exciting");
        }

        @DisplayName("должен сохранять новый комментарий с полной информацией о нем")
        @Test
        void shouldSaveNewComment() {
            var expectedComment = new Comment(
                    "New Comment",
                    dbBooks.getFirst()
            );

            var returnedComment = commentRepository.save(expectedComment);

            assertThat(returnedComment)
                    .isNotNull()
                    .matches(comment -> comment.getId() > 0)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expectedComment);

            var foundComment = commentRepository.findById(returnedComment.getId());
            assertThat(foundComment)
                    .isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .isEqualTo(returnedComment);
        }

        @DisplayName("должен сохранять измененный комментарий")
        @Test
        void shouldSaveUpdatedComment() {
            var bookToUpdate = dbBooks.get(1);
            var expectedComment = new Comment(
                    FIRST_COMMENT_ID,
                    "Edited Comment",
                    bookToUpdate
            );

            assertThat(commentRepository.findById(expectedComment.getId()))
                    .isPresent()
                    .get()
                    .isNotEqualTo(expectedComment);

            var returnedComment = commentRepository.save(expectedComment);

            assertThat(returnedComment)
                    .isNotNull()
                    .matches(comment -> comment.getId() > 0)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedComment);

            assertThat(commentRepository.findById(returnedComment.getId()))
                    .isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .isEqualTo(returnedComment);
        }
    }
}