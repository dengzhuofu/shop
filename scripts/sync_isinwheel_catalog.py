#!/usr/bin/env python3
import argparse
import concurrent.futures
import hashlib
import http.client
import json
import re
import subprocess
import sys
import time
import urllib.parse
import urllib.request
from dataclasses import dataclass
from decimal import Decimal, ROUND_HALF_UP
from html import unescape
from typing import Any


SITE_BASE = "https://www.isinwheel.com"
HTTP_HEADERS = {
    "User-Agent": "Mozilla/5.0 (compatible; isinwheel-sync/1.0; +https://www.isinwheel.com)",
    "Accept": "application/json,text/html,*/*",
}
DEFAULT_STOCK = 20
DEFAULT_TARGET_PER_CATEGORY = 50


ROOT_CATEGORIES = [
    {
        "id": 1,
        "slug": "electric-scooters",
        "name": "Electric Scooter",
        "description": "Portable commuter scooters, off-road builds, and youth models.",
        "children": [
            {
                "id": 11,
                "slug": "commuter-electric-scooter",
                "legacy_slugs": ["commuter-city-ride"],
                "name": "Commuter Scooter",
                "sort_order": 1,
                "sync_priority": 3,
                "description": "Foldable commuter scooters tuned for city riding.",
                "source_handles": [
                    "commuter-electric-scooter",
                    "electric-scooters",
                    "adult-electric-scooters",
                    "affordable-electric-scooters",
                    "portable-electric-scooter",
                    "lightweight-electric-scooter",
                    "foldable-electric-scooters",
                    "travel-electric-scooter",
                ],
            },
            {
                "id": 12,
                "slug": "all-terrain-electric-scooter",
                "legacy_slugs": ["performance-all-terrain-scooters"],
                "name": "Off Road Scooter",
                "sort_order": 2,
                "sync_priority": 2,
                "description": "High-performance scooters for rougher terrain and longer range.",
                "source_handles": [
                    "all-terrain-electric-scooter",
                    "off-road-electric-scooter",
                    "all-terrain-electric-scooter-for-adults",
                    "off-road-electric-scooters-for-adults",
                    "high-performance-electric-scooters",
                    "1000w-electric-scooters",
                    "800w-electric-scooters",
                    "trail-scooter",
                    "long-range-electric-scooters",
                ],
            },
            {
                "id": 19,
                "slug": "electric-scooters-for-kids",
                "legacy_slugs": ["kids-scooter"],
                "name": "Scooter for Kids",
                "sort_order": 3,
                "sync_priority": 1,
                "description": "Safe, lightweight electric scooters designed for kids and teens.",
                "source_handles": [
                    "electric-scooters-for-kids",
                    "electric-scooters-for-5-year-olds",
                    "safe-interesting-scooter-for-kids",
                    "boys-electric-scooter",
                    "girls-electric-scooter",
                    "youth-electric-scooters",
                    "light-up-scooters",
                ],
            },
        ],
    },
    {
        "id": 2,
        "slug": "electric-bike",
        "name": "Electric Bike",
        "description": "Commuter ebikes and all-terrain electric bikes.",
        "children": [
            {
                "id": 13,
                "slug": "commuter-ebikes",
                "legacy_slugs": ["commuter-city-road", "commuter-ebike"],
                "name": "Commuter & City Road",
                "sort_order": 1,
                "sync_priority": 2,
                "description": "City-ready ebikes built for practical everyday rides.",
                "source_handles": [
                    "commuter-ebikes",
                    "commuter-ebike",
                    "city-electric-bike",
                    "adult-folding-electric-bikes",
                    "folding-electric-bike",
                    "portable-electric-bike",
                    "lightweight-folding-e-bikes",
                    "step-through-electric-bikes",
                ],
            },
            {
                "id": 14,
                "slug": "all-terrain-electric-bikes",
                "legacy_slugs": ["off-road-all-terrain", "off-road-ebike", "off-road-electric-bikes"],
                "name": "Off Road & All Terrain",
                "sort_order": 2,
                "sync_priority": 1,
                "description": "Adventure ebikes for trails, cargo, and rougher terrain.",
                "source_handles": [
                    "all-terrain-electric-bikes",
                    "off-road-ebike",
                    "off-road-electric-bikes",
                    "adventure-electric-bikes",
                    "fat-tire-electric-bikes",
                    "cargo-e-bike",
                    "electric-cargo-e-bikes",
                    "full-suspension-mountain-bikes",
                    "electric-mountain-bikes",
                ],
            },
        ],
    },
    {
        "id": 3,
        "slug": "electric-skateboard",
        "name": "Electric Skateboard",
        "description": "Street carving boards and all-terrain electric skateboards.",
        "children": [
            {
                "id": 15,
                "slug": "electric-skateboards",
                "legacy_slugs": ["street-carving", "street-electric-skateboard"],
                "name": "Street & Carving",
                "sort_order": 1,
                "sync_priority": 2,
                "description": "Stable boards for street cruising and carving.",
                "source_handles": [
                    "street-electric-skateboard",
                    "adult-electric-skateboard",
                    "electric-skateboard-with-remote-control",
                    "budget-electric-skateboards",
                    "small-electric-skateboards",
                    "mini-electric-skateboard",
                    "powered-skateboards",
                    "motorized-skateboards",
                ],
            },
            {
                "id": 16,
                "slug": "all-terrain-electric-skateboards",
                "legacy_slugs": ["off-road-terrain-boards", "off-road-electric-skateboard"],
                "name": "Off Road & Terrain",
                "sort_order": 2,
                "sync_priority": 1,
                "description": "All-terrain boards for rough surfaces and bigger wheels.",
                "source_handles": [
                    "all-terrain-electric-skateboards",
                    "off-road-electric-skateboard",
                    "automatic-skateboard",
                ],
            },
        ],
    },
    {
        "id": 4,
        "slug": "accessories",
        "name": "Accessories",
        "description": "Accessories, helmets, bags, and replacement gear.",
        "children": [
            {
                "id": 17,
                "slug": "e-bike-accessories",
                "legacy_slugs": ["safety-gear", "ebike-helmet"],
                "name": "E-bike Accessories",
                "sort_order": 1,
                "sync_priority": 1,
                "description": "Helmets, baskets, and accessories for ebike riders.",
                "source_handles": [
                    "e-bike-accessories",
                    "ebike-helmet",
                    "front-basket-for-electric-bike",
                    "front-basket-for-e-bike",
                    "bicycle-pannier-bags",
                    "universal-accessories",
                ],
            },
            {
                "id": 18,
                "slug": "e-scooter-accessories",
                "legacy_slugs": ["locks-storage", "lock-for-electric-scooter", "electric-scooter-bag"],
                "name": "E-scooter Accessories",
                "sort_order": 2,
                "sync_priority": 2,
                "description": "Bags, locks, and accessories for scooters and boards.",
                "source_handles": [
                    "e-scooter-accessories",
                    "lock-for-electric-scooter",
                    "electric-scooter-bag",
                    "gt2-accessories",
                    "s10-series-accessories",
                    "s9-series-accessories",
                    "x1-accessories",
                    "x3-x3-pro-accessories",
                    "m-r-r-pro-accessories",
                    "e-skateboard-accessories",
                ],
            },
        ],
    },
]


