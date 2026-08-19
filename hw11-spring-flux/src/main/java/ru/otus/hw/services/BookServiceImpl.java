package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookCreateRequestDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.BookUpdateRequestDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.GenreEmbedded;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    public Flux<BookResponseDto> findAll() {
        return bookRepository.findAll()
                .map(BookResponseDto::fromBook);
    }

    public Mono<BookResponseDto> findById(BookRequestDto request) {
        return bookRepository.findById(request.id())
                .switchIfEmpty(Mono.error(new RuntimeException("Book not found with id: " + request.id())))
                .map(BookResponseDto::fromBook);
    }

    public Mono<BookResponseDto> insert(BookCreateRequestDto request) {
        return Mono.zip(
                getAuthor(request.authorId()),
                getGenres(request.genreIds())
        ).flatMap(tuple -> {
            Book book = buildBook(request.title(), tuple.getT1(), tuple.getT2());
            return bookRepository.save(book);
        }).map(BookResponseDto::fromBook);
    }

    public Mono<BookResponseDto> update(BookUpdateRequestDto request) {
        return bookRepository.findById(request.id())
                .switchIfEmpty(Mono.error(new RuntimeException("Book not found with id: " + request.id())))
                .flatMap(book -> updateBook(book, request))
                .flatMap(bookRepository::save)
                .map(BookResponseDto::fromBook);
    }

    public Mono<Void> deleteById(BookRequestDto request) {
        return bookRepository.deleteById(request.id());
    }

    private Mono<Author> getAuthor(Long authorId) {
        return authorRepository.findById(authorId)
                .switchIfEmpty(Mono.error(new RuntimeException("Author not found with id: " + authorId)));
    }

    private Mono<List<Genre>> getGenres(Set<Long> genreIds) {
        return genreRepository.findAllById(genreIds)
                .collectList()
                .flatMap(genres -> {
                    if (genres.size() != genreIds.size()) {
                        return Mono.error(new RuntimeException("Some genres not found"));
                    }
                    return Mono.just(genres);
                });
    }

    private Book buildBook(String title, Author author,
                           List<Genre> genres) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthorId(author.getId());
        book.setAuthorFullName(author.getFullName());
        book.setGenres(genres.stream()
                .map(g -> new GenreEmbedded(g.getId(), g.getName()))
                .collect(Collectors.toSet()));
        return book;
    }

    private Mono<Book> updateBook(Book book, BookUpdateRequestDto request) {
        book.setTitle(request.title());

        return updateAuthor(book, request.authorId())
                .flatMap(b -> updateGenres(b, request.genreIds()));
    }

    private Mono<Book> updateAuthor(Book book, Long newAuthorId) {
        if (!book.getAuthorId().equals(newAuthorId)) {
            return getAuthor(newAuthorId)
                    .map(author -> {
                        book.setAuthorId(author.getId());
                        book.setAuthorFullName(author.getFullName());
                        return book;
                    });
        }
        return Mono.just(book);
    }

    private Mono<Book> updateGenres(Book book, Set<Long> newGenreIds) {
        return getGenres(newGenreIds)
                .map(genres -> {
                    book.setGenres(genres.stream()
                            .map(g -> new GenreEmbedded(g.getId(), g.getName()))
                            .collect(Collectors.toSet()));
                    return book;
                });
    }
}