package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorResponseDto;
import ru.otus.hw.dto.BookCreateRequestDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.BookUpdateRequestDto;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookGenre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookGenreRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookGenreRepository bookGenreRepository;

    @Override
    @Transactional(readOnly = true)
    public Mono<BookResponseDto> findById(BookRequestDto bookRequestDto) {
        return bookRepository.findById(bookRequestDto.id())
                .switchIfEmpty(Mono.error(new NotFoundException("Book not found with id: " + bookRequestDto.id())))
                .flatMap(this::buildBookResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<BookResponseDto> findAll() {
        return bookRepository.findAll()
                .flatMap(this::buildBookResponse);
    }

    @Transactional
    public Mono<BookResponseDto> insert(BookCreateRequestDto createRequest) {
        return validateAuthorAndGenres(createRequest.authorId(), createRequest.genreIds())
                .then(Mono.defer(() -> {
                    Book book = Book.builder()
                            .title(createRequest.title())
                            .authorId(createRequest.authorId())
                            .build();

                    return bookRepository.save(book)
                            .flatMap(savedBook -> {
                                Set<BookGenre> bookGenres = createRequest.genreIds().stream()
                                        .map(genreId -> BookGenre.builder()
                                                .bookId(savedBook.getId())
                                                .genreId(genreId)
                                                .build())
                                        .collect(Collectors.toSet());

                                return bookGenreRepository.saveAll(bookGenres)
                                        .collectList()
                                        .thenReturn(savedBook);
                            })
                            .flatMap(this::buildBookResponse);
                }));
    }

    @Transactional
    public Mono<BookResponseDto> update(BookUpdateRequestDto updateRequest) {
        return validateAuthorAndGenres(updateRequest.authorId(), updateRequest.genreIds())
                .then(Mono.defer(() ->
                        bookRepository.findById(updateRequest.id())
                                .switchIfEmpty(Mono.error(
                                        new NotFoundException("Book not found with id: " + updateRequest.id())))
                                .flatMap(book -> {
                                    book.setTitle(updateRequest.title());
                                    book.setAuthorId(updateRequest.authorId());
                                    return bookRepository.save(book);
                                })
                                .flatMap(updatedBook -> bookGenreRepository.deleteByBookId(updatedBook.getId())
                                        .thenMany(Flux.fromIterable(updateRequest.genreIds())
                                                .map(genreId -> BookGenre.builder()
                                                        .bookId(updatedBook.getId())
                                                        .genreId(genreId)
                                                        .build()))
                                        .collectList()
                                        .flatMap(bookGenres -> bookGenreRepository.saveAll(bookGenres).collectList())
                                        .thenReturn(updatedBook))
                                .flatMap(this::buildBookResponse)
                ));
    }

    @Transactional
    public Mono<Void> deleteById(BookRequestDto request) {
        return bookRepository.existsById(request.id())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new NotFoundException("Book not found with id: " + request.id()));
                    }
                    return bookGenreRepository.deleteByBookId(request.id())
                            .then(bookRepository.deleteById(request.id()));
                });
    }

    private Mono<BookResponseDto> buildBookResponse(Book book) {
        Mono<AuthorResponseDto> authorMono = authorRepository.findById(book.getAuthorId())
                .switchIfEmpty(Mono.error(new NotFoundException("Author not found with id: " + book.getAuthorId())))
                .map(author -> new AuthorResponseDto(author.getId(), author.getFullName()));

        Mono<Set<GenreResponseDto>> genresMono = bookGenreRepository.findByBookId(book.getId())
                .flatMap(bookGenre -> genreRepository.findById(bookGenre.getGenreId())
                        .switchIfEmpty(Mono.error(
                                new NotFoundException("Genre not found with id: " + bookGenre.getGenreId()))))
                .map(genre -> new GenreResponseDto(genre.getId(), genre.getName()))
                .collect(Collectors.toSet());

        return Mono.zip(authorMono, genresMono)
                .map(tuple -> BookResponseDto.builder()
                        .id(book.getId())
                        .title(book.getTitle())
                        .author(tuple.getT1())
                        .genres(tuple.getT2())
                        .build());
    }

    private Mono<Void> validateAuthorAndGenres(Long authorId, Set<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Genres ids must not be null or empty"));
        }

        return authorRepository.findById(authorId)
                .switchIfEmpty(Mono.error(new NotFoundException("Author not found with id: " + authorId)))
                .thenMany(Flux.fromIterable(genreIds)
                        .flatMap(genreId -> genreRepository.findById(genreId)
                                .switchIfEmpty(Mono.error(
                                        new NotFoundException("Genre not found with id: " + genreId)))))
                .then();
    }
}