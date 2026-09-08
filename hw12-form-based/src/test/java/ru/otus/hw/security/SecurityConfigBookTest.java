package ru.otus.hw.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.BookController;
import ru.otus.hw.dto.AuthorResponseDto;
import ru.otus.hw.dto.BookCreateRequestDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.BookUpdateRequestDto;
import ru.otus.hw.dto.GenreResponseDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CustomUserDetailsServiceImpl;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import(SecurityConfig.class)
@DisplayName("Тесты безопасности BookController")
public class SecurityConfigBookTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private CustomUserDetailsServiceImpl customUserDetailsService;

    @Test
    @DisplayName("GET /books - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenAccessingBooksList() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("GET /books/{id} - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenAccessingBookById() throws Exception {
        mockMvc.perform(get("/books/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("GET /books/new - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenAccessingCreateBookForm() throws Exception {
        mockMvc.perform(get("/books/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("POST /books - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenCreatingBook() throws Exception {
        mockMvc.perform(post("/books")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("GET /books/{id}/edit - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenAccessingEditBookForm() throws Exception {
        mockMvc.perform(get("/books/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("PUT /books/{id} - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenUpdatingBook() throws Exception {
        mockMvc.perform(put("/books/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("DELETE /books/{id} - должен перенаправлять на страницу логина")
    public void shouldRedirectToLoginWhenDeletingBook() throws Exception {
        mockMvc.perform(delete("/books/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("GET /books - авторизованный пользователь должен получить доступ к списку книг")
    public void shouldAllowAccessToBooksListWhenAuthenticated() throws Exception {
        AuthorResponseDto author1 = new AuthorResponseDto(1L, "Author 1");
        AuthorResponseDto author2 = new AuthorResponseDto(2L, "Author 2");

        GenreResponseDto genre1 = new GenreResponseDto(1L, "Genre 1");
        GenreResponseDto genre2 = new GenreResponseDto(2L, "Genre 2");

        BookResponseDto book1 = BookResponseDto.builder()
                .id(1L)
                .title("Book 1")
                .author(author1)
                .genres(Set.of(genre1))
                .build();

        BookResponseDto book2 = BookResponseDto.builder()
                .id(2L)
                .title("Book 2")
                .author(author2)
                .genres(Set.of(genre2))
                .build();

        List<BookResponseDto> books = List.of(book1, book2);

        when(bookService.findAll()).thenReturn(books);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("GET /books/{id} - авторизованный пользователь должен получить доступ к книге по ID")
    public void shouldAllowAccessToBookByIdWhenAuthenticated() throws Exception {
        AuthorResponseDto author = new AuthorResponseDto(1L, "Test Author");
        GenreResponseDto genre = new GenreResponseDto(1L, "Test Genre");

        BookResponseDto book = BookResponseDto.builder()
                .id(1L)
                .title("Test Book")
                .author(author)
                .genres(Set.of(genre))
                .build();

        when(bookService.findById(any(BookRequestDto.class))).thenReturn(book);

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("GET /books/new - авторизованный пользователь должен получить доступ к форме создания книги")
    public void shouldAllowAccessToCreateBookFormWhenAuthenticated() throws Exception {
        AuthorResponseDto author1 = new AuthorResponseDto(1L, "Author 1");
        AuthorResponseDto author2 = new AuthorResponseDto(2L, "Author 2");
        List<AuthorResponseDto> authors = List.of(author1, author2);

        GenreResponseDto genre1 = new GenreResponseDto(1L, "Genre 1");
        GenreResponseDto genre2 = new GenreResponseDto(2L, "Genre 2");
        List<GenreResponseDto> genres = List.of(genre1, genre2);

        when(authorService.findAll()).thenReturn(authors);
        when(genreService.findAll()).thenReturn(genres);

        mockMvc.perform(get("/books/new"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("POST /books - авторизованный пользователь должен иметь возможность создать книгу")
    public void shouldAllowCreatingBookWhenAuthenticated() throws Exception {
        AuthorResponseDto author = new AuthorResponseDto(1L, "New Author");
        GenreResponseDto genre = new GenreResponseDto(1L, "New Genre");

        BookResponseDto book = BookResponseDto.builder()
                .id(1L)
                .title("New Book")
                .author(author)
                .genres(Set.of(genre))
                .build();

        when(bookService.insert(any(BookCreateRequestDto.class))).thenReturn(book);

        mockMvc.perform(post("/books")
                        .with(csrf())
                        .param("title", "New Book")
                        .param("authorId", "1")
                        .param("genreIds", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("GET /books/{id}/edit - авторизованный пользователь должен получить доступ к форме редактирования книги")
    public void shouldAllowAccessToEditBookFormWhenAuthenticated() throws Exception {
        AuthorResponseDto author = new AuthorResponseDto(1L, "Test Author");
        GenreResponseDto genre = new GenreResponseDto(1L, "Test Genre");

        BookResponseDto book = BookResponseDto.builder()
                .id(1L)
                .title("Test Book")
                .author(author)
                .genres(Set.of(genre))
                .build();

        AuthorResponseDto author1 = new AuthorResponseDto(1L, "Author 1");
        AuthorResponseDto author2 = new AuthorResponseDto(2L, "Author 2");
        List<AuthorResponseDto> authors = List.of(author1, author2);

        GenreResponseDto genre1 = new GenreResponseDto(1L, "Genre 1");
        GenreResponseDto genre2 = new GenreResponseDto(2L, "Genre 2");
        List<GenreResponseDto> genres = List.of(genre1, genre2);

        when(bookService.findById(any(BookRequestDto.class))).thenReturn(book);
        when(authorService.findAll()).thenReturn(authors);
        when(genreService.findAll()).thenReturn(genres);

        mockMvc.perform(get("/books/1/edit"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("PUT /books/{id} - авторизованный пользователь должен иметь возможность обновить книгу")
    public void shouldAllowUpdatingBookWhenAuthenticated() throws Exception {
        AuthorResponseDto author = new AuthorResponseDto(1L, "Updated Author");
        GenreResponseDto genre = new GenreResponseDto(1L, "Updated Genre");

        BookResponseDto book = BookResponseDto.builder()
                .id(1L)
                .title("Updated Book")
                .author(author)
                .genres(Set.of(genre))
                .build();

        when(bookService.update(any(BookUpdateRequestDto.class))).thenReturn(book);

        mockMvc.perform(put("/books/1")
                        .with(csrf())
                        .param("title", "Updated Book")
                        .param("authorId", "1")
                        .param("genreIds", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/1"));
    }

    @Test
    @WithMockUser(username = "global_admin")
    @DisplayName("DELETE /books/{id} - авторизованный пользователь должен иметь возможность удалить книгу")
    public void shouldAllowDeletingBookWhenAuthenticated() throws Exception {
        mockMvc.perform(delete("/books/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));
    }
}
