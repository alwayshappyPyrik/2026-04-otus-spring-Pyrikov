package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RequiredArgsConstructor
@ShellComponent
public class GenreCommands {

    private final MongoTemplate mongoTemplate;

    @ShellMethod(value = "Find all unique genres from books", key = "ag")
    public String findAllGenres() {
        List<String> genreNames = mongoTemplate.query(Book.class)
                .distinct("genres.name")
                .as(String.class)
                .all();

        if (genreNames.isEmpty()) {
            return "No genres found";
        }

        return genreNames.stream()
                .sorted()
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find books by genre", key = "gbb")
    public String findBooksByGenre(String genreName) {
        Query query = new Query(Criteria.where("genres.name").is(genreName));
        List<Book> books = mongoTemplate.find(query, Book.class);

        if (books.isEmpty()) {
            return "No books found with genre: " + genreName;
        }

        return books.stream()
                .map(Book::getTitle)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }
}