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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(exclude = "book")
@ToString(exclude = "book")
@Document(collection = "comments")
public class Comment {
    @Id
    private String id;

    @Field(name = "text", targetType = FieldType.STRING, write = Field.Write.NON_NULL)
    private String text;

    @DocumentReference(lazy = true)
    @Field(name = "book_id")
    private Book book;

    public Comment(String text, Book book) {
        this.text = text;
        this.book = book;
    }
}