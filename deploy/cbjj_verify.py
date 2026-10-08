"""Verify public HTTPS endpoints and media after publication, without creating orders."""
import concurrent.futures
import json
import urllib.request
from decimal import Decimal, ROUND_HALF_UP
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BASE = 'https://cbjjpower.com'

def request(path, method='GET', json_body=False):
    url = path if path.startswith('https://') else BASE + path
    req = urllib.request.Request(url, method=method, headers={'User-Agent': 'CBJJ-release-verification', 'Accept-Language': 'en'})
    with urllib.request.urlopen(req, timeout=40) as response:
        if response.status != 200: raise RuntimeError('Unexpected status: ' + url)
        body = response.read() if method == 'GET' else b''
        if json_body:
            data = json.loads(body)
            if data.get('code') != 200: raise RuntimeError('API failure: ' + url)
            return data['data']
        return response.status

def main():
    media = json.loads((ROOT / 'output/cbjj-assets-manifest.json').read_text(encoding='utf-8'))
    if not media['complete']: raise RuntimeError('Media validation must pass first')
    config = request('/api/store/config', json_body=True)
    assert config['brand'] == 'CBJJ' and Decimal(str(config['usdCnyRate'])) == Decimal('7.00')
    assert config['currencies'] == ['USD', 'CNY']
    policies = ['shipping-policy', 'refund-policy', 'warranty', 'payment-methods', 'klarna',
                'terms-of-service', 'privacy-policy', 'authorized-sales', 'become-a-dealer', 'affiliate-program']
    pages = ['/', 'https://www.cbjjpower.com/', '/pages/contact-us', '/pages/about-us-1',
             '/home-content'] + ['/pages/' + name for name in policies]
    for path in pages: request(path)
    for currency in ['USD', 'CNY']:
        methods = request('/api/payment/methods?currency=' + currency, json_body=True)
        assert all(not method['enabled'] for method in methods), 'Unconfigured payment unexpectedly enabled'
    skus = 0
    for model in range(1, 16):
        usd = request(f'/api/product/{model}?currency=USD', json_body=True)
        cny = request(f'/api/product/{model}?currency=CNY', json_body=True)
        assert usd['currency'] == 'USD' and cny['currency'] == 'CNY'
        assert usd['title'].startswith('CBJJ')
        chinese = {item['id']: item for item in cny['skuList']}
        for item in usd['skuList']:
            expected = (Decimal(str(item['price'])) * Decimal('7.00')).quantize(Decimal('0.01'), rounding=ROUND_HALF_UP)
            assert Decimal(str(chinese[item['id']]['price'])) == expected
            assert chinese[item['id']]['currency'] == 'CNY'
            skus += 1
    assert skus == 70
    with concurrent.futures.ThreadPoolExecutor(max_workers=4) as pool:
        list(pool.map(lambda asset: request(asset['path'], 'HEAD'), media['assets']))
    report = {'complete': True, 'url': BASE, 'www': True, 'https': True,
              'policyPages': len(policies), 'publicMedia': len(media['assets']),
              'products': 15, 'skuPricing': skus, 'rate': '7.00',
              'onlinePayments': {'USD': False, 'CNY': False, 'Klarna': False}}
    target = ROOT / 'output/cbjj-deploy/public-verification.json'
    target.write_text(json.dumps(report, indent=2), encoding='utf-8')
    print(json.dumps(report, indent=2))

if __name__ == '__main__': main()
