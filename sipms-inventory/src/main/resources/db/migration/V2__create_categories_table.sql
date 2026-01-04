create table  inventory.product_categories (
    category_id bigserial primary key,
    category_name varchar(100) not null ,
    category_code varchar(100) not null unique,
    description text,
    parent_category_id bigint null,
    created_at timestamp not null default current_timestamp,
    update_at timestamp not null default current_timestamp,

    constraint fk_parent_category foreign key(parent_category_id)
        references inventory.product_categories(category_id) on delete restrict ,

    constraint chk_no_self_reference check ( category_id != parent_category_id )
);

create index idx_parent_category on inventory.product_categories(parent_category_id);

comment on table inventory.product_categories is 'Hierarchical category fo products';