ALTER TABLE oms_order ADD COLUMN IF NOT EXISTS pricing_exchange_rate NUMERIC(18,8) NOT NULL DEFAULT 1;
ALTER TABLE pms_review ADD COLUMN IF NOT EXISTS is_demo BOOLEAN NOT NULL DEFAULT FALSE;
-- Pre-CBJJ feedback is retained privately and must not be presented as CBJJ customer feedback.
-- This one-time migration runs before CBJJ reviews can be submitted.
UPDATE pms_review SET is_demo = TRUE;