@dataclass
class CategoryRecord:
    id: int
    parent_id: int | None
    slug: str
    name: str
    description: str
    hero_image: str
    menu_image: str
    sort_order: int


@dataclass
class ProductSeed:
    slug: str
    source_handle: str
    category_id: int
    category_slug: str
    category_name: str
    sort_order: int
    matched_handles: list[str]
    derived_index: int
    derived_label: str


def fetch_json(url: str, retries: int = 5) -> Any:
    last_error: Exception | None = None
    for attempt in range(1, retries + 1):
        try:
            request = urllib.request.Request(url, headers=HTTP_HEADERS)
            with urllib.request.urlopen(request, timeout=30) as response:
                return json.loads(response.read().decode("utf-8"))
        except http.client.IncompleteRead as exc:
            last_error = exc
            partial = exc.partial.decode("utf-8", errors="ignore").strip()
            if partial:
                try:
                    return json.loads(partial)
                except json.JSONDecodeError:
                    pass
            if attempt == retries:
                raise
            time.sleep(1.2 * attempt)
        except Exception as exc:
            last_error = exc
            if attempt == retries:
                raise
            time.sleep(1.2 * attempt)
    raise last_error or RuntimeError(f"Unable to fetch {url}")


def fetch_collections_index() -> dict[str, dict[str, Any]]:
    payload = fetch_json(f"{SITE_BASE}/collections.json?limit=250")
    return {collection["handle"]: collection for collection in payload.get("collections", [])}


def fetch_collection_products(handle: str) -> list[dict[str, Any]]:
    payload = fetch_json(f"{SITE_BASE}/collections/{handle}/products.json?limit=250")
    return payload.get("products", [])


