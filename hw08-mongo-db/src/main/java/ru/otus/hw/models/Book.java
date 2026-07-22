package ru.otus.hw.models;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(exclude = {"author", "genres"})
@ToString(exclude = {"author", "genres"})
@Document(collection = "books")
public class Book {
    @Id
    private String id;

    @Field(name = "title", targetType = FieldType.STRING, write = Field.Write.NON_NULL)
    private String title;

    @DocumentReference(lazy = true, collection = "authors")
    @Field(name = "author_id")
    private Author author;

    @DocumentReference(lazy = true, collection = "genres")
    @Field(name = "genre_ids")
    private List<Genre> genres;

    public Book(String title, Author author, List<Genre> genres) {
        this.title = title;
        this.author = author;
        this.genres = genres;
    }
}