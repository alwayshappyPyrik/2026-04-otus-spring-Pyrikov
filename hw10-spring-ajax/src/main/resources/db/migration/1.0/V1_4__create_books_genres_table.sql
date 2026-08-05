--date: 2026-06-19
--author: alwayshappypyrik

create table books_genres (
    book_id  bigint not null,
    genre_id bigint not null,
    primary key (book_id, genre_id),

    constraint fk_books_genres_book foreign key (book_id) references books(id) on delete cascade,
    constraint fk_books_genres_genre foreign key (genre_id) references genres(id) on delete cascade
);