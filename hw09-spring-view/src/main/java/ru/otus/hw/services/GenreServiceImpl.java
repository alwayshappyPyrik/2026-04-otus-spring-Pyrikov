package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.GenresRequestDto;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.mapper.GenreMapper;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    private final GenreMapper genreMapper;

    @Override
    public List<GenreResponseDto> findAll() {
        return genreRepository.findAll().stream()
                .map(genreMapper::toDto)
                .toList();
    }

    @Override
    public List<GenreResponseDto> findAllByIds(GenresRequestDto genresRequestDto) {
        return genreRepository.findAllByIds(genresRequestDto.id()).stream()
                .map(genreMapper::toDto)
                .toList();
    }
}