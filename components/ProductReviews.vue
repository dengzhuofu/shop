<template>
  <div class="product-reviews" id="customer-reviews">
    <h2 class="reviews-title">Customer Reviews</h2>

    <!-- Summary Section -->
    <div class="reviews-summary">
      <div class="summary-left">
        <div class="average-stars">
          <StarIcon v-for="i in 5" :key="'avg-star-' + i" 
                    :class="['star-icon', { filled: i <= Math.round(summary.averageRating) }]" />
          <span class="average-score">{{ summary.averageRating.toFixed(2) }} out of 5</span>
        </div>
        <p class="based-on">Based on {{ summary.totalReviews }} reviews</p>
      </div>

      <div class="summary-middle">
        <div class="rating-bar-row" v-for="star in 5" :key="'bar-' + star">
          <div class="stars-label">
            <StarIcon v-for="i in 5" :key="'label-star-' + star + '-' + i" 
                      :class="['star-icon small', { filled: i <= (6 - star) }]" />
          </div>
          <div class="progress-bar">
            <div class="progress-fill" 
                 :style="{ width: summary.totalReviews ? ((summary.ratingDistribution[6 - star] || 0) / summary.totalReviews * 100) + '%' : '0%' }">
            </div>
          </div>
          <span class="count-label">{{ summary.ratingDistribution[6 - star] || 0 }}</span>
        </div>
      </div>

      <div class="summary-right">
        <button class="write-review-btn">Write a review</button>
      </div>
    </div>

    <!-- Customer Photos -->
    <div class="customer-photos" v-if="allPhotos.length > 0">
      <h3 class="photos-title">Customer photos & videos</h3>
      <div class="photos-list">
        <img v-for="(photo, index) in allPhotos.slice(0, 7)" :key="'photo-' + index" 
             :src="photo" alt="Customer photo" class="photo-thumbnail" />
        <a href="#" class="see-more" v-if="allPhotos.length > 7">See<br>more</a>
      </div>
    </div>

    <!-- Sorting -->
    <div class="reviews-actions">
      <div class="sort-dropdown">
        <select>
          <option>Pictures First</option>
          <option>Most Recent</option>
          <option>Highest Rating</option>
        </select>
      </div>
    </div>

    <!-- Review List -->
    <div class="reviews-list">
      <div class="review-item" v-for="review in reviews" :key="review.id">
        <div class="review-header">
          <div class="review-stars">
            <StarIcon v-for="i in 5" :key="'review-star-' + review.id + '-' + i" 
                      :class="['star-icon', { filled: i <= review.rating }]" />
          </div>
          <div class="reviewer-info">
            <UserIcon class="user-avatar" />
            <span class="reviewer-name">{{ review.userName }}</span>
            <span class="verified-badge" v-if="review.verifiedPurchase">Verified</span>
          </div>
        </div>
        
        <h4 class="review-title">{{ review.title }}</h4>
        <p class="review-content">{{ review.content }}</p>
        
        <div class="review-images" v-if="review.images && review.images.length > 0">
          <img v-for="(img, imgIndex) in review.images" :key="'rev-img-' + review.id + '-' + imgIndex" 
               :src="img" alt="Review image" class="review-image" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { StarIcon, UserIcon } from 'lucide-vue-next'

const props = defineProps({
  summary: {
    type: Object,
    default: () => ({
      averageRating: 0,
      totalReviews: 0,
      ratingDistribution: { 5: 0, 4: 0, 3: 0, 2: 0, 1: 0 }
    })
  },
  reviews: {
    type: Array,
    default: () => []
  }
})

const allPhotos = computed(() => {
  let photos = []
  props.reviews.forEach(review => {
    if (review.images && review.images.length > 0) {
      photos = [...photos, ...review.images]
    }
  })
  return photos
})
</script>

<style lang="scss" scoped>
.product-reviews {
  margin-top: 60px;
  padding-top: 40px;
  border-top: 1px solid #eaeaea;

  .reviews-title {
    text-align: center;
    font-size: 24px;
    font-weight: 700;
    margin-bottom: 40px;
    color: #111;
  }
}

