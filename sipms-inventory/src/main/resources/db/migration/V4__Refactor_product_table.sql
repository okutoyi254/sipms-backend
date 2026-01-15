DROP  TABLE  inventory.products;
CREATE TABLE inventory.products (
                                    id BIGSERIAL PRIMARY KEY,
                                    product_code VARCHAR(50) NOT NULL UNIQUE,
                                    product_name VARCHAR(200) NOT NULL,
                                    description TEXT,
                                    category_id BIGINT NOT NULL,
                                    standard_price DECIMAL(19, 2) NOT NULL,
                                    standard_cost DECIMAL(19, 2) NOT NULL,
                                    unit_of_measure VARCHAR(50) NOT NULL,
                                    status VARCHAR(50) NOT NULL,
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    created_by VARCHAR(255),
                                    updated_by VARCHAR(255),

                                    CONSTRAINT fk_products_category FOREIGN KEY (category_id)
                                        REFERENCES inventory.product_categories(category_id) ON DELETE RESTRICT,
                                    CONSTRAINT chk_products_standard_price CHECK (standard_price >= 0),
                                    CONSTRAINT chk_products_standard_cost CHECK (standard_cost >= 0),
                                    CONSTRAINT chk_products_code_not_empty CHECK (product_code <> ''),
                                    CONSTRAINT chk_products_name_not_empty CHECK (product_name <> ''),
                                    CONSTRAINT chk_products_uom_not_empty CHECK (unit_of_measure <> '')
);

CREATE INDEX idx_products_product_code ON inventory.products(product_code);
CREATE INDEX idx_products_product_name ON inventory.products(product_name);
CREATE INDEX idx_products_category_id ON inventory.products(category_id);
CREATE INDEX idx_products_status ON inventory.products(status);



CREATE TABLE inventory.product_images (
                                          product_id BIGINT NOT NULL,
                                          image_url VARCHAR(500) NOT NULL,

                                          CONSTRAINT fk_product_images_product FOREIGN KEY (product_id)
                                              REFERENCES inventory.products(id) ON DELETE CASCADE,
                                          CONSTRAINT uk_product_images UNIQUE (product_id, image_url)
);

CREATE INDEX idx_product_images_product_id ON inventory.product_images(product_id);

COMMENT ON TABLE inventory.products IS 'Stores core product information including pricing and status';
COMMENT ON COLUMN inventory.products.id IS 'Primary key - auto-generated database ID';
COMMENT ON COLUMN inventory.products.product_code IS 'Unique business identifier for the product';
COMMENT ON COLUMN inventory.products.product_name IS 'Display name of the product';
COMMENT ON COLUMN inventory.products.description IS 'Detailed description of the product';
COMMENT ON COLUMN inventory.products.category_id IS 'Foreign key to product_categories table';
COMMENT ON COLUMN inventory.products.standard_price IS 'Standard selling price for the product';
COMMENT ON COLUMN inventory.products.standard_cost IS 'Standard cost price for the product';
COMMENT ON COLUMN inventory.products.unit_of_measure IS 'Unit of measure (e.g., kg, pcs, liters)';
COMMENT ON COLUMN inventory.products.status IS 'Product status enum (e.g., ACTIVE, INACTIVE, DISCONTINUED)';

COMMENT ON TABLE inventory.product_images IS 'Stores multiple image URLs associated with products';
COMMENT ON COLUMN inventory.product_images.product_id IS 'Foreign key to products table';
COMMENT ON COLUMN inventory.product_images.image_url IS 'URL or path to product image';