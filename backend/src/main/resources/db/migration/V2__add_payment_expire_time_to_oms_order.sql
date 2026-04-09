ALTER TABLE oms_order
ADD COLUMN payment_expire_time TIMESTAMP;

UPDATE oms_order
SET payment_expire_time = create_time + INTERVAL '15 minute'
WHERE payment_expire_time IS NULL
  AND status IN ('PENDING_PAYMENT', 'PAYMENT_PROCESSING');

CREATE INDEX idx_oms_order_payment_expire_time
ON oms_order (status, payment_expire_time);
