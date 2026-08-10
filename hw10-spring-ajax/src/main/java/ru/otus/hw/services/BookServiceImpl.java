package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookCreateRequestDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.BookUpdateRequestDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @Override
    @Transactional(readOnly = true)
    public BookResponseDto findById(BookRequestDto bookRequestDto) {
        Long id = bookRequestDto.id();

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book with id " + id + " not found"));

        return bookMapper.toDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public BookResponseDto insert(BookCreateRequestDto bookCreateRequestDto) {
        validateGenreIds(bookCreateRequestDto.genreIds());

        Author author = findAuthorById(bookCreateRequestDto.authorId());
        List<Genre> genres = findGenresByIds(bookCreateRequestDto.genreIds());
        Book book = Book.builder()
                .title(bookCreateRequestDto.title())
                .author(author)
                .genres(genres)
                .build();

        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    public BookResponseDto update(BookUpdateRequestDto bookUpdateRequestDto) {
        validateGenreIds(bookUpdateRequestDto.genreIds());

        Book book = bookRepository.findById(bookUpdateRequestDto.id())
                .orElseThrow(() -> new NotFoundException(
                        "Book with id %d not found".formatted(bookUpdateRequestDto.id())
                ));
        Author author = findAuthorById(bookUpdateRequestDto.authorId());
        List<Genre> genres = findGenresByIds(bookUpdateRequestDto.genreIds());

        book.setTitle(bookUpdateRequestDto.title());
        book.setAuthor(author);
        book.setGenres(genres);

        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void deleteById(BookRequestDto bookRequestDto) {
        bookRepository.deleteById(bookRequestDto.id());
    }

    private void validateGenreIds(Set<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            throw new IllegalArgumentException("Genres ids must not be null or empty");
        }
    }

    private Author findAuthorById(Long authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException(
                        "Author with id %d not found".formatted(authorId)
                ));
    }

    private List<Genre> findGenresByIds(Set<Long> genreIds) {
        List<Genre> genres = genreRepository.findAllByIds(genreIds);
        if (genres == null || genres.size() != genreIds.size()) {
            throw new NotFoundException(
                    "One or all genres with ids %s not found".formatted(genreIds)
            );
        }
        return genres;
    }
}