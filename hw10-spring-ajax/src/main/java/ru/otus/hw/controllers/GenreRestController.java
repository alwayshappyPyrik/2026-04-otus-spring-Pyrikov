package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.dto.GenresRequestDto;
import ru.otus.hw.services.GenreService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
public class GenreRestController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<List<GenreResponseDto>> findAllGenres() {
        List<GenreResponseDto> genres = genreService.findAll();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/search")
    public ResponseEntity<List<GenreResponseDto>> findGenresByIds(@RequestParam(required = false) String ids) {
        if (ids == null || ids.trim().isEmpty()) {
            return ResponseEntity.ok(genreService.findAll());
        }

        Set<Long> idSet = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toSet());

        if (idSet.isEmpty()) {
            return ResponseEntity.ok(genreService.findAll());
        }

        GenresRequestDto genresRequestDto = GenresRequestDto.builder()
                .id(idSet)
                .build();

        List<GenreResponseDto> genres = genreService.findAllByIds(genresRequestDto);
        return ResponseEntity.ok(genres);
    }
}
