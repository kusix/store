create table users
(
    id    bigint auto_increment
        primary key,
    name  varchar(255) not null,
    email varchar(255) not null
);

create table addresses
(
    id      bigint auto_increment
        primary key,
    user_id bigint       not null,
    street  varchar(255) not null,
    city    varchar(255) not null,
    zip     varchar(255) not null,
    constraint addresses_users_id_fk
        foreign key (user_id) references users (id)
);

