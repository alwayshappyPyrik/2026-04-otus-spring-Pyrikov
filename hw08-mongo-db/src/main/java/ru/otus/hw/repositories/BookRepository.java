package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {

    @Aggregation(pipeline = {
            "{$lookup: {from: 'authors', localField: 'author', foreignField: '_id', as: 'authorData'}}",
            "{$unwind: {path: '$authorData', preserveNullAndEmptyArrays: true}}",
            "{$addFields: {author: '$authorData'}}",
            "{$project: {authorData: 0}}"
    })
    List<Book> findAllWithAuthor();

}