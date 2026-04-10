-- GOODS RECEIPT NOTE TABLE
CREATE TABLE inventory_goods_receipt (
                                              id SERIAL PRIMARY KEY,
                                              grn_number VARCHAR(50) UNIQUE NOT NULL,
                                              grn_date DATE NOT NULL DEFAULT CURRENT_DATE,
                                              po_id BIGINT NOT NULL REFERENCES procurement.inventory_purchase_order(id),
                                              supplier_id BIGINT NOT NULL REFERENCES procurement.supplier(id),
                                              supplier_invoice_number VARCHAR(100),
                                              supplier_invoice_date DATE,
                                              delivery_note_number VARCHAR(100),
                                              delivery_date DATE,
                                              vehicle_number VARCHAR(50),
                                              received_by INT NOT NULL,
                                              warehouse_location VARCHAR(255),
                                              status VARCHAR(50) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PENDING_INSPECTION', 'INSPECTED', 'APPROVED', 'REJECTED', 'POSTED_TO_INVENTORY')),
                                              total_quantity_ordered DECIMAL(15,3),
                                              total_quantity_received DECIMAL(15,3),
                                              total_quantity_accepted DECIMAL(15,3),
                                              total_quantity_rejected DECIMAL(15,3),
                                              total_amount DECIMAL(15,2) DEFAULT 0.00,
                                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              is_deleted BOOLEAN DEFAULT FALSE
);
