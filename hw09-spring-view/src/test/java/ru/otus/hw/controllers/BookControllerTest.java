package ru.otus.hw.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.AuthorResponseDto;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Контроллер на основе Spring MVC для работы с книжками")
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    private BookResponseDto bookResponseDto;
    private BookRequestDto bookRequestDto;
    private AuthorResponseDto authorResponseDto;
    private GenreResponseDto genreResponseDto1;
    private GenreResponseDto genreResponseDto2;
    private List<BookResponseDto> bookList;
    private List<AuthorResponseDto> authorList;
    private List<GenreResponseDto> genreList;

    @BeforeEach
    void setUp() {
        authorResponseDto = AuthorResponseDto.builder()
                .id(1L)
                .fullName("Test Author")
                .build();

        genreResponseDto1 = GenreResponseDto.builder()
                .id(1L)
                .name("Test Genre 1")
                .build();

        genreResponseDto2 = GenreResponseDto.builder()
                .id(2L)
                .name("Test Genre 2")
                .build();

        bookResponseDto = BookResponseDto.builder()
                .id(1L)
                .title("Test Book")
                .author(authorResponseDto)
                .genres(Set.of(genreResponseDto1, genreResponseDto2))
                .build();

        bookRequestDto = BookRequestDto.builder()
                .id(1L)
                .title("Test Book")
                .authorId(1L)
                .genreIds(Set.of(1L, 2L))
                .build();

        bookList = List.of(bookResponseDto);

        authorList = List.of(authorResponseDto);

        genreList = List.of(genreResponseDto1, genreResponseDto2);
    }

    @Test
    @DisplayName(" должен возвращать список всех книг")
    void findAllBooks_ShouldReturnBookListPage() throws Exception {
        when(bookService.findAll()).thenReturn(bookList);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/list"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", hasSize(1)))
                .andExpect(result -> {
                    @SuppressWarnings("unchecked")
                    List<BookResponseDto> books = (List<BookResponseDto>) Objects.requireNonNull(result.getModelAndView()).getModel().get("books");
                    assertThat(books)
                            .first()
                            .satisfies(book -> {
                                assertThat(book.id()).isEqualTo(1L);
                                assertThat(book.title()).isEqualTo("Test Book");

                                assertThat(book.author()).isNotNull();
                                assertThat(book.author().fullName()).isEqualTo("Test Author");

                                assertThat(book.genres()).isNotNull();
                                assertThat(book.genres()).hasSize(2);
                                assertThat(book.genres())
                                        .extracting(GenreResponseDto::name)
                                        .containsExactlyInAnyOrder("Test Genre 1", "Test Genre 2");
                            });
                });

        verify(bookService, times(1)).findAll();
    }

    @Test
    @DisplayName(" должен возвращать книгу по id")
    void findBookById_ShouldReturnBookDetailPage() throws Exception {
        when(bookService.findById(any(BookRequestDto.class))).thenReturn(Optional.of(bookResponseDto));

        mockMvc.perform(get("/books/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("books/detail"))
                .andExpect(model().attributeExists("book"))
                .andExpect(result -> {
                    BookResponseDto book = (BookResponseDto) Objects.requireNonNull(result.getModelAndView()).getModel().get("book");
                    assertThat(book)
                            .satisfies(b -> {
                                assertThat(b.id()).isEqualTo(1L);
                                assertThat(b.title()).isEqualTo("Test Book");

                                assertThat(b.author()).isNotNull();
                                assertThat(b.author().fullName()).isEqualTo("Test Author");

                                assertThat(b.genres()).isNotNull();
                                assertThat(b.genres()).hasSize(2);
                                assertThat(b.genres())
                                        .extracting(GenreResponseDto::name)
                                        .containsExactlyInAnyOrder("Test Genre 1", "Test Genre 2");
                            });
                });

        verify(bookService, times(1)).findById(any(BookRequestDto.class));
    }

    @Test
    @DisplayName(" должен создавать книгу и редиректить на книги")
    void createBook_ShouldCreateBookAndRedirect() throws Exception {
        when(bookService.insert(any(BookRequestDto.class))).thenReturn(bookResponseDto);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Test Book")
                        .param("fullName", "Test Author")
                        .param("genreName1", "Test Genre 1")
                        .param("genreName2", "Test Genre 2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService, times(1)).insert(any(BookRequestDto.class));
    }

    @Test
    @DisplayName(" должен показывать созданные книги с авторами и жанрами")
    void showCreateForm_ShouldReturnCreateFormWithData() throws Exception {
        when(authorService.findAll()).thenReturn(authorList);
        when(genreService.findAll()).thenReturn(genreList);

        mockMvc.perform(get("/books/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/create"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"))
                .andExpect(model().attribute("authors", hasSize(1)))
                .andExpect(model().attribute("genres", hasSize(2)))
                .andExpect(result -> {
                    ModelAndView mav = result.getModelAndView();

                    Assertions.assertNotNull(mav);
                    BookRequestDto book = (BookRequestDto) mav.getModel().get("book");
                    assertThat(book).isNotNull();
                    assertThat(book.id()).isNull();
                    assertThat(book.title()).isNull();
                    assertThat(book.authorId()).isNull();
                    assertThat(book.genreIds()).isNull();

                    @SuppressWarnings("unchecked")
                    List<AuthorResponseDto> authors = (List<AuthorResponseDto>) mav.getModel().get("authors");
                    assertThat(authors)
                            .hasSize(1)
                            .extracting(AuthorResponseDto::fullName)
                            .containsExactly("Test Author");

                    @SuppressWarnings("unchecked")
                    List<GenreResponseDto> genres = (List<GenreResponseDto>) mav.getModel().get("genres");
                    assertThat(genres)
                            .hasSize(2)
                            .extracting(GenreResponseDto::name)
                            .containsExactlyInAnyOrder("Test Genre 1", "Test Genre 2");
                });

        verify(authorService, times(1)).findAll();
        verify(genreService, times(1)).findAll();
    }

    @Test
    @DisplayName("Должен показывать форму редактирования с книгами, авторами и жанрами")
    void showEditForm_ShouldReturnEditFormWithData() throws Exception {
        when(bookService.findById(any(BookRequestDto.class))).thenReturn(Optional.of(bookResponseDto));
        when(authorService.findAll()).thenReturn(authorList);
        when(genreService.findAll()).thenReturn(genreList);

        mockMvc.perform(get("/books/{id}/edit", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("books/edit"))
                .andExpect(model().attributeExists("book", "authors", "genres"))
                .andExpect(model().attribute("authors", hasSize(1)))
                .andExpect(model().attribute("genres", hasSize(2)))
                .andExpect(result -> {
                    ModelAndView mav = result.getModelAndView();

                    Assertions.assertNotNull(mav);
                    BookRequestDto book = (BookRequestDto) mav.getModel().get("book");
                    assertThat(book)
                            .satisfies(b -> {
                                assertThat(b.id()).isEqualTo(1L);
                                assertThat(b.title()).isEqualTo("Test Book");
                            });

                    @SuppressWarnings("unchecked")
                    List<AuthorResponseDto> authors = (List<AuthorResponseDto>) mav.getModel().get("authors");
                    assertThat(authors)
                            .hasSize(1)
                            .first()
                            .satisfies(author -> {
                                assertThat(author.fullName()).isEqualTo("Test Author");
                            });

                    @SuppressWarnings("unchecked")
                    List<GenreResponseDto> genres = (List<GenreResponseDto>) mav.getModel().get("genres");
                    assertThat(genres)
                            .hasSize(2)
                            .extracting(GenreResponseDto::name)
                            .containsExactlyInAnyOrder("Test Genre 1", "Test Genre 2");
                });

        verify(bookService, times(1)).findById(any(BookRequestDto.class));
        verify(authorService, times(1)).findAll();
        verify(genreService, times(1)).findAll();
    }

    @Test
    @DisplayName("Должен обновить книгу и перенаправить на список книг")
    void updateBook_ShouldRedirectToBooksList() throws Exception {
        when(bookService.update(any(BookRequestDto.class))).thenReturn(bookResponseDto);

        mockMvc.perform(put("/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Test Book")
                        .param("authorId", "1")
                        .param("genreIds", "1")
                        .param("genreIds", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        ArgumentCaptor<BookRequestDto> captor = ArgumentCaptor.forClass(BookRequestDto.class);
        verify(bookService, times(1)).update(captor.capture());

        BookRequestDto captured = captor.getValue();
        assertThat(captured)
                .satisfies(book -> {
                    assertThat(book.id()).isEqualTo(1L);
                    assertThat(book.title()).isEqualTo("Test Book");
                    assertThat(book.authorId()).isEqualTo(1L);
                    assertThat(book.genreIds()).containsExactlyInAnyOrder(1L, 2L);
                });

        assertThat(authorResponseDto.fullName()).isEqualTo("Test Author");
        assertThat(genreResponseDto1.name()).isEqualTo("Test Genre 1");
        assertThat(genreResponseDto2.name()).isEqualTo("Test Genre 2");
    }

    @Test
    @DisplayName("Должен удалить книгу и перенаправить на список книг")
    void deleteBook_ShouldRedirectToBooksList() throws Exception {
        doNothing().when(bookService).deleteById(any(BookRequestDto.class));

        mockMvc.perform(delete("/books/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService, times(1)).deleteById(any(BookRequestDto.class));
    }
}