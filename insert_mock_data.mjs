import pg from 'pg';
const { Client } = pg;

const client = new Client({
  user: 'postgres',
  host: 'localhost',
  database: 'shop',
  password: '1st1st1st',
  port: 5432,
});

const products = [
  {
    name: { en: "X1 Explorer Off-Road Electric Scooter", zh: "X1 探索者 越野电动滑板车" },
    description: { en: "<p>Conquer any terrain with the X1 Explorer. Dual 1200W motors and robust suspension.</p>" },
    price: 1299.99,
    compare_at_price: 1499.99,
    stock: 50,
    pic: 'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=800',
    tags: ["NEW", "Off-Road"],
    images: [
      "https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=800",
      "https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=800"
    ],
    app_image: 'https://via.placeholder.com/60x120?text=APP',
    specs: [
      { label: "Motor", value: "1200W*2", icon: "ActivityIcon" },
      { label: "Top Speed", value: "40 MPH", icon: "ActivityIcon" },
      { label: "Max Range", value: "50 Miles", icon: "NavigationIcon" }
    ],
    quick_know: { en: ["<strong>1200W*2</strong> Dual Motor", "<strong>50 Miles</strong> Range", "<strong>11 inch</strong> Off-road Tires"] },
    upsells: [],
    skus: [
      { sku_code: 'X1-EXP-BLK', price: 1299.99, stock: 25, pic: 'https://images.unsplash.com/photo-1593950315186-76a92975b60c?auto=format&fit=crop&q=80&w=800', specs: { en: { Style: "Standard", Color: "Black" } } },
      { sku_code: 'X1-EXP-RED', price: 1350.00, stock: 25, pic: 'https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=800', specs: { en: { Style: "Standard", Color: "Red" } } }
    ],
    reviews: [
      { user_name: "Mike T.", rating: 5, title: "Absolute Beast", content: "This thing flies! The suspension handles off-road trails perfectly.", images: [] }
    ]
  },
  {
    name: { en: "M2 City Commuter E-Bike", zh: "M2 城市通勤电动自行车" },
    description: { en: "<p>Sleek design, lightweight frame, perfect for your daily commute.</p>" },
    price: 899.00,
    compare_at_price: 1099.00,
    stock: 120,
    pic: 'https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=800',
    tags: ["HOT", "Commuter"],
    images: [
      "https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=800",
      "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&q=80&w=800"
    ],
    app_image: '',
    specs: [
      { label: "Motor", value: "500W", icon: "ActivityIcon" },
      { label: "Top Speed", value: "20 MPH", icon: "ActivityIcon" },
      { label: "Battery", value: "48V 10Ah", icon: "BatteryIcon" }
    ],
    quick_know: { en: ["<strong>500W</strong> Motor", "<strong>Shimano 7-Speed</strong> Gears", "<strong>Foldable</strong> Design"] },
    upsells: [],
    skus: [
      { sku_code: 'M2-CITY-WHT', price: 899.00, stock: 60, pic: 'https://images.unsplash.com/photo-1532298229144-0ec0c57515c7?auto=format&fit=crop&q=80&w=800', specs: { en: { Style: "White Edition" } } }
    ],
    reviews: [
      { user_name: "Sarah L.", rating: 5, title: "Perfect for city", content: "Saves me so much time commuting to work. Highly recommended.", images: [] },
      { user_name: "John D.", rating: 4, title: "Good bike", content: "Solid build quality, but the seat could be more comfortable.", images: [] }
    ]
  },
  {
    name: { en: "G-Pro Electric Skateboard", zh: "G-Pro 智能电动滑板" },
    description: { en: "<p>Experience the thrill of carving the streets with the G-Pro.</p>" },
    price: 450.00,
    compare_at_price: 550.00,
    stock: 200,
    pic: 'https://images.unsplash.com/photo-1563215886-35cb172776fc?auto=format&fit=crop&q=80&w=800',
    tags: ["Flash Sale"],
    images: ["https://images.unsplash.com/photo-1563215886-35cb172776fc?auto=format&fit=crop&q=80&w=800"],
    app_image: '',
    specs: [
      { label: "Motor", value: "400W Dual", icon: "ActivityIcon" },
      { label: "Top Speed", value: "22 MPH", icon: "ActivityIcon" }
    ],
    quick_know: { en: ["<strong>400W</strong> Dual Hub Motors", "<strong>Wireless Remote</strong> Included"] },
    upsells: [],
    skus: [
      { sku_code: 'GPRO-SK8', price: 450.00, stock: 200, pic: 'https://images.unsplash.com/photo-1563215886-35cb172776fc?auto=format&fit=crop&q=80&w=800', specs: { en: { Style: "Pro Deck" } } }
    ],
    reviews: []
  },
  {
    name: { en: "Smart LED Riding Helmet", zh: "智能 LED 骑行头盔" },
    description: { en: "<p>Safety meets technology. Built-in turn signals and brake lights.</p>" },
    price: 89.99,
    compare_at_price: 129.99,
    stock: 300,
    pic: 'https://images.unsplash.com/photo-1557804506-669a67965ba0?auto=format&fit=crop&q=80&w=800',
    tags: ["Accessories"],
    images: ["https://images.unsplash.com/photo-1557804506-669a67965ba0?auto=format&fit=crop&q=80&w=800"],
    app_image: '',
    specs: [
      { label: "Weight", value: "350g", icon: "ActivityIcon" },
      { label: "Battery Life", value: "10 Hours", icon: "BatteryIcon" }
    ],
    quick_know: { en: ["<strong>Impact Resistant</strong> PC Shell", "<strong>LED Turn Signals</strong>"] },
    upsells: [],
    skus: [
      { sku_code: 'HLMT-LED-M', price: 89.99, stock: 150, pic: 'https://images.unsplash.com/photo-1557804506-669a67965ba0?auto=format&fit=crop&q=80&w=800', specs: { en: { Size: "Medium" } } },
      { sku_code: 'HLMT-LED-L', price: 89.99, stock: 150, pic: 'https://images.unsplash.com/photo-1557804506-669a67965ba0?auto=format&fit=crop&q=80&w=800', specs: { en: { Size: "Large" } } }
    ],
    reviews: [
      { user_name: "Alex", rating: 5, title: "Feel much safer", content: "The lights are very bright at night. Great accessory.", images: [] }
    ]
  }
];

