CREATE TABLE inventory.low_stock_alerts (
                                  id BIGSERIAL PRIMARY KEY,
                                  product_id BIGINT NOT NULL,
                                  branch_id BIGINT NOT NULL,
                                  current_stock INTEGER NOT NULL,
                                  minimum_stock_level INTEGER NOT NULL,
                                  shortage_quantity INTEGER NOT NULL,
                                  severity VARCHAR(50) NOT NULL,
                                  status VARCHAR(50) NOT NULL,
                                  alert_date TIMESTAMP NOT NULL,
                                  acknowledged_date TIMESTAMP,
                                  acknowledged_by VARCHAR(255),
                                  transfer_request_id BIGINT,
                                  created_at TIMESTAMP,
                                  updated_at TIMESTAMP,
                                  created_by VARCHAR(255),
                                  updated_by VARCHAR(255),
                                  version INTEGER,

                                  CONSTRAINT fk_low_stock_alerts_product
                                      FOREIGN KEY (product_id) REFERENCES inventory.products(id),
                                  CONSTRAINT fk_low_stock_alerts_branch
                                      FOREIGN KEY (branch_id) REFERENCES "sipms-branch".branches(id),
                                  CONSTRAINT fk_low_stock_alerts_transfer_request
                                      FOREIGN KEY (transfer_request_id) REFERENCES logistics.stock_transfer_requests(id)
);

CREATE INDEX idx_low_stock_alerts_product_id ON inventory.low_stock_alerts(product_id);
CREATE INDEX idx_low_stock_alerts_branch_id ON inventory.low_stock_alerts(branch_id);
CREATE INDEX idx_low_stock_alerts_status ON inventory.low_stock_alerts(status);
CREATE INDEX idx_low_stock_alerts_severity ON inventory.low_stock_alerts(severity);
CREATE INDEX idx_low_stock_alerts_alert_date ON inventory.low_stock_alerts(alert_date);
CREATE INDEX idx_low_stock_alerts_transfer_request_id ON inventory.low_stock_alerts(transfer_request_id);

COMMENT ON TABLE inventory.low_stock_alerts IS 'Stores alerts for products with stock levels below minimum threshold';
COMMENT ON COLUMN inventory.low_stock_alerts.severity IS 'Alert severity level (enum: AlertSeverity)';
COMMENT ON COLUMN inventory.low_stock_alerts.status IS 'Alert status (enum: AlertStatus)';