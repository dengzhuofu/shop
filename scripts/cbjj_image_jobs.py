from pathlib import Path
import json, shutil, argparse
root=Path(__file__).resolve().parents[1]
parser=argparse.ArgumentParser()
parser.add_argument('--all-views',action='store_true',help='Prepare the optional expanded gallery and variant jobs.')
args=parser.parse_args()
frozen=root/'output/cbjj-product4-reference.png'
if not frozen.exists(): shutil.copyfile(root/'public/cbjj/products/4/main.png', frozen)
models={
4:('public/cbjj/products/4/main.png','the mountain ebike in the reference, maintaining its fat tires, bicycle geometry, battery position, front fork and rear suspension. Change ONLY painted bicycle frame panels to dark forest green; tires, seat, forks and battery remain black'),
10:('output/original-product-10.png','the EXACT step-through cruiser electric bicycle in the reference, black base frame, thin commuter tires, brown saddle, rear luggage rack, battery mounted vertically behind the seat tube, straight low step-through top frame. Do not use an integrated downtube battery or fat tires'),
11:('output/original-product-11.jpg','the EXACT small three-wheel kids scooter geometry in the reference: TWO small wheels at the front and ONE at rear, simple silver slender telescoping stem, blue rubber grips, blue low plastic deck with neutral nonfigurative grip texture. Recolor pink plastic parts BLUE for the base variant. No headlight, no cartoon characters, no big scooter deck or adult tires'),
12:('output/original-product-12.png','the EXACT black GT2 adult standing off-road scooter in the reference: thick knobby wheels, compact red suspension arms at front/rear, black tall folding stem, deck accent light, low stem headlight; standard STANDING option, NO seat, NO phone. Preserve its vehicle geometry'),
13:('output/original-product-13.jpg','the EXACT vertically oriented narrow green PCB dashboard module in the reference. Tall narrow black seven-segment display face, red centered tactile button below, rounded lower board end, red/green/white thin wires and their connectors, thicker curved black cable with pale BLUE barrel plug. Preserve narrow vertical shape and mounting holes; NOT a horizontal automotive dashboard. No handlebar for the base option'),
14:('output/original-product-14.jpg','the EXACT black S9 Pro 2026 standing foldable scooter in the reference: slender straight stem, green cable and wheel-ring accents, pneumatic commuter tires, handlebar end amber turn signals, rear disc brake and black textured flat deck. NO suspension or seat added. Preserve dimensions and geometry'),
15:('output/original-product-15.jpg','the EXACT black S Nova scooter in the reference: 8.5-inch thick tires, front/rear short swing-arm suspension, high stem square headlamp, bar-end amber signals, illuminated small side deck strip, compact black deck and rear fender. Preserve this geometry and size ratios; NO seat')}
base='Use imagegen to create a polished photorealistic CBJJ ecommerce product illustration from the provided reference. Remove ALL old brand names/logos, smartphones, app UI, ads, gift banners, watermark text, certification badges, printed model/serial codes and exaggerated light flares. Do not introduce a different product or unconfirmed accessories. No marketing text, no fake specifications, no certification symbols. Realistic materials, crisp edges, coherent functional parts, neutral commercial photography. Subject: '
jobs=[]
for i,(ref,description) in models.items():
 if i==4:ref='output/cbjj-product4-reference.png'
 for kind in ['main','detail','scene']:
  view={'main':'Studio primary image. Show ONE complete product, all wheels visible, centered three-quarter view, soft shadow, clean white background, generous margin. Square image, high resolution.',
        'detail':'Detail product image. A close three-quarter view of real construction and finish using the SAME model and color; show its brake/wheel/frame area or the dashboard button, board and plugs for the electronic module. Clean neutral studio background. Do not add diagrams, callouts or text. Square high-resolution image.',
        'scene':('Show this same dashboard module lying loose, disconnected, on a clean technician work mat beside basic tools. The narrow PCB and blue cable plug are clearly recognizable. Not installed, no measurements or proof-of-certification.' if i==13 else 'Usage setting image. ONE matching product parked fully visible in a quiet closed park or private courtyard in daylight. No traffic, no people, no logos in surroundings; no other vehicles. Do not change the structure, color or wheel count. Square high-resolution image.')}[kind]
  jobs.append({'id':i,'kind':kind,'source':str(root/ref),'target':str(root/f'public/cbjj/products/{i}/{kind}.png'),'prompt':base+description+'. '+view})
