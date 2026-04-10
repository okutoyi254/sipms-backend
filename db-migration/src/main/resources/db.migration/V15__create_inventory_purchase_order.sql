-- PURCHASE ORDER TABLES
CREATE TABLE inventory_purchase_order (
                                          id SERIAL PRIMARY KEY,
                                          po_number VARCHAR(50) UNIQUE NOT NULL,
                                          po_date DATE NOT NULL DEFAULT CURRENT_DATE,
                                          supplier_id BIGINT NOT NULL REFERENCES procurement.supplier(id),
                                          pr_id BIGINT REFERENCES procurement.inventory_purchase_requisition(id),
                                          status VARCHAR(50) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PENDING_APPROVAL', 'APPROVED', 'SENT_TO_SUPPLIER', 'ACKNOWLEDGED', 'PARTIALLY_RECEIVED', 'FULLY_RECEIVED', 'CLOSED', 'CANCELLED')),
                                          delivery_date DATE NOT NULL,
                                          delivery_address TEXT,
                                          payment_terms VARCHAR(100),
                                          payment_method VARCHAR(50) CHECK (payment_method IN ('BANK_TRANSFER', 'CHEQUE', 'CREDIT_CARD', 'CASH')),
                                          subtotal DECIMAL(15,2) DEFAULT 0.00,
                                          tax_amount DECIMAL(15,2) DEFAULT 0.00,
                                          discount_amount DECIMAL(15,2) DEFAULT 0.00,
                                          shipping_cost DECIMAL(15,2) DEFAULT 0.00,
                                          total_amount DECIMAL(15,2) DEFAULT 0.00,
                                          created_by BIGINT NOT NULL,
                                          acknowledged_at TIMESTAMP,
                                          updated_by BIGINT,
                                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          is_deleted BOOLEAN DEFAULT FALSE
);

-- Purchase Order Line Items
CREATE TABLE inventory_purchase_order_item (
                                               id BIGSERIAL PRIMARY KEY,
                                               po_id BIGINT NOT NULL REFERENCES inventory_purchase_order(id) ON DELETE CASCADE,
                                               pr_item_id BIGINT REFERENCES procurement.inventory_purchase_requisition_item(id),
                                               line_number INTEGER NOT NULL,
                                               product_id BIGINT,
                                               product_code VARCHAR(100),
                                               product_name VARCHAR(255) NOT NULL,
                                               unit_of_measure VARCHAR(20) NOT NULL,
                                               quantity_ordered DECIMAL(15,3) NOT NULL,
                                               quantity_received DECIMAL(15,3) DEFAULT 0,
                                               quantity_returned DECIMAL(15,3) DEFAULT 0,
                                               unit_price DECIMAL(15,2) NOT NULL,
                                               discount_percent DECIMAL(5,2) DEFAULT 0.00,
                                               discount_amount DECIMAL(15,2) DEFAULT 0.00,
                                               tax_percent DECIMAL(5,2) DEFAULT 0.00,
                                               tax_amount DECIMAL(15,2) DEFAULT 0.00,
                                               line_total DECIMAL(15,2) GENERATED ALWAYS AS (
                                                   (quantity_ordered * unit_price) - COALESCE(discount_amount, 0) + COALESCE(tax_amount, 0)
                                                   ) STORED,
                                               expected_delivery_date DATE,
                                               specifications TEXT,
                                               status VARCHAR(50) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PARTIALLY_RECEIVED', 'FULLY_RECEIVED', 'CLOSED')),
                                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                               UNIQUE(po_id, line_number)
);