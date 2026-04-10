-- PURCHASE REQUISITION TABLE
CREATE TABLE procurement.inventory_purchase_requisition (
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
                                                     pr_id INT NOT NULL REFERENCES procurement.inventory_purchase_requisition(id) ON DELETE CASCADE,
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
                                                         pr_id INT NOT NULL REFERENCES procurement.inventory_purchase_requisition(id) ON DELETE CASCADE,
                                                         approver_id BIGINT NOT NULL,
                                                         approval_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (approval_status IN ('PENDING', 'APPROVED', 'REJECTED')),
                                                         approved_at TIMESTAMP,
                                                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                         UNIQUE(pr_id)
);