-- Property Management System Database Migration
-- Refactoring: Field Renames, Removals, and Decomposition
-- Date: 2026-04-18
-- Execute these DDL statements to update the database schema

-- ============================================
-- 1. RENAME COLUMNS
-- ============================================

ALTER TABLE properties RENAME COLUMN propertyName TO name;
ALTER TABLE properties RENAME COLUMN developerName TO developer;
ALTER TABLE properties RENAME COLUMN description TO basicDescription;
ALTER TABLE properties RENAME COLUMN pitchReadyPhrases TO keySellingPoints;

-- ============================================
-- 2. DROP COLUMNS (Remove obsolete fields)
-- ============================================

ALTER TABLE properties DROP COLUMN propertyType;
ALTER TABLE properties DROP COLUMN unitType;
ALTER TABLE properties DROP COLUMN priceComputations;
ALTER TABLE properties DROP COLUMN developerLinks;

-- ============================================
-- 3. ADD NEW COLUMNS (Decomposed from developerLinks)
-- ============================================

ALTER TABLE properties ADD COLUMN brochurePdfUrl TEXT;
ALTER TABLE properties ADD COLUMN inventoryLink TEXT;

-- ============================================
-- 4. MIGRATE listingType VALUES TO COMMA-SEPARATED FORMAT
-- ============================================
-- This handles existing records that have single listingType values
-- and ensures they are compatible with the new comma-separated format

-- Example: If you have existing "Pre-Selling" values, they remain as "Pre-Selling"
-- If you have "Ready-For-Occupancy", they remain as "Ready-For-Occupancy"
-- Multi-select values will be comma-separated: "Pre-Selling,RFO" etc.

-- No UPDATE needed if values are already in the correct format
-- If you need to standardize any values, run manual UPDATE statements as needed:
-- UPDATE properties SET listingType = 'Pre-Selling' WHERE listingType = 'preselling';
-- UPDATE properties SET listingType = 'RFO' WHERE listingType = 'ready-for-occupancy';

-- ============================================
-- 5. VERIFY MIGRATION (Post-execution checks)
-- ============================================
-- After running the above statements, verify the new schema:

-- DESCRIBE properties;
-- SELECT * FROM properties LIMIT 1;

-- Expected columns after migration:
-- - id (UUID) - unchanged
-- - name (VARCHAR) - renamed from propertyName
-- - basicDescription (TEXT) - renamed from description
-- - developer (VARCHAR) - renamed from developerName
-- - priceRangeMin (DOUBLE) - unchanged
-- - priceRangeMax (DOUBLE) - unchanged
-- - location (VARCHAR) - unchanged
-- - listingType (VARCHAR) - unchanged (format now supports comma-separated values)
-- - petFriendly (BOOLEAN) - unchanged
-- - parkingAvailable (BOOLEAN) - unchanged
-- - turnoverDate (VARCHAR) - unchanged
-- - amenities (TEXT) - unchanged
-- - keySellingPoints (TEXT) - renamed from pitchReadyPhrases
-- - brochurePdfUrl (TEXT) - NEW
-- - inventoryLink (TEXT) - NEW
-- - createdAt (TIMESTAMP) - unchanged
-- - updatedAt (TIMESTAMP) - unchanged
-- - createdBy (VARCHAR/UUID) - unchanged

-- REMOVED columns:
-- - propertyType
-- - unitType
-- - priceComputations
-- - developerLinks

-- ============================================
-- NOTES
-- ============================================
-- 1. Back up the database before executing these statements
-- 2. Test in a development environment first
-- 3. Update any database connection strings or ORM configurations if needed
-- 4. After migration, redeploy backend application to use updated entity fields
-- 5. Frontend will automatically use new field names from API responses
-- 6. listingType now supports arrays: ["Pre-Selling", "RFO", "Rent-To-Own"]
--    These are stored as comma-separated strings in the database

-- ============================================
-- PHASE 2: PROPERTY UNITS (New Feature)
-- ============================================
-- Date: 2026-04-29
-- Adds support for multiple units per property

-- ============================================
-- 1. CREATE property_units TABLE
-- ============================================

