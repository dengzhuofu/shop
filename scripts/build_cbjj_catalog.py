"""Produce reviewable catalog content and an incremental migration. Never changes stock or prices."""
import html
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
original = json.loads((ROOT / 'output/cbjj-original-catalog.json').read_text(encoding='utf-8'))

# Specific descriptive content is based on the existing model and catalog specifications.
titles = {
 1: ('CBJJ S9 Pro Pneumatic Tire Electric Scooter', 'CBJJ S9 Pro 充气轮胎电动滑板车'),
 2: ('CBJJ S Nova Pro Commuting Electric Scooter', 'CBJJ S Nova Pro 通勤电动滑板车'),
 3: ('CBJJ U8 Electric Bike for Adults', 'CBJJ U8 成人电动自行车'),
 4: ('CBJJ M50 Mountain Ebike', 'CBJJ M50 山地电动自行车'),
 5: ('CBJJ V8 Electric Skateboard with Remote', 'CBJJ V8 遥控电动滑板'),
 6: ('CBJJ V10 Off Road Electric Skateboard', 'CBJJ V10 越野电动滑板'),
 7: ('CBJJ Electric Bike Cable Lock', 'CBJJ 电动自行车钢缆锁'),
 8: ('CBJJ Adult Riding Helmet', 'CBJJ 成人骑行头盔'),
 9: ('CBJJ U1 Folding Electric Scooter', 'CBJJ U1 折叠电动滑板车'),
 10: ('CBJJ U2 Electric Cruiser Bike', 'CBJJ U2 休闲电动自行车'),
 11: ('CBJJ Mini Three-Wheel Kids Electric Scooter', 'CBJJ Mini 儿童三轮电动滑板车'),
 12: ('CBJJ GT2 1000W Off Road Electric Scooter — 2026 Edition', 'CBJJ GT2 1000W 越野电动滑板车 · 2026 版'),
 13: ('CBJJ Dashboard for S9 Pro / S9 Max Electric Scooter', 'CBJJ S9 Pro / S9 Max 电动滑板车仪表盘'),
 14: ('CBJJ S9 Pro Pneumatic Tire Electric Scooter — 2026 Edition', 'CBJJ S9 Pro 充气轮胎电动滑板车 · 2026 版'),
 15: ('CBJJ S Nova Commuting Electric Scooter', 'CBJJ S Nova 通勤电动滑板车'),
}
summaries = {
 1: ('A foldable city scooter with 10-inch pneumatic tires.', '配备 10 英寸充气轮胎的折叠式城市滑板车。'),
 2: ('A commuter scooter with a 1000W catalog motor and 48V 13Ah battery.', '现有商品参数标注 1000W 电机、48V 13Ah 电池的通勤滑板车。'),
 3: ('A step-through electric bike for adult everyday riding.', '采用低跨点车架，适合成人日常骑行。'),
 4: ('A mountain ebike with 26 × 4-inch tires for varied surfaces.', '配备 26 × 4 英寸轮胎，适合多种骑行路面。'),
 5: ('An electric longboard supplied with a wireless handheld remote.', '搭配无线手持遥控器的电动长板。'),
 6: ('An all-terrain electric board with a wide composite deck.', '采用宽复合板面的全地形电动滑板。'),
 7: ('A protective-coated steel cable lock for everyday parking.', '带保护包覆层的钢缆锁，适合日常停车固定。'),
 8: ('A ventilated adult helmet with a PC shell and EPS liner.', '采用 PC 外壳与 EPS 内衬的透气成人头盔。'),
 9: ('A folding commuter scooter with a 48V 10Ah catalog battery.', '现有商品参数标注 48V 10Ah 电池的折叠通勤滑板车。'),
 10: ('A step-through cruiser bike available in black or white.', '提供黑、白两款的低跨点休闲电动自行车。'),
 11: ('A three-wheel kids scooter offered in blue, pink and two-unit packs.', '提供蓝色、粉色及双台组合的儿童三轮滑板车。'),
 12: ('The GT2 2026 off-road model, with standard and seated options.', 'GT2 2026 越野款，提供标准版与带座版本。'),
 13: ('A replacement dashboard selected by scooter model and handlebar configuration.', '按滑板车型号及是否包含车把选择的替换仪表盘。'),
 14: ('The S9 Pro 2026 edition, sold as a single unit or a two-scooter pack.', 'S9 Pro 2026 版，提供单台与双台组合。'),
 15: ('A commuter scooter with 8.5-inch tires and a 600W peak catalog motor.', '现有商品参数标注 8.5 英寸轮胎、600W 峰值电机的通勤滑板车。'),
}
guides = {
 1: ('Check tire pressure, the folding latch and braking response before each ride. The 350W standard model suits short urban trips; select a variant for its own price and stock.', '每次骑行前检查胎压、折叠锁扣和制动响应。基础版为 350W 城市车型，请按所选 SKU 核对价格与库存。'),
 2: ('Check the battery charge, folding mechanism and brakes before departure. Range and speed are catalog estimates, affected by rider weight, temperature and terrain.', '出发前检查电量、折叠机构和刹车。续航与速度为现有商品资料的估算值，受骑行者重量、温度及地形影响。'),
 3: ('Confirm frame fit and saddle position, and check both disc brakes before riding. Charge using the supplied compatible charger and keep electrical connections dry.', '先确认车架与身高适配、调整坐垫，并检查前后碟刹。使用随附的适配充电器，保持电气接口干燥。'),
 4: ('Inspect tire pressure and brake response for the intended surface. A mountain or fat-tire configuration does not guarantee public-road eligibility in every region.', '按目标路面检查胎压和制动响应。山地及宽胎配置不代表符合所有地区的公共道路准入要求。'),
 5: ('Practice remote operation and braking at low speed on a dry, level, closed area. Wear a properly fitted helmet; do not ride in standing water.', '先在干燥、平整、封闭场地低速练习遥控操作和制动。佩戴合适头盔，不在积水中滑行。'),
 6: ('Inspect the wheels, trucks and remote charge before use. Start with a smooth, dry training surface; rough surfaces can reduce traction and range.', '使用前检查车轮、桥架及遥控器电量。先在平整干燥路面练习，复杂路面可能降低抓地力和续航。'),
 7: ('Secure the vehicle frame to a fixed anchor, and keep the keys separate from the lock. A cable lock is a parking deterrent and does not guarantee theft prevention.', '将车辆主车架锁在固定锚点上，钥匙与锁分开保管。钢缆锁用于提升停车防护，不保证杜绝盗窃。'),
 8: ('Choose M or L using the selected variant, and confirm a snug fit before use. Replace a helmet after a significant impact, even when damage is not visible.', '按所选版本选择 M 或 L，并确认佩戴稳固。头盔受到明显撞击后应更换，即使外观没有可见损伤。'),
 9: ('Check the folding latch, battery charge and brakes before riding. Fold only after the scooter has stopped; keep fingers away from moving joints.', '骑行前检查折叠锁扣、电量和刹车。完全停稳后再折叠，手指远离活动关节。'),
 10: ('Adjust the saddle and check the brakes, tires and battery attachment before riding. The listed gift is a bike lock; the included one-year warranty is the standard limited warranty.', '骑行前调整坐垫，检查刹车、轮胎及电池固定状态。所列赠品为车锁，随附一年保修属于标准有限保修。'),
 11: ('Use only under adult supervision in a closed, dry area away from traffic. Choose the child’s fit according to the supplied manual; wear a helmet and protective pads.', '仅在成人监护下，于远离交通的干燥封闭场地使用。按随附说明书确认儿童身高与使用适配，佩戴头盔及护具。'),
 12: ('Select GT2 Standard or GT2 with Seat; both listed options include a scooter bag. Check the seat fasteners when fitted and verify local road restrictions before use.', '选择 GT2 标准版或带座版，现有两种选项均包含滑板车包。带座版须检查座椅紧固件，并在使用前核对当地道路规定。'),
 13: ('Select S9 Pro or S9 Max and the matching with/without-handlebar option. Check the existing connector, wiring and controller revision with support before ordering; disconnect power for installation.', '先选择 S9 Pro 或 S9 Max，再选择含车把或不含车把版本。下单前请联系客服核对原接口、线序及控制器版本；安装前断开电源。'),
 14: ('Select S9 Pro ×1 or ×2. A two-unit pack includes two scooters and the standard accessories for each; the displayed SKU price is for the complete selected pack.', '选择 S9 Pro ×1 或 ×2。双台组合包含两台车及各自标准配件；所显示 SKU 价格为整个所选组合的价格。'),
 15: ('Check the lights, brakes and tires before departure. The listed 600W figure is peak motor power, not a promise of continuous output; use the supplied compatible charger.', '出发前检查灯光、刹车和轮胎。600W 为所列电机峰值功率，不代表持续输出承诺；使用随附的适配充电器。'),
}
translation = {
 'Battery':'电池','Motor':'电机','Range':'续航（估算）','Max Range':'续航（估算）','Top Speed':'最高速度（估算）',
 'Motor Capacity':'电机功率','Tires':'轮胎','Tire':'轮胎','Frame':'车架','Brakes':'刹车','Weight':'重量',
 'Deck':'板面','Remote':'遥控器','Wheel':'车轮','Length':'长度','Core':'锁芯结构','Shell':'外壳/内衬',
 'Load Capacity':'载重','Climbing Ability':'爬坡能力','Type':'类型','Model':'型号','Version':'版本','Pack':'组合数量',
 'Colors':'颜色','Handlebar':'车把配置','Options':'可选配置','Wheels':'车轮数量','Compatibility':'适配条件',
 'Scooter body':'滑板车主体','Charger':'适配充电器','Toolkit':'工具包','Manual':'使用说明书','User manual':'使用说明书',
 'Bike frame':'车架主体','Bike body':'自行车主体','Pedals':'脚踏','Board':'滑板主体','Helmet':'头盔',
 'Cable lock':'钢缆锁','Keys':'钥匙','Padding set':'衬垫','Dashboard module':'仪表盘模块','Harness':'线束',
 'Midnight Black':'午夜黑','Pearl White':'珍珠白','Graphite Black':'石墨黑','Storm Grey':'风暴灰','Matte Black':'哑光黑',
 'Ocean Blue':'海洋蓝','Forest Green':'森林绿','Sand':'沙色','Black':'黑色','Gray':'灰色','White':'白色','Blue':'蓝色','Pink':'粉色',
 'Sunset Red':'日落红','Desert Sand':'沙漠色','Glacier White':'冰川白','Safety Orange':'安全橙','Slate Grey':'岩灰色',
 'Standard':'标准套装','City Kit':'城市套装','Travel Kit':'旅行套装','Accessory Kit':'配件套装','Commuter Plus':'通勤增强套装',
 'Adventure Kit':'探索套装','Explorer Kit':'探险套装','Explorer Pack':'探险组合','City Surf Kit':'城市滑行套装','Spare Battery Kit':'备用电池套装',
 'Single':'单件','Twin Pack':'双件套','Visor Kit':'带遮阳配件套装','Commuter':'通勤版','Lite':'Lite 版','Pro':'Pro 版',
 'Step-through':'低跨点版','Mountain':'山地版','Street':'街道版','Carbon Flex':'Carbon Flex 版','Off Road':'越野版','All Terrain':'全地形版',
 'Accessory':'配件版','Long Reach':'延长版','Compact':'紧凑版','City':'城市版','City Comfort':'城市舒适套装',
 'Bike Chain Lock & 1 Year Warranty':'车锁及标准一年有限保修','Mini Pro Standard':'Mini Pro 标准包装',
 'Mini Pro with Gift Box':'Mini Pro 礼盒包装','Blue+Pink':'蓝色+粉色（两台）','Blue+Blue':'蓝色+蓝色（两台）','Pink+Pink':'粉色+粉色（两台）',
 'GT2 Standard':'GT2 标准版','GT2 with Seat':'GT2 带座版','Scooter Bag':'滑板车包','2026 Upgraded Edition':'2026 升级版',
 'S9 Pro*1':'S9 Pro ×1','S9 Pro*2':'S9 Pro ×2',
 'S9 Pro With Handlebar with Turn Signals':'S9 Pro 含转向灯车把', 'S9 Max With Handlebar with Turn Signals':'S9 Max 含转向灯车把',
 'S9 Pro Without Handlebar with Turn Signals':'S9 Pro 转向灯配置，不含车把','S9 Max Without Handlebar with Turn Signals':'S9 Max 转向灯配置，不含车把',
 '10 inch pneumatic':'10 英寸充气轮胎','8.5 inch pneumatic':'8.5 英寸充气轮胎','Step-through aluminum':'低跨点铝合金车架',
 'Mechanical disc':'机械碟刹','8-layer maple':'8 层枫木','2.4G wireless':'2.4G 无线遥控','All-terrain':'全地形车轮',
 'Wide maple composite':'宽枫木复合板','Steel cable':'钢缆','600W peak':'600W 峰值',
}
def zh(value):
 value = str(value)
 if value in translation: return translation[value]
 return value.replace('Miles','英里').replace('miles','英里').replace('MPH','英里/小时').replace('Up to','最高约').replace('grade','坡度').replace('lbs','磅')