jobs.append({'id':8,'kind':'scene','source':str(root/'public/cbjj/products/8/main.png'),'target':str(root/'public/cbjj/products/8/scene.png'),'prompt':'Use imagegen to create a realistic product showcase illustration of the EXACT adult riding helmet from this reference. Preserve the round skate-style shell, ventilation layout, tiny integral front brim, padded black liner and black chin straps. One matte-black helmet on a wooden park bench with an out-of-focus bicycle in the distant background. No separate removable visor, no road-racing helmet shape, no rider, no brand logos, no text or certification claims. The helmet is the clear, sharply focused main subject. Square image, high resolution, natural daylight.'})
# Only physical product configuration differences are illustrated; unspecified gift-box design is not invented.
changes={
1:{'pearl-white':'Recolor ONLY painted body panels pearl white; same single scooter, same black deck and tires.'},
2:{'storm-grey':'Recolor ONLY painted body panels storm grey; same single scooter and components.'},
3:{'ocean-blue':'Recolor ONLY painted bicycle frame ocean blue; same single ebike, black tires, fork and battery.'},
4:{'sand':'Recolor ONLY painted bicycle frame warm sand beige; same single fat-tire ebike and suspension.'},
5:{'forest-green':'Recolor deck artwork forest green in a plain nonfigurative design; same one electric longboard, black grip surface, same wheels and small handheld remote.',
   'sunset-red':'Recolor deck artwork sunset red in a plain nonfigurative design; same one electric longboard and small handheld remote.'},
6:{'desert-sand':'Recolor only board deck panels desert sand beige; same one off-road board and wheels.',
   'glacier-white':'Recolor only board deck panels glacier white; same one off-road board, black grip and tires.'},
7:{'black-twin':'Show exactly TWO matching black cable locks and their keys, separate readable outlines, same cable construction. No bicycle or scooter.',
   'safety-orange-twin':'Show exactly TWO matching cable locks with safety orange protective outer sleeves and keys. Same coil and lock mechanism.',
   'slate-grey':'Show exactly ONE matching cable lock with slate grey protective outer sleeve and its keys.'},
8:{'black-visor':'Show exactly ONE same matte black adult bicycle riding helmet, with a simple short removable black visor attached at the front. Preserve open face, vents and strap. NOT a motorcycle full-face helmet.',
   'glacier-white':'Show exactly ONE same adult helmet with glacier white outer shell, black lining and strap, no visor.',
   'glacier-white-visor':'Show exactly ONE same adult helmet with glacier white outer shell, black lining and strap, short removable black front visor.',
   'sand-visor':'Show exactly ONE same adult helmet with warm sand outer shell, black lining and strap, short removable black front visor.'},
9:{'pearl-white':'Recolor ONLY painted scooter panels pearl white; same single folding scooter and geometry.',
   'storm-grey':'Recolor ONLY painted scooter panels storm grey; same single folding scooter and geometry.'},
10:{'white':'Recolor ONLY bicycle frame white; same single step-through cruiser, brown saddle, black vertical seat-tube battery and rear rack.'},
11:{'pink':'Show ONE matching kids three-wheel scooter; change blue plastic grips and deck to soft pink. Keep two tiny front wheels and one rear.',
    'blue-blue':'Show exactly TWO matching BLUE kids three-wheel scooters, separate outlines, two front wheels plus one rear on each.',
    'blue-pink':'Show exactly TWO matching kids three-wheel scooters: ONE BLUE and ONE PINK. Keep the same small child-scaled deck, silver stem and two front wheels plus one rear on each.',
    'pink-pink':'Show exactly TWO matching PINK kids three-wheel scooters; both same child scale and two front wheels plus one rear.'},
12:{'seat':'Show ONE identical GT2 scooter with a removable simple black padded seat on a post mounted securely on the REAR deck, clear standing deck in front. Preserve tires, red suspension arms, stem and controls. No new wheel or oversize platform.'},
13:{'with-handlebar':'Show ONE same narrow dashboard module and wires, together with ONE complete plain black electric scooter handlebar assembly with rubber grips, brake lever. Show the handlebar assembly alongside the loose module so its actual board and connectors remain visible. No unconfirmed turn signals or accessory mounts. Keep module geometry and do not add a scooter vehicle or smartphone.'},
14:{'two-pack':'Show exactly TWO identical matching S9 Pro 2026 scooters side by side, separate full outlines and visible wheels. Preserve green accents and flat commuter scooter deck. No other vehicle.'},
15:{'gray':'Change ONLY painted scooter panels and stem to medium gray; same ONE S Nova, black tires, deck grip and suspension.',
    'white':'Change ONLY painted scooter panels and stem to clean white; same ONE S Nova, black tires, deck grip and suspension.'}
}
for i,variants in changes.items():
 for key,prompt in variants.items():
  jobs.append({'id':i,'kind':'variant','key':key,'source':str(root/f'public/cbjj/products/{i}/main.png'),'target':str(root/f'public/cbjj/products/{i}/variants/{key}.png'),
               'prompt':base+prompt+' Maintain the EXACT referenced product construction. Primary catalog image on pure white background, soft shadow, centered with all products fully visible. Do not add logos, carton boxes or text. Square high-resolution commercial photograph.'})
if not args.all_views:jobs=[job for job in jobs if job['kind']=='main']
(root/'output/cbjj-image-jobs.json').write_text(json.dumps(jobs,ensure_ascii=False,indent=2),encoding='utf-8')
print(f'{len(jobs)} remaining image generation jobs: {sum(j["kind"]!="variant" for j in jobs)} product views, {sum(j["kind"]=="variant" for j in jobs)} variants.')

