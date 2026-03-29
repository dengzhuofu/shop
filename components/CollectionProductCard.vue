<template>
  <article class="collection-product-card">
    <NuxtLink class="card-link" :to="product.url">
      <div class="media">
        <img :src="product.image" :alt="product.title" loading="lazy" />
        <span v-if="product.badge" class="badge">{{ product.badge }}</span>
      </div>
    </NuxtLink>

    <div class="content">
      <div class="rating-row">
        <div class="stars">
          <StarIcon v-for="idx in 5" :key="idx" class="star" />
        </div>
        <span class="score">{{ product.rating.toFixed(1) }}</span>
        <span class="reviews">{{ product.reviews }} reviews</span>
      </div>

      <NuxtLink class="title-link" :to="product.url">
        <h3 class="title">{{ product.title }}</h3>
      </NuxtLink>

      <div class="price-row">
        <span class="price">${{ product.price.toFixed(2) }}</span>
        <span class="compare-price">${{ product.compareAtPrice.toFixed(2) }}</span>
        <span class="save">Save ${{ (product.compareAtPrice - product.price).toFixed(2) }}</span>
      </div>

      <button class="action-btn" type="button">
        {{ product.cta || 'Choose options' }}
      </button>

      <ul class="spec-grid">
        <li v-for="spec in product.specs" :key="spec.label" class="spec-item">
          <span class="spec-value">{{ spec.value }}</span>
          <span class="spec-label">{{ spec.label }}</span>
        </li>
      </ul>
    </div>
  </article>
</template>

<script setup>
import { StarIcon } from 'lucide-vue-next'

defineProps({
  product: {
    type: Object,
    required: true
  }
})
</script>

<style scoped lang="scss">
.collection-product-card {
  border-radius: 14px;
  border: 1px solid #eceff3;
  background: #fff;
  overflow: hidden;
  height: 100%;
  display: flex;
  flex-direction: column;

  .card-link {
    display: block;
  }

  .media {
    position: relative;
    background: #f6f7f8;
    aspect-ratio: 1 / 1;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.35s ease;
    }

    .badge {
      position: absolute;
      top: 10px;
      right: 10px;
      z-index: 2;
      padding: 6px 10px;
      border-radius: 16px;
      font-size: 11px;
      font-weight: 700;
      letter-spacing: 0.02em;
      color: #fff;
      background: #101828;
    }
  }

  &:hover {
    .media img {
      transform: scale(1.04);
    }
  }

  .content {
    padding: 14px 14px 16px;
    display: flex;
    flex-direction: column;
    flex: 1;
  }

  .rating-row {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-bottom: 10px;

    .stars {
      display: inline-flex;
      align-items: center;
      gap: 2px;

      .star {
        width: 12px;
        height: 12px;
        color: #f59f0b;
        fill: currentColor;
      }
    }

    .score,
    .reviews {
      font-size: 12px;
      color: #5b6472;
    }

    .score {
      font-weight: 600;
      color: #232933;
    }
  }

  .title-link {
    display: block;
    margin-bottom: 8px;
  }

  .title {
    font-size: 15px;
    line-height: 1.35;
    color: #101828;
    min-height: 42px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .price-row {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 12px;

    .price {
      font-size: 24px;
      font-weight: 700;
      color: #d92d20;
      line-height: 1;
    }

    .compare-price {
      font-size: 14px;
      color: #98a2b3;
      text-decoration: line-through;
      line-height: 1;
    }

    .save {
      font-size: 13px;
      font-weight: 600;
      color: #d92d20;
      line-height: 1;
    }
  }

  .action-btn {
    width: 100%;
    background: #101828;
    color: #fff;
    border-radius: 999px;
    padding: 11px 14px;
    font-size: 14px;
    font-weight: 600;
    margin-bottom: 12px;
    transition: background-color 0.2s ease;

    &:hover {
      background: #1f2937;
    }
  }

  .spec-grid {
    border-top: 1px solid #e6e9ef;
    margin-top: auto;
    padding-top: 12px;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 10px 8px;
  }

  .spec-item {
    min-height: 40px;
  }

  .spec-value {
    display: block;
    font-size: 13px;
    font-weight: 700;
    color: #101828;
    line-height: 1.2;
  }

  .spec-label {
    display: block;
    font-size: 11px;
    color: #667085;
    margin-top: 3px;
    line-height: 1.2;
  }
}
</style>