def fetch_product_detail(handle: str) -> dict[str, Any]:
    encoded_handle = urllib.parse.quote(handle, safe="")
    return fetch_json(f"{SITE_BASE}/products/{encoded_handle}.js")


def absolute_url(url: str | None) -> str:
    if not url:
        return ""
    if url.startswith("//"):
        return f"https:{url}"
    if url.startswith("/"):
        return f"{SITE_BASE}{url}"
    return url


def bounded_media_url(url: str | None, max_length: int = 255) -> str:
    full_url = absolute_url(url)
    if not full_url:
        return ""

    if len(full_url) <= max_length:
        return full_url

    parsed = urllib.parse.urlsplit(full_url)
    basename = parsed.path.rsplit("/", 1)[-1]
    if "/files/" in parsed.path:
        candidate = f"{SITE_BASE}/cdn/shop/files/{basename}"
    elif "/collections/" in parsed.path:
        candidate = f"{SITE_BASE}/cdn/shop/collections/{basename}"
    elif "/products/" in parsed.path:
        candidate = f"{SITE_BASE}/cdn/shop/products/{basename}"
    else:
        candidate = f"{parsed.scheme}://{parsed.netloc}{parsed.path}"

    if parsed.query:
        with_query = f"{candidate}?{parsed.query}"
        if len(with_query) <= max_length:
            return with_query

    return candidate if len(candidate) <= max_length else ""


def strip_html(value: str | None) -> str:
    if not value:
        return ""
    cleaned = re.sub(r"<[^>]+>", " ", unescape(value))
    cleaned = re.sub(r"\s+", " ", cleaned)
    return cleaned.strip()


def localized(value: Any) -> str:
    return json.dumps({"en": value, "zh": value}, ensure_ascii=False)


def decimal_price(value: Any) -> str | None:
    if value in (None, "", False):
        return None
    amount = Decimal(str(value)) / Decimal("100")
    return str(amount.quantize(Decimal("0.01"), rounding=ROUND_HALF_UP))


def sql_quote(value: Any) -> str:
    if value is None:
        return "NULL"
    if isinstance(value, bool):
        return "TRUE" if value else "FALSE"
    if isinstance(value, (int, float, Decimal)):
        return str(value)
    text = str(value).replace("'", "''")
    return f"'{text}'"


def slugify(value: str, max_length: int = 180) -> str:
    normalized = re.sub(r"[^a-z0-9]+", "-", value.lower())
    normalized = re.sub(r"-{2,}", "-", normalized).strip("-")
    return normalized[:max_length].rstrip("-") or "catalog-item"


def skuify(value: str, max_length: int = 100) -> str:
    normalized = re.sub(r"[^A-Z0-9]+", "-", value.upper())
    normalized = re.sub(r"-{2,}", "-", normalized).strip("-")
    return normalized[:max_length].rstrip("-") or "SKU"


def build_unique_sku_code(base_sku: str, product_slug: str, variant_index: int) -> str:
    prefix = skuify(base_sku, max_length=72)
    digest = hashlib.sha1(f"{product_slug}:{variant_index}:{base_sku}".encode("utf-8")).hexdigest()[:12].upper()
    return f"{prefix}-{digest}"


