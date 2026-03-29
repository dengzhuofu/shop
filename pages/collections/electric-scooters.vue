<template>
  <div class="electric-scooters-page">
    <section class="collection-header container">
      <h1 class="title">Electric Scooters</h1>
      <p class="subtitle">{{ sortedProducts.length }} products</p>
    </section>

    <section class="toolbar container">
      <button class="filter-btn" type="button" @click="isFilterOpen = !isFilterOpen">
        <SlidersHorizontalIcon class="icon" />
        Show filters
      </button>

      <div class="sort-wrap">
        <label for="sort-by">Sort by:</label>
        <div class="select-wrap">
          <select id="sort-by" v-model="sortBy">
            <option value="featured">Featured</option>
            <option value="best-selling">Best selling</option>
            <option value="title-asc">Alphabetically, A-Z</option>
            <option value="title-desc">Alphabetically, Z-A</option>
            <option value="price-asc">Price, low to high</option>
            <option value="price-desc">Price, high to low</option>
            <option value="created-asc">Date, old to new</option>
            <option value="created-desc">Date, new to old</option>
          </select>
          <ChevronDownIcon class="icon" />
        </div>
      </div>
    </section>

    <section class="mobile-filters" :class="{ 'is-open': isFilterOpen }">
      <div class="container mobile-filters-inner">
        <div class="filter-group">
          <h3>Availability</h3>
          <label><input type="checkbox" /> In stock</label>
          <label><input type="checkbox" /> Out of stock</label>
        </div>
        <div class="filter-group">
          <h3>Price</h3>
          <label><input type="checkbox" /> Under $300</label>
          <label><input type="checkbox" /> $300 to $700</label>
          <label><input type="checkbox" /> Above $700</label>
        </div>
      </div>
    </section>

    <section class="product-list container">
      <div class="grid">
        <CollectionProductCard v-for="product in sortedProducts" :key="product.id" :product="product" />
      </div>

      <div class="show-more-wrap">
        <button class="show-more-btn" type="button">Show more</button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { ChevronDownIcon, SlidersHorizontalIcon } from 'lucide-vue-next'
import CollectionProductCard from '~/components/CollectionProductCard.vue'

const sortBy = ref('featured')
const isFilterOpen = ref(false)

