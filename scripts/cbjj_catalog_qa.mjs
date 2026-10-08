import fs from 'node:fs/promises'
import assert from 'node:assert/strict'
import { chromium } from 'playwright'

// Public/read-only checks; safe to run against the published site as well.
const base = process.env.CBJJ_QA_BASE || 'http://127.0.0.1:3000'
const api = base.includes('127.0.0.1') ? 'http://127.0.0.1:8081' : base + '/api'
const strictImages = process.env.CBJJ_QA_IMAGES === 'true'
const original = JSON.parse(await fs.readFile('output/cbjj-original-catalog.json', 'utf8'))
const output = 'output/cbjj-browser-qa'
await fs.mkdir(output, { recursive: true })
const browser = await chromium.launch({ headless: true })
const context = await browser.newContext({ viewport: { width: 320, height: 844 } })
await context.addCookies([{ name: 'lang', value: 'en', url: base }, { name: 'shop-market', value: 'US', url: base }])
await context.route('https://fonts.googleapis.com/**', r => r.abort())
await context.route('https://fonts.gstatic.com/**', r => r.abort())
if (base.includes('127.0.0.1')) await context.route('**/api/**', async route => {
  const url = new URL(route.request().url())
  url.host = '127.0.0.1:8081'; url.pathname = url.pathname.replace(/^\/api/, '')
  const response = await route.fetch({ url: url.toString() }); await route.fulfill({ response })
})
const page = await context.newPage()
const errors = []; page.on('pageerror', error => errors.push(String(error)))
const report = { base, strictImages, variants: [], unavailable: [], pricing: [], legacy: [], checks: {}, errors }
const labels = { color: 'Color', bundle: 'Bundle', style: 'Style', size: 'Size', model: 'Model',
  'with gift box': 'With Gift Box', 'free gift included': 'Free Gift Included', 'buy more save more': 'Buy More Save More', title: 'Title' }
