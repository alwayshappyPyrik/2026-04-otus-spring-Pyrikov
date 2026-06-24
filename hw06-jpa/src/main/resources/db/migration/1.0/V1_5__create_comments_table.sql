--date: 2026-06-19
--author: alwayshappypyrik
create sequence comments_id_seq
    start with 1
    increment by 1;

create table comments
(
    id bigint default nextval('comments_id_seq'),
    text text not null,
    book_id bigint not null,
    primary key (id),

    constraint fk_comments_book foreign key (book_id) references books(id) on delete cascade
)