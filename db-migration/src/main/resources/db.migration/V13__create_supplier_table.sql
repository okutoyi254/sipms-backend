-- SUPPLIER MANAGEMENT TABLE
CREATE TABLE procurement.supplier (
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