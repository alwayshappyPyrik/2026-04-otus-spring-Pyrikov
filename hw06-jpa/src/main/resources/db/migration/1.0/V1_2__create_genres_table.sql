--date: 2026-06-19
--author: alwayshappypyrik

create sequence genres_id_seq
    start with 1
    increment by 1;

create table genres (
    id   bigint default nextval('genres_id_seq'),
    name varchar(255) not null unique,
    primary key (id)
);