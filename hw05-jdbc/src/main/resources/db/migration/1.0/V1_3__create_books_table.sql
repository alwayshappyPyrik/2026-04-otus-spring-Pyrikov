--date: 2026-06-11
--author: alwayshappypyrik

create table books (
    id        bigserial,
    title     varchar(255),
    author_id bigint references authors (id) on delete cascade,
    primary key (id)
);