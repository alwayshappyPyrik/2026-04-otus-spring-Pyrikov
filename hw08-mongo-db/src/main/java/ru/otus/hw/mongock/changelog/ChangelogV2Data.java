package ru.otus.hw.mongock.changelog;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private final List<Object> addedBookIds = new ArrayList<>();

    private final List<Object> addedCommentIds = new ArrayList<>();

    @Execution
    public void migrate() {
        log.info("Executing migration: v1-initial-data");
        insertAuthors();
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

    private void insertBooks() {
        Document author1 = mongoTemplate.getCollection("authors")
                .find(new Document("full_name", "Author_1")).first();
        Document author2 = mongoTemplate.getCollection("authors")
                .find(new Document("full_name", "Author_2")).first();
        Document author3 = mongoTemplate.getCollection("authors")
                .find(new Document("full_name", "Author_3")).first();

        List<Document> books = Arrays.asList(
                createBook("BookTitle_1", author1, "Genre_1", "Genre_2"),
                createBook("BookTitle_2", author2, "Genre_3", "Genre_4"),
                createBook("BookTitle_3", author3, "Genre_5", "Genre_6")
        );

        for (Document book : books) {
            mongoTemplate.getCollection("books").insertOne(book);
            addedBookIds.add(book.get("_id"));
        }
        log.info("Added {} books", addedBookIds.size());
    }

    private Document createBook(String title, Document author, String genre1, String genre2) {
        return new Document("title", title)
                .append("author_id", author != null ? author.get("_id") : null)
                .append("genres", Arrays.asList(
                        new Document("name", genre1),
                        new Document("name", genre2)
                ));
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