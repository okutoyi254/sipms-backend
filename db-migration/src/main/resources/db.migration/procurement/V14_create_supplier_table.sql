
-- SUPPLIER MANAGEMENT TABLE
CREATE TABLE supplier (
                          id SERIAL PRIMARY KEY,
                          supplier_code VARCHAR(50) UNIQUE NOT NULL,
                          name VARCHAR(255) NOT NULL,
                          registration_number VARCHAR(100),
                          tax_id VARCHAR(100),
                          email VARCHAR(255),
                          phone VARCHAR(50),
                          website VARCHAR(255),
                          address TEXT,
                          city VARCHAR(100),
                          supplier_type VARCHAR(50) NOT NULL CHECK (supplier_type IN ('MANUFACTURER', 'DISTRIBUTOR', 'WHOLESALER', 'RETAILER')),
                          status VARCHAR(50) NOT NULL DEFAULT 'PENDING_APPROVAL' CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'PENDING_APPROVAL')),
                          total_orders INTEGER DEFAULT 0,
                          preferred_supplier BOOLEAN DEFAULT FALSE,
                          contract_start_date DATE,
                          contract_end_date DATE,
                          created_by INT,
                          updated_by INT,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          is_deleted BOOLEAN DEFAULT FALSE,
                          contact_person VARCHAR(255) NOT NULL,
                          designation VARCHAR(100),
                          quality_rating INTEGER CHECK (quality_rating BETWEEN 1 AND 5),
                          delivery_rating INTEGER CHECK (delivery_rating BETWEEN 1 AND 5),
                          price_rating INTEGER CHECK (price_rating BETWEEN 1 AND 5),
                          service_rating INTEGER CHECK (service_rating BETWEEN 1 AND 5),
                          overall_rating DECIMAL(3,2) GENERATED ALWAYS AS (
                                     (COALESCE(quality_rating, 0) + COALESCE(delivery_rating, 0) +
                                      COALESCE(price_rating, 0) + COALESCE(service_rating, 0)) / 4.0
                                     ) STORED,
                          comments TEXT
);

-- PURCHASE REQUISITION TABLE
CREATE TABLE inventory_purchase_requisition (
                                      id SERIAL PRIMARY KEY,
                                      pr_number VARCHAR(50) UNIQUE NOT NULL,
                                      pr_date DATE NOT NULL DEFAULT CURRENT_DATE,
                                      department_id INT,
                                      requested_by INT NOT NULL,
                                      cost_center_id INT,
                                      project_code VARCHAR(100),
                                      required_date DATE NOT NULL,
                                      priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),
                                      status VARCHAR(50) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'PARTIALLY_CONVERTED', 'FULLY_CONVERTED', 'CANCELLED')),
                                      purpose TEXT,
                                      justification TEXT,
                                      estimated_budget DECIMAL(15,2),
                                      total_amount DECIMAL(15,2) DEFAULT 0.00,
                                      approval_workflow_id VARCHAR(100),
                                      rejection_reason TEXT,
                                      approved_by BIGINT,
                                      approved_at TIMESTAMP,
                                      created_by INT NOT NULL,
                                      updated_by BIGINT,
                                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      is_deleted BOOLEAN DEFAULT FALSE
);

-- Purchase Requisition Line Items
CREATE TABLE inventory_purchase_requisition_item (
                                           id SERIAL PRIMARY KEY,
                                           pr_id INT NOT NULL REFERENCES inventory_purchase_requisition(id) ON DELETE CASCADE,
                                           line_number INTEGER NOT NULL,
                                           product_id INT,
                                           product_code VARCHAR(100),
                                           product_name VARCHAR(255) NOT NULL,
                                           unit_of_measure VARCHAR(20) NOT NULL,
                                           quantity_requested DECIMAL(15,3) NOT NULL,
                                           quantity_approved DECIMAL(15,3),
                                           estimated_unit_price DECIMAL(15,2),
                                           estimated_total DECIMAL(15,2) GENERATED ALWAYS AS (quantity_requested * COALESCE(estimated_unit_price, 0)) STORED,
                                           required_date DATE,
                                           status VARCHAR(50) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'PARTIALLY_ORDERED', 'FULLY_ORDERED')),
                                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           UNIQUE(pr_id, line_number)
);

