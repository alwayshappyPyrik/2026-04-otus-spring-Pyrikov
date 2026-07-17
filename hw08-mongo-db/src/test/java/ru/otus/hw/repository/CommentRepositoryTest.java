package ru.otus.hw.repository;

import io.mongock.test.springboot.junit5.MongockSpringbootJUnit5IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.config.TestMongockConfig;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Spring Data MongoDB для работы с комментариями")
@DataMongoTest
@ActiveProfiles("test")
@Import(TestMongockConfig.class)
public class CommentRepositoryTest extends MongockSpringbootJUnit5IntegrationTestBase {

    private static final String FIRST_COMMENT_TEXT = "Book 1 is interesting and exciting";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookRepository bookRepository;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("authors");
        mongoTemplate.dropCollection("books");
        mongoTemplate.dropCollection("comments");
        mongoTemplate.dropCollection("mongockChangeLog");

        executeMongock();
    }

    @DisplayName("должен загружать информацию о нужном комментарии по его id")
    @Test
    void shouldFindExpectedCommentById() {
        var allComments = commentRepository.findAll();
        assertThat(allComments).isNotEmpty();

        var commentId = allComments.getFirst().getId();

        var actualComment = commentRepository.findById(commentId);

        assertThat(actualComment)
                .isPresent()
                .hasValueSatisfying(comment -> {
                    assertThat(comment.getId()).isEqualTo(commentId);
                    assertThat(comment.getText()).isEqualTo(FIRST_COMMENT_TEXT);
                    assertThat(comment.getBook()).isNotNull();
                });
    }

    @DisplayName("должен удалять комментарий по id")
    @Test
    void shouldDeleteComment() {
        var allComments = commentRepository.findAll();
        assertThat(allComments).isNotEmpty();

        var commentId = allComments.getFirst().getId();

        assertThat(commentRepository.findById(commentId)).isPresent();

        commentRepository.deleteById(commentId);

        assertThat(commentRepository.findById(commentId)).isEmpty();
    }

    @Nested
    @DisplayName("Тяжелые тесты для комментариев")
    class CommentRepositoryHeavyTest {

        private Book dbBook;

        @BeforeEach
        void setUp() {
            dbBooks = bookRepository.findAll();
            dbBook = dbBooks.getFirst();
        }

        @DisplayName("должен загружать список всех комментариев по заданной книге")
        @Test
        void shouldReturnCorrectCommentsBySelectedBookIds() {
            var comments = commentRepository.findAllByBookId(dbBook.getId());

            assertThat(comments)
                    .isNotNull()
                    .isNotEmpty()
                    .allMatch(c -> !c.getId().isBlank())
                    .allMatch(c -> c.getBook() != null)
                    .allMatch(c -> c.getBook().getId().equals(dbBook.getId()))
                    .allMatch(c -> c.getText() != null && !c.getText().isEmpty());

            assertThat(comments)
                    .extracting(Comment::getText)
                    .containsExactlyInAnyOrder(FIRST_COMMENT_TEXT);
        }

        @DisplayName("должен сохранять новый комментарий с полной информацией о нем")
        @Test
        void shouldSaveNewComment() {
            var expectedComment = new Comment(
                    "New Comment",
                    dbBook
            );

            var returnedComment = commentRepository.save(expectedComment);

            assertThat(returnedComment)
                    .isNotNull()
                    .satisfies(comment -> {
                        assertThat(comment.getId()).isNotBlank();
                        assertThat(comment.getText()).isEqualTo("New Comment");
                        assertThat(comment.getBook())
                                .isNotNull()
                                .satisfies(book -> {
                                    assertThat(book.getId()).isEqualTo(dbBook.getId());
                                    assertThat(book.getTitle()).isEqualTo(dbBook.getTitle());
                                });
                    });

            var foundComment = commentRepository.findById(returnedComment.getId());
            assertThat(foundComment)
                    .isPresent()
                    .get()
                    .satisfies(comment -> {
                        assertThat(comment.getId()).isEqualTo(returnedComment.getId());
                        assertThat(comment.getText()).isEqualTo(returnedComment.getText());
                        assertThat(comment.getBook().getId()).isEqualTo(returnedComment.getBook().getId());
                    });
        }

        @DisplayName("должен сохранять измененный комментарий")
        @Test
        void shouldSaveUpdatedComment() {
            var allComments = commentRepository.findAll();
            assertThat(allComments).isNotEmpty();

            var commentToUpdate = allComments.getFirst();
            var bookToUpdate = dbBooks.get(1);

            var expectedComment = new Comment(
                    commentToUpdate.getId(),
                    "Edited Comment",
                    bookToUpdate
            );

            assertThat(commentRepository.findById(expectedComment.getId()))
                    .isPresent()
                    .get()
                    .satisfies(comment -> {
                        assertThat(comment.getText()).isNotEqualTo("Edited Comment");
                        assertThat(comment.getBook().getId()).isNotEqualTo(bookToUpdate.getId());
                    });

            var returnedComment = commentRepository.save(expectedComment);

            assertThat(returnedComment)
                    .isNotNull()
                    .satisfies(comment -> {
                        assertThat(comment.getId()).isNotBlank();
                        assertThat(comment.getId()).isEqualTo(expectedComment.getId());
                        assertThat(comment.getText()).isEqualTo("Edited Comment");
                        assertThat(comment.getBook())
                                .isNotNull()
                                .satisfies(book -> {
                                    assertThat(book.getId()).isEqualTo(bookToUpdate.getId());
                                    assertThat(book.getTitle()).isEqualTo(bookToUpdate.getTitle());
                                });
                    });

            var foundComment = commentRepository.findById(returnedComment.getId());
            assertThat(foundComment)
                    .isPresent()
                    .get()
                    .satisfies(comment -> {
                        assertThat(comment.getId()).isEqualTo(returnedComment.getId());
                        assertThat(comment.getText()).isEqualTo("Edited Comment");
                        assertThat(comment.getBook().getId()).isEqualTo(bookToUpdate.getId());
                    });
        }
    }
}