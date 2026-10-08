"""Copy one built-in imagegen output into its approved public path and record provenance."""
import hashlib
import json
import shutil
import sys
from datetime import datetime, timezone
from pathlib import Path

root = Path(__file__).resolve().parents[1]
source = Path(sys.argv[1]).resolve()
job = json.loads((root / 'output/cbjj-image-jobs.json').read_text(encoding='utf-8'))[int(sys.argv[2])]
target = Path(job['target']).resolve()
if not target.is_relative_to(root / 'public/cbjj') or target.suffix != '.png':
    raise ValueError('Unexpected project image destination')
if not source.is_file() or not source.read_bytes().startswith(b'\x89PNG\r\n\x1a\n'):
    raise ValueError('Image generation did not produce a valid local PNG')
target.parent.mkdir(parents=True, exist_ok=True)
shutil.copyfile(source, target)
record = {**job, 'view': job['kind'], 'path': str(target), 'source': str(source),
          'reference': job['source'], 'tool': 'image_gen',
          'savedAt': datetime.now(timezone.utc).isoformat(),
          'sha256': hashlib.sha256(target.read_bytes()).hexdigest()}
with (root / 'output/cbjj-assets.jsonl').open('a', encoding='utf-8') as journal:
    journal.write(json.dumps(record, ensure_ascii=False) + '\n')
print(target.relative_to(root / 'public').as_posix())
