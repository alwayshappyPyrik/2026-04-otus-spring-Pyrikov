package ru.otus.hw.mapper;

import org.mapstruct.Mapper;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.models.Genre;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreResponseDto toDto(Genre genre);
}