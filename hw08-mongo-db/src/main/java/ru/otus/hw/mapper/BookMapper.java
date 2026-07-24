package ru.otus.hw.mapper;

import org.mapstruct.Mapper;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.models.Book;

@Mapper(componentModel = "spring", uses = {AuthorMapper.class, GenreMapper.class})
public interface BookMapper {
    BookResponseDto toDto(Book book);
}