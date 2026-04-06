#!/usr/bin/env python3
import argparse
import json
import re
import urllib.request
from html import unescape
from pathlib import Path


SITE_URL = "https://www.isinwheel.com/"
HEADERS = {
    "User-Agent": "Mozilla/5.0 (compatible; isinwheel-home-sync/1.0; +https://www.isinwheel.com)",
    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
}


def fetch_html() -> str:
    request = urllib.request.Request(SITE_URL, headers=HEADERS)
    with urllib.request.urlopen(request, timeout=45) as response:
        return response.read().decode("utf-8", errors="replace")


def absolute_url(value: str | None) -> str:
    if not value:
        return ""
    value = unescape(value)
    if value.startswith("//"):
        return f"https:{value}"
    if value.startswith("/"):
        return f"https://www.isinwheel.com{value}"
    return value


def clean_text(value: str | None) -> str:
    if not value:
        return ""
    text = re.sub(r"<[^>]+>", " ", unescape(value))
    text = re.sub(r"\s+", " ", text)
    return text.strip()


def parse_hero_slides(html: str) -> list[dict]:
    slideshow_match = re.search(r"<slideshow-element.*?</slideshow-words>", html, re.S)
    if not slideshow_match:
        return []
    slideshow_html = slideshow_match.group(0)

    banner_blocks = re.findall(
        r'<div class="banner media--adapt mobile:media--adapt w-full overflow-hidden" data-type="(image|video)" >(.*?)</div>(?=<div class="banner media--adapt|</slideshow-element>)',
        slideshow_html,
        re.S,
    )
    banners: list[dict] = []
    for index, (banner_type, body) in enumerate(banner_blocks):
        images = re.findall(r'<img src="([^"]+)"', body, re.S)
        if not images:
            continue
        image = images[0] if banner_type == "image" else images[-1]
        link_match = re.search(
            r'<a href="([^"]+)" class="block absolute top-0 left-0 w-full h-full"',
            body,
        )
        banners.append(
            {
                "index": index,
                "image": absolute_url(image),
                "linkUrl": absolute_url(link_match.group(1) if link_match else "/"),
            }
        )

    word_blocks = {
        int(match.group(1)): match.group(2)
        for match in re.finditer(
            r'<div class="slideshow-word[^"]*" data-index="(\d+)"[^>]*>(.*?)</div>(?=(?:<style>|</slideshow-words>))',
            slideshow_html,
            re.S,
        )
    }

    slides = []
    total = max(len(banners), len(word_blocks))
    for index in range(total):
        banner = banners[index] if index < len(banners) else {}
        body = word_blocks.get(index, "")
        title_match = re.search(r'video_title.*?>(.*?)</split-words>', body, re.S)
        subtitle_match = re.search(r'video_tips.*?>(.*?)</split-words>', body, re.S)
        cta_match = re.search(r'<a class="button[^"]*" href="([^"]+)".*?<span[^>]*>(.*?)</span>', body, re.S)
        title = clean_text(title_match.group(1) if title_match else "")
        subtitle = clean_text(subtitle_match.group(1) if subtitle_match else "")
        cta_label = clean_text(cta_match.group(2) if cta_match else "Shop now") or "Shop now"
        link_url = absolute_url(cta_match.group(1) if cta_match else banner.get("linkUrl") or "/")
        image = banner.get("image", "")
        if not image:
            continue
        slug_match = re.search(r"/([^/?#]+)$", link_url)
        slides.append(
            {
                "index": index,
                "slug": clean_text(slug_match.group(1) if slug_match else f"slide-{index}") or f"slide-{index}",
                "title": title,
                "subtitle": subtitle,
                "image": image,
                "linkUrl": link_url,
                "ctaLabel": cta_label,
            }
        )
    return slides


