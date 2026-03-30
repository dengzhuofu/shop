-- 用户表
CREATE TABLE sys_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 商品表
CREATE TABLE pms_product (
    id BIGSERIAL PRIMARY KEY,
    name JSONB NOT NULL,
    description JSONB, -- 存储富文本，支持多语言：{"zh": "<p>中文描述</p>", "en": "<p>English description</p>"}
    price DECIMAL(10, 2) NOT NULL,
    compare_at_price DECIMAL(10, 2),
    stock INT NOT NULL DEFAULT 0,
    pic VARCHAR(255),
    tags JSONB, -- 数组 ["Spring Sale", "NEW"]
    images JSONB, -- 轮播图数组
    app_image VARCHAR(255),
    specs JSONB, -- 核心参数 [{"label": "Max Power", "value": "1000W", "icon": "ActivityIcon"}]
    quick_know JSONB, -- 存储富文本特性列表/说明，支持多语言
    upsells JSONB, -- 促销配件
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 商品规格表 (SKU)
CREATE TABLE pms_sku (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku_code VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    pic VARCHAR(255),
    description JSONB, -- 存储富文本，支持多语言
    specs JSONB, -- SKU的规格键值对：{"zh": {"颜色": "黑色", "尺寸": "大"}, "en": {"Color": "Black", "Size": "Large"}}
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 购物车表
CREATE TABLE oms_cart_item (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    sku_id BIGINT,
    quantity INT NOT NULL DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 订单表
CREATE TABLE oms_order (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_sn VARCHAR(64) NOT NULL UNIQUE,
    total_amount DECIMAL(10, 2) NOT NULL,
    status INT NOT NULL DEFAULT 0, -- 0: 待付款, 1: 已付款, 2: 已发货, 3: 已完成, 4: 已取消
    receiver_name VARCHAR(100),
    receiver_phone VARCHAR(32),
    receiver_address VARCHAR(255),
    pay_type INT, -- 1: Alipay, 2: Wechat, 3: Credit Card, 4: PayPal
    pay_time TIMESTAMP,
    delivery_company VARCHAR(64),
    delivery_sn VARCHAR(64),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 订单项表
CREATE TABLE oms_order_item (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    sku_id BIGINT,
    product_name VARCHAR(255),
    product_pic VARCHAR(255),
    sku_code VARCHAR(100),
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10, 2) NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 评论表
CREATE TABLE pms_review (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT,
    user_name VARCHAR(50) NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    title VARCHAR(255),
    content TEXT,
    images JSONB,
    verified_purchase BOOLEAN DEFAULT TRUE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入初始用户 (密码为 '123456')
INSERT INTO sys_user (username, password, nickname) VALUES ('admin', '123456', '管理员');

-- 插入示例商品数据
INSERT INTO pms_product (
    name, description, price, compare_at_price, stock, pic, 
    tags, images, app_image, specs, quick_know, upsells
) VALUES 
(
    '{"zh": "isinwheel S9 Pro 气动轮胎电动滑板车", "en": "isinwheel S9 Pro Pneumatic Tire Electric Scooter"}', 
    '{"zh": "<p>isinwheel S9 Pro 气动轮胎电动滑板车...</p>", "en": "<p>isinwheel S9 Pro Pneumatic Tire Electric Scooter...</p>"}', 
    269.99, 399.99, 100, 'https://via.placeholder.com/400x400?text=S9+Pro+1',
    '["Spring Sale", "Save $130.00"]',
    '["https://via.placeholder.com/400x400?text=S9+Pro+1", "https://via.placeholder.com/400x400?text=S9+Pro+2"]',
    'https://via.placeholder.com/60x120?text=APP',
    '[{"label": "Motor Capacity", "value": "350W", "icon": "ZapIcon"}, {"label": "Max Range", "value": "19 Miles", "icon": "NavigationIcon"}, {"label": "Top Speed", "value": "19 MPH", "icon": "ActivityIcon"}, {"label": "Battery Capacity", "value": "36V 7.5Ah", "icon": "BatteryIcon"}]',
    '{"zh": "<ul><li><strong>350W</strong> 电机容量</li></ul>", "en": "<ul><li><strong>350W</strong> Motor Capacity</li></ul>"}',
    '[]'
),
(
    '{"zh": "S Nova Pro 通勤电动滑板车", "en": "S Nova Pro Commuting Electric Scooter"}', 
    '{"zh": "<p>S Nova Pro 通勤电动滑板车...</p>", "en": "<p>S Nova Pro Commuting Electric Scooter...</p>"}', 
    489.99, 599.99, 100, 'https://via.placeholder.com/400x400?text=S+Nova+Pro+1',
    '["NEW", "Spring Sale", "Save $110.00"]',
    '["https://via.placeholder.com/400x400?text=S+Nova+Pro+1", "https://via.placeholder.com/400x400?text=S+Nova+Pro+2"]',
    'https://via.placeholder.com/60x120?text=APP',
    '[{"label": "Max Power", "value": "1000W", "icon": "ZapIcon"}, {"label": "Max Range", "value": "38 Miles", "icon": "NavigationIcon"}, {"label": "Top Speed", "value": "28 MPH", "icon": "ActivityIcon"}, {"label": "Battery Capacity", "value": "48V 13Ah", "icon": "BatteryIcon"}]',
    '{"zh": "<ul><li><strong>1000W</strong> 最大功率</li></ul>", "en": "<ul><li><strong>1000W</strong> Max Power</li></ul>"}',
    '[]'
),
(
    '{"zh": "GT1 双电机越野电动滑板车", "en": "GT1 Dual Motor Off-Road Electric Scooter"}', 
    '{"zh": "<p>GT1 双电机越野电动滑板车...</p>", "en": "<p>GT1 Dual Motor Off-Road Electric Scooter...</p>"}', 
    649.99, 799.99, 100, 'https://via.placeholder.com/400x400?text=GT1',
    '["NEW", "Spring Sale", "Save $150.00"]',
    '["https://via.placeholder.com/400x400?text=GT1"]',
    '',
    '[{"label": "Motor Capacity", "value": "800W*2", "icon": "ZapIcon"}, {"label": "Max Range", "value": "35 Miles", "icon": "NavigationIcon"}, {"label": "Top Speed", "value": "32 MPH", "icon": "ActivityIcon"}, {"label": "Battery Capacity", "value": "48V 13Ah", "icon": "BatteryIcon"}]',
    '{"zh": "<ul><li><strong>800W*2</strong> 电机容量</li></ul>", "en": "<ul><li><strong>800W*2</strong> Motor Capacity</li></ul>"}',
    '[]'
),
(
    '{"zh": "isinwheel H7Pro 1200W 高端通勤电动滑板车", "en": "isinwheel H7Pro 1200W High-End Commuting Electric Scooter"}', 
    '{"zh": "<p>isinwheel H7Pro 1200W 高端通勤电动滑板车...</p>", "en": "<p>isinwheel H7Pro 1200W High-End Commuting Electric Scooter...</p>"}', 
    849.99, 1099.99, 100, 'https://via.placeholder.com/400x400?text=H7Pro',
    '["HOT", "Spring Sale", "Save $250.00"]',
    '["https://via.placeholder.com/400x400?text=H7Pro"]',
    '',
    '[{"label": "Motor Capacity", "value": "1200W", "icon": "ZapIcon"}, {"label": "Max Range", "value": "43 Miles", "icon": "NavigationIcon"}, {"label": "Top Speed", "value": "38 MPH", "icon": "ActivityIcon"}, {"label": "Fat Tires", "value": "16*4\"", "icon": "BatteryIcon"}]',
    '{"zh": "<ul><li><strong>1200W</strong> 电机容量</li></ul>", "en": "<ul><li><strong>1200W</strong> Motor Capacity</li></ul>"}',
    '[]'
),
(
    '{"zh": "U1 通勤电动自行车", "en": "U1 Electric Bike Commuter Ebike"}', 
    '{"zh": "<p>U1 通勤电动自行车...</p>", "en": "<p>U1 Electric Bike Commuter Ebike...</p>"}', 
    899.99, 1199.99, 100, 'https://via.placeholder.com/400x400?text=U1+Bike',
    '["HOT", "Save $300.00"]',
    '["https://via.placeholder.com/400x400?text=U1+Bike"]',
    '',
    '[{"label": "Motor Capacity", "value": "500W", "icon": "ZapIcon"}, {"label": "Max Range", "value": "45 Miles", "icon": "NavigationIcon"}, {"label": "Top Speed", "value": "20 MPH", "icon": "ActivityIcon"}, {"label": "Battery Capacity", "value": "48V 15Ah", "icon": "BatteryIcon"}]',
    '{"zh": "<ul><li><strong>500W</strong> 电机容量</li></ul>", "en": "<ul><li><strong>500W</strong> Motor Capacity</li></ul>"}',
    '[]'
),
(
    '{"zh": "V8 带遥控电动滑板", "en": "V8 Electric Skateboard with Remote"}', 
    '{"zh": "<p>V8 带遥控电动滑板...</p>", "en": "<p>V8 Electric Skateboard with Remote...</p>"}', 
    199.99, 299.99, 100, 'https://via.placeholder.com/400x400?text=V8+Skateboard',
    '["Flash Sale"]',
    '["https://via.placeholder.com/400x400?text=V8+Skateboard"]',
    '',
    '[{"label": "Motor Capacity", "value": "400W", "icon": "ZapIcon"}, {"label": "Max Range", "value": "12 Miles", "icon": "NavigationIcon"}, {"label": "Top Speed", "value": "15 MPH", "icon": "ActivityIcon"}, {"label": "Battery Capacity", "value": "36V 4Ah", "icon": "BatteryIcon"}]',
    '{"zh": "<ul><li><strong>400W</strong> 电机容量</li></ul>", "en": "<ul><li><strong>400W</strong> Motor Capacity</li></ul>"}',
    '[]'
);

INSERT INTO pms_sku (product_id, sku_code, price, stock, pic, description, specs) VALUES 
(1, 'IP15P-256G-BLK', 9999.00, 50, 'https://example.com/iphone15pro-black.jpg', '{"zh": "<p>黑色版 256GB</p>", "en": "<p>Black 256GB</p>"}', '{"zh": {"颜色": "黑色", "内存": "256GB"}, "en": {"Color": "Black", "Storage": "256GB"}}'),
(1, 'IP15P-256G-WHT', 9999.00, 50, 'https://example.com/iphone15pro-white.jpg', '{"zh": "<p>白色版 256GB</p>", "en": "<p>White 256GB</p>"}', '{"zh": {"颜色": "白色", "内存": "256GB"}, "en": {"Color": "White", "Storage": "256GB"}}');

-- 插入示例评论数据
INSERT INTO pms_review (product_id, user_id, user_name, rating, title, content, images, verified_purchase) VALUES
(1, 1, 'Veezy f.C.', 5, 'Adult scooter', 'The scooter is a lightweight pickup and very easy to carry on the bus and in back of a car trunk. The function on the scooter is very simple and the speed lifts off at 19 speed. I can literally drive the scooter for 3 days without it going dead on me and the battery life lasts longer', '["https://example.com/review1-1.jpg", "https://example.com/review1-2.jpg"]', TRUE),
(1, NULL, 'Lisa B.', 5, 'Well Made Scooter', 'Ordered the scooter as a gift for my son for the holidays. It was super easy to put together and begin using right away. He is over 6ft tall and the scooter supports his weight and height perfectly.', '[]', TRUE),
(1, NULL, 'John D.', 4, 'Great Value', 'Good product for the price. The battery life is slightly less than advertised but overall very satisfied.', '[]', TRUE);

-- 插入示例订单数据
INSERT INTO oms_order (id, user_id, order_sn, total_amount, status, receiver_name, receiver_phone, receiver_address, pay_type, create_time) VALUES
(1, 1, 'ORD202603300001', 9999.00, 0, 'John Doe', '13800138000', '123 Tech Street, Silicon Valley, CA', 3, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(2, 1, 'ORD202603300002', 19998.00, 2, 'John Doe', '13800138000', '123 Tech Street, Silicon Valley, CA', 4, CURRENT_TIMESTAMP - INTERVAL '3 days');

-- 插入示例订单项数据
INSERT INTO oms_order_item (order_id, product_id, sku_id, product_name, product_pic, sku_code, quantity, price) VALUES
(1, 1, 1, 'iPhone 15 Pro', 'https://example.com/iphone15pro-black.jpg', 'IP15P-256G-BLK', 1, 9999.00),
(2, 1, 1, 'iPhone 15 Pro', 'https://example.com/iphone15pro-black.jpg', 'IP15P-256G-BLK', 1, 9999.00),
(2, 1, 2, 'iPhone 15 Pro', 'https://example.com/iphone15pro-white.jpg', 'IP15P-256G-WHT', 1, 9999.00);

-- 重置序列 (以防后续插入主键冲突)
SELECT setval('oms_order_id_seq', (SELECT MAX(id) FROM oms_order));
SELECT setval('oms_order_item_id_seq', (SELECT MAX(id) FROM oms_order_item));

