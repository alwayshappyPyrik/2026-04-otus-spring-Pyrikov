package ru.otus.hw.models;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Document(collection = "genres")
public class Genre {
    @Id
    private String id;

    @Field(name = "name", targetType = FieldType.STRING, write = Field.Write.NON_NULL)
    private String name;
}