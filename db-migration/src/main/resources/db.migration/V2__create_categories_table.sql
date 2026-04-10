create table  inventory.product_categories (
    id bigserial primary key,
    category_name varchar(100) not null ,
    category_code varchar(100) not null unique,
    description text,
    parent_category_id bigint null,
    created_at timestamp not null default current_timestamp,
    update_at timestamp not null default current_timestamp,

    constraint fk_parent_category foreign key(parent_category_id)
        references inventory.product_categories(id) on delete restrict ,

    constraint chk_no_self_reference check ( id != parent_category_id )
);


ALTER TABLE inventory.product_categories
    RENAME COLUMN update_at TO updated_at;

ALTER TABLE inventory.product_categories
    ADD COLUMN is_global BOOLEAN NOT NULL DEFAULT true;

ALTER TABLE inventory.product_categories
    ADD COLUMN created_by VARCHAR(255);

ALTER TABLE inventory.product_categories
    ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE inventory.product_categories
    ADD CONSTRAINT uk_category_name UNIQUE (category_name);

ALTER TABLE inventory.product_categories
    ADD CONSTRAINT chk_category_code_not_empty CHECK (category_code <> '');

ALTER TABLE inventory.product_categories
    ADD CONSTRAINT chk_category_name_not_empty CHECK (category_name <> '');

CREATE INDEX idx_category_category_name ON inventory.product_categories(category_name);
create index idx_parent_category on inventory.product_categories(parent_category_id);



COMMENT ON COLUMN inventory.product_categories.is_global IS 'Indicates if category is global (true) or branch-specific (false)';
COMMENT ON COLUMN inventory.product_categories.created_by IS 'User who created the category';
COMMENT ON COLUMN inventory.product_categories.updated_by IS 'User who last updated the category';
COMMENT ON COLUMN inventory.product_categories.updated_at IS 'Timestamp when the category was last updated';

comment on table inventory.product_categories is 'Hierarchical category fo products';