-- Purchase Requisition Approval Workflow
CREATE TABLE inventory_purchase_requisition_approval (
                                               id SERIAL PRIMARY KEY,
                                               pr_id INT NOT NULL REFERENCES inventory_purchase_requisition(id) ON DELETE CASCADE,
                                               approver_id BIGINT NOT NULL,
                                               approval_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (approval_status IN ('PENDING', 'APPROVED', 'REJECTED')),
                                               approved_at TIMESTAMP,
                                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                               UNIQUE(pr_id)
);

-- PURCHASE ORDER TABLES
CREATE TABLE inventory_purchase_order (
                                id SERIAL PRIMARY KEY,
                                po_number VARCHAR(50) UNIQUE NOT NULL,
                                po_date DATE NOT NULL DEFAULT CURRENT_DATE,
                                supplier_id BIGINT NOT NULL REFERENCES supplier(id),
                                pr_id BIGINT REFERENCES inventory_purchase_requisition(id),
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
                                     pr_item_id BIGINT REFERENCES inventory_purchase_requisition_item(id),
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

-- GOODS RECEIPT NOTE TABLE
CREATE TABLE inventory_goods_receipt_note (
                                    id SERIAL PRIMARY KEY,
                                    grn_number VARCHAR(50) UNIQUE NOT NULL,
                                    grn_date DATE NOT NULL DEFAULT CURRENT_DATE,
                                    po_id BIGINT NOT NULL REFERENCES inventory_purchase_order(id),
                                    supplier_id BIGINT NOT NULL REFERENCES supplier(id),
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

-- Goods Receipt Note Line Items
CREATE TABLE goods_receipt_item (
                                    id SERIAL PRIMARY KEY,
                                    grn_id BIGINT NOT NULL REFERENCES inventory_goods_receipt_note(id) ON DELETE CASCADE,
                                    po_item_id BIGINT NOT NULL REFERENCES inventory_purchase_order_item(id),
                                    line_number INTEGER NOT NULL,
                                    product_id INT,
                                    product_code VARCHAR(100),
                                    product_name VARCHAR(255) NOT NULL,
                                    batch_number VARCHAR(100),
                                    manufacture_date DATE,
                                    expiry_date DATE,
                                    unit_of_measure VARCHAR(20) NOT NULL,
                                    quantity_ordered DECIMAL(15,3) NOT NULL,
                                    quantity_received DECIMAL(15,3) NOT NULL,
                                    quantity_accepted DECIMAL(15,3) NOT NULL,
                                    quantity_rejected DECIMAL(15,3) DEFAULT 0,
                                    unit_price DECIMAL(15,2) NOT NULL,
                                    line_total DECIMAL(15,2) GENERATED ALWAYS AS (quantity_accepted * unit_price) STORED,
                                    condition VARCHAR(20) CHECK (condition IN ('GOOD', 'DAMAGED', 'EXPIRED', 'DEFECTIVE')),
                                    rejection_reason TEXT,
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    UNIQUE(grn_id, line_number)
);


CREATE INDEX idx_supplier_code ON supplier(supplier_code) WHERE is_deleted = FALSE;
CREATE INDEX idx_supplier_status ON supplier(status) WHERE is_deleted = FALSE;
CREATE INDEX idx_supplier_type ON supplier(supplier_type) WHERE is_deleted = FALSE;
CREATE INDEX idx_supplier_rating ON supplier(overall_rating DESC) WHERE is_deleted = FALSE;
CREATE INDEX idx_supplier_preferred ON supplier(preferred_supplier) WHERE preferred_supplier = TRUE AND is_deleted = FALSE;
CREATE INDEX idx_supplier_contact_supplier ON supplier(email);
CREATE INDEX idx_supplier_rating_po ON supplier(overall_rating);

-- Purchase Requisition Indexes
CREATE INDEX idx_pr_number ON inventory_purchase_requisition(pr_number) WHERE is_deleted = FALSE;
CREATE INDEX idx_pr_status ON inventory_purchase_requisition(status) WHERE is_deleted = FALSE;
CREATE INDEX idx_pr_priority ON inventory_purchase_requisition(priority) WHERE is_deleted = FALSE;

-- Purchase Requisition Item Indexes
CREATE INDEX idx_pri_pr ON inventory_purchase_requisition_item(pr_id);
CREATE INDEX idx_pri_product ON inventory_purchase_requisition_item(product_id);
CREATE INDEX idx_pri_status ON inventory_purchase_requisition_item(status);

-- Purchase Order Indexes
CREATE INDEX idx_po_number ON inventory_purchase_order(po_number) WHERE is_deleted = FALSE;
CREATE INDEX idx_po_status ON inventory_purchase_order(status) WHERE is_deleted = FALSE;
CREATE INDEX idx_po_supplier ON inventory_purchase_order(supplier_id) WHERE is_deleted = FALSE;
CREATE INDEX idx_po_pr ON inventory_purchase_order(pr_id) WHERE is_deleted = FALSE;

-- Purchase Order Item Indexes
CREATE INDEX idx_poi_po ON inventory_purchase_order_item(po_id);
CREATE INDEX idx_poi_pr_item ON inventory_purchase_order_item(pr_item_id);
CREATE INDEX idx_poi_product ON inventory_purchase_order_item(product_id);
CREATE INDEX idx_poi_status ON inventory_purchase_order_item(status);

-- Goods Receipt Note Indexes
CREATE INDEX idx_grn_number ON inventory_goods_receipt_note(grn_number) WHERE is_deleted = FALSE;
CREATE INDEX idx_grn_status ON inventory_goods_receipt_note(status) WHERE is_deleted = FALSE;
CREATE INDEX idx_grn_po ON inventory_goods_receipt_note(po_id) WHERE is_deleted = FALSE;
CREATE INDEX idx_grn_supplier ON inventory_goods_receipt_note(supplier_id) WHERE is_deleted = FALSE;

-- Goods Receipt Item Indexes
CREATE INDEX idx_gri_grn ON goods_receipt_item(grn_id);
CREATE INDEX idx_gri_po_item ON goods_receipt_item(po_item_id);
CREATE INDEX idx_gri_product ON goods_receipt_item(product_id);
CREATE INDEX idx_gri_batch ON goods_receipt_item(batch_number);
CREATE INDEX idx_gri_expiry ON goods_receipt_item(expiry_date);

CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply trigger to all tables with updated_at column
CREATE TRIGGER update_supplier_updated_at BEFORE UPDATE ON supplier
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_purchase_requisition_updated_at BEFORE UPDATE ON inventory_purchase_requisition
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_purchase_requisition_item_updated_at BEFORE UPDATE ON inventory_purchase_requisition_item
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_purchase_requisition_approval_updated_at BEFORE UPDATE ON inventory_purchase_requisition_approval
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_purchase_order_updated_at BEFORE UPDATE ON inventory_purchase_order
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_purchase_order_item_updated_at BEFORE UPDATE ON inventory_purchase_order_item
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_goods_receipt_note_updated_at BEFORE UPDATE ON inventory_goods_receipt_note
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_goods_receipt_item_updated_at BEFORE UPDATE ON goods_receipt_item
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- VIEWS FOR COMMON QUERIES

-- View: Active Suppliers with Contact Info
CREATE OR REPLACE VIEW v_active_suppliers AS
SELECT

    sc.contact_person,
    sc.email as contact_email,
    sc.phone as contact_phone
FROM supplier sc
WHERE sc.status = 'ACTIVE' AND sc.is_deleted = FALSE;

-- View: Pending Purchase Requisitions
CREATE OR REPLACE VIEW v_pending_purchase_requisitions AS
SELECT
    pr.*,
    COUNT(pri.id) as total_items,
    SUM(pri.estimated_total) as calculated_total
FROM inventory_purchase_requisition pr
         LEFT JOIN inventory_purchase_requisition_item pri ON pr.id = pri.pr_id
WHERE pr.status IN ('PENDING_APPROVAL', 'APPROVED')
  AND pr.is_deleted = FALSE
GROUP BY pr.id;

-- View: Purchase Order Summary
CREATE OR REPLACE VIEW v_purchase_order_summary AS
SELECT
    po.*,
    s.name as supplier_name,
    s.supplier_code,
    COUNT(poi.id) as total_items,
    SUM(poi.quantity_ordered) as total_quantity_ordered,
    SUM(poi.quantity_received) as total_quantity_received
FROM inventory_purchase_order po
         INNER JOIN supplier s ON po.supplier_id = s.id
         LEFT JOIN inventory_purchase_order_item poi ON po.id = poi.po_id
WHERE po.is_deleted = FALSE
GROUP BY po.id, s.name, s.supplier_code;



