package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenresRequestDto;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.mapper.GenreMapper;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    private final GenreMapper genreMapper;

    @Override
    public Flux<GenreResponseDto> findAll() {
        return genreRepository.findAll()
                .map(genreMapper::toDto);
    }

    @Override
    public Flux<GenreResponseDto> findAllByIds(GenresRequestDto genresRequestDto) {
        return genreRepository.findAllByIds(genresRequestDto.id())
                .map(genreMapper::toDto);
    }
}