const escape = text => text.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
const money = value => Number(value).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
const get = async path => {
  const response = await context.request.get(api + path, { headers: { 'Accept-Language': 'en' } })
  assert.equal(response.status(), 200)
  const body = await response.json(); assert.equal(body.code, 200); return body.data
}
try {
  for (const model of original) {
    const product = await get('/product/' + model.id + '?currency=USD')
    const chinese = await get('/product/' + model.id + '?currency=CNY')
    assert.equal(product.skuList.length, model.skuList.length)
    assert.equal(product.currency, 'USD')
    assert.equal(chinese.currency, 'CNY')
    for (const sku of product.skuList) {
      assert.equal(sku.price, model.skuList.find(item => item.id === sku.id).price, 'Preserved base price ' + sku.id)
      const converted = chinese.skuList.find(item => item.id === sku.id)
      assert.equal(converted.currency, 'CNY')
      assert.equal(converted.price, Number((sku.price * 7).toFixed(2)), 'CNY unit price ' + sku.id)
      report.pricing.push({ sku: sku.id, USD: sku.price, CNY: converted.price })
    }
    const mountedProduct = page.waitForResponse(response =>
      new URL(response.url()).pathname === '/api/product/' + model.id && response.status() === 200)
    await page.goto(base + '/products/' + model.id, { waitUntil: 'domcontentloaded' })
    await (await mountedProduct).finished()
    await page.locator('h1.product-title').waitFor()
    await page.waitForFunction(() => document.querySelector('#__nuxt')?.__vue_app__?.config.globalProperties.$nuxt?.isHydrating === false)
    await page.waitForTimeout(100)
    for (const sku of product.skuList) {
      if (sku.stock <= 0 || sku.status !== 'ACTIVE') { report.unavailable.push({ product: model.id, sku: sku.id, stock: sku.stock, status: sku.status }); continue }
      for (let pass = 0; pass < 3; pass++) {
        for (const [key, value] of Object.entries(sku.attributes)) {
          const label = labels[key] || key.replace(/\b\w/g, c => c.toUpperCase())
          const group = page.locator('.product-info .selector-group').filter({
            has: page.locator('.selector-label').filter({ hasText: new RegExp('^' + escape(label) + ':') })
          })
          assert.equal(await group.count(), 1, 'Variant control ' + key)
          const button = group.getByRole('button', { name: value, exact: true })
          assert.ok(await button.isEnabled(), 'Reachable SKU ' + sku.id + ', option ' + value)
          await button.click()
        }
        let selected = true
        for (const [key, value] of Object.entries(sku.attributes)) {
          const label = labels[key] || key.replace(/\b\w/g, c => c.toUpperCase())
          const group = page.locator('.product-info .selector-group').filter({
            has: page.locator('.selector-label').filter({ hasText: new RegExp('^' + escape(label) + ':') })
          })
          if (await group.locator('strong').innerText() !== value) selected = false
        }
        if (selected && await page.locator('.main-image').getAttribute('src') === sku.pic) break
      }
      assert.equal(await page.locator('.main-image').getAttribute('src'), sku.pic, 'Variant picture ' + sku.id)
      for(const [key,value] of Object.entries(sku.attributes)) {
        const label=labels[key] || key.replace(/\b\w/g,c=>c.toUpperCase())
        const group=page.locator('.product-info .selector-group').filter({has:page.locator('.selector-label').filter({hasText:new RegExp('^'+escape(label)+':')})})
        assert.equal(await group.locator('strong').innerText(),value,'Selected '+key+' for SKU '+sku.id)
      }
      assert.ok((await page.locator('.product-info .current-price').innerText()).includes(money(sku.price)), 'Variant price ' + sku.id)
      if (strictImages) await page.waitForFunction(() => { const img = document.querySelector('.main-image'); return img?.complete && img.naturalWidth > 0 })
      report.variants.push({ product: model.id, sku: sku.id, currency: sku.currency, price: sku.price, image: sku.pic })
    }
    const old = '/products/' + model.slug
    const response = await page.goto(base + old + '?ref=legacy', { waitUntil: 'domcontentloaded' })
    await page.locator('h1.product-title').waitFor()
    assert.equal(response.status(), 200)
    assert.ok(!page.url().includes('isinwheel'))
    assert.ok(page.url().includes('ref=legacy'))
    assert.ok((await page.locator('h1').innerText()).includes('CBJJ'))
    report.legacy.push({ old, current: new URL(page.url()).pathname })
  }
  await page.goto(base + '/search', { waitUntil: 'domcontentloaded' })
  await page.waitForFunction(() => document.querySelector('#__nuxt')?.__vue_app__?.config.globalProperties.$nuxt?.isHydrating === false)
  for (const term of ['s9', 'helmet', '头盔']) {
    const result = page.waitForResponse(response => new URL(response.url()).pathname === '/api/product/list' && response.status() === 200)
    await page.locator('#product-search').fill(term)
    await page.locator('.search-input button').click()
    await (await result).finished()
    await page.waitForFunction(() => /products found/.test(document.querySelector('.search-page [role=status]')?.textContent || ''))
    assert.ok(await page.locator('.search-results .product-card').count() > 0, 'Search ' + term)
  }
  report.checks.search = ['s9', 'helmet', '头盔']
  await page.goto(base + '/pages/contact-us', { waitUntil: 'domcontentloaded' })
  await page.waitForFunction(() => document.querySelector('#__nuxt')?.__vue_app__?.config.globalProperties.$nuxt?.isHydrating === false)
  assert.equal(await page.locator('.contact-details a[href="mailto:1479030891@qq.com"]').count(), 1)
  assert.equal(await page.locator('.contact-details a[href="tel:+8613310853090"]').count(), 1)
  assert.equal(await page.locator('.contact-details a[href="tel:+85266576931"]').count(), 1)
  const cdp = await context.newCDPSession(page); await cdp.send('Page.enable')
  let mailto; cdp.on('Page.frameRequestedNavigation', event => { if (event.url.startsWith('mailto:')) mailto = event.url })
  const inputs = page.locator('.contact-grid form input')
  await inputs.nth(0).fill('验收 A & B'); await inputs.nth(1).fill('qa@example.com'); await inputs.nth(2).fill('TEST-123')
  await page.locator('.contact-grid textarea').fill('中文问题 & refund?\nSecond line')
  await page.locator('.contact-grid form button').click(); await page.waitForTimeout(250)
  assert.ok(mailto?.startsWith('mailto:1479030891@qq.com?'))
  const uri = new URL(mailto)
  assert.ok(uri.searchParams.get('subject').includes('TEST-123'))
  assert.ok(uri.searchParams.get('body').includes('中文问题 & refund?\nSecond line'))
  assert.ok((await page.locator('.contact-grid [role=status]').innerText()).includes('Send the message'))
  report.checks.contactMailto = true
  for (const route of ['/', '/pages/shipping-policy', '/products/4', '/products/11', '/products/13', '/cart']) {
    await page.goto(base + route, { waitUntil: 'domcontentloaded' }); await page.waitForTimeout(250)
    assert.ok(!(await page.evaluate(() => document.documentElement.scrollWidth > innerWidth + 2)), '320px layout ' + route)
  }
  report.checks.mobileWidth = 320
  await context.addCookies([{ name: 'shop-market', value: 'CN', url: base }])
  await page.goto(base + '/products/1', { waitUntil: 'domcontentloaded' }); await page.waitForTimeout(350)
  assert.equal(await page.locator('.market-selector select').first().inputValue(), 'CN')
  assert.ok((await page.locator('.product-info .current-price').innerText()).includes('1,889.93'))
  report.checks.marketSelector = 'CN/CNY 1889.93'
  for (const currency of ['USD', 'CNY']) {
    const methods = await get('/payment/methods?currency=' + currency)
    assert.ok(methods.every(method => !method.enabled), 'Unconfigured production channels remain disabled')
  }
  report.checks.payments = 'USD/CNY disabled; Klarna unavailable'
  assert.deepEqual(errors, [])
  report.complete = true
} catch (error) {
  report.complete = false; report.failure = String(error); process.exitCode = 1
} finally {
  await fs.writeFile(output + '/catalog.json', JSON.stringify(report, null, 2))
  await browser.close()
  console.log(JSON.stringify({ complete: report.complete, pricing: report.pricing.length, variants: report.variants.length, unavailable: report.unavailable.length, legacy: report.legacy.length, checks: report.checks, failure: report.failure, errors }, null, 2))
}
