--date: 2026-06-19
--author: alwayshappypyrik

create sequence authors_id_seq
    start with 1
    increment by 1;

create table authors (
    id        bigint default nextval('authors_id_seq'),
    full_name varchar(255) not null,
    primary key (id)
);