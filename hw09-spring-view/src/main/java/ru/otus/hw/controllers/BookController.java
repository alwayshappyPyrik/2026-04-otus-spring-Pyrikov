package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.GenreService;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping
    public String findAllBooks(Model model) {
        var books = bookService.findAll();
        model.addAttribute("books", books);
        return "books/list";
    }

    @GetMapping("/{id}")
    public String findBookById(@PathVariable long id, Model model) {
        BookRequestDto bookRequestDto = BookRequestDto.builder()
                .id(id)
                .build();

        var book = bookService.findById(bookRequestDto)
                .orElseThrow(() -> new NotFoundException("Book with id " + id + " not found"));
        model.addAttribute("book", book);
        return "books/detail";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", BookRequestDto.builder().build());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "books/create";
    }

    @PostMapping
    public String createBook(@ModelAttribute BookRequestDto bookRequestDto) {
        bookService.insert(bookRequestDto);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable long id, Model model) {
        var bookRequestDto = BookRequestDto.builder()
                .id(id)
                .build();

        var bookResponse = bookService.findById(bookRequestDto)
                .orElseThrow(() -> new NotFoundException("Book with id " + id + " not found"));

        var bookForm = BookRequestDto.builder()
                .id(bookResponse.id())
                .title(bookResponse.title())
                .authorId(bookResponse.author().id())
                .genreIds(bookResponse.genres().stream()
                        .map(GenreResponseDto::id)
                        .collect(Collectors.toSet()))
                .build();

        model.addAttribute("book", bookForm);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "books/edit";
    }

    @PutMapping("/{id}")
    public String updateBook(@PathVariable long id,
                             @ModelAttribute BookRequestDto bookRequestDto) {

        var book = BookRequestDto.builder()
                .id(id)
                .title(bookRequestDto.title())
                .authorId(bookRequestDto.authorId())
                .genreIds(bookRequestDto.genreIds())
                .build();

        bookService.update(book);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable long id) {
        var bookRequestDto = BookRequestDto.builder()
                .id(id)
                .build();

        bookService.deleteById(bookRequestDto);
        return "redirect:/books";
    }
}