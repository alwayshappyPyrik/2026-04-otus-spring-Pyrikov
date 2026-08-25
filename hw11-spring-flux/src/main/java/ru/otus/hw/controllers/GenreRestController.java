package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.dto.GenresRequestDto;
import ru.otus.hw.services.GenreService;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
public class GenreRestController {

    private final GenreService genreService;

    @GetMapping
    public Flux<GenreResponseDto> findAllGenres() {
        return genreService.findAll();
    }

    @GetMapping("/search")
    public Flux<GenreResponseDto> findGenresByIds(@RequestParam(required = false) String ids) {
        if (ids == null || ids.trim().isEmpty()) {
            return genreService.findAll();
        }

        Set<Long> idSet = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toSet());

        if (idSet.isEmpty()) {
            return genreService.findAll();
        }

        GenresRequestDto genresRequestDto = GenresRequestDto.builder()
                .id(idSet)
                .build();

        return genreService.findAllByIds(genresRequestDto);
    }
}
