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

import java.util.Set;

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

    @DocumentReference(lazy = true)
    @Field(name = "author_id")
    private Author author;

    @Field(name = "genres", targetType = FieldType.ARRAY, write = Field.Write.NON_NULL)
    private Set<Genre> genres;

    public Book(String title, Author author, Set<Genre> genres) {
        this.title = title;
        this.author = author;
        this.genres = genres;
    }
}