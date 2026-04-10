ALTER TABLE procurement.inventory_purchase_requisition_approval
    ADD approval_level INTEGER;

ALTER TABLE procurement.inventory_purchase_requisition_approval
    ALTER COLUMN approval_level SET NOT NULL;