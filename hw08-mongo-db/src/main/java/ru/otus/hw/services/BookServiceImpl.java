package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @Override
    public Optional<BookResponseDto> findById(BookRequestDto bookRequestDto) {
        return bookRepository.findById(bookRequestDto.id())
                .map(bookMapper::toDto);
    }

    @Override
    public List<BookResponseDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookResponseDto insert(BookRequestDto bookRequestDto) {
        validateGenreIds(bookRequestDto.genreIds());

        Author author = findAuthorById(bookRequestDto.author().id());
        List<Genre> genres = findGenresByIds(bookRequestDto.genreIds());

        Book book = Book.builder()
                .title(bookRequestDto.title())
                .author(author)
                .genres(genres)
                .build();

        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public BookResponseDto update(BookRequestDto bookRequestDto) {
        validateGenreIds(bookRequestDto.genreIds());

        Book book = bookRepository.findById(bookRequestDto.id())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book with id %s not found".formatted(bookRequestDto.id())
                ));
        Author author = findAuthorById(bookRequestDto.author().id());
        List<Genre> genres = findGenresByIds(bookRequestDto.genreIds());

        book.setTitle(bookRequestDto.title());
        book.setAuthor(author);
        book.setGenres(genres);

        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void deleteById(BookRequestDto bookRequestDto) {
        bookRepository.deleteById(bookRequestDto.id());
    }

    private void validateGenreIds(Set<String> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            throw new IllegalArgumentException("Genres ids must not be null or empty");
        }
    }

    private Author findAuthorById(String authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Author with id %s not found".formatted(authorId)
                ));
    }

    private List<Genre> findGenresByIds(Set<String> genreIds) {
        List<Genre> genres = genreRepository.findAllByIds(genreIds);
        if (genres == null || genres.size() != genreIds.size()) {
            throw new EntityNotFoundException(
                    "One or all genres with ids %s not found".formatted(genreIds)
            );
        }
        return genres;
    }
}