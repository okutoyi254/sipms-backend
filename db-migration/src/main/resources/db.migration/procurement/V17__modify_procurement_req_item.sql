ALTER TABLE inventory_purchase_requisition_item
    ADD estimated_unit_cost DECIMAL(15, 2);


ALTER TABLE inventory_purchase_requisition_item
    ALTER COLUMN quantity_approved SET DEFAULT 0;