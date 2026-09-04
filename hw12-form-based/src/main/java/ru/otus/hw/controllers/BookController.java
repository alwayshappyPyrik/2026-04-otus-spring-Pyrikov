package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.otus.hw.dto.BookCreateRequestDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.BookUpdateRequestDto;
import ru.otus.hw.dto.GenreResponseDto;
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
    public String findAll(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books/list";
    }

    @GetMapping("/{id}")
    public String findBookById(@PathVariable Long id, Model model) {
        BookRequestDto request = BookRequestDto.builder().id(id).build();
        model.addAttribute("book", bookService.findById(request));
        return "books/detail";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", BookCreateRequestDto.builder().build());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "books/new";
    }

    @PostMapping
    public String createBook(@Valid @ModelAttribute("book") BookCreateRequestDto createRequest,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "books/new";
        }

        bookService.insert(createRequest);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        BookRequestDto request = BookRequestDto.builder().id(id).build();
        BookResponseDto bookResponse = bookService.findById(request);

        BookUpdateRequestDto bookForm = BookUpdateRequestDto.builder()
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
    public String updateBook(@PathVariable Long id,
                             @Valid @ModelAttribute("book") BookUpdateRequestDto updateRequest,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "books/edit";
        }

        BookUpdateRequestDto request = updateRequest.toBuilder()
                .id(id)
                .build();

        BookResponseDto updatedBook = bookService.update(request);
        return "redirect:/books/" + updatedBook.id();
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable Long id) {
        BookRequestDto request = BookRequestDto.builder().id(id).build();
        bookService.deleteById(request);
        return "redirect:/books";
    }
}