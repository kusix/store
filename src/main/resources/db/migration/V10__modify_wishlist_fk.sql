alter table wishlist
    drop foreign key wishlist_product_id_fk;

alter table wishlist
    add constraint wishlist_product_id_fk
        foreign key (product_id) references product (id)
            on delete cascade;

