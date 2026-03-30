-- 插入完整的 S Nova Pro 商品数据
INSERT INTO pms_product (
    name, 
    description, 
    price, 
    compare_at_price, 
    stock, 
    pic, 
    tags, 
    images, 
    app_image, 
    specs, 
    quick_know, 
    upsells
) VALUES 
(
    '{"en": "S Nova Pro Commuting Electric Scooter"}', 
    '{"en": "<p>S Nova Pro Commuting Electric Scooter</p>"}', 
    489.99, 
    599.99, 
    200, 
    'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=800',
    '["Spring Sale"]',
    '["https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=800", "https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=800", "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=800", "https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=800", "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=800"]',
    'https://via.placeholder.com/60x120?text=APP',
    '[{"label": "Max Power", "value": "1000W", "icon": "ActivityIcon"}, {"label": "Max Range", "value": "38 Miles", "icon": "NavigationIcon"}, {"label": "Top Speed", "value": "28 MPH", "icon": "ActivityIcon"}, {"label": "Battery Capacity", "value": "48V 13Ah", "icon": "BatteryIcon"}, {"label": "Charging Time", "value": "6-7 Hours", "icon": "BatteryIcon"}, {"label": "Max Load", "value": "264 Lbs", "icon": "ActivityIcon"}]',
    '{"en": ["<strong>1000W</strong> Max Power, Max Speed <strong>28 MPH</strong>. <strong>30%</strong> Hill Climbing", "<strong>48V 13Ah</strong> Battery, <strong>38 Miles</strong> Max Range, <strong>6-7H</strong> Charging Time", "<strong>10 inch</strong> Pneumatic Tire, Front and Rear <strong>Dual Suspension</strong>, <strong>Disc Brake</strong>, Easy to <strong>Fold</strong>", "<strong>ALUMINUM</strong> Frame, <strong>264Lbs</strong> Max Load", "Bright <strong>Headlight, Multi-Color Ambient Light</strong> and Smart Turn Signals", "Safety Certified: Brand New <strong>App Supported, 365-day</strong> Quality Assurance"]}',
    '[{"id": 1, "name": "Cable Lock for Escooter", "image": "https://via.placeholder.com/80x80?text=Lock", "value": "35.99"}, {"id": 2, "name": "14-Day Free Trial", "image": "https://via.placeholder.com/80x80?text=Trial", "value": "75.99"}, {"id": 3, "name": "One Year Warranty", "image": "https://via.placeholder.com/80x80?text=Warranty", "value": "73.99"}]'
);

-- 获取刚刚插入的商品ID
DO $$
DECLARE
    new_product_id BIGINT;
BEGIN
    SELECT id INTO new_product_id FROM pms_product WHERE name->>'en' = 'S Nova Pro Commuting Electric Scooter' LIMIT 1;
    
    -- 插入对应的SKU数据 (Styles and Bundles combinations)
    INSERT INTO pms_sku (product_id, sku_code, price, stock, pic, description, specs) VALUES 
    (new_product_id, 'S-NOVA-PRO-1', 489.99, 100, 'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=800', '{"en": "<p>2026 Upgraded Edition - S9 Pro*1</p>"}', '{"en": {"Style": "2026 Upgraded Edition", "Bundle": "S9 Pro*1"}}'),
    (new_product_id, 'S-NOVA-PRO-2', 950.00, 100, 'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=800', '{"en": "<p>2026 Upgraded Edition - S9 Pro*2</p>"}', '{"en": {"Style": "2026 Upgraded Edition", "Bundle": "S9 Pro*2"}}');
END $$;
