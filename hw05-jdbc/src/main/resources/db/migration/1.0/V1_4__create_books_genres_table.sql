--date: 2026-06-11
--author: alwayshappypyrik

create table books_genres (
    book_id  bigint references books (id) on delete cascade,
    genre_id bigint references genres (id) on delete cascade,
    primary key (book_id, genre_id)
);