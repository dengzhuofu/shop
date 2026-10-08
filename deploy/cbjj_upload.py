"""Upload only verified release artifacts, reusing identical staged files."""
import argparse
import hashlib
import json
import re
import shlex
from pathlib import Path
from cbjj_remote import connect, execute

ROOT = Path(__file__).resolve().parents[1]
ALLOWED = {'backend.jar', 'frontend.tar.gz', 'cbjj_release.py',
           'V4__cbjj_currency_and_review_provenance.sql', 'V5__cbjj_catalog_content.sql'}

def main():
    parser = argparse.ArgumentParser(); parser.add_argument('release'); args = parser.parse_args()
    if not re.fullmatch(r'[A-Za-z0-9_-]+', args.release): raise ValueError('Invalid release name')
    local = ROOT / 'output/cbjj-deploy' / args.release
    manifest = json.loads((local / 'manifest.json').read_text(encoding='utf-8'))
    if set(manifest['sha256']) != ALLOWED: raise ValueError('Unexpected production artifact list')
    for name, expected in manifest['sha256'].items():
        if hashlib.sha256((local / name).read_bytes()).hexdigest() != expected:
            raise RuntimeError('Local checksum mismatch: ' + name)
    remote = '/opt/shop/releases/' + args.release
    client = connect()
    try:
        execute(client, 'mkdir -p ' + shlex.quote(remote))
        execute(client, 'test ! -e ' + shlex.quote(remote + '/private/published.json'))
        sftp = client.open_sftp(); sftp.get_channel().settimeout(60)
        for name in list(manifest['sha256']) + ['manifest.json']:
            target = remote + '/' + name
            existing = execute(client, 'if [ -f ' + shlex.quote(target) + ' ]; then sha256sum ' + shlex.quote(target) + '; fi', False)
            expected = hashlib.sha256((local / name).read_bytes()).hexdigest()
            if existing.split()[:1] == [expected]:
                print('Verified staged ' + name, flush=True); continue
            last = [0]
            def progress(done, total):
                if done - last[0] >= 8 * 1024 * 1024 or done == total:
                    print(f'{name}: {done // (1024*1024)}/{total // (1024*1024)} MiB', flush=True)
                    last[0] = done
            sftp.put(str(local / name), target, callback=progress)
        sftp.close()
        execute(client, 'python3 ' + shlex.quote(remote + '/cbjj_release.py') + ' verify')
    finally: client.close()

if __name__ == '__main__': main()
