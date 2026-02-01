CREATE TABLE logistics.stock_transfer_items (
                                      id BIGSERIAL PRIMARY KEY,
                                      transfer_request_id BIGINT NOT NULL,
                                      product_id BIGINT NOT NULL,
                                      requested_quantity INTEGER NOT NULL,
                                      approved_quantity INTEGER,
                                      shipped_quantity INTEGER,
                                      received_quantity INTEGER,
                                      damaged_quantity INTEGER DEFAULT 0,
                                      batch_number VARCHAR(100),
                                      unit_cost DECIMAL(19, 2),
                                      created_at TIMESTAMP,
                                      updated_at TIMESTAMP,
                                      created_by VARCHAR(255),
                                      updated_by VARCHAR(255),
                                      version INTEGER,

                                      CONSTRAINT fk_stock_transfer_items_transfer_request
                                          FOREIGN KEY (transfer_request_id) REFERENCES logistics.stock_transfer_requests(id)
                                              ON DELETE CASCADE,
                                      CONSTRAINT fk_stock_transfer_items_product
                                          FOREIGN KEY (product_id) REFERENCES inventory.products(id)
);

CREATE INDEX idx_stock_transfer_items_transfer_request_id
    ON logistics.stock_transfer_items(transfer_request_id);

COMMENT ON TABLE logistics.stock_transfer_items IS 'Stores individual product items within stock transfer requests';
COMMENT ON COLUMN logistics.stock_transfer_items.damaged_quantity IS 'Quantity damaged during transfer, defaults to 0';