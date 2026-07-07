package ru.otus.hw.repositories;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями ")
@DataJpaTest
@Import(JpaCommentRepository.class)
public class JpaCommentRepositoryTest {

    private static final long FIRST_COMMENT_ID = 1L;
    private static final int EXPECTED_NUMBER_OF_COMMENTS = 1;

    private static final long EXPECTED_QUERIES_COUNT = 1L;

    @Autowired
    private JpaCommentRepository jpaCommentRepository;

    @Autowired
    private TestEntityManager em;

    private List<Book> dbBooks;

    @DisplayName("должен загружать информацию о нужном комментарии по ее id")
    @Test
    void shouldFindExpectedCommentById() {
        var optionalActualComment = jpaCommentRepository.findById(FIRST_COMMENT_ID);
        var expectedComment = em.find(Comment.class, FIRST_COMMENT_ID);
        assertThat(optionalActualComment).isPresent().get()
                .usingRecursiveComparison().isEqualTo(expectedComment);
    }

    @DisplayName("должен удалять комментарий по id")
    @Test
    void shouldDeleteComment() {
        assertThat(jpaCommentRepository.findById(1L)).isPresent();
        jpaCommentRepository.deleteById(1L);
        assertThat(jpaCommentRepository.findById(1L)).isEmpty();
    }

    @Nested
    public class JpaCommentRepositoryHeavyTest {

        @BeforeEach
        void setUp() {
            dbBooks = getDbBooks();
        }

        @DisplayName("должен загружать список всех комментариев по заданной книге")
        @Test
        void shouldReturnCorrectCommentsBySelectedBookIds() {
            SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory()
                    .unwrap(SessionFactory.class);
            sessionFactory.getStatistics().setStatisticsEnabled(true);

            List<Book> dbBooks = getDbBooks();
            Book expectedBook = dbBooks.getFirst();

            var comments = jpaCommentRepository.findAllByBookId(expectedBook.getId());
            assertThat(comments).isNotNull().hasSize(EXPECTED_NUMBER_OF_COMMENTS)
                    .allMatch(c -> c.getId() > 0)
                    .allMatch(c -> c.getBook() != null)
                    .allMatch(c -> c.getBook().getId() == expectedBook.getId())
                    .allMatch(c -> c.getText() != null);

            assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(EXPECTED_QUERIES_COUNT);
        }

        @DisplayName("должен сохранять новый комментарий c полной информации о ней")
        @Test
        void shouldSaveNewComment() {
            var expectedComment = new Comment(
                    "newComment",
                    dbBooks.getFirst()
            );

            var returnedComment = jpaCommentRepository.save(expectedComment);

            assertThat(returnedComment).isNotNull()
                    .matches(comment -> comment.getId() > 0)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expectedComment);

            var foundComment = jpaCommentRepository.findById(returnedComment.getId());
            assertThat(foundComment)
                    .isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .isEqualTo(returnedComment);
        }

        @DisplayName("должен сохранять измененный комментарий")
        @Test
        void shouldSaveUpdatedComment() {
            var expectedComment = new Comment(1L, "editedComment", dbBooks.get(1));

            assertThat(jpaCommentRepository.findById(expectedComment.getId()))
                    .isPresent()
                    .get()
                    .isNotEqualTo(expectedComment);

            var returnedComment = jpaCommentRepository.save(expectedComment);
            assertThat(returnedComment).isNotNull()
                    .matches(comment -> comment.getId() > 0)
                    .usingRecursiveComparison().isEqualTo(expectedComment);

            assertThat(jpaCommentRepository.findById(returnedComment.getId()))
                    .isPresent()
                    .get()
                    .isEqualTo(returnedComment);
        }
    }

    private List<Book> getDbBooks() {
        return List.of(
                em.find(Book.class, 1L),
                em.find(Book.class, 2L),
                em.find(Book.class, 3L)
        );
    }
}