CREATE TABLE logistics.stock_transfer_requests (
                                         id BIGSERIAL PRIMARY KEY,
                                         transfer_number VARCHAR(50) NOT NULL UNIQUE,
                                         source_branch_id BIGINT NOT NULL,
                                         destination_branch_id BIGINT NOT NULL,
                                         status VARCHAR(50) NOT NULL,
                                         priority VARCHAR(50) NOT NULL,
                                         request_date DATE NOT NULL,
                                         approval_date DATE,
                                         requested_by VARCHAR(100),
                                         approved_by VARCHAR(100),
                                         shipped_by VARCHAR(100),
                                         received_by VARCHAR(100),
                                         shipping_carrier VARCHAR(100),
                                         tracking_number VARCHAR(100),
                                         created_at TIMESTAMP,
                                         updated_at TIMESTAMP,
                                         created_by VARCHAR(255),
                                         updated_by VARCHAR(255),
                                         version INTEGER,

                                         CONSTRAINT fk_stock_transfer_requests_source_branch
                                             FOREIGN KEY (source_branch_id) REFERENCES "sipms-branch".branches(id),
                                         CONSTRAINT fk_stock_transfer_requests_destination_branch
                                             FOREIGN KEY (destination_branch_id) REFERENCES "sipms-branch".branches(id),
                                         CONSTRAINT chk_different_branches
                                             CHECK (source_branch_id != destination_branch_id)
);

CREATE INDEX idx_stock_transfer_requests_status ON logistics.stock_transfer_requests(status);
CREATE INDEX idx_stock_transfer_requests_request_date ON logistics.stock_transfer_requests(request_date);

CREATE INDEX idx_stock_transfer_requests_branch_status
    ON logistics.stock_transfer_requests(source_branch_id, status);

COMMENT ON TABLE logistics.stock_transfer_requests IS 'Stores stock transfer requests between branches';