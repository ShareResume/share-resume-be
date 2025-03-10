create table users
(
    id       uuid primary key,
    nick     varchar(255) not null unique,
    email    varchar(255) not null unique,
    password varchar(255) not null,
    role     varchar(255) not null
);