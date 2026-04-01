UPDATE pms_sku
SET specs = jsonb_build_object(
    'en',
    jsonb_strip_nulls(
      jsonb_build_object(
        'style', COALESCE(specs->>'style', specs->>'Style', specs->>'款式'),
        'bundle', COALESCE(specs->>'bundle', specs->>'Bundle', specs->>'套餐', specs->>'组合'),
        'color', COALESCE(specs->>'color', specs->>'Color', specs->>'颜色')
      )
    ),
    'zh',
    jsonb_strip_nulls(
      jsonb_build_object(
        'style', COALESCE(specs->>'款式', specs->>'style', specs->>'Style'),
        'bundle', COALESCE(specs->>'套餐', specs->>'组合', specs->>'bundle', specs->>'Bundle'),
        'color', COALESCE(specs->>'颜色', specs->>'color', specs->>'Color')
      )
    )
)
WHERE jsonb_typeof(specs) = 'object'
  AND (specs ? 'style' OR specs ? 'Style' OR specs ? '款式'
    OR specs ? 'bundle' OR specs ? 'Bundle' OR specs ? '套餐' OR specs ? '组合'
    OR specs ? 'color' OR specs ? 'Color' OR specs ? '颜色');

UPDATE oms_cart_item c
SET selected_attributes_snapshot = jsonb_build_object(
    'attributes', COALESCE(s.specs->'en', s.specs),
    'attributeDisplay', COALESCE(s.specs->'en', s.specs),
    'lang', 'en'
)
FROM pms_sku s
WHERE c.sku_id = s.id;

UPDATE oms_order_item oi
SET sku_attributes_snapshot = jsonb_build_object(
    'attributes', COALESCE(s.specs->'en', s.specs),
    'attributeDisplay', COALESCE(s.specs->'en', s.specs),
    'lang', 'en'
)
FROM pms_sku s
WHERE oi.sku_id = s.id;
