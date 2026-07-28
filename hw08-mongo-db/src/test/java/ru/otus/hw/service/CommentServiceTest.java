package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.CommentRequestDto;
import ru.otus.hw.dto.CommentResponseDto;
import ru.otus.hw.mapper.CommentMapperImpl;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.CommentServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с комментариями")
@DataMongoTest
@Import({
        CommentServiceImpl.class,
        CommentMapperImpl.class
})
public class CommentServiceTest {

    private static final int EXPECTED_NUMBER_OF_COMMENTS = 3;
    private static final String FIRST_COMMENT_TEXT = "Book 1 is interesting and exciting";
    private static final String SECOND_COMMENT_TEXT = "I didn't like Book 2";
    private static final String THIRD_COMMENT_TEXT = "Book 3 is really bad, I wouldn't recommend it to anyone";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private CommentService commentService;

    private String book1Id;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("authors");
        mongoTemplate.dropCollection("books");
        mongoTemplate.dropCollection("genres");
        mongoTemplate.dropCollection("comments");

        List<Author> authors = List.of(
                new Author(null, "Author_1"),
                new Author(null, "Author_2"),
                new Author(null, "Author_3")
        );
        authors.forEach(author -> mongoTemplate.save(author, "authors"));

        List<Genre> genres = List.of(
                new Genre(null, "Genre_1"),
                new Genre(null, "Genre_2"),
                new Genre(null, "Genre_3"),
                new Genre(null, "Genre_4"),
                new Genre(null, "Genre_5"),
                new Genre(null, "Genre_6")
        );
        genres.forEach(genre -> mongoTemplate.save(genre, "genres"));

        List<Author> dbAuthors = authorRepository.findAll();
        List<Genre> dbGenres = genreRepository.findAll();

        List<Book> books = List.of(
                new Book(null, "BookTitle_1", dbAuthors.get(0), List.of(dbGenres.get(0), dbGenres.get(1))),
                new Book(null, "BookTitle_2", dbAuthors.get(1), List.of(dbGenres.get(2), dbGenres.get(3))),
                new Book(null, "BookTitle_3", dbAuthors.get(2), List.of(dbGenres.get(4), dbGenres.get(5)))
        );
        books.forEach(book -> mongoTemplate.save(book, "books"));

        List<Book> savedBooks = bookRepository.findAll();
        book1Id = savedBooks.get(0).getId();

        List<Comment> comments = List.of(
                new Comment(null, FIRST_COMMENT_TEXT, savedBooks.get(0)),
                new Comment(null, SECOND_COMMENT_TEXT, savedBooks.get(1)),
                new Comment(null, THIRD_COMMENT_TEXT, savedBooks.get(2))
        );
        comments.forEach(comment -> mongoTemplate.save(comment, "comments"));
    }

    @DisplayName("должен загружать комментарий по его id")
    @Test
    void shouldFindCommentById() {
        var allComments = commentRepository.findAll();
        assertThat(allComments).isNotEmpty();

        var comment = allComments.getFirst();
        var commentId = comment.getId();

        CommentRequestDto request = CommentRequestDto.builder()
                .id(commentId)
                .build();

        Optional<CommentResponseDto> actualComment = commentService.findById(request);

        assertThat(actualComment)
                .isPresent()
                .hasValueSatisfying(commentDto -> {
                    assertThat(commentDto.id()).isEqualTo(commentId);
                    assertThat(commentDto.text()).isEqualTo(FIRST_COMMENT_TEXT);
                });
    }

    @DisplayName("должен загружать все комментарии по id книги")
    @Test
    void shouldFindAllCommentsByBookId() {
        BookRequestDto bookRequestDto = BookRequestDto.builder()
                .id(book1Id)
                .build();

        CommentRequestDto request = CommentRequestDto.builder()
                .book(bookRequestDto)
                .build();

        List<CommentResponseDto> comments = commentService.findAllByBookId(request);

        assertThat(comments)
                .isNotNull()
                .hasSize(1)
                .allMatch(c -> c.text() != null && !c.text().isBlank())
                .extracting(CommentResponseDto::text)
                .containsExactly(FIRST_COMMENT_TEXT);
    }

    @Nested
    @DisplayName("Тяжелые тесты с комментариями")
    class CommentServiceCrudTest {

        @DisplayName("должен создавать новый комментарий для книги")
        @Test
        void shouldInsertComment() {
            String newCommentText = "New Comment";

            BookRequestDto bookRequestDto = BookRequestDto.builder()
                    .id(book1Id)
                    .build();

            CommentRequestDto request = CommentRequestDto.builder()
                    .book(bookRequestDto)
                    .text(newCommentText)
                    .build();

            CommentResponseDto createdComment = commentService.insert(request);

            assertThat(createdComment)
                    .isNotNull()
                    .satisfies(dto -> {
                        assertThat(dto.id()).isNotBlank();
                        assertThat(dto.text()).isEqualTo(newCommentText);
                    });

            var foundComment = commentRepository.findById(createdComment.id());
            assertThat(foundComment)
                    .isPresent()
                    .get()
                    .satisfies(comment -> {
                        assertThat(comment.getId()).isEqualTo(createdComment.id());
                        assertThat(comment.getText()).isEqualTo(newCommentText);
                        assertThat(comment.getBook().getId()).isEqualTo(book1Id);
                    });
        }

        @DisplayName("должен обновлять существующий комментарий")
        @Test
        void shouldUpdateComment() {
            var allComments = commentRepository.findAll();
            assertThat(allComments).isNotEmpty();

            var comment = allComments.getFirst();
            var commentId = comment.getId();
            String updatedText = "Edited Comment";

            CommentRequestDto request = CommentRequestDto.builder()
                    .id(commentId)
                    .text(updatedText)
                    .build();

            CommentResponseDto updatedComment = commentService.update(request);

            assertThat(updatedComment)
                    .isNotNull()
                    .satisfies(dto -> {
                        assertThat(dto.id()).isEqualTo(commentId);
                        assertThat(dto.text()).isEqualTo(updatedText);
                    });

            var foundComment = commentRepository.findById(commentId);
            assertThat(foundComment)
                    .isPresent()
                    .get()
                    .satisfies(c -> {
                        assertThat(c.getId()).isEqualTo(commentId);
                        assertThat(c.getText()).isEqualTo(updatedText);
                        assertThat(c.getBook().getId()).isEqualTo(comment.getBook().getId());
                    });
        }

        @DisplayName("должен удалять комментарий по id")
        @Test
        void shouldDeleteComment() {
            var allComments = commentRepository.findAll();
            assertThat(allComments).isNotEmpty();

            var comment = allComments.getFirst();
            var commentId = comment.getId();

            assertThat(commentRepository.findById(commentId)).isPresent();

            CommentRequestDto request = CommentRequestDto.builder()
                    .id(commentId)
                    .build();

            commentService.deleteById(request);

            assertThat(commentRepository.findById(commentId)).isEmpty();
        }
    }
}