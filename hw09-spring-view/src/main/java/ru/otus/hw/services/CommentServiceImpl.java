package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentRequestDto;
import ru.otus.hw.dto.CommentResponseDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final CommentMapper commentMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentResponseDto> findById(CommentRequestDto commentRequestDto) {
        return commentRepository.findById(commentRequestDto.id())
                .map(commentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> findAllByBookId(CommentRequestDto commentRequestDto) {
        Long bookId = commentRequestDto.book().id();

        List<Comment> comments = commentRepository.findAllByBookId(bookId);

        if (comments.isEmpty()) {
            throw new EntityNotFoundException("Comments for bookId %d not found".formatted(bookId));
        }

        return comments.stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentResponseDto insert(CommentRequestDto commentRequestDto) {
        Book book = bookRepository.findById(commentRequestDto.book().id())
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found"
                        .formatted(commentRequestDto.book().id())));

        Comment comment = Comment.builder()
                .text(commentRequestDto.text())
                .book(book)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    @Transactional
    public CommentResponseDto update(CommentRequestDto commentRequestDto) {
        var comment = commentRepository.findById(commentRequestDto.id())
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found"
                        .formatted(commentRequestDto.id())));

        comment.setText(commentRequestDto.text());

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    @Transactional
    public void deleteById(CommentRequestDto commentRequestDto) {
        commentRepository.deleteById(commentRequestDto.id());
    }
}