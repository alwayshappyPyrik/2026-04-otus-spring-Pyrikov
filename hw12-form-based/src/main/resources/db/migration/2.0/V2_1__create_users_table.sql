--date: 2026-09-02
--author: alwayshappypyrik

create sequence users_id_seq
    start with 1
    increment by 1;

create table users
(
    id bigint default nextval('users_id_seq'),
    login varchar(50) not null unique,
    password varchar(255) not null,
    email varchar(100) not null unique,
    enabled boolean not null default true,
    primary key (id)
)