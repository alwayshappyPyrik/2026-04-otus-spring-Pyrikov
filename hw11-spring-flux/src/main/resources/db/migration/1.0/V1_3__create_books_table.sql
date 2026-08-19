--date: 2026-06-19
--author: alwayshappypyrik

create sequence books_id_seq
    start with 1
    increment by 1;

create table books
(
    id               bigint default nextval('books_id_seq'),
    title            varchar(255) not null,
    author_id        bigint       not null,
    author_full_name varchar(255) not null,
    genre_ids        text,
    genre_names      text,
    primary key (id)
);