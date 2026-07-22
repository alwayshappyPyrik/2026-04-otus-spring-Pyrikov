package ru.otus.hw.mongock.changelog;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@ChangeUnit(
        id = "v1-initial-data",
        order = "002",
        author = "alwayshappypyrik",
        systemVersion = "1.0"
)
@RequiredArgsConstructor
@Slf4j
public class ChangelogV2Data {

    private final MongoTemplate mongoTemplate;

    private final List<Object> addedAuthorIds = new ArrayList<>();

    private final List<Object> addedGenreIds = new ArrayList<>();

    private final List<Object> addedBookIds = new ArrayList<>();

    private final List<Object> addedCommentIds = new ArrayList<>();

    @Execution
    public void migrate() {
        log.info("Executing migration: v1-initial-data");
        insertAuthors();
        insertGenres();
        insertBooks();
        insertComments();
        log.info("Migration v1-initial-data completed!");
    }

    private void insertAuthors() {
        List<Document> authors = Arrays.asList(
                new Document("full_name", "Author_1"),
                new Document("full_name", "Author_2"),
                new Document("full_name", "Author_3")
        );

        for (Document author : authors) {
            mongoTemplate.getCollection("authors").insertOne(author);
            addedAuthorIds.add(author.get("_id"));
        }
        log.info("Added {} authors", addedAuthorIds.size());
    }

    private void insertGenres() {
        List<Document> genres = Arrays.asList(
                new Document("name", "Genre_1"),
                new Document("name", "Genre_2"),
                new Document("name", "Genre_3"),
                new Document("name", "Genre_4"),
                new Document("name", "Genre_5"),
                new Document("name", "Genre_6")
        );

        for (Document genre : genres) {
            mongoTemplate.getCollection("genres").insertOne(genre);
            addedGenreIds.add(genre.get("_id"));
        }
        log.info("Added {} genres", addedGenreIds.size());
    }

    private void insertBooks() {
        Map<String, Document> authors = getAuthorsMap();
        Map<String, Document> genres = getGenresMap();

        List<Document> books = createBooks(authors, genres);
        saveBooks(books);
    }

    private Map<String, Document> getAuthorsMap() {
        Map<String, Document> authorsMap = new HashMap<>();
        for (Document author : mongoTemplate.getCollection("authors").find()) {
            authorsMap.put(author.getString("full_name"), author);
        }
        return authorsMap;
    }

    private Map<String, Document> getGenresMap() {
        Map<String, Document> genresMap = new HashMap<>();
        for (Document genre : mongoTemplate.getCollection("genres").find()) {
            genresMap.put(genre.getString("name"), genre);
        }
        return genresMap;
    }

    private List<Document> createBooks(Map<String, Document> authors, Map<String, Document> genres) {
        return Arrays.asList(
                createBook("BookTitle_1", authors.get("Author_1"),
                        List.of(genres.get("Genre_1"), genres.get("Genre_2"))),
                createBook("BookTitle_2", authors.get("Author_2"),
                        List.of(genres.get("Genre_3"), genres.get("Genre_4"))),
                createBook("BookTitle_3", authors.get("Author_3"),
                        List.of(genres.get("Genre_5"), genres.get("Genre_6")))
        );
    }

    private void saveBooks(List<Document> books) {
        for (Document book : books) {
            mongoTemplate.getCollection("books").insertOne(book);
            addedBookIds.add(book.get("_id"));
        }
        log.info("Added {} books", addedBookIds.size());
    }

    private Document createBook(String title, Document author, List<Document> genres) {
        List<Object> genreIds = new ArrayList<>();
        for (Document genre : genres) {
            if (genre != null) {
                genreIds.add(genre.get("_id"));
            }
        }

        return new Document("title", title)
                .append("author_id", author != null ? author.get("_id") : null)
                .append("genre_ids", genreIds);  // ← Массив ID жанров (как в @DocumentReference)
    }

    private void insertComments() {
        Document book1 = mongoTemplate.getCollection("books")
                .find(new Document("title", "BookTitle_1")).first();
        Document book2 = mongoTemplate.getCollection("books")
                .find(new Document("title", "BookTitle_2")).first();
        Document book3 = mongoTemplate.getCollection("books")
                .find(new Document("title", "BookTitle_3")).first();

        List<Document> comments = Arrays.asList(
                createComment("Book 1 is interesting and exciting", book1),
                createComment("I didn't like Book 2", book2),
                createComment("Book 3 is really bad, I wouldn't recommend it to anyone", book3)
        );

        for (Document comment : comments) {
            mongoTemplate.getCollection("comments").insertOne(comment);
            addedCommentIds.add(comment.get("_id"));
        }
        log.info("Added {} comments", addedCommentIds.size());
    }

    private Document createComment(String text, Document book) {
        return new Document("text", text)
                .append("book_id", book != null ? book.get("_id") : null);
    }

    @RollbackExecution
    public void rollback() {
        log.info("Rolling back migration: v1-initial-data");
        deleteComments();
        deleteBooks();
        deleteGenres();
        deleteAuthors();
        log.info("Rollback v1-initial-data completed!");
    }

    private void deleteComments() {
        if (!addedCommentIds.isEmpty()) {
            for (Object id : addedCommentIds) {
                mongoTemplate.getCollection("comments")
                        .deleteOne(new Document("_id", id));
            }
            log.info("Removed {} comments", addedCommentIds.size());
        }
    }

    private void deleteBooks() {
        if (!addedBookIds.isEmpty()) {
            for (Object id : addedBookIds) {
                mongoTemplate.getCollection("books")
                        .deleteOne(new Document("_id", id));
            }
            log.info("Removed {} books", addedBookIds.size());
        }
    }

    private void deleteGenres() {
        if (!addedGenreIds.isEmpty()) {
            for (Object id : addedGenreIds) {
                mongoTemplate.getCollection("genres")
                        .deleteOne(new Document("_id", id));
            }
            log.info("Removed {} genres", addedGenreIds.size());
        }
    }

    private void deleteAuthors() {
        if (!addedAuthorIds.isEmpty()) {
            for (Object id : addedAuthorIds) {
                mongoTemplate.getCollection("authors")
                        .deleteOne(new Document("_id", id));
            }
            log.info("Removed {} authors", addedAuthorIds.size());
        }
    }
}