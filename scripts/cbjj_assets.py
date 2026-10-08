"""Validate every published catalog asset, including base images shared by SKUs.

This checks PNG headers and hashes only; it does not create or edit raster images.
"""
import hashlib
import json
import struct
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
def main():
    catalog = json.loads((ROOT / 'data/cbjj-catalog.json').read_text(encoding='utf-8'))
    paths = {'/cbjj/hero/hero.png', '/cbjj/payments/klarna.svg',
             '/cbjj/payments/visa.png', '/cbjj/payments/mastercard.svg'}
    for item in catalog:
        paths.update(item['images'])
        paths.add(item['pic'])
        if not item['images']:
            raise RuntimeError('Missing main image for product ' + str(item['id']))
        for sku in item['skuUpdates']:
            paths.update(sku['images'])
            paths.add(sku['pic'])
    missing = []
    assets = []
    for name in sorted(paths):
        file = ROOT / 'public' / name.lstrip('/')
        if not file.is_file():
            missing.append(name)
            continue
        raw = file.read_bytes()
        record = {'path': name, 'bytes': len(raw), 'sha256': hashlib.sha256(raw).hexdigest()}
        if file.suffix == '.png':
            if not raw.startswith(b'\x89PNG\r\n\x1a\n') or raw[12:16] != b'IHDR':
                raise RuntimeError('Invalid PNG: ' + name)
            record['width'], record['height'] = struct.unpack('>II', raw[16:24])
            minimum = 64 if '/payments/' in name else 512
            if min(record['width'], record['height']) < minimum:
                raise RuntimeError('Insufficient image resolution: ' + name)
        elif b'<svg' not in raw:
            raise RuntimeError('Invalid SVG: ' + name)
        assets.append(record)
    report = {'complete': not missing, 'products': len(catalog),
              'skus': sum(len(item['skuUpdates']) for item in catalog),
              'productViews': sum(len(item['images']) for item in catalog),
              'variantImages': len({sku['pic'] for item in catalog for sku in item['skuUpdates']}),
              'assets': assets, 'missing': missing}
    out = ROOT / 'output/cbjj-assets-manifest.json'
    out.parent.mkdir(exist_ok=True)
    out.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding='utf-8')
    print(json.dumps({key: value for key, value in report.items() if key != 'assets'}, ensure_ascii=False))
    if missing: raise SystemExit(1)

if __name__ == '__main__': main()
