

CREATE TABLE "inventory".branch_products (
                                 id BIGSERIAL PRIMARY KEY,
                                 product_id BIGINT NOT NULL,
                                 branch_id BIGINT NOT NULL,
                                 is_available_at_branch BOOLEAN NOT NULL DEFAULT true,
                                 branch_price DECIMAL(19, 2),
                                 branch_cost DECIMAL(19, 2),
                                 current_stock INTEGER NOT NULL DEFAULT 0,
                                 reserved_stock INTEGER NOT NULL DEFAULT 0,
                                 available_stock INTEGER NOT NULL DEFAULT 0,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 created_by VARCHAR(255),
                                 updated_by VARCHAR(255),

                                 CONSTRAINT fk_branch_products_product FOREIGN KEY (product_id)
                                     REFERENCES inventory.products(id) ON DELETE CASCADE,
                                 CONSTRAINT fk_branch_products_branch FOREIGN KEY (branch_id)
                                     REFERENCES "sipms-branch".branches(id) ON DELETE CASCADE,
                                 CONSTRAINT uk_branch_products_product_branch UNIQUE (product_id, branch_id),
                                 CONSTRAINT chk_branch_products_price CHECK (branch_price IS NULL OR branch_price >= 0),
                                 CONSTRAINT chk_branch_products_cost CHECK (branch_cost IS NULL OR branch_cost >= 0),
                                 CONSTRAINT chk_branch_products_current_stock CHECK (current_stock >= 0),
                                 CONSTRAINT chk_branch_products_reserved_stock CHECK (reserved_stock >= 0),
                                 CONSTRAINT chk_branch_products_available_stock CHECK (available_stock >= 0)
);

CREATE INDEX idx_branch_products_product_id ON "inventory".branch_products(product_id);
CREATE INDEX idx_branch_products_branch_id ON "inventory".branch_products(branch_id);
CREATE INDEX idx_branch_products_available
    ON "inventory".branch_products(is_available_at_branch);
CREATE INDEX idx_branch_products_stock ON "inventory".branch_products(current_stock, available_stock);

COMMENT ON TABLE "inventory".branch_products IS 'Stores product inventory and pricing information specific to each branch';
COMMENT ON COLUMN "inventory".branch_products.is_available_at_branch IS 'Indicates if the product is currently available at this branch';
COMMENT ON COLUMN "inventory".branch_products.branch_price IS 'Selling price of the product at this specific branch';
COMMENT ON COLUMN "inventory".branch_products.branch_cost IS 'Cost price of the product at this specific branch';
COMMENT ON COLUMN "inventory".branch_products.current_stock IS 'Total current stock quantity at the branch';
COMMENT ON COLUMN "inventory".branch_products.reserved_stock IS 'Stock quantity reserved for pending orders';
COMMENT ON COLUMN "inventory".branch_products.available_stock IS 'Stock quantity available for new orders (current_stock - reserved_stock)';