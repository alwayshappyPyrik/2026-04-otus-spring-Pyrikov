package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.CommentRequestDto;
import ru.otus.hw.dto.CommentResponseDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public Mono<CommentResponseDto> findById(CommentRequestDto commentRequestDto) {
        Long id = commentRequestDto.id();

        return commentRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Comment with id " + id + " not found")))
                .flatMap(this::buildCommentResponse);
    }

    @Transactional(readOnly = true)
    public Flux<CommentResponseDto> findAllByBookId(CommentRequestDto commentRequestDto) {
        Long bookId = commentRequestDto.book().id();

        return commentRepository.findAllByBookId(bookId)
                .flatMap(this::buildCommentResponse)
                .switchIfEmpty(Flux.error(new NotFoundException("Comments for bookId " + bookId + " not found")));
    }

    @Transactional
    public Mono<CommentResponseDto> insert(CommentRequestDto commentRequestDto) {
        Long bookId = commentRequestDto.book().id();

        return bookRepository.findById(bookId)
                .switchIfEmpty(Mono.error(new NotFoundException("Book with id " + bookId + " not found")))
                .flatMap(book -> {
                    Comment comment = Comment.builder()
                            .text(commentRequestDto.text())
                            .bookId(book.getId())
                            .build();

                    return commentRepository.save(comment)
                            .flatMap(this::buildCommentResponse);
                });
    }

    @Transactional
    public Mono<CommentResponseDto> update(CommentRequestDto commentRequestDto) {
        Long id = commentRequestDto.id();
        Long bookId = commentRequestDto.book().id();

        return bookRepository.findById(bookId)
                .switchIfEmpty(Mono.error(new NotFoundException("Book with id " + bookId + " not found")))
                .flatMap(book ->
                        commentRepository.findById(id)
                                .switchIfEmpty(Mono.error(new NotFoundException("Comment with id " + id + " not found")))
                                .flatMap(comment -> {
                                    comment.setText(commentRequestDto.text());
                                    comment.setBookId(book.getId());
                                    return commentRepository.save(comment);
                                })
                                .flatMap(this::buildCommentResponse)
                );
    }

    @Transactional
    public Mono<Void> deleteById(CommentRequestDto commentRequestDto) {
        Long id = commentRequestDto.id();

        return commentRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new NotFoundException("Comment with id " + id + " not found"));
                    }
                    return commentRepository.deleteById(id);
                });
    }

    private Mono<CommentResponseDto> buildCommentResponse(Comment comment) {
        return bookRepository.findById(comment.getBookId())
                .switchIfEmpty(Mono.error(new NotFoundException("Book not found with id: " + comment.getBookId())))
                .map(book -> CommentResponseDto.builder()
                        .id(comment.getId())
                        .text(comment.getText())
                        .book(BookResponseDto.builder()
                                .id(book.getId())
                                .title(book.getTitle())
                                .build())
                        .build());
    }
}