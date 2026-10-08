"""Publish locally built CBJJ artifacts without recreating Postgres or replacing TLS."""
import argparse, hashlib, json, os, re, subprocess, time
from pathlib import Path

ROOT = Path(__file__).resolve().parent
STATE = ROOT / 'private'
NAMES = ('shop-backend', 'shop-frontend')

def run(arguments, input=None, capture=True):
    return subprocess.run(arguments, input=input, text=True, check=True,
                          stdout=subprocess.PIPE if capture else None,
                          stderr=subprocess.PIPE if capture else None).stdout

def inspect(name):
    return json.loads(run(['docker', 'inspect', name]))[0]

def write_private(path, content):
    path.write_text(content, encoding='utf-8')
    os.chmod(path, 0o600)

def verify_bundle():
    expected = json.loads((ROOT / 'manifest.json').read_text())
    for name, checksum in expected['sha256'].items():
        file = ROOT / name
        if not file.is_file() or hashlib.sha256(file.read_bytes()).hexdigest() != checksum:
            raise RuntimeError('Artifact checksum failed: ' + name)
    for name, checksum in expected.get('reusedMedia', {}).items():
        if not re.fullmatch(r'cbjj/(products/\d+/(main|detail|scene)\.png|hero/hero\.png|payments/(visa\.png|mastercard\.svg|klarna\.svg))', name):
            raise RuntimeError('Unexpected reused media path')
        file = ROOT / 'frontend/public' / name
        if not file.is_file() or hashlib.sha256(file.read_bytes()).hexdigest() != checksum:
            raise RuntimeError('Reused media checksum failed: ' + name)
    return expected

def financial_snapshot():
    sql = """SELECT json_build_object(
    'orders',(SELECT coalesce(json_agg(row_to_json(o) ORDER BY o.id),'[]'::json)
      FROM (SELECT id,order_sn,currency,subtotal_amount,shipping_amount,tax_amount,
             discount_amount,total_amount FROM oms_order) o),
    'items',(SELECT coalesce(json_agg(row_to_json(i) ORDER BY i.id),'[]'::json)
      FROM (SELECT id,order_id,product_id,sku_id,quantity,unit_price,line_amount
             FROM oms_order_item) i),
    'users',(SELECT count(*) FROM sys_user),
    'catalog',(SELECT coalesce(json_agg(row_to_json(p) ORDER BY p.id),'[]'::json)
      FROM (SELECT id,price,stock FROM pms_product) p),
    'skus',(SELECT coalesce(json_agg(row_to_json(s) ORDER BY s.id),'[]'::json)
      FROM (SELECT id,product_id,sku_code,price,stock FROM pms_sku) s));"""
    return json.loads(run(['docker','exec','shop-db','psql','-U','shop','-d','shop','-Atc',sql]))

def migrate():
    files = ['V4__cbjj_currency_and_review_provenance.sql','V5__cbjj_catalog_content.sql']
    run(['docker','exec','shop-db','psql','-U','shop','-d','shop','-v','ON_ERROR_STOP=1','-c',
         'CREATE TABLE IF NOT EXISTS cbjj_release_migration (name text PRIMARY KEY, checksum text NOT NULL, applied_at timestamptz NOT NULL DEFAULT now());'])
    for name in files:
        checksum = hashlib.sha256((ROOT/name).read_bytes()).hexdigest()
        previous = run(['docker','exec','shop-db','psql','-U','shop','-d','shop','-Atc',
                        "SELECT checksum FROM cbjj_release_migration WHERE name='"+name+"';"]).strip()
        if previous:
            if previous != checksum: raise RuntimeError('An applied migration has changed: '+name)
            continue
        sql = "BEGIN;\n" + (ROOT/name).read_text(encoding='utf-8')
        sql += "\nINSERT INTO cbjj_release_migration(name,checksum) VALUES ('"+name+"','"+checksum+"');\nCOMMIT;\n"
        run(['docker','exec','-i','shop-db','psql','-U','shop','-d','shop','-v','ON_ERROR_STOP=1'],input=sql)
        print('Applied additive migration: '+name)

