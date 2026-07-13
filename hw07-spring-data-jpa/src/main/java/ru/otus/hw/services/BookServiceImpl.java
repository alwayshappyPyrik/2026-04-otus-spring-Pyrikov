package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<BookResponseDto> findById(BookRequestDto bookRequestDto) {
        return bookRepository.findById(bookRequestDto.id())
                .map(bookMapper::toDto);
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
    public BookResponseDto insert(BookRequestDto bookRequestDto) {
        return saveBook(bookRequestDto.title(), bookRequestDto.author().id(), bookRequestDto.genreIds());
    }

    @Override
    @Transactional
    public BookResponseDto update(BookRequestDto bookRequestDto) {
        return updateBook(bookRequestDto.id(),
                bookRequestDto.title(),
                bookRequestDto.author().id(),
                bookRequestDto.genreIds());
    }

    @Override
    @Transactional
    public void deleteById(BookRequestDto bookRequestDto) {
        bookRepository.deleteById(bookRequestDto.id());
    }

    private BookResponseDto saveBook(String title, long authorId, Set<Long> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));

        var genres = genreRepository.findAllByIds(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        Book book = Book.builder()
                .title(title)
                .author(author)
                .genres(genres)
                .build();

        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    private BookResponseDto updateBook(long id, String title, long authorId, Set<Long> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(id)));

        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));

        var genres = genreRepository.findAllByIds(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        book.setTitle(title);
        book.setAuthor(author);
        book.setGenres(genres);

        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }
}