def localized(en, cn): return {'en':en,'zh':cn}
def sql(value):
 if value is None: return 'NULL'
 if isinstance(value,(list,dict)): value=json.dumps(value,ensure_ascii=False,separators=(',',':'))
 return "'"+str(value).replace("'","''")+"'"
def variant_key(p,sku):
 a=sku['attributes'];i=p['id']
 if i==11: return a['style'].lower().replace('+','-')+('-gift' if 'Gift Box' in a['with gift box'] else '')
 if i==12: return 'seat' if 'with Seat' in a['style'] else 'standard'
 if i==13: return 'with-handlebar' if ' With Handlebar' in a['model'] else 'module'
 if i==14: return 'two-pack' if a['buy more save more'].endswith('*2') else 'single'
 color=a.get('color',a.get('style','Black'))
 key=re.sub('[^a-z0-9]+','-',color.lower()).strip('-')
 if i==7 and a.get('bundle')=='Twin Pack':key+='-twin'
 if i==8 and a.get('bundle')=='Visor Kit':key+='-visor'
 return key

result=[];statements=['-- CBJJ content only: preserves product/SKU IDs, models, prices, inventory and historical order snapshots.']
for p in original:
 i=p['id'];summary=summaries[i];guide=guides[i]
 specs=p['specTable'] if i not in [10,11,12,13,14] else {
 10:[{'label':'Model','value':'U2'},{'label':'Frame','value':'Step-through'},{'label':'Colors','value':'Black / White'}],
 11:[{'label':'Model','value':'Mini'},{'label':'Wheels','value':'3'},{'label':'Colors','value':'Blue / Pink'},{'label':'Pack','value':'1 / 2'}],
 12:[{'label':'Model','value':'GT2'},{'label':'Motor','value':'1000W'},{'label':'Version','value':'2026'},{'label':'Options','value':'Standard / With Seat'}],
 13:[{'label':'Model','value':'S9 Pro / S9 Max'},{'label':'Handlebar','value':'With / Without'},{'label':'Compatibility','value':'Confirm connector and controller revision'}],
 14:[{'label':'Model','value':'S9 Pro'},{'label':'Version','value':'2026'},{'label':'Tires','value':'Pneumatic'},{'label':'Pack','value':'1 / 2'}],
 }[i]
 special_values={'Black / White':'黑色 / 白色','Blue / Pink':'蓝色 / 粉色','Step-through':'低跨点','Standard / With Seat':'标准版 / 带座版','With / Without':'含车把 / 不含车把','Confirm connector and controller revision':'请核对接口和控制器版本','Pneumatic':'充气轮胎'}
 cn_specs=[{'label':zh(v['label']),'value':special_values.get(v['value'],zh(v['value']))} for v in specs]
 boxes=p['boxItems'];cn_boxes=[zh(x) for x in boxes]
 if i==13:
  boxes=['Dashboard module','Harness','Manual','Handlebar assembly only when the with-handlebar option is selected.']
  cn_boxes=['仪表盘模块','线束','使用说明书','仅含车把选项包含车把组件。']
 if i==10:boxes+=['Bike lock'];cn_boxes+=['车锁']
 if i==12:boxes+=['Scooter bag (both variants)', 'Seat assembly (seated variant only)'];cn_boxes+=['滑板车包（两种版本均含）','座椅组件（仅带座版）']
 if i in [11,14]:boxes+=['Standard accessories are supplied for each scooter in a two-unit pack.'];cn_boxes+=['双台组合包含每台车对应的标准配件。']
 if i==11:boxes+=['Gift packaging when the gift-box option is selected.'];cn_boxes+=['选择礼盒选项时包含礼盒包装。']
 if i==8:boxes+=['Visor accessory when the Visor Kit is selected.'];cn_boxes+=['选择 Visor Kit 时包含遮阳配件。']
 if i<=9:
  boxes+=['Other kit contents and version differences are not specified in the current catalog; confirm them with support before purchase.']
  cn_boxes+=['现有资料未列明其他套装附件及版本差异，请在购买前联系客服确认。']
 detail_note=('Catalog parameters describe the base version. Range and speed vary with conditions; check local road rules. '+guide[0] if i not in [7,8,13] else guide[0])
 detail_cn=('参数描述基础版；续航及速度受使用条件影响，请核对当地道路规定。'+guide[1] if i not in [7,8,13] else guide[1])
 # The merchant reduced image scope: use verified views and one accurate main
 # for models whose old generated details depicted a different construction.
 views=['main','detail','scene'] if i in [1,2,3,5,6,7,9] else ['main','detail'] if i==8 else ['main']
 images=[f'/cbjj/products/{i}/{v}.png' for v in views]
 faqs_en=[{'question':'Is this item available for preorder?','answer':'No. Only variants showing available stock can be ordered; unavailable options cannot be purchased.'},
 {'question':'What should I check before ordering?','answer':guide[0]},
 {'question':'What delivery and after-sales policies apply?','answer':'Dispatch in 1–3 business days. Free standard delivery: US/Canada 5–10 business days, mainland China 3–7. Request returns within 30 days of delivery; one-year limited warranty. See the full policies for conditions.'},
 {'question':'Are the images inventory or certification evidence?','answer':'No. Generated illustrations are used only to demonstrate products; they are not stock, procurement or certification evidence.'}]
 faqs_cn=[{'question':'可以预订缺货款吗？','answer':'不可以。仅售现货，只有显示可用库存的版本才能下单。'},
 {'question':'购买前应核对什么？','answer':guide[1]},
 {'question':'适用哪些配送与售后政策？','answer':'1–3 个工作日发货，标准配送免运费。美国/加拿大预计运输 5–10 个工作日，中国大陆 3–7 个工作日。签收后 30 天内可申请退货，提供一年有限保修，具体条件请查看完整政策。'},
 {'question':'图片可用作库存或认证证明吗？','answer':'不可以。生成图片仅用于商品展示，不作为库存、进货或认证证据。'}]
 upsells=[];upsells_cn=[]
 for addon in p['upsells']:
  a=dict(addon);code=a['code'];a['image']='/cbjj/products/7/main.png' if 'lock' in code else '/cbjj/products/8/main.png' if 'helmet' in code else '/cbjj/hero/hero.png'
  if 'warranty' in code:
   n=2 if '2y' in code else 1;a['name']=f'{n}-Year Extended Limited Warranty';a['description']=f'Adds {n} year(s) after the included one-year limited warranty. The same defect coverage and exclusions apply; statutory rights remain unaffected.'
   name_cn=f'{n} 年延长有限保修';desc_cn=f'在随附的一年有限保修结束后延长 {n} 年，适用相同的缺陷保障和除外条款，不影响法定权利。'
  elif 'lock' in code:a['name']='CBJJ Cable Lock';a['description']='Steel cable parking lock. Confirm mounting and reach before purchase.';name_cn='CBJJ 钢缆锁';desc_cn='用于停车固定的钢缆锁，购买前请核对固定方式及长度适配。'
  elif 'helmet' in code:a['name']='CBJJ Riding Helmet';a['description']='Adult helmet. Confirm size and fit before purchase.';name_cn='CBJJ 骑行头盔';desc_cn='成人头盔，购买前请确认尺寸及佩戴适配。'
  else:a['name']='CBJJ '+re.sub('isinwheel','',a['name'],flags=re.I).strip();name_cn=a['name'];desc_cn='请核对所选配件的适配条件。'
  upsells.append(a);upsells_cn.append({**a,'name':name_cn,'description':desc_cn})
 values={'slug':re.sub('^isinwheel-','cbjj-',p['slug'],flags=re.I),'name':localized(*titles[i]),'subtitle':localized(*summary),
 'description':localized('<p>'+html.escape(summary[0])+'</p><p>'+html.escape(detail_note)+'</p>','<p>'+html.escape(summary[1])+'</p><p>'+html.escape(detail_cn)+'</p>'),
 'pic':images[0],'images':images,'app_image':'','tags':localized([],[]),'spec_table':localized(specs,cn_specs),
 'specs':localized([{**v,'icon': 'BatteryIcon' if v['label']=='Battery' else 'ZapIcon'} for v in specs[:4]],[{**v,'icon':'BatteryIcon' if specs[j]['label']=='Battery' else 'ZapIcon'} for j,v in enumerate(cn_specs[:4])]),
 'quick_know':localized([summary[0],guide[0],'Only in-stock variants are sold.'],[summary[1],guide[1],'仅销售有库存的所选版本。']),
 'box_items':localized(boxes,cn_boxes),'faqs':localized(faqs_en,faqs_cn),'upsells':localized(upsells,upsells_cn)}
 skus=[]
 for sku in p['skuList']:
  attrs=sku['attributes'];cn_attrs={k:zh(v) for k,v in attrs.items()};key=variant_key(p,sku);pic=images[0]
  desc=' / '.join(str(v) for v in attrs.values())+'. '+('Confirm connector and controller revision before ordering.' if i==13 else 'Package and fit information are shown in the product details.')
  change={'pic':pic,'images':[pic],'description':localized(desc,' / '.join(cn_attrs.values())+'。'+('购买前请核对接口及控制器版本。' if i==13 else '包装及适配条件请查看商品详情。')),'specs':localized(attrs,cn_attrs)}
  statements.append('UPDATE pms_sku SET '+', '.join(f'{k}={sql(v)}' for k,v in change.items())+f' WHERE id={sku["id"]} AND product_id={i};')
  skus.append({'id':sku['id'],'key':key,**change})
 statements.append('UPDATE pms_product SET '+', '.join(f'{k}={sql(v)}' for k,v in values.items())+f' WHERE id={i};')
 result.append({'id':i,**values,'skuUpdates':skus})