def create(snapshot, production):
    config=snapshot['Config']; host=snapshot['HostConfig']
    name=snapshot['Name'].lstrip('/')
    env=dict(item.split('=',1) for item in config.get('Env',[]) if '=' in item)
    if production and name=='shop-backend':
        env.update(SHOP_USD_CNY_RATE='7.00',PAYMENT_MOCK_ENABLED='false',
                   PAYMENT_ALIPAY_FALLBACK_TO_MOCK='false',SPRING_FLYWAY_ENABLED='false',
                   PAYMENT_ALIPAY_SUBJECT_PREFIX='CBJJ',PAYMENT_DEFAULT_PROVIDER='none')
    env_file=STATE/(name+('-new' if production else '-rollback')+'.env')
    write_private(env_file,'\n'.join(key+'='+value for key,value in env.items())+'\n')
    command=['docker','create','--name',name,'--restart',host['RestartPolicy']['Name'],
             '--network',host['NetworkMode'],'--env-file',str(env_file)]
    if config.get('WorkingDir'): command+=['-w',config['WorkingDir']]
    if config.get('User'): command+=['--user',config['User']]
    if host.get('Memory'): command+=['--memory',str(host['Memory'])]
    for key,value in (config.get('Labels') or {}).items(): command+=['--label',key+'='+value]
    for binding in host.get('Binds') or []:
        if production:
            if ':/app/app.jar:' in binding:
                binding=str(ROOT/'backend.jar')+':/app/app.jar:ro,Z'
            elif ':/app/.output:' in binding:
                binding=str(ROOT/'frontend')+':/app/.output:ro,Z'
        command+=['-v',binding]
    # Existing EC2 application containers have no published ports; Nginx proxies on shop-net.
    if host.get('PortBindings'): raise RuntimeError('Unexpected application port bindings')
    entrypoint=config.get('Entrypoint') or []
    if entrypoint: command+=['--entrypoint',entrypoint[0]]
    command+=[config['Image']]
    if entrypoint: command+=entrypoint[1:]
    command+=config.get('Cmd') or []
    run(command);run(['docker','start',name])
    print('Started '+name)

def wait_ready():
    for attempt in range(60):
        try:
            data=json.loads(run(['docker','exec','shop-frontend','wget','-qO-',
                                 'http://shop-backend:8081/store/config']))
            homepage=run(['docker','exec','shop-frontend','wget','-qO-','http://127.0.0.1:3000/'])
            if data.get('code')==200 and data.get('data',{}).get('brand')=='CBJJ' and 'CBJJ' in homepage:
                return
        except (subprocess.CalledProcessError,ValueError):
            pass
        time.sleep(2)
    raise RuntimeError('Application health check failed')

def rollback():
    snapshots=json.loads((STATE/'previous-containers.json').read_text())
    for snapshot in snapshots:
        name=snapshot['Name'].lstrip('/')
        exists=subprocess.run(['docker','inspect',name],stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL).returncode==0
        if exists: run(['docker','rm','-f',name])
        previous=STATE/(name+'.previous-name')
        if previous.exists():
            previous_name=previous.read_text().strip()
            if subprocess.run(['docker','inspect',previous_name],stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL).returncode==0:
                run(['docker','rename',previous_name,name]);run(['docker','start',name]);continue
        create(snapshot,False)
    run(['docker','exec','shop-nginx','nginx','-t'])
    run(['docker','exec','shop-nginx','nginx','-s','reload'])
    print('Previous application restored. Additive database columns and catalog content retained.')

def publish():
    manifest=verify_bundle()
    if not re.fullmatch(r'[a-zA-Z0-9_-]+',ROOT.name): raise RuntimeError('Invalid versioned directory')
    if not Path('/opt/shop/backups/20260918-before-cbjj/shop.dump').is_file():
        raise RuntimeError('The private pre-release backup must exist')
    if (STATE/'published.json').exists(): raise RuntimeError('Release already published')
    STATE.mkdir(exist_ok=True,mode=0o700);os.chmod(STATE,0o700)
    snapshots=[inspect(name) for name in NAMES]
    write_private(STATE/'previous-containers.json',json.dumps(snapshots))
    before=financial_snapshot();write_private(STATE/'financial-before.json',json.dumps(before))
    (ROOT/'frontend').mkdir(exist_ok=True)
    run(['tar','-xzf',str(ROOT/'frontend.tar.gz'),'-C',str(ROOT/'frontend')])
    migrate()
    after=financial_snapshot()
    if before != after: raise RuntimeError('Migration changed historical amounts, IDs, stock, prices or user count')
    write_private(STATE/'financial-after-migration.json',json.dumps(after))
    changed=[]
    try:
        for snapshot in snapshots:
            name=snapshot['Name'].lstrip('/');previous_name=name+'-previous-'+ROOT.name
            run(['docker','stop','--time','30',name]);run(['docker','rename',name,previous_name])
            write_private(STATE/(name+'.previous-name'),previous_name)
            changed.append(name);create(snapshot,True)
        wait_ready()
        # Refresh Nginx DNS after recreating the named application containers.
        run(['docker','exec','shop-nginx','nginx','-t'])
        run(['docker','exec','shop-nginx','nginx','-s','reload'])
        for url in ['https://cbjjpower.com/','https://cbjjpower.com/api/store/config',
                    'https://www.cbjjpower.com/','https://cbjjpower.com/pages/shipping-policy']:
            run(['curl','-fLsS','--max-time','30','-o','/dev/null',url])
        write_private(STATE/'published.json',json.dumps({'release':ROOT.name,'published_at':time.time(),'manifest':manifest}))
        print('Published '+ROOT.name+'; Postgres, data volume, TLS, ACME and certificate renewal preserved.')
    except Exception:
        if changed: rollback()
        raise

if __name__=='__main__':
    parser=argparse.ArgumentParser();parser.add_argument('action',choices=['publish','rollback','verify'])
    args=parser.parse_args()
    if args.action=='publish':publish()
    elif args.action=='rollback':rollback()
    else:verify_bundle();print('Release checksums verified')
