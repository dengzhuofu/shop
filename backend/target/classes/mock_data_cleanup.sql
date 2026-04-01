DELETE FROM oms_order_item;
DELETE FROM oms_order;
DELETE FROM oms_cart_item;
DELETE FROM ums_user_address;
DELETE FROM pms_review;
DELETE FROM pms_sku;
DELETE FROM pms_product;
DELETE FROM sys_user WHERE username <> 'admin';
