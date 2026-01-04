create table inventory.products(
    product_id bigserial primary key,
    product_name varchar(200) not null ,
    description text,
    category_id bigint not null ,
    unit_price decimal(10,2) not null,
    quantity_in_stock int not null default 0,
    created_at timestamp not null default current_timestamp,

    constraint fk_product_category foreign key (category_id)
                     references inventory.product_categories(category_id) on delete restrict
);

create index idx_product_name on inventory.products(product_name);
CREATE INDEX idx_category_id ON inventory.products(category_id);