def derived_label(category_slug: str, derived_index: int) -> str:
    presets = {
        "commuter-electric-scooter": ["Urban Edition", "Metro Plus", "City Flex", "Daily Ride", "Compact Touring"],
        "all-terrain-electric-scooter": ["Trail Edition", "Adventure Plus", "Summit Ride", "Explorer Build", "Rally Pack"],
        "electric-scooters-for-kids": ["Youth Edition", "Glow Ride", "Weekend Fun", "Campus Cruise", "Play Mode"],
        "commuter-ebikes": ["City Edition", "Metro Pack", "Commuter Plus", "Street Build", "Daily Boost"],
        "all-terrain-electric-bikes": ["Explorer Edition", "Summit Pack", "Trail Build", "Adventure Pro", "Ridge Touring"],
        "electric-skateboards": ["Street Edition", "Cruise Pack", "Carve Build", "Flex Ride", "Campus Mode"],
        "all-terrain-electric-skateboards": ["Terrain Edition", "Rally Pack", "Trail Build", "Explorer Deck", "All Surface"],
        "e-bike-accessories": ["Rider Kit", "Travel Pack", "Commuter Bundle", "Everyday Set", "Safety Pack"],
        "e-scooter-accessories": ["Scoot Kit", "Storage Pack", "Urban Bundle", "Travel Set", "Daily Carry"],
    }
    labels = presets.get(category_slug, ["Series", "Edition", "Bundle", "Set", "Pack"])
    base = labels[(derived_index - 1) % len(labels)]
    round_number = ((derived_index - 1) // len(labels)) + 2
    return f"{base} {round_number}"


def shift_images(images: list[str], offset: int) -> list[str]:
    if not images:
        return []
    pivot = offset % len(images)
    if pivot == 0:
        return images
    return images[pivot:] + images[:pivot]


def adjust_price(value: str | None, derived_index: int, minimum: Decimal = Decimal("5.00")) -> str | None:
    if value is None:
        return None
    base = Decimal(str(value))
    offsets = [
        Decimal("0.02"),
        Decimal("0.04"),
        Decimal("0.06"),
        Decimal("0.08"),
        Decimal("-0.03"),
        Decimal("-0.05"),
        Decimal("0.09"),
        Decimal("-0.01"),
    ]
    offset = offsets[(derived_index - 1) % len(offsets)]
    adjusted = (base * (Decimal("1.00") + offset)).quantize(Decimal("0.01"), rounding=ROUND_HALF_UP)
    if adjusted < minimum:
        adjusted = minimum
    return str(adjusted)


def adjust_compare_at(compare_at_price: str | None, price: str, derived_index: int) -> str | None:
    if compare_at_price is None:
        return None
    adjusted = adjust_price(compare_at_price, derived_index)
    if adjusted is None:
        return None
    if Decimal(adjusted) <= Decimal(price):
        adjusted = str((Decimal(price) + Decimal("20.00")).quantize(Decimal("0.01"), rounding=ROUND_HALF_UP))
    return adjusted


def build_specs(detail: dict[str, Any], collection_name: str, available_count: int) -> str:
    entries = [
        {"label": "Collection", "value": collection_name, "icon": "NavigationIcon"},
        {"label": "Variants", "value": str(len(detail.get("variants", []))), "icon": "ActivityIcon"},
        {"label": "Vendor", "value": detail.get("vendor") or "isinwheel", "icon": "ZapIcon"},
        {
            "label": "Availability",
            "value": "In stock" if available_count > 0 else "Sold out",
            "icon": "BatteryIcon",
        },
    ]
    return localized(entries)


def build_quick_know(detail: dict[str, Any], matched_handles: list[str]) -> str:
    lines = [
        f"Vendor: {detail.get('vendor') or 'isinwheel'}",
        f"Product type: {detail.get('type') or 'Catalog item'}",
        f"Collections: {', '.join(matched_handles[:4])}" if matched_handles else "Collections: Catalog",
    ]
    if detail.get("compare_at_price") and detail.get("price") and detail["compare_at_price"] > detail["price"]:
        lines.append("Discounted from the regular price.")
    return localized(lines)


def build_spec_table(detail: dict[str, Any], collection_name: str, matched_handles: list[str]) -> str:
    table = [
        {"label": "Collection", "value": collection_name},
        {"label": "Vendor", "value": detail.get("vendor") or "isinwheel"},
        {"label": "Type", "value": detail.get("type") or "Catalog item"},
        {"label": "Tags", "value": ", ".join(matched_handles[:4]) if matched_handles else "Catalog"},
    ]
    return localized(table)


def normalize_tags(detail: dict[str, Any], is_new: bool, matched_handles: list[str]) -> str:
    raw_tags = detail.get("tags") or []
    if isinstance(raw_tags, str):
        tags = [part.strip() for part in raw_tags.split(",") if part.strip()]
    else:
        tags = [str(part).strip() for part in raw_tags if str(part).strip()]

    normalized = []
    if is_new and "NEW" not in normalized:
        normalized.append("NEW")
    if len(matched_handles) > 1 and "HOT" not in normalized:
        normalized.append("HOT")
    if detail.get("compare_at_price") and detail.get("price") and detail["compare_at_price"] > detail["price"]:
        normalized.append("Spring Sale")
    for tag in tags:
        upper = tag.upper()
        if upper in {"NEW", "HOT"} and upper not in normalized:
            normalized.append(upper)
    return localized(normalized)


def normalize_variant_specs(detail: dict[str, Any], variant: dict[str, Any]) -> str:
    specs: dict[str, str] = {}
    for index, option in enumerate(detail.get("options", []), start=1):
        option_name = str(option.get("name") or f"option{index}").strip() or f"option{index}"
        option_key = option_name.lower()
        option_value = variant.get(f"option{index}")
        if option_value:
            specs[option_key] = str(option_value)
    return localized(specs)


def build_category_records(collection_index: dict[str, dict[str, Any]]) -> list[CategoryRecord]:
    records: list[CategoryRecord] = []
    for root_index, root in enumerate(ROOT_CATEGORIES, start=1):
        hero_image = ""
        menu_image = ""
        for child in root["children"]:
            source = next((collection_index.get(handle) for handle in child["source_handles"] if collection_index.get(handle)), None)
            source_image = bounded_media_url(((source or {}).get("image") or {}).get("src"))
            if source_image:
                hero_image = source_image
                menu_image = hero_image
                break
        records.append(
            CategoryRecord(
                id=root["id"],
                parent_id=None,
                slug=root["slug"],
                name=root["name"],
                description=root["description"],
                hero_image=hero_image,
                menu_image=menu_image,
                sort_order=root_index,
            )
        )
        for child_index, child in enumerate(root["children"], start=1):
            source = next((collection_index.get(handle) for handle in child["source_handles"] if collection_index.get(handle)), None)
            source_description = strip_html(source.get("description") if source else "")
            source_image = bounded_media_url(((source or {}).get("image") or {}).get("src"))
            records.append(
                CategoryRecord(
                    id=child["id"],
                    parent_id=root["id"],
                    slug=child["slug"],
                    name=child["name"],
                    description=source_description or child["description"],
                    hero_image=source_image,
                    menu_image=source_image,
                    sort_order=child.get("sort_order", child_index),
                )
            )
    return records


def build_category_product_seeds(
    collection_index: dict[str, dict[str, Any]], target_per_category: int
) -> tuple[list[ProductSeed], list[dict[str, Any]]]:
    seeds: list[ProductSeed] = []
    summaries: list[dict[str, Any]] = []
    for root in ROOT_CATEGORIES:
        children = sorted(root["children"], key=lambda child: child.get("sync_priority", child.get("sort_order", 99)))
        for child in children:
            candidates: list[dict[str, Any]] = []
            candidate_by_handle: dict[str, dict[str, Any]] = {}
            for source_handle in child["source_handles"]:
                source_collection = collection_index.get(source_handle)
                if not source_collection:
                    print(f"[warn] missing source collection: {source_handle}", file=sys.stderr)
                    continue
                products = fetch_collection_products(source_handle)
                for product in products:
                    handle = product.get("handle")
                    if not handle:
                        continue
                    if handle not in candidate_by_handle:
                        candidate_by_handle[handle] = {
                            "source_handle": handle,
                            "matched_handles": [],
                        }
                        candidates.append(candidate_by_handle[handle])
                    if source_handle not in candidate_by_handle[handle]["matched_handles"]:
                        candidate_by_handle[handle]["matched_handles"].append(source_handle)

            if target_per_category > 0:
                selected_candidates = candidates[:target_per_category]
            else:
                selected_candidates = candidates

            if not selected_candidates:
                summaries.append(
                    {
                        "categorySlug": child["slug"],
                        "categoryName": child["name"],
                        "sourceUniqueProducts": 0,
                        "generatedProducts": 0,
                        "derivedProducts": 0,
                    }
                )
                continue

            for sort_order, candidate in enumerate(selected_candidates, start=1):
                seeds.append(
                    ProductSeed(
                        slug=slugify(f"{candidate['source_handle']}-{child['slug']}"),
                        source_handle=candidate["source_handle"],
                        category_id=child["id"],
                        category_slug=child["slug"],
                        category_name=child["name"],
                        sort_order=sort_order,
                        matched_handles=list(candidate["matched_handles"]),
                        derived_index=0,
                        derived_label="",
                    )
                )

            derived_products = 0
            if target_per_category > 0 and len(selected_candidates) < target_per_category:
                base_count = len(selected_candidates)
                for next_index in range(base_count + 1, target_per_category + 1):
                    base_candidate = selected_candidates[(next_index - base_count - 1) % base_count]
                    derived_index = ((next_index - base_count - 1) // base_count) + 1
                    seeds.append(
                        ProductSeed(
                            slug=slugify(f"{base_candidate['source_handle']}-{child['slug']}-{derived_index + 1}"),
                            source_handle=base_candidate["source_handle"],
                            category_id=child["id"],
                            category_slug=child["slug"],
                            category_name=child["name"],
                            sort_order=next_index,
                            matched_handles=list(base_candidate["matched_handles"]),
                            derived_index=derived_index,
                            derived_label=derived_label(child["slug"], derived_index),
                        )
                    )
                    derived_products += 1

            summaries.append(
                {
                    "categorySlug": child["slug"],
                    "categoryName": child["name"],
                    "sourceUniqueProducts": len(candidates),
                    "generatedProducts": len(selected_candidates) + derived_products,
                    "derivedProducts": derived_products,
                }
            )
    return seeds, summaries


def load_product_details(product_seeds: list[ProductSeed], max_workers: int) -> dict[str, dict[str, Any]]:
    details: dict[str, dict[str, Any]] = {}
    handles = sorted({seed.source_handle for seed in product_seeds})
    with concurrent.futures.ThreadPoolExecutor(max_workers=max_workers) as executor:
        future_map = {executor.submit(fetch_product_detail, handle): handle for handle in handles}
        for future in concurrent.futures.as_completed(future_map):
            handle = future_map[future]
            try:
                details[handle] = future.result()
            except Exception as exc:
                print(f"[warn] product fetch failed: {handle}: {exc}", file=sys.stderr)
    return details


def build_sql(
    category_records: list[CategoryRecord],
    product_seeds: list[ProductSeed],
    details: dict[str, dict[str, Any]],
    deactivate_missing: bool,
) -> tuple[str, dict[str, int]]:
    statements = ["BEGIN;"]

    all_category_ids = [record.id for record in category_records]
    if deactivate_missing:
        synced_slugs = sorted(seed.slug for seed in product_seeds)
        statements.append(
            "UPDATE pms_product SET published = FALSE, stock = 0, update_time = CURRENT_TIMESTAMP "
            f"WHERE category_id IN ({', '.join(str(category_id) for category_id in all_category_ids)}) "
            f"AND slug NOT IN ({', '.join(sql_quote(slug) for slug in synced_slugs)});"
        )

    for record in category_records:
        statements.append(
            "INSERT INTO pms_category (id, parent_id, slug, name, description, hero_image, menu_image, sort_order, published) "
            f"VALUES ({record.id}, {sql_quote(record.parent_id)}, {sql_quote(record.slug)}, {sql_quote(localized(record.name))}::jsonb, "
            f"{sql_quote(localized(record.description))}::jsonb, {sql_quote(record.hero_image)}, {sql_quote(record.menu_image)}, {record.sort_order}, TRUE) "
            "ON CONFLICT (id) DO UPDATE SET "
            "parent_id = EXCLUDED.parent_id, "
            "slug = EXCLUDED.slug, "
            "name = EXCLUDED.name, "
            "description = EXCLUDED.description, "
            "hero_image = EXCLUDED.hero_image, "
            "menu_image = EXCLUDED.menu_image, "
            "sort_order = EXCLUDED.sort_order, "
            "published = TRUE, "
            "update_time = CURRENT_TIMESTAMP;"
        )

    product_count = 0
    sku_count = 0
    for seed in product_seeds:
        detail = details.get(seed.source_handle)
        if not detail:
            print(f"[warn] missing product detail for seed: {seed.source_handle}", file=sys.stderr)
            continue
        resolved_category_id = seed.category_id
        resolved_category_name = seed.category_name
        images = [absolute_url(url) for url in detail.get("images", []) if absolute_url(url)]
        rotated_images = shift_images(images, seed.derived_index)
        first_image = rotated_images[0] if rotated_images else absolute_url(detail.get("featured_image"))
        product_pic = bounded_media_url(first_image)
        raw_variants = detail.get("variants", []) or []
        variants = raw_variants or [
            {
                "id": "default",
                "sku": seed.source_handle,
                "price": detail.get("price"),
                "compare_at_price": detail.get("compare_at_price"),
                "available": True,
                "title": "Default",
                "name": detail.get("title") or seed.source_handle,
            }
        ]
        available_count = sum(1 for variant in variants if variant.get("available"))
        stock = max(1, available_count) * DEFAULT_STOCK
        price = decimal_price(detail.get("price")) or "0.00"
        compare_at_price = decimal_price(detail.get("compare_at_price"))
        if seed.derived_index > 0:
            price = adjust_price(price, seed.derived_index) or price
            compare_at_price = adjust_compare_at(compare_at_price, price, seed.derived_index)
            stock = max(DEFAULT_STOCK, stock + (seed.derived_index % 4) * 3)
        published_at = detail.get("published_at") or detail.get("created_at") or ""
        is_new = bool(published_at and published_at[:4] >= "2025")
        base_title = (detail.get("title") or seed.source_handle).strip()
        title = base_title if seed.derived_index == 0 else f"{base_title} - {seed.derived_label}"
        description_html = detail.get("description") or f"<p>{base_title}</p>"
        if seed.derived_index > 0:
            description_html = (
                f"{description_html}<p>{seed.derived_label} for the {resolved_category_name.lower()} catalog.</p>"
            )
        matched_handles = seed.matched_handles
        subtitle = resolved_category_name if seed.derived_index == 0 else f"{resolved_category_name} / {seed.derived_label}"
        statements.append(
            "INSERT INTO pms_product (category_id, slug, name, subtitle, description, price, compare_at_price, stock, pic, is_new, tags, images, app_image, "
            "specs, quick_know, upsells, spec_table, box_items, faqs, published, sort_order) "
            f"VALUES ({resolved_category_id}, {sql_quote(seed.slug)}, {sql_quote(localized(title))}::jsonb, "
            f"{sql_quote(localized(subtitle))}::jsonb, {sql_quote(localized(description_html))}::jsonb, {sql_quote(price)}, {sql_quote(compare_at_price)}, "
            f"{stock}, {sql_quote(product_pic)}, {sql_quote(is_new)}, {sql_quote(normalize_tags(detail, is_new, matched_handles))}::jsonb, "
            f"{sql_quote(json.dumps(rotated_images or images, ensure_ascii=False))}::jsonb, '', "
            f"{sql_quote(build_specs(detail, resolved_category_name, available_count))}::jsonb, "
            f"{sql_quote(build_quick_know(detail, matched_handles))}::jsonb, "
            f"{sql_quote(localized([]))}::jsonb, "
            f"{sql_quote(build_spec_table(detail, resolved_category_name, matched_handles))}::jsonb, "
            f"{sql_quote(localized([]))}::jsonb, {sql_quote(localized([]))}::jsonb, TRUE, {seed.sort_order}) "
            "ON CONFLICT (slug) DO UPDATE SET "
            "category_id = EXCLUDED.category_id, "
            "name = EXCLUDED.name, "
            "subtitle = EXCLUDED.subtitle, "
            "description = EXCLUDED.description, "
            "price = EXCLUDED.price, "
            "compare_at_price = EXCLUDED.compare_at_price, "
            "stock = EXCLUDED.stock, "
            "pic = EXCLUDED.pic, "
            "is_new = EXCLUDED.is_new, "
            "tags = EXCLUDED.tags, "
            "images = EXCLUDED.images, "
            "app_image = EXCLUDED.app_image, "
            "specs = EXCLUDED.specs, "
            "quick_know = EXCLUDED.quick_know, "
            "upsells = EXCLUDED.upsells, "
            "spec_table = EXCLUDED.spec_table, "
            "box_items = EXCLUDED.box_items, "
            "faqs = EXCLUDED.faqs, "
            "published = TRUE, "
            "sort_order = EXCLUDED.sort_order, "
            "update_time = CURRENT_TIMESTAMP;"
        )
        product_count += 1

        sku_codes = []
        for variant_index, variant in enumerate(variants, start=1):
            base_sku = str(variant.get("sku") or f"{seed.source_handle}-{variant.get('id') or variant_index}")
            sku_suffix = f"{base_sku}-{seed.category_slug}-{seed.sort_order}-{variant_index}"
            sku_code = build_unique_sku_code(sku_suffix, seed.slug, variant_index)
            sku_codes.append(sku_code)
            variant_image = absolute_url(
                (variant.get("featured_image") or {}).get("src")
                or ((variant.get("featured_media") or {}).get("preview_image") or {}).get("src")
                or first_image
            )
            variant_pic = bounded_media_url(variant_image)
            variant_images = [variant_image] if variant_image else (rotated_images or images)
            variant_price = decimal_price(variant.get("price")) or price
            variant_compare_at = decimal_price(variant.get("compare_at_price"))
            if seed.derived_index > 0:
                variant_price = adjust_price(variant_price, seed.derived_index) or price
                variant_compare_at = adjust_compare_at(variant_compare_at, variant_price, seed.derived_index)
            variant_stock = DEFAULT_STOCK if variant.get("available") or seed.derived_index > 0 else 0
            variant_description = variant.get("name") or f"{title} - {variant.get('title') or 'Default'}"
            if seed.derived_index > 0:
                variant_description = f"{variant_description} / {seed.derived_label}"
            variant_status = "ACTIVE" if variant_stock > 0 else "INACTIVE"
            statements.append(
                "INSERT INTO pms_sku (product_id, sku_code, price, compare_at_price, stock, pic, images, description, specs, status) "
                f"VALUES ((SELECT id FROM pms_product WHERE slug = {sql_quote(seed.slug)}), {sql_quote(sku_code)}, {sql_quote(variant_price)}, "
                f"{sql_quote(variant_compare_at)}, {variant_stock}, {sql_quote(variant_pic)}, "
                f"{sql_quote(json.dumps(variant_images, ensure_ascii=False))}::jsonb, {sql_quote(localized(variant_description))}::jsonb, "
                f"{sql_quote(normalize_variant_specs(detail, variant))}::jsonb, {sql_quote(variant_status)}) "
                "ON CONFLICT (sku_code) DO UPDATE SET "
                "product_id = EXCLUDED.product_id, "
                "price = EXCLUDED.price, "
                "compare_at_price = EXCLUDED.compare_at_price, "
                "stock = EXCLUDED.stock, "
                "pic = EXCLUDED.pic, "
                "images = EXCLUDED.images, "
                "description = EXCLUDED.description, "
                "specs = EXCLUDED.specs, "
                "status = EXCLUDED.status, "
                "update_time = CURRENT_TIMESTAMP;"
            )
            sku_count += 1
        statements.append(
            f"UPDATE pms_sku SET stock = 0, status = 'INACTIVE', update_time = CURRENT_TIMESTAMP "
            f"WHERE product_id = (SELECT id FROM pms_product WHERE slug = {sql_quote(seed.slug)}) "
            f"AND sku_code NOT IN ({', '.join(sql_quote(code) for code in sku_codes)});"
        )

    statements.append(
        "SELECT setval(pg_get_serial_sequence('pms_category', 'id'), COALESCE((SELECT MAX(id) FROM pms_category), 1), true);"
    )
    statements.append(
        "SELECT setval(pg_get_serial_sequence('pms_product', 'id'), COALESCE((SELECT MAX(id) FROM pms_product), 1), true);"
    )
    statements.append(
        "SELECT setval(pg_get_serial_sequence('pms_sku', 'id'), COALESCE((SELECT MAX(id) FROM pms_sku), 1), true);"
    )
    statements.append("COMMIT;")
    summary = {
        "categories": len(category_records),
        "products": product_count,
        "skus": sku_count,
    }
    return "\n".join(statements) + "\n", summary


def main() -> int:
    parser = argparse.ArgumentParser(description="Sync isinwheel product catalog into the shop database.")
    parser.add_argument("--apply", action="store_true", help="Execute the generated SQL through --psql-cmd.")
    parser.add_argument("--psql-cmd", help="Command used to execute SQL, for example: docker exec -i shop-db-1 psql -v ON_ERROR_STOP=1 -U shop -d shop")
    parser.add_argument("--output", help="Optional path to write the generated SQL.")
    parser.add_argument("--max-workers", type=int, default=8, help="Concurrent product detail requests.")
    parser.add_argument("--deactivate-missing", action="store_true", help="Hide synced-category products that are no longer present in the source catalog.")
    parser.add_argument(
        "--target-per-category",
        type=int,
        default=DEFAULT_TARGET_PER_CATEGORY,
        help="Number of products to keep per child collection. Values <= 0 keep every unique source product.",
    )
    args = parser.parse_args()

    collection_index = fetch_collections_index()
    product_seeds, category_summaries = build_category_product_seeds(collection_index, args.target_per_category)
    details = load_product_details(product_seeds, max(1, args.max_workers))
    category_records = build_category_records(collection_index)
    sql_text, summary = build_sql(category_records, product_seeds, details, args.deactivate_missing)

    print(
        json.dumps(
            {
                "categories": summary["categories"],
                "products": summary["products"],
                "skus": summary["skus"],
                "sourceCollections": sum(len(child["source_handles"]) for root in ROOT_CATEGORIES for child in root["children"]),
                "targetPerCategory": args.target_per_category,
                "childCollections": category_summaries,
            },
            ensure_ascii=False,
            indent=2,
        )
    )

    if args.output:
        with open(args.output, "w", encoding="utf-8") as fh:
            fh.write(sql_text)
        print(f"[info] wrote SQL to {args.output}")

    if args.apply:
        if not args.psql_cmd:
            print("--apply requires --psql-cmd", file=sys.stderr)
            return 2
        completed = subprocess.run(args.psql_cmd, input=sql_text.encode("utf-8"), shell=True)
        if completed.returncode != 0:
            return completed.returncode
        print("[info] catalog sync applied successfully")
    elif not args.output:
        print("[info] dry run complete")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
