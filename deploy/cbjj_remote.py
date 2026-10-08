"""Run explicit release operations over the already authorized EC2 SSH connection.

Credentials come from CBJJ_SSH_PASSWORD or the local deployment history; they are
never uploaded. Runtime snapshots and database backups stay in private directories.
"""
import argparse
import json
import logging
import os
import re
import shlex
from pathlib import Path
import paramiko

ROOT = Path(__file__).resolve().parents[1]
logging.getLogger('paramiko').setLevel(logging.CRITICAL)

def connect():
    password = os.environ.get('CBJJ_SSH_PASSWORD')
    if not password:
        source = ROOT / '.waylog/history/2026-08-12_10-03Z-The_following_is_the_Codex_agent_history_whose_req.md'
        match = re.search(r'(?:密码|password)\s*(?:是|为|[:：=])\s*([^\s]+)', source.read_text(encoding='utf-8'), re.I)
        if not match: raise RuntimeError('No authorized SSH credential found')
        password = match.group(1)
    client = paramiko.SSHClient()
    client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    client.connect(os.environ.get('CBJJ_SSH_HOST','18.223.87.96'),username='ec2-user',password=password,
                   look_for_keys=False,allow_agent=False,timeout=20,banner_timeout=20,auth_timeout=20)
    return client

def execute(client, command, print_output=True):
    _, stdout, stderr = client.exec_command(command,timeout=180)
    output = stdout.read().decode('utf-8',errors='replace')
    error = stderr.read().decode('utf-8',errors='replace')
    code = stdout.channel.recv_exit_status()
    if print_output:
        print(output, end='')
        if error: print(error,end='')
    if code: raise RuntimeError(f'Remote command failed with exit code {code}')
    return output

def backup(client, name):
    if not re.fullmatch(r'[a-zA-Z0-9_-]+',name): raise ValueError('Invalid backup name')
    remote='/opt/shop/backups/'+name
    execute(client, f'mkdir -p {shlex.quote(remote)} && chmod 700 {shlex.quote(remote)}')
    execute(client, f'docker exec shop-db pg_dump -U shop -d shop -Fc > {remote}/shop.dump && chmod 600 {remote}/shop.dump')
    execute(client, f'docker inspect shop-backend shop-frontend shop-nginx shop-db > {remote}/containers.json && chmod 600 {remote}/containers.json',False)
    execute(client, f'cp /opt/shop/releases/ae8a4ad/default.conf {remote}/nginx.conf && sudo cp /opt/shop/.env {remote}/runtime.env && sudo chmod 600 {remote}/runtime.env')
    local=ROOT/'output/cbjj-deploy'/name;local.mkdir(parents=True,exist_ok=True)
    sftp=client.open_sftp()
    for filename in ['shop.dump','containers.json','nginx.conf']:
        sftp.get(remote+'/'+filename,str(local/filename))
    sftp.close()
    print('Private database and container/configuration backups saved: '+remote)

def main():
    parser=argparse.ArgumentParser();parser.add_argument('action',choices=['exec','backup','upload'])
    parser.add_argument('value');parser.add_argument('destination',nargs='?');args=parser.parse_args()
    client=connect()
    try:
        if args.action=='exec':execute(client,args.value)
        elif args.action=='backup':backup(client,args.value)
        else:
            if not args.destination or not args.destination.startswith('/opt/shop/releases/'):
                raise ValueError('Uploads must target a versioned /opt/shop/releases directory')
            parent=args.destination.rsplit('/',1)[0];execute(client,'mkdir -p '+shlex.quote(parent))
            sftp=client.open_sftp();sftp.put(str(Path(args.value).resolve()),args.destination);sftp.close()
            print('Uploaded production artifact: '+args.destination)
    finally:client.close()

if __name__=='__main__': main()
