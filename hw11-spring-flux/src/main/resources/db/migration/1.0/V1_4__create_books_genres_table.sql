--date: 2026-06-19
--author: alwayshappypyrik

create sequence books_genres_id_seq
    start with 1
    increment by 1;

create table books_genres (
    id       bigint default nextval('books_genres_id_seq'),
    book_id  bigint not null,
    genre_id bigint not null,
    primary key (id),

    constraint fk_books_genres_book foreign key (book_id) references books(id) on delete cascade,
    constraint fk_books_genres_genre foreign key (genre_id) references genres(id) on delete cascade,
    constraint unique_book_genre unique (book_id, genre_id)
);