async function insertMockData() {
  try {
    await client.connect();
    console.log('Connected to PostgreSQL successfully.');

    for (const p of products) {
      const productQuery = `
        INSERT INTO pms_product (
          name, description, price, compare_at_price, stock, pic, 
          tags, images, app_image, specs, quick_know, upsells
        ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12)
        RETURNING id;
      `;
      const productValues = [
        JSON.stringify(p.name), JSON.stringify(p.description), p.price, p.compare_at_price, p.stock, p.pic,
        JSON.stringify(p.tags), JSON.stringify(p.images), p.app_image, JSON.stringify(p.specs), JSON.stringify(p.quick_know), JSON.stringify(p.upsells)
      ];

      const res = await client.query(productQuery, productValues);
      const productId = res.rows[0].id;
      console.log(`Inserted Product: ${p.name.en} (ID: ${productId})`);

      for (const sku of p.skus) {
        const skuQuery = `
          INSERT INTO pms_sku (product_id, sku_code, price, stock, pic, description, specs) 
          VALUES ($1, $2, $3, $4, $5, $6, $7);
        `;
        const skuValues = [
          productId, sku.sku_code, sku.price, sku.stock, sku.pic, JSON.stringify({ en: `<p>${sku.sku_code}</p>` }), JSON.stringify(sku.specs)
        ];
        await client.query(skuQuery, skuValues);
        console.log(`  Inserted SKU: ${sku.sku_code}`);
      }

      for (const rev of p.reviews) {
        const revQuery = `
          INSERT INTO pms_review (product_id, user_id, user_name, rating, title, content, images, verified_purchase)
          VALUES ($1, $2, $3, $4, $5, $6, $7, $8);
        `;
        const revValues = [
          productId, null, rev.user_name, rev.rating, rev.title, rev.content, JSON.stringify(rev.images), true
        ];
        await client.query(revQuery, revValues);
        console.log(`  Inserted Review by: ${rev.user_name}`);
      }
    }

    console.log('All mock data inserted successfully!');
  } catch (err) {
    console.error('Error inserting mock data:', err);
  } finally {
    await client.end();
  }
}

insertMockData();