// 集合页 Mock 数据按目标站点卡片顺序整理。
const products = [
  {
    id: 's9-pro',
    url: '/products/isinwheel%C2%AE-s9-pneumatic-tireelectric-scooter',
    title: 'isinwheel S9 Pro Pneumatic Tire Electric Scooter',
    image: 'https://www.isinwheel.com/cdn/shop/files/1_1_32ee4ef9-a06d-460f-bef5-09f8effe5dd2.jpg?v=1752493545&width=600',
    badge: 'Promotion',
    rating: 5,
    reviews: 61,
    price: 249.99,
    compareAtPrice: 399.99,
    createdAt: '2024-09-01',
    specs: [
      { label: 'Motor Capacity', value: '500W' },
      { label: 'Max Range', value: '19 Miles' },
      { label: 'Top Speed', value: '19 MPH' },
      { label: 'Battery Capacity', value: '36V 7.5Ah' }
    ]
  },
  {
    id: 's-nova-commuting',
    url: '/products/isinwheel-s-nova-electric-scooter-for-adults',
    title: 'S Nova Commuting Electric Scooter',
    image: 'https://www.isinwheel.com/cdn/shop/files/2025.7.10S_nova_website_1024x1024.png?v=1753423190&width=600',
    badge: 'Promotion',
    rating: 5,
    reviews: 36,
    price: 279.99,
    compareAtPrice: 499.99,
    createdAt: '2024-09-05',
    specs: [
      { label: 'Motor Capacity', value: '500W' },
      { label: 'Max Range', value: '19 Miles' },
      { label: 'Top Speed', value: '19 MPH' },
      { label: 'Battery Capacity', value: '36V 7.5Ah' }
    ]
  },
  {
    id: 's-nova-pro',
    url: '/products/s-nova-pro-electric-scooter',
    title: 'S Nova Pro Commuting Electric Scooter',
    image: 'https://www.isinwheel.com/cdn/shop/files/2025.7.10S_nova_Pro_website_1024x1024.png?v=1753422418&width=600',
    badge: 'Promotion',
    rating: 5,
    reviews: 39,
    price: 399.99,
    compareAtPrice: 599.99,
    createdAt: '2024-09-10',
    specs: [
      { label: 'Max Power', value: '1000W' },
      { label: 'Max Range', value: '31 Miles' },
      { label: 'Top Speed', value: '28 MPH' },
      { label: 'Battery Capacity', value: '48V 10.4Ah' }
    ]
  },
  {
    id: 's10max',
    url: '/products/isinwheel%C2%AE-s10max-1000w-high-end-commuting-electric-scooter',
    title: 'isinwheel S10Max 1000W High-End Commuting Electric Scooter',
    image: 'https://www.isinwheel.com/cdn/shop/files/2_efca3654-b6ba-4767-a8ea-250214f188e8.jpg?v=1751289278&width=600',
    badge: 'Promotion',
    rating: 5,
    reviews: 26,
    price: 519.99,
    compareAtPrice: 899.99,
    createdAt: '2024-09-15',
    specs: [
      { label: 'Motor Capacity', value: '1000W' },
      { label: 'Max Range', value: '37 Miles' },
      { label: 'Top Speed', value: '28 MPH' },
      { label: 'Battery Capacity', value: '48V 15Ah' }
    ]
  },
  {
    id: 'gt1',
    url: '/products/isinwheel-gt1-800w-off-road-electric-scooter',
    title: 'GT1 800W Off Road Electric Scooter',
    image: 'https://www.isinwheel.com/cdn/shop/files/1_2_97eec9e3-8a8c-47f7-ac54-bf56d02e00ec.jpg?v=1751969324&width=600',
    badge: 'Promotion',
    rating: 4.9,
    reviews: 76,
    price: 649.99,
    compareAtPrice: 799.99,
    createdAt: '2024-09-20',
    specs: [
      { label: 'Max Power', value: '800W*2' },
      { label: 'Max Range', value: '37 Miles' },
      { label: 'Top Speed', value: '32 MPH' },
      { label: 'Battery Capacity', value: '48V 15Ah' }
    ]
  },
  {
    id: 'gt2',
    url: '/products/isinwheel-gt2-800w-off-road-electric-scooter',
    title: 'GT2 800W Off Road Electric Scooter',
    image: 'https://www.isinwheel.com/cdn/shop/files/35e97af8e06497d0f8f8288328499c8.jpg?v=1733398793&width=600',
    badge: 'Promotion',
    rating: 5,
    reviews: 47,
    price: 749.99,
    compareAtPrice: 999.99,
    createdAt: '2024-09-25',
    specs: [
      { label: 'Max Power', value: '800W*2' },
      { label: 'Max Range', value: '37 Miles' },
      { label: 'Top Speed', value: '32 MPH' },
      { label: 'Battery Capacity', value: '48V 15Ah' }
    ]
  },
  {
    id: 'h7pro',
    url: '/products/isinwheel%C2%AE-h7pro-1200w-high-end-commuting-electric-scooter',
    title: 'isinwheel H7Pro 1200W High-End Commuting Electric Scooter',
    image: 'https://www.isinwheel.com/cdn/shop/files/H7pro2.jpg?v=1751951985&width=600',
    badge: 'Promotion',
    rating: 5,
    reviews: 55,
    price: 849.99,
    compareAtPrice: 1099.99,
    createdAt: '2024-10-01',
    specs: [
      { label: 'Motor Capacity', value: '1200W' },
      { label: 'Max Range', value: '43 Miles' },
      { label: 'Top Speed', value: '38 MPH' },
      { label: 'Tire Size', value: '16*4"' }
    ]
  },
  {
    id: 'gt4',
    url: '/products/gt4-electric-scooter-for-adults',
    title: 'GT4 2000W Off Road Electric Scooter',
    image: 'https://www.isinwheel.com/cdn/shop/files/2025.7.10GT4_website_1024x1024.png?v=1753422003&width=600',
    badge: 'Promotion',
    rating: 4.9,
    reviews: 123,
    price: 899.99,
    compareAtPrice: 1299.99,
    createdAt: '2024-10-05',
    specs: [
      { label: 'Max Power', value: '2000W*2' },
      { label: 'Max Range', value: '37 Miles' },
      { label: 'Top Speed', value: '43 MPH' },
      { label: 'Battery Capacity', value: '60V 20Ah' }
    ]
  },
  {
    id: 's2',
    url: '/products/s2-electric-scooter-for-adults',
    title: 'S2 Electric Scooter For Adults',
    image: 'https://www.isinwheel.com/cdn/shop/files/2025.7.10S2_website_1024x1024.png?v=1753421056&width=600',
    badge: 'Promotion',
    rating: 5,
    reviews: 31,
    price: 139.99,
    compareAtPrice: 169.99,
    createdAt: '2024-10-10',
    specs: [
      { label: 'Motor Capacity', value: '350W' },
      { label: 'Max Range', value: '15 Miles' },
      { label: 'Top Speed', value: '15.5 MPH' },
      { label: 'Battery Capacity', value: '36V 5.2Ah' }
    ]
  },
  {
    id: 's4',
    url: '/products/s4-electric-scooter-for-adults',
    title: 'S4 Electric Scooter For Adults',
    image: 'https://www.isinwheel.com/cdn/shop/files/2025.7.10S4_website_1024x1024.png?v=1753420520&width=600',
    badge: 'Promotion',
    rating: 4.9,
    reviews: 115,
    price: 169.99,
    compareAtPrice: 219.99,
    createdAt: '2024-10-15',
    specs: [
      { label: 'Motor Capacity', value: '350W' },
      { label: 'Max Range', value: '19 Miles' },
      { label: 'Top Speed', value: '19 MPH' },
      { label: 'Battery Capacity', value: '36V 7.5Ah' }
    ]
  },
  {
    id: 's4-pro-seat',
    url: '/products/new-s4-pro-e-scooter-with-seat-for-adult',
    title: 'New S4 Pro e Scooter with Seat for Adult',
    image: 'https://www.isinwheel.com/cdn/shop/files/1.jpg?v=1737092594&width=600',
    badge: 'Promotion',
    rating: 4.8,
    reviews: 42,
    price: 259.99,
    compareAtPrice: 399.99,
    createdAt: '2024-10-20',
    specs: [
      { label: 'Motor Capacity', value: '500W' },
      { label: 'Max Range', value: '25 Miles' },
      { label: 'Top Speed', value: '21 MPH' },
      { label: 'Battery Capacity', value: '36V 10.4Ah' }
    ]
  },
  {
    id: 'kids-3-wheel',
    url: '/products/isinwheel-3-wheels-electric-scooter-for-kids-ages-3-12-7-miles-long-range',
    title: 'isinwheel 3 Wheels Electric Scooter for Kids Ages 3-12, 7 Miles Long Range',
    image: 'https://www.isinwheel.com/cdn/shop/files/1_6594575a-c373-4362-8d75-f6f3f621ece0.jpg?v=1750907667&width=600',
    badge: 'Promotion',
    rating: 4.9,
    reviews: 53,
    price: 149.99,
    compareAtPrice: 219.99,
    createdAt: '2024-10-25',
    specs: [
      { label: 'Motor Capacity', value: '200W' },
      { label: 'Max Range', value: '7 Miles' },
      { label: 'Top Speed', value: '11 MPH' },
      { label: 'Battery Capacity', value: '21.6V 6Ah' }
    ]
  }
]

