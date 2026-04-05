DROP TABLE IF EXISTS pay_payment_intent CASCADE;
DROP TABLE IF EXISTS oms_order_item CASCADE;
DROP TABLE IF EXISTS oms_order CASCADE;
DROP TABLE IF EXISTS sms_coupon_user CASCADE;
DROP TABLE IF EXISTS sms_coupon CASCADE;
DROP TABLE IF EXISTS ums_user_address CASCADE;
DROP TABLE IF EXISTS oms_cart_item CASCADE;
DROP TABLE IF EXISTS pms_review CASCADE;
DROP TABLE IF EXISTS pms_sku CASCADE;
DROP TABLE IF EXISTS pms_product CASCADE;
DROP TABLE IF EXISTS pms_category CASCADE;
DROP TABLE IF EXISTS sys_user CASCADE;

CREATE TABLE sys_user (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(128) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(64) NOT NULL,
    last_name VARCHAR(64) NOT NULL,
    nickname VARCHAR(64),
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    last_login_time TIMESTAMP,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pms_category (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT,
    slug VARCHAR(128) NOT NULL UNIQUE,
    name TEXT NOT NULL,
    description TEXT,
    hero_image VARCHAR(255),
    menu_image VARCHAR(255),
    sort_order INT NOT NULL DEFAULT 0,
    published BOOLEAN NOT NULL DEFAULT TRUE,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pms_product (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL REFERENCES pms_category(id),
    slug VARCHAR(180) NOT NULL UNIQUE,
    name TEXT NOT NULL,
    subtitle TEXT,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    compare_at_price DECIMAL(10, 2),
    stock INT NOT NULL DEFAULT 0,
    pic VARCHAR(255),
    tags TEXT,
    images TEXT,
    app_image VARCHAR(255),
    specs TEXT,
    quick_know TEXT,
    upsells TEXT,
    spec_table TEXT,
    box_items TEXT,
    faqs TEXT,
    published BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pms_sku (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES pms_product(id),
    sku_code VARCHAR(100) NOT NULL UNIQUE,
    price DECIMAL(10, 2) NOT NULL,
    compare_at_price DECIMAL(10, 2),
    stock INT NOT NULL DEFAULT 0,
    pic VARCHAR(255),
    images TEXT,
    description TEXT,
    specs TEXT,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pms_review (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES pms_product(id),
    user_id BIGINT,
    user_name VARCHAR(50) NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    title VARCHAR(255),
    content TEXT,
    images TEXT,
    verified_purchase BOOLEAN DEFAULT TRUE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE oms_cart_item (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES sys_user(id),
    product_id BIGINT NOT NULL REFERENCES pms_product(id),
    sku_id BIGINT NOT NULL REFERENCES pms_sku(id),
    quantity INT NOT NULL DEFAULT 1,
    selected_attributes_snapshot TEXT,
    selected_addons_snapshot TEXT,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ums_user_address (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES sys_user(id),
    country VARCHAR(64) NOT NULL DEFAULT 'United States',
    first_name VARCHAR(64),
    last_name VARCHAR(64),
    phone VARCHAR(32),
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(64),
    state VARCHAR(64),
    zip_code VARCHAR(32),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sms_coupon (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    title TEXT NOT NULL,
    description TEXT,
    threshold_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(10, 2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sms_coupon_user (
    id BIGSERIAL PRIMARY KEY,
    coupon_id BIGINT NOT NULL REFERENCES sms_coupon(id),
    user_id BIGINT NOT NULL REFERENCES sys_user(id),
    status VARCHAR(32) NOT NULL DEFAULT 'CLAIMED',
    claimed_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(coupon_id, user_id)
);

CREATE TABLE oms_order (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES sys_user(id),
    order_sn VARCHAR(64) NOT NULL UNIQUE,
    subtotal_amount DECIMAL(10, 2) NOT NULL,
    tax_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING_PAYMENT',
    payment_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    currency VARCHAR(16) NOT NULL DEFAULT 'USD',
    country VARCHAR(16) NOT NULL DEFAULT 'US',
    preview_token VARCHAR(128),
    coupon_code VARCHAR(64),
    coupon_user_id BIGINT REFERENCES sms_coupon_user(id),
    coupon_discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    payment_intent_id BIGINT,
    receiver_name VARCHAR(100),
    receiver_phone VARCHAR(32),
    receiver_address VARCHAR(255),
    receiver_country VARCHAR(64),
    receiver_first_name VARCHAR(64),
    receiver_last_name VARCHAR(64),
    receiver_address_line1 VARCHAR(255),
    receiver_address_line2 VARCHAR(255),
    receiver_city VARCHAR(64),
    receiver_state VARCHAR(64),
    receiver_zip_code VARCHAR(32),
    payment_method VARCHAR(32),
    pay_txn_no VARCHAR(64),
    checkout_source VARCHAR(16) DEFAULT 'cart',
    shipping_method VARCHAR(128),
    shipping_amount DECIMAL(10,2) DEFAULT 0,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    remark VARCHAR(255),
    pay_time TIMESTAMP,
    delivery_company VARCHAR(64),
    delivery_sn VARCHAR(64),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE oms_order_item (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES oms_order(id),
    product_id BIGINT NOT NULL REFERENCES pms_product(id),
    sku_id BIGINT NOT NULL REFERENCES pms_sku(id),
    product_name TEXT NOT NULL,
    product_pic VARCHAR(255),
    sku_code VARCHAR(100),
    sku_attributes_snapshot TEXT,
    addons_snapshot TEXT,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    line_amount DECIMAL(10, 2) NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pay_payment_intent (
    id BIGSERIAL PRIMARY KEY,
    intent_no VARCHAR(64) NOT NULL UNIQUE,
    order_id BIGINT NOT NULL REFERENCES oms_order(id),
    user_id BIGINT NOT NULL REFERENCES sys_user(id),
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(16) NOT NULL DEFAULT 'USD',
    method_code VARCHAR(32) NOT NULL,
    provider_key VARCHAR(32) NOT NULL DEFAULT 'mock',
    status VARCHAR(32) NOT NULL DEFAULT 'CREATED',
    client_secret VARCHAR(128),
    mock_result VARCHAR(32) DEFAULT 'pending',
    paid_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO sys_user (id, email, password_hash, first_name, last_name, nickname, email_verified, status)
VALUES
(1, 'admin@isinwheel.local', '123456', 'Admin', 'User', 'Admin User', FALSE, 'ACTIVE'),
(2, 'sarah@isinwheel.local', '123456', 'Sarah', 'Miller', 'Sarah Miller', FALSE, 'ACTIVE');

INSERT INTO pms_category (id, slug, name, description, hero_image, menu_image, sort_order, published) VALUES
(1, 'electric-scooters',
 '{"en":"Electric Scooter","zh":"电动滑板车"}',
 '{"en":"Portable commuter scooters built for the city.","zh":"为城市通勤打造的便携式电动滑板车。"}',
 'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=1600',
 'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=600',
 1, TRUE),
(2, 'electric-bike',
 '{"en":"Electric Bike","zh":"电动自行车"}',
 '{"en":"Powerful ebikes for commuting and weekend adventures.","zh":"适合通勤与周末骑行的高性能电动自行车。"}',
 'https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&q=80&w=1600',
 'https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&q=80&w=600',
 2, TRUE),
(3, 'electric-skateboard',
 '{"en":"Electric Skateboard","zh":"电动滑板"}',
 '{"en":"Responsive boards tuned for carving and speed.","zh":"为 carving 与速度感打造的高响应电动滑板。"}',
 'https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&q=80&w=1600',
 'https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&q=80&w=600',
 3, TRUE),
(4, 'accessories',
 '{"en":"Accessories","zh":"配件"}',
 '{"en":"Ride-ready add-ons and replacement parts.","zh":"骑行必备附加配件与替换件。"}',
 'https://images.unsplash.com/photo-1516117172878-fd2c41f4a759?auto=format&fit=crop&q=80&w=1600',
 'https://images.unsplash.com/photo-1516117172878-fd2c41f4a759?auto=format&fit=crop&q=80&w=600',
 4, TRUE);

INSERT INTO pms_product (
    id, category_id, slug, name, subtitle, description, price, compare_at_price, stock, pic, tags, images, app_image,
    specs, quick_know, upsells, spec_table, box_items, faqs, published, sort_order
) VALUES
(1, 1, 'isinwheel-s9-pro-pneumatic-tire-electric-scooter',
 '{"en":"isinwheel S9 Pro Pneumatic Tire Electric Scooter","zh":"isinwheel S9 Pro 气动轮胎电动滑板车"}',
 '{"en":"Urban-ready commuter scooter","zh":"适合城市通勤的轻便滑板车"}',
 '{"en":"<p>The S9 Pro is built for last-mile commuting with a foldable frame, pneumatic tires, and stable everyday performance.</p>","zh":"<p>S9 Pro 采用可折叠车架与气动轮胎，适合最后一公里通勤与日常出行。</p>"}',
 269.99, 399.99, 160,
 'https://images.unsplash.com/photo-1593941707874-ef25b8b4a92b?auto=format&fit=crop&q=80&w=1200',
 '{"en":["Spring Sale"],"zh":["春季促销"]}',
 '["https://images.unsplash.com/photo-1593941707874-ef25b8b4a92b?auto=format&fit=crop&q=80&w=1200","https://images.unsplash.com/photo-1587574293340-e0011c4e8ecf?auto=format&fit=crop&q=80&w=1200","https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=1200"]',
 '',
 '{"en":[{"label":"Motor Capacity","value":"350W","icon":"ZapIcon"},{"label":"Max Range","value":"19 Miles","icon":"NavigationIcon"},{"label":"Top Speed","value":"19 MPH","icon":"ActivityIcon"},{"label":"Battery","value":"36V 7.5Ah","icon":"BatteryIcon"}],"zh":[{"label":"电机功率","value":"350W","icon":"ZapIcon"},{"label":"最长续航","value":"19 英里","icon":"NavigationIcon"},{"label":"最高时速","value":"19 MPH","icon":"ActivityIcon"},{"label":"电池","value":"36V 7.5Ah","icon":"BatteryIcon"}]}',
 '{"en":["350W motor tuned for city commuting","10-inch pneumatic tires smooth out cracked pavement","Fold-and-go frame for apartment and office life"],"zh":["350W 电机适合城市通勤","10 英寸气动轮胎提升舒适性","可折叠车架，适合公寓与办公室场景"]}',
 '{"en":[{"code":"warranty-1y","name":"1-Year Extended Warranty","description":"Add one extra year of coverage.","price":79.99,"compareAtPrice":99.99,"image":"https://images.unsplash.com/photo-1517677208171-0bc6725a3e60?auto=format&fit=crop&q=80&w=600"},{"code":"cable-lock","name":"Scooter Cable Lock","description":"Protect your scooter while parking.","price":29.99,"compareAtPrice":39.99,"image":"https://images.unsplash.com/photo-1503736334956-4c8f8e92946d?auto=format&fit=crop&q=80&w=600"}],"zh":[{"code":"warranty-1y","name":"1年延保","description":"额外增加一年保障。","price":79.99,"compareAtPrice":99.99,"image":"https://images.unsplash.com/photo-1517677208171-0bc6725a3e60?auto=format&fit=crop&q=80&w=600"},{"code":"cable-lock","name":"滑板车钢缆锁","description":"停车时保护你的滑板车。","price":29.99,"compareAtPrice":39.99,"image":"https://images.unsplash.com/photo-1503736334956-4c8f8e92946d?auto=format&fit=crop&q=80&w=600"}]}',
 '{"en":[{"label":"Battery","value":"36V 7.5Ah"},{"label":"Motor","value":"350W"},{"label":"Range","value":"19 Miles"},{"label":"Top Speed","value":"19 MPH"},{"label":"Tires","value":"10 inch pneumatic"}],"zh":[{"label":"电池","value":"36V 7.5Ah"},{"label":"电机","value":"350W"},{"label":"续航","value":"19 英里"},{"label":"最高时速","value":"19 MPH"},{"label":"轮胎","value":"10 英寸气动轮胎"}]}',
 '{"en":["Scooter body","Charger","Toolkit","Manual"],"zh":["车身","充电器","工具包","说明书"]}',
 '{"en":[{"question":"Is the scooter waterproof?","answer":"It is splash resistant for daily commuting, but should not be submerged."},{"question":"Can I carry it on public transit?","answer":"Yes, the folding frame is designed for mixed commute scenarios."}],"zh":[{"question":"这款滑板车防水吗？","answer":"它具备日常通勤防泼溅能力，但不能浸水。"},{"question":"能带上公共交通吗？","answer":"可以，可折叠车架适合混合通勤场景。"}]}',
 TRUE, 1),
(2, 1, 's-nova-pro-commuting-electric-scooter',
 '{"en":"S Nova Pro Commuting Electric Scooter","zh":"S Nova Pro 通勤电动滑板车"}',
 '{"en":"Long-range city performance","zh":"长续航城市性能款"}',
 '{"en":"<p>S Nova Pro balances power, comfort, and range for daily commuting and weekend rides.</p>","zh":"<p>S Nova Pro 平衡了动力、舒适性与续航，适合日常通勤与周末骑行。</p>"}',
 489.99, 599.99, 120,
 'https://images.unsplash.com/photo-1587574293340-e0011c4e8ecf?auto=format&fit=crop&q=80&w=1200',
 '{"en":["NEW","Spring Sale"],"zh":["新品","春季促销"]}',
 '["https://images.unsplash.com/photo-1587574293340-e0011c4e8ecf?auto=format&fit=crop&q=80&w=1200","https://images.unsplash.com/photo-1593941707874-ef25b8b4a92b?auto=format&fit=crop&q=80&w=1200"]',
 '',
 '{"en":[{"label":"Max Power","value":"1000W","icon":"ZapIcon"},{"label":"Max Range","value":"38 Miles","icon":"NavigationIcon"},{"label":"Top Speed","value":"28 MPH","icon":"ActivityIcon"},{"label":"Battery","value":"48V 13Ah","icon":"BatteryIcon"}],"zh":[{"label":"最大功率","value":"1000W","icon":"ZapIcon"},{"label":"最长续航","value":"38 英里","icon":"NavigationIcon"},{"label":"最高时速","value":"28 MPH","icon":"ActivityIcon"},{"label":"电池","value":"48V 13Ah","icon":"BatteryIcon"}]}',
 '{"en":["1000W peak output for hill starts","Dual suspension for city comfort","Integrated lighting for commuting"],"zh":["1000W 峰值输出，轻松起步爬坡","双重减震提升城市舒适性","集成灯组适合日常通勤"]}',
 '{"en":[{"code":"warranty-2y","name":"2-Year Extended Warranty","description":"Add two extra years of coverage.","price":99.99,"compareAtPrice":129.99,"image":"https://images.unsplash.com/photo-1517677208171-0bc6725a3e60?auto=format&fit=crop&q=80&w=600"}],"zh":[{"code":"warranty-2y","name":"2年延保","description":"额外增加两年保障。","price":99.99,"compareAtPrice":129.99,"image":"https://images.unsplash.com/photo-1517677208171-0bc6725a3e60?auto=format&fit=crop&q=80&w=600"}]}',
 '{"en":[{"label":"Motor","value":"1000W"},{"label":"Range","value":"38 Miles"},{"label":"Top Speed","value":"28 MPH"},{"label":"Battery","value":"48V 13Ah"}],"zh":[{"label":"电机","value":"1000W"},{"label":"续航","value":"38 英里"},{"label":"最高时速","value":"28 MPH"},{"label":"电池","value":"48V 13Ah"}]}',
 '{"en":["Scooter body","Charger","Toolkit","Manual"],"zh":["车身","充电器","工具包","说明书"]}',
 '{"en":[{"question":"Is it suitable for commuting?","answer":"Yes, this model is tuned for everyday urban commuting."}],"zh":[{"question":"适合通勤吗？","answer":"适合，这款车型就是为日常城市通勤调校的。"}]}',
 TRUE, 2);

INSERT INTO pms_product (
    id, category_id, slug, name, subtitle, description, price, compare_at_price, stock, pic, tags, images, app_image,
    specs, quick_know, upsells, spec_table, box_items, faqs, published, sort_order
) VALUES
(3, 2, 'isinwheel-u8-electric-bike-for-adults',
 '{"en":"isinwheel U8 Electric Bike for Adults","zh":"isinwheel U8 成人电动自行车"}',
 '{"en":"Compact daily ebike","zh":"紧凑型日常电助力自行车"}',
 '{"en":"<p>U8 is a step-through commuter ebike built for easy city riding and weekend park loops.</p>","zh":"<p>U8 是一款低跨点通勤电助力自行车，适合城市通勤与周末轻松骑行。</p>"}',
 799.99, 999.99, 80,
 'https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&q=80&w=1200',
 '{"en":["Best Seller"],"zh":["热卖"]}',
 '["https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&q=80&w=1200","https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&q=80&w=1200"]',
 '',
 '{"en":[{"label":"Motor","value":"750W","icon":"ZapIcon"},{"label":"Range","value":"55 Miles","icon":"NavigationIcon"},{"label":"Top Speed","value":"20 MPH","icon":"ActivityIcon"},{"label":"Battery","value":"48V 15Ah","icon":"BatteryIcon"}],"zh":[{"label":"电机","value":"750W","icon":"ZapIcon"},{"label":"续航","value":"55 英里","icon":"NavigationIcon"},{"label":"最高时速","value":"20 MPH","icon":"ActivityIcon"},{"label":"电池","value":"48V 15Ah","icon":"BatteryIcon"}]}',
 '{"en":["Step-through frame for comfortable starts and stops","Rear rack included for errands and commuting","Front suspension improves rough-road comfort"],"zh":["低跨点车架，上下车更轻松","自带后货架，适合通勤与采购","前叉减震提升复杂路面舒适性"]}',
 '{"en":[{"code":"rear-basket","name":"Rear Basket","description":"Add extra storage for groceries or gear.","price":49.99,"compareAtPrice":69.99,"image":"https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=600"}],"zh":[{"code":"rear-basket","name":"后置车篮","description":"增加买菜或日常载物空间。","price":49.99,"compareAtPrice":69.99,"image":"https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=600"}]}',
 '{"en":[{"label":"Battery","value":"48V 15Ah"},{"label":"Motor","value":"750W"},{"label":"Frame","value":"Step-through aluminum"},{"label":"Brakes","value":"Mechanical disc"}],"zh":[{"label":"电池","value":"48V 15Ah"},{"label":"电机","value":"750W"},{"label":"车架","value":"铝合金低跨点车架"},{"label":"刹车","value":"机械碟刹"}]}',
 '{"en":["Bike frame","Battery","Charger","Pedals","Toolkit"],"zh":["车身","电池","充电器","脚踏","工具包"]}',
 '{"en":[{"question":"Can I remove the battery?","answer":"Yes, the battery is removable for charging indoors."}],"zh":[{"question":"电池可以拆下来吗？","answer":"可以，支持拆卸后在室内充电。"}]}',
 TRUE, 3),
(4, 2, 'isinwheel-m50-mountain-ebike',
 '{"en":"isinwheel M50 Mountain Ebike","zh":"isinwheel M50 山地电助力自行车"}',
 '{"en":"Trail-ready adventure ebike","zh":"适合越野探索的电助力车型"}',
 '{"en":"<p>M50 is built for mixed terrain with fat tires, confident power delivery, and a rugged frame.</p>","zh":"<p>M50 配备宽胎与强劲动力输出，适合多地形探索与通勤兼顾。</p>"}',
 1199.99, 1399.99, 50,
 'https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&q=80&w=1200',
 '{"en":["Adventure"],"zh":["越野"]}',
 '["https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&q=80&w=1200","https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&q=80&w=1200"]',
 '',
 '{"en":[{"label":"Motor","value":"1000W","icon":"ZapIcon"},{"label":"Range","value":"65 Miles","icon":"NavigationIcon"},{"label":"Top Speed","value":"28 MPH","icon":"ActivityIcon"},{"label":"Battery","value":"48V 20Ah","icon":"BatteryIcon"}],"zh":[{"label":"电机","value":"1000W","icon":"ZapIcon"},{"label":"续航","value":"65 英里","icon":"NavigationIcon"},{"label":"最高时速","value":"28 MPH","icon":"ActivityIcon"},{"label":"电池","value":"48V 20Ah","icon":"BatteryIcon"}]}',
 '{"en":["Fat tires for all-terrain stability","Hydraulic disc brakes for controlled descents","Integrated display keeps ride data within view"],"zh":["宽胎设计提升全地形稳定性","液压碟刹让下坡更可控","集成仪表让骑行信息一目了然"]}',
 '{"en":[{"code":"phone-mount","name":"Handlebar Phone Mount","description":"Keep navigation within view.","price":24.99,"compareAtPrice":34.99,"image":"https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&q=80&w=600"}],"zh":[{"code":"phone-mount","name":"车把手机支架","description":"让导航始终保持在视线范围内。","price":24.99,"compareAtPrice":34.99,"image":"https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&q=80&w=600"}]}',
 '{"en":[{"label":"Battery","value":"48V 20Ah"},{"label":"Motor","value":"1000W"},{"label":"Top Speed","value":"28 MPH"},{"label":"Tire","value":"26 x 4.0 inch"}],"zh":[{"label":"电池","value":"48V 20Ah"},{"label":"电机","value":"1000W"},{"label":"最高时速","value":"28 MPH"},{"label":"轮胎","value":"26 x 4.0 英寸"}]}',
 '{"en":["Bike frame","Battery","Charger","Pedals","Toolkit"],"zh":["车身","电池","充电器","脚踏","工具包"]}',
 '{"en":[{"question":"Is the M50 suitable for trails?","answer":"Yes, it is tuned for gravel paths, city roads, and light trails."}],"zh":[{"question":"M50 适合越野路况吗？","answer":"适合碎石路、城市道路以及轻度山地场景。"}]}',
 TRUE, 4),
(5, 3, 'isinwheel-v8-electric-skateboard-with-remote',
 '{"en":"isinwheel V8 Electric Skateboard with Remote","zh":"isinwheel V8 遥控电动滑板"}',
 '{"en":"Stable carving for everyday fun","zh":"稳定好控，适合日常玩乐"}',
 '{"en":"<p>V8 delivers a confidence-inspiring ride with simple controls and a comfortable deck flex.</p>","zh":"<p>V8 提供易上手的控制体验与舒适板面弹性，适合日常通勤与休闲滑行。</p>"}',
 329.99, 429.99, 90,
 'https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&q=80&w=1200',
 '{"en":["Carving"],"zh":["灵活转向"]}',
 '["https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&q=80&w=1200"]',
 '',
 '{"en":[{"label":"Max Power","value":"700W","icon":"ZapIcon"},{"label":"Range","value":"12 Miles","icon":"NavigationIcon"},{"label":"Top Speed","value":"22 MPH","icon":"ActivityIcon"},{"label":"Battery","value":"36V 4Ah","icon":"BatteryIcon"}],"zh":[{"label":"最大功率","value":"700W","icon":"ZapIcon"},{"label":"续航","value":"12 英里","icon":"NavigationIcon"},{"label":"最高时速","value":"22 MPH","icon":"ActivityIcon"},{"label":"电池","value":"36V 4Ah","icon":"BatteryIcon"}]}',
 '{"en":["Wireless remote for intuitive acceleration and braking","Flexible deck keeps the ride smooth","Compact size is easy to carry indoors"],"zh":["无线遥控器让加速与制动更直观","板面弹性带来更平顺的骑行感受","紧凑尺寸便于携带进室内"]}',
 '{"en":[{"code":"wrist-guard","name":"Protective Wrist Guard Set","description":"Extra confidence for new riders.","price":19.99,"compareAtPrice":29.99,"image":"https://images.unsplash.com/photo-1515879218367-8466d910aaa4?auto=format&fit=crop&q=80&w=600"}],"zh":[{"code":"wrist-guard","name":"护腕套装","description":"为新手提供更多安全感。","price":19.99,"compareAtPrice":29.99,"image":"https://images.unsplash.com/photo-1515879218367-8466d910aaa4?auto=format&fit=crop&q=80&w=600"}]}',
 '{"en":[{"label":"Battery","value":"36V 4Ah"},{"label":"Motor","value":"700W"},{"label":"Deck","value":"8-layer maple"},{"label":"Remote","value":"2.4G wireless"}],"zh":[{"label":"电池","value":"36V 4Ah"},{"label":"电机","value":"700W"},{"label":"板面","value":"8层枫木"},{"label":"遥控","value":"2.4G 无线"}]}',
 '{"en":["Board","Remote","Charger","Toolkit"],"zh":["板身","遥控器","充电器","工具包"]}',
 '{"en":[{"question":"Is this a good first electric skateboard?","answer":"Yes, the V8 is designed to be approachable for new riders."}],"zh":[{"question":"适合作为第一块电动滑板吗？","answer":"适合，V8 的调校对新手很友好。"}]}',
 TRUE, 5),
(6, 3, 'isinwheel-v10-off-road-electric-skateboard',
 '{"en":"isinwheel V10 Off Road Electric Skateboard","zh":"isinwheel V10 越野电动滑板"}',
 '{"en":"Bigger wheels for rougher terrain","zh":"更大的轮组，更适合复杂地形"}',
 '{"en":"<p>V10 adds all-terrain wheels and stronger output for riders who want more confidence outside smooth pavement.</p>","zh":"<p>V10 通过全地形轮组和更强动力输出，为复杂路面提供更强通过性与稳定感。</p>"}',
 499.99, 629.99, 40,
 'https://images.unsplash.com/photo-1508979828023-5f79c6b6e81d?auto=format&fit=crop&q=80&w=1200',
 '{"en":["Off Road"],"zh":["越野"]}',
 '["https://images.unsplash.com/photo-1508979828023-5f79c6b6e81d?auto=format&fit=crop&q=80&w=1200"]',
 '',
 '{"en":[{"label":"Max Power","value":"1200W","icon":"ZapIcon"},{"label":"Range","value":"18 Miles","icon":"NavigationIcon"},{"label":"Top Speed","value":"24 MPH","icon":"ActivityIcon"},{"label":"Battery","value":"36V 7Ah","icon":"BatteryIcon"}],"zh":[{"label":"最大功率","value":"1200W","icon":"ZapIcon"},{"label":"续航","value":"18 英里","icon":"NavigationIcon"},{"label":"最高时速","value":"24 MPH","icon":"ActivityIcon"},{"label":"电池","value":"36V 7Ah","icon":"BatteryIcon"}]}',
 '{"en":["All-terrain wheels tackle imperfect surfaces","Strong acceleration for experienced riders","Wide deck improves high-speed stability"],"zh":["全地形轮组适应不平整路面","更强加速适合进阶玩家","更宽板面提升高速稳定性"]}',
 '{"en":[{"code":"helmet-addon","name":"Rider Helmet","description":"Essential protection for higher-speed rides.","price":39.99,"compareAtPrice":59.99,"image":"https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=600"}],"zh":[{"code":"helmet-addon","name":"骑行头盔","description":"高速骑行场景下的基础保护。","price":39.99,"compareAtPrice":59.99,"image":"https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=600"}]}',
 '{"en":[{"label":"Battery","value":"36V 7Ah"},{"label":"Motor","value":"1200W"},{"label":"Wheel","value":"All-terrain"},{"label":"Deck","value":"Wide maple composite"}],"zh":[{"label":"电池","value":"36V 7Ah"},{"label":"电机","value":"1200W"},{"label":"轮组","value":"全地形轮"},{"label":"板面","value":"宽版复合枫木"}]}',
 '{"en":["Board","Remote","Charger","Toolkit"],"zh":["板身","遥控器","充电器","工具包"]}',
 '{"en":[{"question":"Can it handle uneven pavement?","answer":"Yes, the off-road wheels are tuned for rougher surfaces."}],"zh":[{"question":"能应对颠簸路面吗？","answer":"可以，越野轮组就是为更复杂的地面场景准备的。"}]}',
 TRUE, 6),
(7, 4, 'electric-bike-cable-lock',
 '{"en":"Electric Bike Cable Lock","zh":"电动自行车钢缆锁"}',
 '{"en":"Simple daily anti-theft essential","zh":"日常防盗必备"}',
 '{"en":"<p>A durable cable lock sized for bikes, scooters, and rack parking.</p>","zh":"<p>适用于自行车、滑板车和车架停放场景的耐用钢缆锁。</p>"}',
 29.99, 39.99, 180,
 'https://images.unsplash.com/photo-1503736334956-4c8f8e92946d?auto=format&fit=crop&q=80&w=1200',
 '{"en":["Accessory"],"zh":["配件"]}',
 '["https://images.unsplash.com/photo-1503736334956-4c8f8e92946d?auto=format&fit=crop&q=80&w=1200"]',
 '',
 '{"en":[{"label":"Length","value":"120 cm","icon":"NavigationIcon"},{"label":"Material","value":"Steel cable","icon":"ZapIcon"}],"zh":[{"label":"长度","value":"120 厘米","icon":"NavigationIcon"},{"label":"材质","value":"钢缆","icon":"ZapIcon"}]}',
 '{"en":["Coated steel cable protects paint","Easy twist-lock operation"],"zh":["包胶钢缆减少刮蹭","旋钮锁止，操作简单"]}',
 '{"en":[],"zh":[]}',
 '{"en":[{"label":"Length","value":"120 cm"},{"label":"Core","value":"Steel cable"}],"zh":[{"label":"长度","value":"120 厘米"},{"label":"内芯","value":"钢缆"}]}',
 '{"en":["Cable lock","Keys"],"zh":["钢缆锁","钥匙"]}',
 '{"en":[{"question":"Can it lock two bikes together?","answer":"It works best for a single bike or scooter frame-to-rack lock."}],"zh":[{"question":"可以同时锁两辆车吗？","answer":"更适合单车或滑板车与固定架的锁定场景。"}]}',
 TRUE, 7),
(8, 4, 'adult-riding-helmet',
 '{"en":"Adult Riding Helmet","zh":"成人骑行头盔"}',
 '{"en":"Daily protection with lightweight comfort","zh":"轻量舒适的日常防护"}',
 '{"en":"<p>A lightweight helmet for scooter, ebike, and skateboard commuting.</p>","zh":"<p>适用于滑板车、电助力自行车和电动滑板通勤的轻量头盔。</p>"}',
 49.99, 69.99, 140,
 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=1200',
 '{"en":["Safety"],"zh":["安全装备"]}',
 '["https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=1200"]',
 '',
 '{"en":[{"label":"Weight","value":"320 g","icon":"BatteryIcon"},{"label":"Fit","value":"54-61 cm","icon":"NavigationIcon"}],"zh":[{"label":"重量","value":"320 克","icon":"BatteryIcon"},{"label":"头围","value":"54-61 厘米","icon":"NavigationIcon"}]}',
 '{"en":["Adjustable dial fit system","Ventilated shell for daily comfort"],"zh":["旋钮调节头围系统","多孔通风结构提升舒适性"]}',
 '{"en":[],"zh":[]}',
 '{"en":[{"label":"Weight","value":"320 g"},{"label":"Shell","value":"PC + EPS"}],"zh":[{"label":"重量","value":"320 克"},{"label":"外壳","value":"PC + EPS"}]}',
 '{"en":["Helmet","Padding set","Manual"],"zh":["头盔","内衬套装","说明书"]}',
 '{"en":[{"question":"Is it suitable for scooters and skateboards?","answer":"Yes, it is designed for everyday personal mobility use."}],"zh":[{"question":"适用于滑板车和滑板吗？","answer":"适用，面向日常个人出行防护场景设计。"}]}',
 TRUE, 8);

INSERT INTO pms_sku (
    id, product_id, sku_code, price, compare_at_price, stock, pic, images, description, specs, status
) VALUES
(1, 1, 'S9PRO-BLK-STD', 269.99, 399.99, 90,
 'https://images.unsplash.com/photo-1593941707874-ef25b8b4a92b?auto=format&fit=crop&q=80&w=1200',
 '["https://images.unsplash.com/photo-1593941707874-ef25b8b4a92b?auto=format&fit=crop&q=80&w=1200"]',
 '{"en":"Midnight black commuter build","zh":"午夜黑通勤版"}',
 '{"en":{"color":"Midnight Black","bundle":"Standard","style":"Commuter"},"zh":{"颜色":"午夜黑","套餐":"标准版","款式":"通勤版"}}',
 'ACTIVE'),
(2, 1, 'S9PRO-WHT-ACC', 299.99, 429.99, 0,
 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=1200',
 '["https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=1200"]',
 '{"en":"Pearl white city bundle","zh":"珍珠白城市礼包版"}',
 '{"en":{"color":"Pearl White","bundle":"City Kit","style":"Commuter"},"zh":{"颜色":"珍珠白","套餐":"城市礼包","款式":"通勤版"}}',
 'ACTIVE'),
(3, 2, 'SNOVA-BLK-STD', 489.99, 599.99, 60,
 'https://images.unsplash.com/photo-1587574293340-e0011c4e8ecf?auto=format&fit=crop&q=80&w=1200',
 '["https://images.unsplash.com/photo-1587574293340-e0011c4e8ecf?auto=format&fit=crop&q=80&w=1200"]',
 '{"en":"Black long-range setup","zh":"黑色长续航版本"}',
 '{"en":{"color":"Graphite Black","bundle":"Standard","style":"Pro"},"zh":{"颜色":"石墨黑","套餐":"标准版","款式":"Pro 版"}}',
 'ACTIVE'),
(4, 2, 'SNOVA-GRY-COM', 529.99, 649.99, 30,
 'https://images.unsplash.com/photo-1593941707874-ef25b8b4a92b?auto=format&fit=crop&q=80&w=1200',
 '["https://images.unsplash.com/photo-1593941707874-ef25b8b4a92b?auto=format&fit=crop&q=80&w=1200"]',
 '{"en":"Grey commuter plus kit","zh":"灰色通勤增强版"}',
 '{"en":{"color":"Storm Grey","bundle":"Commuter Plus","style":"Pro"},"zh":{"颜色":"风暴灰","套餐":"通勤增强版","款式":"Pro 版"}}',
 'ACTIVE'),
(5, 3, 'U8-BLK-STD', 799.99, 999.99, 35,
 'https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&q=80&w=1200',
 '["https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&q=80&w=1200"]',
 '{"en":"Black commuter configuration","zh":"黑色通勤配置"}',
 '{"en":{"color":"Matte Black","bundle":"Standard","style":"Step-through"},"zh":{"颜色":"磨砂黑","套餐":"标准版","款式":"低跨版"}}',
 'ACTIVE'),
(6, 3, 'U8-BLU-ACC', 849.99, 1049.99, 22,
 'https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&q=80&w=1200',
 '["https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&q=80&w=1200"]',
 '{"en":"Blue city accessory bundle","zh":"蓝色城市配件套装"}',
 '{"en":{"color":"Ocean Blue","bundle":"Accessory Kit","style":"Step-through"},"zh":{"颜色":"海洋蓝","套餐":"配件套装","款式":"低跨版"}}',
 'ACTIVE'),
(7, 4, 'M50-GRN-STD', 1199.99, 1399.99, 18,
 'https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&q=80&w=1200',
 '["https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&q=80&w=1200"]',
 '{"en":"Forest green standard build","zh":"森林绿标准版"}',
 '{"en":{"color":"Forest Green","bundle":"Standard","style":"Mountain"},"zh":{"颜色":"森林绿","套餐":"标准版","款式":"山地版"}}',
 'ACTIVE'),
(8, 4, 'M50-SND-PRO', 1299.99, 1499.99, 12,
 'https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&q=80&w=1200',
 '["https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&q=80&w=1200"]',
 '{"en":"Sand adventure bundle","zh":"沙岩色越野套装"}',
 '{"en":{"color":"Sand","bundle":"Adventure Kit","style":"Mountain"},"zh":{"颜色":"沙岩色","套餐":"越野套装","款式":"山地版"}}',
 'ACTIVE'),
(9, 5, 'V8-BLK-STD', 329.99, 429.99, 48,
 'https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&q=80&w=1200',
 '["https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&q=80&w=1200"]',
 '{"en":"Standard black carve setup","zh":"标准黑色 carving 版本"}',
 '{"en":{"color":"Black","bundle":"Standard","style":"Street"},"zh":{"颜色":"黑色","套餐":"标准版","款式":"街道版"}}',
 'ACTIVE'),
(10, 6, 'V10-BLK-OFF', 499.99, 629.99, 20,
 'https://images.unsplash.com/photo-1508979828023-5f79c6b6e81d?auto=format&fit=crop&q=80&w=1200',
 '["https://images.unsplash.com/photo-1508979828023-5f79c6b6e81d?auto=format&fit=crop&q=80&w=1200"]',
 '{"en":"Off-road black setup","zh":"黑色越野版本"}',
 '{"en":{"color":"Black","bundle":"Standard","style":"Off Road"},"zh":{"颜色":"黑色","套餐":"标准版","款式":"越野版"}}',
 'ACTIVE'),
(11, 7, 'LOCK-BLK-ONE', 29.99, 39.99, 180,
 'https://images.unsplash.com/photo-1503736334956-4c8f8e92946d?auto=format&fit=crop&q=80&w=1200',
 '["https://images.unsplash.com/photo-1503736334956-4c8f8e92946d?auto=format&fit=crop&q=80&w=1200"]',
 '{"en":"Single black cable lock","zh":"黑色单只钢缆锁"}',
 '{"en":{"color":"Black","bundle":"Single","style":"Accessory"},"zh":{"颜色":"黑色","套餐":"单只装","款式":"配件"}}',
 'ACTIVE'),
(12, 8, 'HELMET-M-BLK', 49.99, 69.99, 75,
 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=1200',
 '["https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=1200"]',
 '{"en":"Medium black helmet","zh":"中码黑色头盔"}',
 '{"en":{"color":"Black","bundle":"Single","style":"M"},"zh":{"颜色":"黑色","套餐":"单只装","款式":"M 码"}}',
 'ACTIVE');

INSERT INTO pms_review (id, product_id, user_id, user_name, rating, title, content, images, verified_purchase) VALUES
(1, 1, 1, 'Admin User', 5, 'Great commuter scooter', 'Smooth ride and easy to fold for office storage.', '[]', TRUE),
(2, 2, 2, 'Sarah Miller', 5, 'Perfect for daily commute', 'The range is enough for my full work week recharges.', '[]', TRUE),
(3, 3, 2, 'Sarah Miller', 4, 'Comfortable city ebike', 'Step-through frame makes downtown riding stress-free.', '[]', TRUE),
(4, 5, 1, 'Admin User', 5, 'Fun first electric skateboard', 'Stable enough for casual carving and neighborhood rides.', '[]', TRUE);

INSERT INTO ums_user_address (
    id, user_id, country, first_name, last_name, phone, address_line1, address_line2, city, state, zip_code, is_default
) VALUES
(1, 1, 'United States', 'Admin', 'User', '4155550123', '100 Market Street', 'Suite 8', 'San Francisco', 'CA', '94105', TRUE),
(2, 2, 'United States', 'Sarah', 'Miller', '2065550188', '88 Pine Avenue', '', 'Seattle', 'WA', '98101', TRUE);

INSERT INTO sms_coupon (id, code, title, description, threshold_amount, discount_amount, active) VALUES
(1, 'WELCOME10', '{"en":"$10 off your first ride","zh":"首单立减 $10"}', '{"en":"No-threshold coupon for your first checkout.","zh":"首单无门槛优惠券。"}', 0, 10, TRUE),
(2, 'SAVE50', '{"en":"$50 off orders over $500","zh":"满 $500 减 $50"}', '{"en":"Applies when your order subtotal reaches $500.","zh":"订单小计满 $500 可用。"}', 500, 50, TRUE);

INSERT INTO sms_coupon_user (id, coupon_id, user_id, status) VALUES
(1, 1, 1, 'CLAIMED'),
(2, 2, 1, 'CLAIMED'),
(3, 1, 2, 'CLAIMED');

INSERT INTO oms_order (
    id, user_id, order_sn, subtotal_amount, tax_amount, total_amount, status, payment_status, currency, country,
    preview_token, coupon_code, coupon_user_id, coupon_discount_amount, payment_intent_id, receiver_name, receiver_phone,
    receiver_address, receiver_country, receiver_first_name, receiver_last_name, receiver_address_line1, receiver_address_line2,
    receiver_city, receiver_state, receiver_zip_code, payment_method, pay_txn_no, checkout_source, shipping_method,
    shipping_amount, discount_amount, remark, pay_time
) VALUES
(1, 1, 'ORD202604050001SEED', 489.99, 39.60, 519.59, 'PAID', 'PAID', 'USD', 'US',
 'seed-preview-token', 'WELCOME10', 1, 10.00, 1, 'Admin User', '4155550123',
 '100 Market Street, San Francisco, CA, 94105', 'United States', 'Admin', 'User', '100 Market Street', 'Suite 8',
 'San Francisco', 'CA', '94105', 'credit_card', 'MOCK_TXN_SEED01', 'cart',
 'UPS Ground/FedEx Home Delivery(2-5 Business Days)', 0.00, 10.00, 'Seed paid order', CURRENT_TIMESTAMP);

INSERT INTO oms_order_item (
    id, order_id, product_id, sku_id, product_name, product_pic, sku_code, sku_attributes_snapshot, addons_snapshot,
    quantity, unit_price, line_amount
) VALUES
(1, 1, 2, 3,
 '{"en":"S Nova Pro Commuting Electric Scooter","zh":"S Nova Pro 通勤电动滑板车"}',
 'https://images.unsplash.com/photo-1587574293340-e0011c4e8ecf?auto=format&fit=crop&q=80&w=1200',
 'SNOVA-BLK-STD',
 '{"attributes":{"color":"Graphite Black","bundle":"Standard","style":"Pro"},"attributeDisplay":{"color":"Graphite Black","bundle":"Standard","style":"Pro"},"lang":"en"}',
 '[]',
 1, 489.99, 489.99);

INSERT INTO pay_payment_intent (
    id, intent_no, order_id, user_id, amount, currency, method_code, provider_key, status, client_secret, mock_result, paid_time
) VALUES
(1, 'PI_SEED_0001', 1, 1, 519.59, 'USD', 'credit_card', 'mock', 'SUCCEEDED', 'mock_secret_seed_0001', 'success', CURRENT_TIMESTAMP);

SELECT setval('sys_user_id_seq', COALESCE((SELECT MAX(id) FROM sys_user), 1), TRUE);
SELECT setval('pms_category_id_seq', COALESCE((SELECT MAX(id) FROM pms_category), 1), TRUE);
SELECT setval('pms_product_id_seq', COALESCE((SELECT MAX(id) FROM pms_product), 1), TRUE);
SELECT setval('pms_sku_id_seq', COALESCE((SELECT MAX(id) FROM pms_sku), 1), TRUE);
SELECT setval('pms_review_id_seq', COALESCE((SELECT MAX(id) FROM pms_review), 1), TRUE);
SELECT setval('oms_cart_item_id_seq', COALESCE((SELECT MAX(id) FROM oms_cart_item), 1), TRUE);
SELECT setval('ums_user_address_id_seq', COALESCE((SELECT MAX(id) FROM ums_user_address), 1), TRUE);
SELECT setval('sms_coupon_id_seq', COALESCE((SELECT MAX(id) FROM sms_coupon), 1), TRUE);
SELECT setval('sms_coupon_user_id_seq', COALESCE((SELECT MAX(id) FROM sms_coupon_user), 1), TRUE);
SELECT setval('oms_order_id_seq', COALESCE((SELECT MAX(id) FROM oms_order), 1), TRUE);
SELECT setval('oms_order_item_id_seq', COALESCE((SELECT MAX(id) FROM oms_order_item), 1), TRUE);
SELECT setval('pay_payment_intent_id_seq', COALESCE((SELECT MAX(id) FROM pay_payment_intent), 1), TRUE);
