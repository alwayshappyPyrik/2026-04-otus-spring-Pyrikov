package ru.otus.hw.models;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "comments")
public class Comment {

    @Id
    @Column("id")
    private Long id;

    @Column("text")
    private String text;

    @Column("book_id")
    private Long bookId;

    public Comment(String text, Long bookId) {
        this.text = text;
        this.bookId = bookId;
    }
}