ALTER TABLE inventory_purchase_requisition
    ADD description VARCHAR(255);

ALTER TABLE inventory_purchase_requisition
    ALTER COLUMN description SET NOT NULL;

ALTER TABLE inventory_purchase_requisition
    DROP COLUMN approval_workflow_id CASCADE ;

ALTER TABLE inventory_purchase_requisition
    DROP COLUMN cost_center_id;

ALTER TABLE inventory_purchase_requisition
    DROP COLUMN estimated_budget;

ALTER TABLE inventory_purchase_requisition
    DROP COLUMN project_code;

ALTER TABLE inventory_purchase_requisition
    DROP COLUMN purpose;

ALTER TABLE inventory_purchase_requisition
    DROP COLUMN required_date;