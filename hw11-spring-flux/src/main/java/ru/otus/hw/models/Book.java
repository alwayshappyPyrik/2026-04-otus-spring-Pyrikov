package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Table(name = "books")
public class Book {

    @Id
    @Column("id")
    private Long id;

    @Column("title")
    private String title;

    @Column("author_id")
    private Long authorId;

    @Column("author_full_name")
    private String authorFullName;

    @Column("genre_ids")
    private String genreIds;

    @Column("genre_names")
    private String genreNames;

    @Transient
    private Set<GenreEmbedded> genres;

    public void setGenres(Set<GenreEmbedded> genres) {
        this.genres = genres;
        if (genres != null && !genres.isEmpty()) {
            Set<GenreEmbedded> uniqueGenres = new LinkedHashSet<>(genres);
            this.genreIds = uniqueGenres.stream()
                    .map(g -> String.valueOf(g.getId()))
                    .collect(Collectors.joining(","));
            this.genreNames = uniqueGenres.stream()
                    .map(GenreEmbedded::getName)
                    .collect(Collectors.joining(","));
        } else {
            this.genreIds = null;
            this.genreNames = null;
        }
    }

    public Set<GenreEmbedded> getGenres() {
        if (this.genres == null && this.genreIds != null && this.genreNames != null) {
            String[] ids = this.genreIds.split(",");
            String[] names = this.genreNames.split(",");
            this.genres = new LinkedHashSet<>();
            for (int i = 0; i < Math.min(ids.length, names.length); i++) {
                this.genres.add(new GenreEmbedded(
                        Long.parseLong(ids[i].trim()),
                        names[i].trim()
                ));
            }
        }
        return this.genres != null ? this.genres : new LinkedHashSet<>();
    }
}