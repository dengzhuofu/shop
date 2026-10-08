"""Create a narrow allowlisted production bundle; local secrets/backups never enter it."""
import argparse
import hashlib
import json
import re
import shutil
import tarfile
from datetime import datetime, timezone
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('release')
    parser.add_argument('--reuse-media', action='store_true', help='Exclude unchanged CBJJ media; stage and verify identical media on the server first')
    args = parser.parse_args()
    if not re.fullmatch(r'[A-Za-z0-9_-]+', args.release):
        raise ValueError('Invalid release name')
    assets = json.loads((ROOT / 'output/cbjj-assets-manifest.json').read_text(encoding='utf-8'))
    if not assets['complete'] or assets['products'] != 15 or assets['skus'] != 70:
        raise RuntimeError('All catalog media must pass validation before packaging')
    frontend = ROOT / '.output'
    if not (frontend / 'server/index.mjs').is_file():
        raise RuntimeError('Missing Nuxt production build')
    for asset in assets['assets']:
        file = frontend / 'public' / asset['path'].lstrip('/')
        if not file.is_file() or hashlib.sha256(file.read_bytes()).hexdigest() != asset['sha256']:
            raise RuntimeError('Production build contains a missing/stale asset: ' + asset['path'])
    target = ROOT / 'output/cbjj-deploy' / args.release
    target.mkdir(parents=True, exist_ok=True)
    copies = {'backend.jar': ROOT / 'backend/target/backend-0.0.1-SNAPSHOT.jar',
              'cbjj_release.py': ROOT / 'deploy/cbjj_release.py'}
    for name in ['V4__cbjj_currency_and_review_provenance.sql', 'V5__cbjj_catalog_content.sql']:
        copies[name] = ROOT / 'backend/src/main/resources/db/migration' / name
    for name, source in copies.items(): shutil.copyfile(source, target / name)
    with tarfile.open(target / 'frontend.tar.gz', 'w:gz') as archive:
        for name in ['server', 'nitro.json']:
            if (frontend / name).exists(): archive.add(frontend / name, arcname=name)
        # Identical main/default/gift photographs are aliases, not new generated views.
        # POSIX tar hard links avoid uploading their full PNG bytes repeatedly.
        seen = {}
        asset_hashes = {'public/' + item['path'].lstrip('/'): item['sha256'] for item in assets['assets']}
        for file in sorted((frontend / 'public').rglob('*')):
            if not file.is_file(): continue
            name = file.relative_to(frontend).as_posix()
            if args.reuse_media and name.startswith('public/cbjj/'):
                continue
            checksum = asset_hashes.get(name)
            if checksum and checksum in seen:
                entry = archive.gettarinfo(str(file), arcname=name)
                entry.type = tarfile.LNKTYPE; entry.linkname = seen[checksum]; entry.size = 0
                archive.addfile(entry)
            else:
                archive.add(file, arcname=name)
                if checksum: seen[checksum] = name
    files = list(copies) + ['frontend.tar.gz']
    manifest = {'release': args.release, 'createdAt': datetime.now(timezone.utc).isoformat(),
                'brand': 'CBJJ', 'media': {key: assets[key] for key in ['products', 'skus', 'productViews', 'variantImages']},
                'sha256': {name: hashlib.sha256((target / name).read_bytes()).hexdigest() for name in files}}
    if args.reuse_media:
        manifest['reusedMedia'] = {item['path'].lstrip('/'): item['sha256'] for item in assets['assets']}
    (target / 'manifest.json').write_text(json.dumps(manifest, indent=2), encoding='utf-8')
    print('Production bundle: ' + str(target))
    print('Allowlisted files: ' + ', '.join(files + ['manifest.json']))

if __name__ == '__main__': main()
