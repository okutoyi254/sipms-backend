
CREATE TABLE inventory.product_batch (
                                         id BIGSERIAL PRIMARY KEY,
                                         product_id BIGINT NOT NULL,
                                         branch_id BIGINT NOT NULL,
                                         batch_number VARCHAR(100) NOT NULL,
                                         quantity INTEGER NOT NULL DEFAULT 0,
                                         quantity_reserved INTEGER NOT NULL DEFAULT 0,
                                         quantity_available INTEGER NOT NULL DEFAULT 0,
                                         manufacturing_date DATE,
                                         expiry_date DATE,
                                         supplier_reference VARCHAR(255),
                                         purchase_order_number VARCHAR(100),
                                         status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
                                         notes TEXT,
                                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         created_by VARCHAR(255),
                                         updated_by VARCHAR(255),

                                         CONSTRAINT fk_product_batch_product FOREIGN KEY (product_id)
                                             REFERENCES inventory.products(id) ON DELETE RESTRICT,
                                         CONSTRAINT fk_product_batch_branch FOREIGN KEY (branch_id)
                                             REFERENCES "sipms-branch".branches(id) ON DELETE RESTRICT,
                                         CONSTRAINT uk_product_batch_number UNIQUE (product_id, batch_number),
                                         CONSTRAINT chk_product_batch_quantity CHECK (quantity >= 0),
                                         CONSTRAINT chk_product_batch_qty_reserved CHECK (quantity_reserved >= 0),
                                         CONSTRAINT chk_product_batch_qty_available CHECK (quantity_available >= 0),
                                         CONSTRAINT chk_product_batch_qty_logic CHECK (quantity_available = quantity - quantity_reserved),
                                         CONSTRAINT chk_product_batch_dates CHECK (
                                             expiry_date IS NULL OR manufacturing_date IS NULL OR expiry_date > manufacturing_date
                                             ),
                                         CONSTRAINT chk_product_batch_batch_number_not_empty CHECK (batch_number <> '')
);

CREATE INDEX idx_product_batch_product_id ON inventory.product_batch(product_id);
CREATE INDEX idx_product_batch_branch_id ON inventory.product_batch(branch_id);
CREATE INDEX idx_product_batch_batch_number ON inventory.product_batch(batch_number);
CREATE INDEX idx_product_batch_expiry_date ON inventory.product_batch(expiry_date);
CREATE INDEX idx_product_batch_status ON inventory.product_batch(status);
CREATE INDEX idx_product_batch_product_branch ON inventory.product_batch(product_id, branch_id);
CREATE INDEX idx_product_batch_available ON inventory.product_batch(quantity_available) WHERE quantity_available > 0;
CREATE INDEX idx_product_batch_expiring_soon ON inventory.product_batch(expiry_date)
    WHERE expiry_date IS NOT NULL AND status = 'ACTIVE';

COMMENT ON TABLE inventory.product_batch IS 'Comprehensive batch/lot tracking for inventory management with FIFO/FEFO support';
COMMENT ON COLUMN inventory.product_batch.id IS 'Primary key - auto-generated database ID';
COMMENT ON COLUMN inventory.product_batch.product_id IS 'Foreign key to products table';
COMMENT ON COLUMN inventory.product_batch.branch_id IS 'Foreign key to branches table where batch is stored';
COMMENT ON COLUMN inventory.product_batch.batch_number IS 'Unique batch/lot identifier per product';
COMMENT ON COLUMN inventory.product_batch.quantity IS 'Total quantity in this batch';
COMMENT ON COLUMN inventory.product_batch.quantity_reserved IS 'Quantity reserved for pending orders';
COMMENT ON COLUMN inventory.product_batch.quantity_available IS 'Quantity available for new orders (quantity - quantity_reserved)';
COMMENT ON COLUMN inventory.product_batch.manufacturing_date IS 'Date when the batch was manufactured';
COMMENT ON COLUMN inventory.product_batch.expiry_date IS 'Date when the batch expires';
COMMENT ON COLUMN inventory.product_batch.supplier_reference IS 'Supplier batch/lot reference number';
COMMENT ON COLUMN inventory.product_batch.purchase_order_number IS 'Purchase order number for this batch';
COMMENT ON COLUMN inventory.product_batch.status IS 'Batch status (ACTIVE, QUARANTINE, EXPIRED, RECALLED, DAMAGED, DEPLETED, BLOCKED)';
COMMENT ON COLUMN inventory.product_batch.notes IS 'Additional notes or comments about the batch';