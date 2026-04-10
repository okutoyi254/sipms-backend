ALTER TABLE procurement.inventory_purchase_requisition
    ADD description VARCHAR(255);

ALTER TABLE procurement.inventory_purchase_requisition
    ALTER COLUMN description SET NOT NULL;

ALTER TABLE procurement.inventory_purchase_requisition
    DROP COLUMN approval_workflow_id CASCADE ;

ALTER TABLE procurement.inventory_purchase_requisition
    DROP COLUMN cost_center_id;

ALTER TABLE procurement.inventory_purchase_requisition
    DROP COLUMN estimated_budget;

ALTER TABLE procurement.inventory_purchase_requisition
    DROP COLUMN project_code;

ALTER TABLE procurement.inventory_purchase_requisition
    DROP COLUMN purpose;

ALTER TABLE procurement.inventory_purchase_requisition
    DROP COLUMN required_date;