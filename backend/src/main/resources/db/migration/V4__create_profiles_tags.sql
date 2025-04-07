create table profiles
(
    id             bigint                 not null
        primary key,
    bio            text                   not null,
    phone_number   varchar(20)            not null,
    date_of_birth  date                   null,
    loyalty_points int UNSIGNED default 0 not null,
    constraint profiles_users_id_fk
        foreign key (id) references users (id)
);

create table tags
(
    id   int auto_increment
        primary key,
    name varchar(255) not null
);

create table users_tags
(
    user_id bigint not null,
    tag_id  int    not null,
    constraint users_tags_pk
        primary key (user_id, tag_id),
    constraint users_tags_tags_id_fk
        foreign key (tag_id) references tags (id)
            on delete cascade,
    constraint users_tags_users_id_fk
        foreign key (user_id) references users (id)
            on delete cascade
);
