package ru.otus.hw.mapper;

import org.mapstruct.Mapper;
import ru.otus.hw.dto.CommentRequestDto;
import ru.otus.hw.dto.CommentResponseDto;
import ru.otus.hw.models.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentResponseDto toDto(Comment comment);

    CommentRequestDto toRequestDto(CommentResponseDto commentResponseDto);
}