CREATE TABLE IF NOT EXISTS property_units (
    id VARCHAR(36) PRIMARY KEY,
    property_id VARCHAR(36) NOT NULL,
    unit_type VARCHAR(255) NOT NULL,
    floor_area DOUBLE PRECISION,
    lot_area DOUBLE PRECISION,
    reservation_fee DOUBLE PRECISION NOT NULL,
    equity_period_months INT NOT NULL,
    monthly_equity DOUBLE PRECISION NOT NULL,
    total_selling_price DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_property_units_property_id ON property_units(property_id);

-- ============================================
-- 1b. CREATE TRIGGER FOR AUTO-UPDATE TIMESTAMP
-- ============================================

CREATE OR REPLACE FUNCTION update_property_units_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP; 
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_property_units_updated_at ON property_units;

CREATE TRIGGER trigger_property_units_updated_at
BEFORE UPDATE ON property_units
FOR EACH ROW
EXECUTE FUNCTION update_property_units_updated_at();

-- ============================================
-- 2. CREATE property_unit_financing TABLE (ElementCollection)
-- ============================================
-- Stores financing types for each unit (supports multi-select)
-- Example: ["Cash Only", "Bank Financing", "In-House"]

CREATE TABLE IF NOT EXISTS property_unit_financing (
    unit_id VARCHAR(36) NOT NULL,
    financing_type VARCHAR(255) NOT NULL,
    PRIMARY KEY (unit_id, financing_type),
    FOREIGN KEY (unit_id) REFERENCES property_units(id) ON DELETE CASCADE
);

-- ============================================
-- 3. MIGRATE EXISTING PROPERTIES (Create default unit for each)
-- ============================================
-- Creates 1 default unit per existing property
-- This ensures backward compatibility

INSERT INTO property_units (
    id, 
    property_id, 
    unit_type, 
    floor_area, 
    lot_area, 
    reservation_fee, 
    equity_period_months, 
    monthly_equity, 
    total_selling_price, 
    created_at, 
    updated_at
)
SELECT 
    UUID(),
    p.id,
    'Default' AS unit_type,
    NULL AS floor_area,
    NULL AS lot_area,
    0 AS reservation_fee,
    0 AS equity_period_months,
    0 AS monthly_equity,
    0 AS total_selling_price,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM properties p
WHERE NOT EXISTS (
    SELECT 1 FROM property_units pu WHERE pu.property_id = p.id
);

-- ============================================
-- 4. ADD DEFAULT FINANCING TYPE FOR MIGRATED UNITS
-- ============================================

INSERT INTO property_unit_financing (unit_id, financing_type)
SELECT id, 'Cash Only' AS financing_type
FROM property_units
WHERE id NOT IN (SELECT DISTINCT unit_id FROM property_unit_financing);

-- ============================================
-- 5. VERIFY MIGRATION
-- ============================================
-- SELECT COUNT(*) as unit_count FROM property_units;
-- SELECT pu.id, pu.property_id, pu.unit_type, COUNT(puf.financing_type) as financing_options 
-- FROM property_units pu
-- LEFT JOIN property_unit_financing puf ON pu.id = puf.unit_id
-- GROUP BY pu.id;

-- ============================================
-- NOTES - PHASE 2
-- ============================================
-- 1. property_units stores individual unit information
-- 2. property_unit_financing stores financing options via ElementCollection
-- 3. All existing properties receive 1 "Default" unit for backward compatibility
-- 4. Units are cascade deleted when property is deleted
-- 5. monthly_equity and total_selling_price are MANUALLY ENTERED (not calculated)
--    Future formula (if needed): totalSellingPrice = reservationFee + (monthlyEquity * equityPeriodMonths)
-- 6. listingType is inherited from parent property (not duplicated in units)

-- ============================================
-- PHASE 3: REAL-TIME MESSAGING
-- ============================================
-- Date: 2026-05-15
-- Adds persistent conversations and messages for registered users and agents

CREATE TABLE IF NOT EXISTS conversations (
    id BIGSERIAL PRIMARY KEY,
    registered_user_id VARCHAR(36) NOT NULL,
    assigned_agent_id VARCHAR(36),
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_conversations_status CHECK (status IN ('OPEN', 'ASSIGNED', 'CLOSED')),
    CONSTRAINT fk_conversations_registered_user FOREIGN KEY (registered_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_conversations_assigned_agent FOREIGN KEY (assigned_agent_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS messages (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    sender_id VARCHAR(36) NOT NULL,
    sender_role VARCHAR(30) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_messages_sender_role CHECK (sender_role IN ('REGISTERED_USER', 'AGENT')),
    CONSTRAINT fk_messages_conversation FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_conversations_registered_user_id ON conversations(registered_user_id);
CREATE INDEX IF NOT EXISTS idx_conversations_assigned_agent_id ON conversations(assigned_agent_id);
CREATE INDEX IF NOT EXISTS idx_conversations_status ON conversations(status);
CREATE INDEX IF NOT EXISTS idx_messages_conversation_id_created_at ON messages(conversation_id, created_at);

CREATE OR REPLACE FUNCTION update_conversations_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_conversations_updated_at ON conversations;

CREATE TRIGGER trigger_conversations_updated_at
BEFORE UPDATE ON conversations
FOR EACH ROW
EXECUTE FUNCTION update_conversations_updated_at();
