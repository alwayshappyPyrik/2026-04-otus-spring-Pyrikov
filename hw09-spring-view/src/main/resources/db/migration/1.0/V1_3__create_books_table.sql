--date: 2026-06-19
--author: alwayshappypyrik

create sequence books_id_seq
    start with 1
    increment by 1;

create table books (
    id        bigint default nextval('books_id_seq'),
    title     varchar(255) not null,
    author_id bigint not null,
    primary key (id),

    constraint fk_books_author foreign key (author_id) references authors(id) on delete cascade
);