// 排序逻辑严格映射 Shopify 常见排序选项。
const sortedProducts = computed(() => {
  const list = [...products]

  switch (sortBy.value) {
    case 'best-selling':
      return list.sort((a, b) => b.reviews - a.reviews)
    case 'title-asc':
      return list.sort((a, b) => a.title.localeCompare(b.title))
    case 'title-desc':
      return list.sort((a, b) => b.title.localeCompare(a.title))
    case 'price-asc':
      return list.sort((a, b) => a.price - b.price)
    case 'price-desc':
      return list.sort((a, b) => b.price - a.price)
    case 'created-asc':
      return list.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
    case 'created-desc':
      return list.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    default:
      return list
  }
})
</script>

<style scoped lang="scss">
.electric-scooters-page {
  padding: 20px 0 70px;
}

.collection-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 16px;

  .title {
    font-size: 34px;
    line-height: 1.2;
    font-weight: 700;
    color: #101828;
  }

  .subtitle {
    font-size: 14px;
    color: #667085;
  }
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-top: 1px solid #eaecf0;
  border-bottom: 1px solid #eaecf0;
  padding-top: 14px;
  padding-bottom: 14px;
  margin-bottom: 24px;
  gap: 12px;

  .filter-btn {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    color: #101828;
    font-size: 14px;
    font-weight: 500;

    .icon {
      width: 17px;
      height: 17px;
    }
  }

  .sort-wrap {
    display: inline-flex;
    align-items: center;
    gap: 10px;

    label {
      font-size: 14px;
      color: #344054;
      white-space: nowrap;
    }

    .select-wrap {
      position: relative;

      select {
        appearance: none;
        border: 1px solid #d0d5dd;
        border-radius: 10px;
        padding: 8px 34px 8px 12px;
        font-size: 14px;
        color: #101828;
        background: #fff;
        min-width: 210px;
      }

      .icon {
        width: 16px;
        height: 16px;
        color: #667085;
        position: absolute;
        right: 10px;
        top: 50%;
        transform: translateY(-50%);
        pointer-events: none;
      }
    }
  }
}

