package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.dto.GenreRequestDto;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.services.GenreService;

import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RequiredArgsConstructor
@ShellComponent
public class GenreCommands {

    private final GenreService genreService;

    @ShellMethod(value = "Find all genres", key = "ag")
    public String findAllGenres() {
        return genreService.findAll().stream()
                .map(GenreResponseDto::toString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find all genres by ids (comma-separated, e.g.: gbids 1,3,4)", key = "gbids")
    public String findAllByIds(Set<String> ids) {
        GenreRequestDto genreRequestDto = GenreRequestDto.builder()
                .id(ids)
                .build();

        return genreService.findAllByIds(genreRequestDto).stream()
                .map(GenreResponseDto::toString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }
}