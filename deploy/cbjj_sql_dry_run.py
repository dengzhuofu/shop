"""Check the proposed incremental SQL inside a rollback-only production transaction."""
import importlib.util, json
from pathlib import Path
from cbjj_remote import connect
root=Path(__file__).resolve().parents[1]
parts=[(root/'backend/src/main/resources/db/migration'/name).read_text(encoding='utf-8')
       for name in ['V4__cbjj_currency_and_review_provenance.sql','V5__cbjj_catalog_content.sql']]
spec=importlib.util.spec_from_file_location('release',root/'deploy/cbjj_release.py')
release=importlib.util.module_from_spec(spec);spec.loader.exec_module(release)
queries=[]
release.run=lambda arguments: queries.append(arguments[-1]) or '{}'
release.financial_snapshot()
sql="BEGIN;\nSELECT 'BEFORE:'||("+queries[0].strip().removeprefix('SELECT ').removesuffix(';')+")::text;\n"
sql+='\n'.join(parts)+"\nSELECT 'AFTER:'||("+queries[0].strip().removeprefix('SELECT ').removesuffix(';')+")::text;\n"
sql+="SELECT 'VISIBLE_REVIEWS:'||count(*) FROM pms_review WHERE is_demo=false;\nROLLBACK;\n"
client=connect()
try:
 stdin,stdout,stderr=client.exec_command('docker exec -i shop-db psql -U shop -d shop -v ON_ERROR_STOP=1 -At',timeout=60)
 stdin.write(sql);stdin.channel.shutdown_write()
 output=stdout.read().decode('utf-8');error=stderr.read().decode('utf-8')
 if stdout.channel.recv_exit_status(): raise RuntimeError('Incremental SQL validation failed: '+error)
 before=json.loads(next(line.removeprefix('BEFORE:') for line in output.splitlines() if line.startswith('BEFORE:')))
 after=json.loads(next(line.removeprefix('AFTER:') for line in output.splitlines() if line.startswith('AFTER:')))
 if before!=after: raise RuntimeError('Proposed migration changes historical prices, totals, stock, IDs or user counts')
 if not output.rstrip().endswith('ROLLBACK'): raise RuntimeError('Rollback confirmation missing')
 target=root/'output/cbjj-deploy/sql-dry-run.json';target.parent.mkdir(parents=True,exist_ok=True)
 target.write_text(json.dumps({'historical_snapshot_unchanged':True,'rolled_back':True,'products':len(after['catalog']),
                             'skus':len(after['skus']),'orders':len(after['orders']),'users':after['users'],
                             'visible_pre_cbjj_reviews':int(next(line.split(':')[1] for line in output.splitlines() if line.startswith('VISIBLE_REVIEWS:')))},indent=2),encoding='utf-8')
 print('Production incremental SQL dry run passed and rolled back. Historical totals, prices, inventory, IDs and user counts unchanged.')
finally: client.close()

