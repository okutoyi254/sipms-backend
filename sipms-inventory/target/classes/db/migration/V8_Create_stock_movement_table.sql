
CREATE TABLE inventory.stock_movements (
                                           id BIGSERIAL PRIMARY KEY,
                                           product_id BIGINT NOT NULL,
                                           branch_id BIGINT NOT NULL,
                                           movement_type VARCHAR(50) NOT NULL,
                                           quantity INTEGER NOT NULL,
                                           from_branch_id BIGINT,
                                           to_branch_id BIGINT,
                                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           created_by VARCHAR(255),
                                           updated_by VARCHAR(255),

                                           CONSTRAINT fk_stock_movements_product FOREIGN KEY (product_id)
                                               REFERENCES inventory.products(id) ON DELETE RESTRICT,
                                           CONSTRAINT fk_stock_movements_branch FOREIGN KEY (branch_id)
                                               REFERENCES "sipms-branch".branches(id) ON DELETE RESTRICT,
                                           CONSTRAINT fk_stock_movements_from_branch FOREIGN KEY (from_branch_id)
                                               REFERENCES "sipms-branch".branches(id) ON DELETE RESTRICT,
                                           CONSTRAINT fk_stock_movements_to_branch FOREIGN KEY (to_branch_id)
                                               REFERENCES "sipms-branch".branches(id) ON DELETE RESTRICT,
                                           CONSTRAINT chk_stock_movements_quantity_positive CHECK (quantity > 0),
                                           CONSTRAINT chk_stock_movements_transfer_logic CHECK (
                                               (movement_type = 'TRANSFER' AND from_branch_id IS NOT NULL AND to_branch_id IS NOT NULL) OR
                                               (movement_type != 'TRANSFER')
                                               ),
                                           CONSTRAINT chk_stock_movements_no_self_transfer CHECK (
                                               from_branch_id IS NULL OR to_branch_id IS NULL OR from_branch_id != to_branch_id
                                               )
);

CREATE INDEX idx_stock_movements_product_id ON inventory.stock_movements(product_id);
CREATE INDEX idx_stock_movements_branch_id ON inventory.stock_movements(branch_id);
CREATE INDEX idx_stock_movements_movement_type ON inventory.stock_movements(movement_type);
CREATE INDEX idx_stock_movements_from_branch_id ON inventory.stock_movements(from_branch_id);
CREATE INDEX idx_stock_movements_to_branch_id ON inventory.stock_movements(to_branch_id);
CREATE INDEX idx_stock_movements_created_at ON inventory.stock_movements(created_at DESC);
CREATE INDEX idx_stock_movements_product_branch_date ON inventory.stock_movements(product_id, branch_id, created_at DESC);

COMMENT ON TABLE inventory.stock_movements IS 'Audit trail of all inventory movements including receipts, sales, adjustments, and transfers';
COMMENT ON COLUMN inventory.stock_movements.id IS 'Primary key - auto-generated database ID';
COMMENT ON COLUMN inventory.stock_movements.product_id IS 'Foreign key to products table';
COMMENT ON COLUMN inventory.stock_movements.branch_id IS 'Foreign key to the primary branch involved in the movement';
COMMENT ON COLUMN inventory.stock_movements.movement_type IS 'Type of movement (e.g., RECEIPT, SALE, ADJUSTMENT, TRANSFER, RETURN)';
COMMENT ON COLUMN inventory.stock_movements.quantity IS 'Quantity moved (always positive, direction determined by movement_type)';
COMMENT ON COLUMN inventory.stock_movements.from_branch_id IS 'Source branch for transfers (null for non-transfer movements)';
COMMENT ON COLUMN inventory.stock_movements.to_branch_id IS 'Destination branch for transfers (null for non-transfer movements)';
COMMENT ON COLUMN inventory.stock_movements.created_at IS 'Timestamp when the movement occurred';
COMMENT ON COLUMN inventory.stock_movements.created_by IS 'User who initiated the movement';