def parse_feature_video(html: str) -> dict:
    match = re.search(
        r'id="shopify-section-template--[^"]+__video-with-text-overlay".*?<img src="([^"]+)".*?<iframe src="([^"]+)"',
        html,
        re.S,
    )
    if not match:
        return {}
    return {
        "poster": absolute_url(match.group(1)),
        "embedUrl": match.group(2),
    }


def parse_reviews(html: str) -> list[dict]:
    reviews = []
    for match in re.finditer(r"<div class='jdgm-carousel-item'[^>]*>(.*?)</div>\s*</div>\s*</div>\s*</div>", html, re.S):
        block = match.group(1)
        name_match = re.search(r"jdgm-carousel-item__reviewer-name[^>]*>\s*(.*?)\s*</span>", block, re.S)
        body_match = re.search(r"jdgm-carousel-item__review-body'><p>(.*?)</p>", block, re.S)
        title_match = re.search(r"jdgm-carousel-item__review-title[^>]*>(.*?)</div>", block, re.S)
        image_match = re.search(r"jdgm-carousel-item__product-image' alt='[^']*' data-src='([^']+)'", block)
        product_match = re.search(r"jdgm-carousel-item__product-title[^>]*>\s*(.*?)\s*</div>", block, re.S)
        date_match = re.search(r"data-time='([^']+)'", block)
        if not name_match or not body_match:
            continue
        reviews.append(
            {
                "name": clean_text(name_match.group(1)),
                "title": clean_text(title_match.group(1) if title_match else ""),
                "text": clean_text(body_match.group(1)),
                "image": absolute_url(image_match.group(1) if image_match else ""),
                "productTitle": clean_text(product_match.group(1) if product_match else ""),
                "date": clean_text(date_match.group(1) if date_match else ""),
                "verified": "jdgm-rev__buyer-badge" in block,
            }
        )
        if len(reviews) >= 4:
            break
    return reviews


def parse_blog_cards(html: str) -> list[dict]:
    cards = []
    section_match = re.search(
        r'id="shopify-section-template--[^"]+__blog-posts-collage".*?<slider-element.*?</slider-element>',
        html,
        re.S,
    )
    if not section_match:
        return cards
    section_html = section_match.group(0)
    pattern = re.compile(
        r'<div class="card article-card.*?'
        r'<a href="([^"]+)" class="article-card__link[^"]*"[^>]*>.*?'
        r'<img src="([^"]+)".*?'
        r'<time[^>]*>(.*?)</time>.*?'
        r'#comments">(.*?)</a>.*?'
        r'<a class="article-card__title[^"]*" href="[^"]+">(.*?)</a>',
        re.S,
    )
    for index, match in enumerate(pattern.finditer(section_html), start=1):
        cards.append(
            {
                "id": index,
                "image": absolute_url(match.group(2)),
                "title": clean_text(match.group(5)),
                "url": absolute_url(match.group(1)),
                "date": clean_text(match.group(3)),
                "comments": clean_text(match.group(4)),
            }
        )
        if len(cards) >= 3:
            break
    return cards


def main() -> int:
    parser = argparse.ArgumentParser(description="Scrape homepage content from isinwheel.")
    parser.add_argument(
        "--output",
        default="data/isinwheel-home.json",
        help="Output JSON path relative to repo root.",
    )
    args = parser.parse_args()

    html = fetch_html()
    payload = {
        "source": SITE_URL,
        "heroSlides": parse_hero_slides(html),
        "featureVideo": parse_feature_video(html),
        "customerReviews": parse_reviews(html),
        "blogCards": parse_blog_cards(html),
    }

    output_path = Path(args.output)
    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(json.dumps(payload, ensure_ascii=False, indent=2), encoding="utf-8")
    print(json.dumps({
        "heroSlides": len(payload["heroSlides"]),
        "customerReviews": len(payload["customerReviews"]),
        "blogCards": len(payload["blogCards"]),
        "output": str(output_path),
    }, ensure_ascii=False, indent=2))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
