CREATE TABLE inventory.delivery_logs (
                               delivery_id BIGSERIAL PRIMARY KEY,
                               supplier_name VARCHAR(100) NOT NULL,
                               supplier_email VARCHAR(150) NOT NULL,
                               total_items INT NOT NULL,
                               total_price DECIMAL(10,2) NOT NULL,
                               delivery_date TIMESTAMP NOT NULL,
                               delivery_status VARCHAR(20) NOT NULL,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE INDEX idx_delivery_status ON inventory.delivery_logs(delivery_status);
CREATE INDEX idx_supplier_email ON inventory.delivery_logs(supplier_email);


COMMENT ON TABLE inventory.delivery_logs IS 'Stores delivery logs information for suppliers deliveries';