.reviews-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 900px;
  margin: 0 auto 40px;
  flex-wrap: wrap;
  gap: 30px;

  .summary-left {
    text-align: center;
    .average-stars {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 4px;
      margin-bottom: 8px;
      
      .star-icon {
        width: 20px;
        height: 20px;
        color: #d1d5db;
        fill: transparent;
        &.filled {
          color: #f59e0b;
          fill: #f59e0b;
        }
      }

      .average-score {
        font-size: 16px;
        font-weight: 600;
        margin-left: 8px;
        color: #111;
      }
    }

    .based-on {
      font-size: 14px;
      color: #666;
    }
  }

  .summary-middle {
    flex: 1;
    min-width: 300px;

    .rating-bar-row {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 6px;

      .stars-label {
        display: flex;
        width: 70px;
        justify-content: flex-end;
        .star-icon.small {
          width: 12px;
          height: 12px;
          color: #d1d5db;
          fill: transparent;
          &.filled {
            color: #f59e0b;
            fill: #f59e0b;
          }
        }
      }

      .progress-bar {
        flex: 1;
        height: 10px;
        background-color: #f3f4f6;
        border-radius: 5px;
        overflow: hidden;

        .progress-fill {
          height: 100%;
          background-color: #fbbf24;
          border-radius: 5px;
        }
      }

      .count-label {
        width: 30px;
        font-size: 12px;
        color: #666;
      }
    }
  }

  .summary-right {
    .write-review-btn {
      background-color: #000;
      color: #fff;
      padding: 12px 32px;
      border-radius: 4px;
      font-weight: 600;
      font-size: 14px;
      border: none;
      cursor: pointer;
      transition: background-color 0.2s;

      &:hover {
        background-color: #333;
      }
    }
  }
}

.customer-photos {
  max-width: 900px;
  margin: 0 auto 40px;
  padding: 20px 0;
  border-top: 1px solid #eaeaea;
  border-bottom: 1px solid #eaeaea;

  .photos-title {
    font-size: 14px;
    font-weight: 600;
    color: #666;
    margin-bottom: 16px;
  }

  .photos-list {
    display: flex;
    gap: 10px;
    overflow-x: auto;
    padding-bottom: 10px;

    .photo-thumbnail {
      width: 80px;
      height: 80px;
      object-fit: cover;
      border-radius: 4px;
      cursor: pointer;
    }

    .see-more {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 80px;
      height: 80px;
      background-color: #f9fafb;
      border-radius: 4px;
      font-size: 12px;
      color: #666;
      text-decoration: underline;
      text-align: center;
    }
  }
}

.reviews-actions {
  max-width: 900px;
  margin: 0 auto 20px;
  
  .sort-dropdown select {
    padding: 8px 12px;
    border: none;
    background: transparent;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    outline: none;
  }
}

.reviews-list {
  max-width: 900px;
  margin: 0 auto;

  .review-item {
    padding: 24px 0;
    border-bottom: 1px solid #eaeaea;

    &:last-child {
      border-bottom: none;
    }

    .review-header {
      margin-bottom: 12px;

      .review-stars {
        display: flex;
        gap: 2px;
        margin-bottom: 8px;

        .star-icon {
          width: 16px;
          height: 16px;
          color: #d1d5db;
          fill: transparent;
          &.filled {
            color: #f59e0b;
            fill: #f59e0b;
          }
        }
      }

      .reviewer-info {
        display: flex;
        align-items: center;
        gap: 8px;

        .user-avatar {
          width: 20px;
          height: 20px;
          color: #666;
          background: #f3f4f6;
          border-radius: 50%;
          padding: 2px;
        }

        .reviewer-name {
          font-size: 14px;
          font-weight: 600;
          color: #111;
        }

        .verified-badge {
          background-color: #000;
          color: #fff;
          font-size: 10px;
          padding: 2px 6px;
          border-radius: 2px;
          font-weight: bold;
        }
      }
    }

    .review-title {
      font-size: 16px;
      font-weight: 700;
      color: #111;
      margin-bottom: 8px;
    }

    .review-content {
      font-size: 14px;
      line-height: 1.6;
      color: #4b5563;
      margin-bottom: 16px;
    }

    .review-images {
      display: flex;
      gap: 10px;

      .review-image {
        width: 100px;
        height: 100px;
        object-fit: cover;
        border-radius: 4px;
        cursor: pointer;
      }
    }
  }
}

@media (max-width: 768px) {
  .reviews-summary {
    flex-direction: column;
    align-items: center;
    .summary-left, .summary-middle, .summary-right {
      width: 100%;
    }
  }
}
</style>