
CREATE TABLE branches (
                          id BIGSERIAL PRIMARY KEY,
                          branch_id VARCHAR(255) NOT NULL UNIQUE,
                          branch_name VARCHAR(255) NOT NULL,
                          branch_location VARCHAR(500),
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          created_by VARCHAR(255),
                          updated_by VARCHAR(255),

                          CONSTRAINT chk_branches_branch_id_not_empty CHECK (branch_id <> ''),
                          CONSTRAINT chk_branches_name_not_empty CHECK (branch_name <> '')
);

CREATE INDEX idx_branches_branch_id ON branches(branch_id);
CREATE INDEX idx_branches_name ON branches(branch_name);
CREATE INDEX idx_branches_location ON branches(branch_location);

COMMENT ON TABLE branches IS 'Stores branch/location information for the organization';
COMMENT ON COLUMN branches.id IS 'Primary key - auto-generated database ID';
COMMENT ON COLUMN branches.branch_id IS 'Business identifier for the branch (unique)';
COMMENT ON COLUMN branches.branch_name IS 'Display name of the branch';
COMMENT ON COLUMN branches.branch_location IS 'Physical location/address of the branch';
