create table category
(
    id   tinyint auto_increment
        primary key,
    name varchar(255) not null
);

create table product
(
    id          bigint auto_increment
        primary key,
    name        varchar(255)            not null,
    price       decimal(10, 2) not null,
    category_id tinyint                 not null,
    constraint product_category_id_fk
        foreign key (category_id) references category (id)
);
