import fs from 'node:fs/promises'
import path from 'node:path'
import { chromium } from 'playwright'
const base=process.env.CBJJ_QA_BASE || 'http://127.0.0.1:3000'
const output=path.resolve('output/cbjj-browser-qa')
await fs.mkdir(output,{recursive:true})
const browser=await chromium.launch({headless:true})
const policies=['shipping-policy','refund-policy','warranty','payment-methods','klarna','terms-of-service','privacy-policy','authorized-sales','become-a-dealer','affiliate-program']
const routes=['/','/pages/about-us-1','/pages/contact-us','/pages/support-faq','/pages/photos','/pages/cbjj-videos','/blogs/news','/search',...policies.map(p=>'/pages/'+p),...['all','electric-scooters','electric-bike','electric-skateboard','accessories'].map(p=>'/collections/'+p),...Array.from({length:15},(_,i)=>'/products/'+(i+1))]
const report={base,started:new Date().toISOString(),pages:[],errors:[]}
for(const config of [{lang:'en',market:'US',width:1440,height:1000},{lang:'zh',market:'CN',width:390,height:844}]){
 const context=await browser.newContext({viewport:{width:config.width,height:config.height},locale:config.lang==='zh'?'zh-CN':'en-US'})
 await context.addCookies([{name:'lang',value:config.lang,url:base},{name:'shop-market',value:config.market,url:base}])
 if(base.includes('127.0.0.1')) await context.route('**/api/**',async route=>{
  const url=new URL(route.request().url());url.host='127.0.0.1:8081';url.pathname=url.pathname.replace(/^\/api/,'')
  try{const response=await route.fetch({url:url.toString()});await route.fulfill({response})}catch(error){await route.abort()}
 })
 await context.route('https://fonts.googleapis.com/**',r=>r.abort())
 await context.route('https://fonts.gstatic.com/**',r=>r.abort())
 const page=await context.newPage()
 const errors=[];page.on('pageerror',err=>errors.push(String(err)))
 for(const route of routes){
  const startErrors=errors.length
  try{
   const mountedProduct=route.startsWith('/products/')?page.waitForResponse(response=>new URL(response.url()).pathname==='/api/product/'+route.split('/').at(-1)&&response.status()===200):null
   const response=await page.goto(base+route,{waitUntil:'domcontentloaded',timeout:30000})
   await page.waitForFunction(()=>document.querySelector('#__nuxt')?.__vue_app__?.config.globalProperties.$nuxt?.isHydrating===false)
   if(mountedProduct) await (await mountedProduct).finished()
   await page.waitForTimeout(100)
   if(route.startsWith('/products/')) await page.locator('h1.product-title').waitFor({timeout:10000})
   if(process.env.CBJJ_QA_IMAGES==='true') await page.evaluate(async()=>{
    for(const img of document.images) img.loading='eager'
    await Promise.all([...document.images].map(img=>img.decode().catch(()=>{})))
   })
   const result=await page.evaluate(()=>{
    const text=document.body.innerText
    return {title:document.title,lang:document.documentElement.lang,h1:document.querySelector('h1')?.textContent?.trim(),
     overflow:document.documentElement.scrollWidth>innerWidth+2,
     oldBrand:/isinwheel|trustpilot|LIMITED TIME OFFER|MOCK_TXN|sandbox|We accept|SALE END IN|The reference page|This section restores|S Nova Story/i.test(text),
     brokenImages:[...document.images].filter(img=>img.loading!=='lazy'&&img.complete&&!img.naturalWidth).map(img=>img.getAttribute('src')),
     footerLinks:[...document.querySelectorAll('footer a[href]')].map(a=>a.getAttribute('href')),
     price:document.querySelector('.product-info .current-price')?.textContent?.trim()}
   })
   report.pages.push({route,...config,status:response.status(),...result,errors:errors.slice(startErrors)})
   if(['/', '/pages/contact-us','/pages/shipping-policy','/products/7','/products/8','/products/13'].includes(route))
    await page.screenshot({path:path.join(output,config.lang+'-'+(route.replaceAll('/','-')||'home')+'.png'),fullPage:true})
  }catch(error){report.errors.push({route,...config,error:String(error)})}
 }
 await context.close()
}
await browser.close()
await fs.writeFile(path.join(output,'pages.json'),JSON.stringify(report,null,2))
const findings=report.pages.filter(p=>p.status!==200||p.overflow||p.oldBrand||p.errors.length)
console.log(JSON.stringify({pages:report.pages.length,findings,errors:report.errors,missingImagePages:report.pages.filter(p=>p.brokenImages.length).map(p=>({route:p.route,lang:p.lang,images:p.brokenImages}))},null,2))
if (findings.length || report.errors.length || (process.env.CBJJ_QA_IMAGES === 'true' && report.pages.some(p=>p.brokenImages.length))) process.exitCode=1