for category,product in {1:1,2:3,3:5,4:8,11:1,12:12,13:3,14:4,15:5,16:6,17:8,18:7,19:11}.items():
 item=next(p for p in result if p['id']==product)
 hero=next((image for image in item['images'] if image.endswith('/scene.png')),item['pic'])
 statements.append(f"UPDATE pms_category SET hero_image='{hero}', menu_image='/cbjj/products/{product}/main.png' WHERE id={category};")
statements += ["UPDATE pms_category SET name='{\"en\":\"Scooters for Kids\",\"zh\":\"儿童滑板车\"}', description='{\"en\":\"Three-wheel scooters for supervised use in closed areas.\",\"zh\":\"用于成人监护下封闭场地的三轮滑板车。\"}' WHERE id=19;",
 "UPDATE pms_category SET name='{\"en\":\"Street & Carving\",\"zh\":\"街道滑行\"}', description='{\"en\":\"Electric boards for smooth, dry riding surfaces.\",\"zh\":\"适用于平整干燥路面的电动滑板。\"}' WHERE id=15;",
 "UPDATE cms_promotion_activity SET enabled=false, desktop_bg='/cbjj/hero/hero.png', mobile_bg='/cbjj/hero/hero.png', link_url='/collections/electric-scooters' WHERE code='spring-ride-festival' OR countdown_end_at IS NULL OR countdown_end_at<CURRENT_TIMESTAMP;",
 "UPDATE pms_review SET is_demo=true WHERE user_id IS NULL;"]
(ROOT/'data/cbjj-catalog.json').write_text(json.dumps(result,ensure_ascii=False,indent=2),encoding='utf-8')
migration='\n\n'.join(statements)+'\n'
(ROOT/'backend/src/main/resources/db/migration/V5__cbjj_catalog_content.sql').write_text(migration,encoding='utf-8')
# Fresh, empty installations get the same published content. Never execute this initializer on an existing database.
schema_path=ROOT/'backend/src/main/resources/schema.sql'
marker='-- GENERATED CBJJ CONTENT FOR EMPTY-DATABASE INITIALIZATION'
schema=schema_path.read_text(encoding='utf-8').split(marker)[0].rstrip()
schema_path.write_text(schema+'\n\n'+marker+'\n'+migration,encoding='utf-8')
print(f'Prepared {len(result)} products and {sum(len(p["skuUpdates"]) for p in result)} SKU content updates; stock and prices preserved.')