.mobile-filters {
  display: none;
  border-bottom: 1px solid #eaecf0;
  margin-bottom: 24px;

  &.is-open {
    display: block;
  }

  .mobile-filters-inner {
    display: flex;
    gap: 28px;
    padding-top: 12px;
    padding-bottom: 16px;
  }

  .filter-group {
    display: flex;
    flex-direction: column;
    gap: 8px;

    h3 {
      font-size: 13px;
      font-weight: 700;
      color: #101828;
      margin-bottom: 2px;
    }

    label {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      font-size: 13px;
      color: #344054;
    }
  }
}

.product-list {
  .grid {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 18px;
  }

  .show-more-wrap {
    display: flex;
    justify-content: center;
    margin-top: 28px;
  }

  .show-more-btn {
    border: 1px solid #1d2939;
    border-radius: 999px;
    color: #1d2939;
    font-size: 14px;
    font-weight: 600;
    padding: 10px 26px;
    transition: all 0.2s ease;

    &:hover {
      background: #1d2939;
      color: #fff;
    }
  }
}

@media (max-width: 1440px) {
  .product-list {
    .grid {
      grid-template-columns: repeat(3, minmax(0, 1fr));
    }
  }
}

@media (max-width: 1024px) {
  .collection-header {
    .title {
      font-size: 28px;
    }
  }

  .product-list {
    .grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }
}

@media (max-width: 768px) {
  .electric-scooters-page {
    padding-top: 14px;
  }

  .collection-header {
    flex-direction: column;
    gap: 4px;
    margin-bottom: 12px;

    .title {
      font-size: 24px;
    }
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;

    .sort-wrap {
      justify-content: space-between;

      .select-wrap {
        flex: 1;

        select {
          width: 100%;
          min-width: 0;
        }
      }
    }
  }

  .mobile-filters {
    .mobile-filters-inner {
      flex-direction: column;
      gap: 14px;
    }
  }

  .product-list {
    .grid {
      grid-template-columns: 1fr;
      gap: 14px;
    }
  }
}
</style>
