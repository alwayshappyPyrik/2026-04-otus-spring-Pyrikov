package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.otus.hw.dto.GenresRequestDto;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.services.GenreService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public String findAllGenres(Model model) {
        List<GenreResponseDto> genres = genreService.findAll();
        model.addAttribute("genres", genres);
        return "genres/list";
    }

    @GetMapping("/search")
    public String findGenresByIds(String ids, Model model) {
        if (ids == null || ids.trim().isEmpty()) {
            return "redirect:/genres";
        }

        Set<Long> idSet = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toSet());

        if (idSet.isEmpty()) {
            return "redirect:/genres";
        }

        GenresRequestDto genresRequestDto = GenresRequestDto.builder()
                .id(idSet)
                .build();

        List<GenreResponseDto> genres = genreService.findAllByIds(genresRequestDto);
        model.addAttribute("genres", genres);
        model.addAttribute("searchIds", ids);
        return "genres/list";
    }
}