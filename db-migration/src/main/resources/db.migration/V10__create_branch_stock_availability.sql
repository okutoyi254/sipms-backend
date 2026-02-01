
CREATE TABLE IF NOT EXISTS inventory.branch_stock_availability_view (
                                                              id BIGSERIAL PRIMARY KEY,
                                                              product_id BIGINT NOT NULL,
                                                              branch_id BIGINT NOT NULL,
                                                              available_quantity INTEGER NOT NULL DEFAULT 0,
                                                              reserved_quantity INTEGER NOT NULL DEFAULT 0,
                                                              average_cost NUMERIC(19, 4) NULL,
                                                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,


                                                              CONSTRAINT chk_available_quantity CHECK (available_quantity >= 0),
                                                              CONSTRAINT chk_reserved_quantity CHECK (reserved_quantity >= 0),
                                                              CONSTRAINT chk_average_cost CHECK (average_cost >= 0),


                                                              CONSTRAINT fk_branch_stock_product
                                                                  FOREIGN KEY (product_id)
                                                                      REFERENCES inventory.products(id)
                                                                      ON DELETE RESTRICT
                                                                      ON UPDATE CASCADE,

                                                              CONSTRAINT fk_branch_stock_branch
                                                                  FOREIGN KEY (branch_id)
                                                                      REFERENCES "sipms-branch".branches(id)
                                                                      ON DELETE RESTRICT
                                                                      ON UPDATE CASCADE,

                                                              CONSTRAINT uk_product_branch UNIQUE (product_id, branch_id)
);

COMMENT ON TABLE inventory.branch_stock_availability_view IS 'Tracks product stock availability and reservations per branch';

COMMENT ON COLUMN inventory.branch_stock_availability_view.id IS 'Primary key identifier';
COMMENT ON COLUMN inventory.branch_stock_availability_view.product_id IS 'Reference to product in inventory module';
COMMENT ON COLUMN inventory.branch_stock_availability_view.branch_id IS 'Reference to branch';
COMMENT ON COLUMN inventory.branch_stock_availability_view.available_quantity IS 'Current available quantity for sale';
COMMENT ON COLUMN inventory.branch_stock_availability_view.reserved_quantity IS 'Quantity reserved for pending orders';
COMMENT ON COLUMN inventory.branch_stock_availability_view.average_cost IS 'Average cost of the product at this branch';

CREATE INDEX idx_branch_stock_product_id
    ON inventory.branch_stock_availability_view(product_id);

CREATE INDEX idx_branch_stock_branch_id
    ON inventory.branch_stock_availability_view(branch_id);

CREATE INDEX idx_branch_stock_availability
    ON inventory.branch_stock_availability_view(available_quantity);

CREATE INDEX idx_branch_stock_branch_product
    ON inventory.branch_stock_availability_view(branch_id, product_id);

CREATE INDEX idx_branch_stock_search
    ON inventory.branch_stock_availability_view(branch_id, product_id, available_quantity);

CREATE OR REPLACE FUNCTION update_branch_stock_availability_updated_at()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_branch_stock_availability_updated_at
    BEFORE UPDATE ON inventory.branch_stock_availability_view
    FOR EACH ROW
EXECUTE FUNCTION update_branch_stock_availability_updated_at();

