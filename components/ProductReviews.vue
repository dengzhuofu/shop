<template>
  <section class="product-reviews" aria-labelledby="customer-reviews-title">
    <div class="reviews-shell">
      <h2 id="customer-reviews-title" class="reviews-title">Customer Reviews</h2>

      <div class="reviews-summary">
        <div class="summary-panel average-panel">
          <div class="average-stars">
            <StarIcon
              v-for="star in 5"
              :key="`summary-star-${star}`"
              class="star-icon"
              :class="{ filled: star <= roundedAverage }"
            />
          </div>
          <p class="average-score">{{ averageRatingText }} out of 5</p>
          <p class="based-on">Based on {{ summary.totalReviews }} reviews</p>
        </div>

        <div class="summary-panel distribution-panel">
          <div v-for="star in [5, 4, 3, 2, 1]" :key="`dist-${star}`" class="rating-bar-row">
            <div class="stars-label">
              <StarIcon
                v-for="icon in 5"
                :key="`dist-star-${star}-${icon}`"
                class="mini-star"
                :class="{ filled: icon <= star }"
              />
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: `${distributionPercent(star)}%` }" />
            </div>
            <span class="count-label">{{ summary.ratingDistribution?.[star] || 0 }}</span>
          </div>
        </div>

        <div class="summary-panel summary-cta">
          <button type="button" class="write-review-btn" @click="openComposer">
            Write a review
          </button>
          <p class="summary-note">Share your setup, photos, and ride impressions.</p>
        </div>
      </div>

      <div v-if="allPhotos.length" class="customer-media">
        <p class="media-title">Customer photos &amp; videos</p>
        <div class="media-strip">
          <button
            v-for="(photo, index) in visiblePhotos"
            :key="`photo-${index}`"
            type="button"
            class="media-thumb"
            @click="activePreview = photo"
          >
            <img :src="photo" alt="Customer review media" />
          </button>
          <button
            v-if="allPhotos.length > visiblePhotos.length"
            type="button"
            class="media-more"
            @click="showAllPhotos = !showAllPhotos"
          >
            {{ showAllPhotos ? 'Show less' : 'See more' }}
          </button>
        </div>
      </div>

      <div class="reviews-toolbar">
        <label class="sort-label">
          <span>Sort by</span>
          <select v-model="sortBy" class="sort-select">
            <option value="pictures">Pictures First</option>
            <option value="recent">Most Recent</option>
            <option value="highest">Highest Rating</option>
          </select>
        </label>
      </div>

      <div v-if="loading && !reviews.length" class="reviews-empty">
        Loading reviews...
      </div>

      <div v-else-if="!sortedReviews.length" class="reviews-empty">
        No reviews yet. Be the first to share how your ride feels.
      </div>

      <div v-else class="reviews-list">
        <article v-for="review in sortedReviews" :key="review.id" class="review-card">
          <div class="review-stars">
            <StarIcon
              v-for="star in 5"
              :key="`review-star-${review.id}-${star}`"
              class="star-icon small"
              :class="{ filled: star <= review.rating }"
            />
          </div>

          <div class="review-meta">
            <div class="avatar-pill">{{ reviewerInitial(review.userName) }}</div>
            <div class="review-meta-text">
              <div class="reviewer-line">
                <span class="reviewer-name">{{ review.userName }}</span>
                <span v-if="review.verifiedPurchase" class="verified-badge">Verified</span>
              </div>
              <span class="review-date">{{ formatDate(review.createTime) }}</span>
            </div>
          </div>

          <h3 v-if="review.title" class="review-title">{{ review.title }}</h3>
          <p class="review-content">{{ review.content }}</p>

          <div v-if="review.images?.length" class="review-images">
            <button
              v-for="(image, imageIndex) in review.images"
              :key="`review-image-${review.id}-${imageIndex}`"
              type="button"
              class="review-image-btn"
              @click="activePreview = image"
            >
              <img :src="image" :alt="`${review.userName} review image ${imageIndex + 1}`" class="review-image" />
            </button>
          </div>

          <div v-if="review.merchantReply" class="merchant-reply">
            <p class="merchant-title">&gt;&gt; iSinwheel Official Store replied:</p>
            <p class="merchant-body">{{ review.merchantReply }}</p>
          </div>
        </article>
      </div>

      <div v-if="hasMore && !loading" class="load-more-row">
        <button type="button" class="load-more-btn" @click="loadMore">
          Load more reviews
        </button>
      </div>
    </div>

    <Teleport to="body">
      <div v-if="composerOpen" class="review-modal" @click.self="composerOpen = false">
        <div class="review-modal-card">
          <div class="modal-header">
            <div>
              <p class="modal-eyebrow">Write a review</p>
              <h3>{{ productTitle }}</h3>
            </div>
            <button type="button" class="modal-close" @click="composerOpen = false">
              <XIcon />
            </button>
          </div>

          <form class="review-form" @submit.prevent="submitReview">
            <label class="field">
              <span>Rating</span>
              <div class="rating-picker">
                <button
                  v-for="star in 5"
                  :key="`picker-${star}`"
                  type="button"
                  class="rating-star-btn"
                  :class="{ active: star <= draft.rating }"
                  @click="draft.rating = star"
                >
                  <StarIcon class="rating-star-icon" />
                </button>
              </div>
            </label>

            <label class="field">
              <span>Title</span>
              <input v-model="draft.title" type="text" maxlength="255" placeholder="Summarize your ride in a few words" />
            </label>

            <label class="field">
              <span>Review</span>
              <textarea
                v-model="draft.content"
                rows="5"
                maxlength="2000"
                placeholder="How does the scooter feel on daily rides, pavement cracks, hills, or evening commutes?"
              />
            </label>

            <div class="field">
              <div class="field-heading">
                <span>Photo URLs</span>
                <button
                  v-if="draft.images.length < 4"
                  type="button"
                  class="inline-action"
                  @click="draft.images.push('')"
                >
                  Add one
                </button>
              </div>
              <div class="image-inputs">
                <div v-for="(_, index) in draft.images" :key="`draft-image-${index}`" class="image-input-row">
                  <input
                    v-model="draft.images[index]"
                    type="url"
                    maxlength="600"
                    placeholder="https://example.com/your-photo.jpg"
                  />
                  <button
                    v-if="draft.images.length > 1"
                    type="button"
                    class="remove-image-btn"
                    @click="draft.images.splice(index, 1)"
                  >
                    Remove
                  </button>
                </div>
              </div>
            </div>

            <div class="modal-actions">
              <button type="button" class="secondary-btn" @click="composerOpen = false">
                Cancel
              </button>
              <button type="submit" class="primary-btn" :disabled="submitting">
                {{ submitting ? 'Publishing...' : 'Publish review' }}
              </button>
            </div>
          </form>
        </div>
      </div>

      <div v-if="activePreview" class="preview-lightbox" @click.self="activePreview = ''">
        <div class="preview-shell">
          <button type="button" class="preview-close" @click="activePreview = ''">
            <XIcon />
          </button>
          <img :src="activePreview" alt="Customer review preview" class="preview-image" />
        </div>
      </div>
    </Teleport>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { StarIcon, XIcon } from 'lucide-vue-next'

interface ReviewSummary {
  averageRating: number
  totalReviews: number
  ratingDistribution: Record<number, number>
}

interface ReviewItem {
  id: number
  userName: string
  rating: number
  title?: string | null
  content: string
  images: string[]
  verifiedPurchase: boolean
  createTime: string
  merchantReply?: string | null
}

const props = defineProps<{
  productId: number | null
  productTitle: string
}>()

const emit = defineEmits<{
  (event: 'summary-change', payload: ReviewSummary): void
}>()

const session = useShopSession()
const loading = ref(false)
const submitting = ref(false)
const composerOpen = ref(false)
const showAllPhotos = ref(false)
const activePreview = ref('')
const sortBy = ref<'pictures' | 'recent' | 'highest'>('pictures')
const currentPage = ref(1)
const pageSize = 12
const totalReviewsCount = ref(0)
const reviews = ref<ReviewItem[]>([])
const summary = ref<ReviewSummary>({
  averageRating: 0,
  totalReviews: 0,
  ratingDistribution: { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 },
})

const draft = reactive({
  rating: 5,
  title: '',
  content: '',
  images: [''],
})

const roundedAverage = computed(() => Math.round(summary.value.averageRating || 0))
const averageRatingText = computed(() => Number(summary.value.averageRating || 0).toFixed(2))
const hasMore = computed(() => reviews.value.length < totalReviewsCount.value)

const allPhotos = computed(() =>
  reviews.value.flatMap((review) => (Array.isArray(review.images) ? review.images : [])),
)

const visiblePhotos = computed(() => (showAllPhotos.value ? allPhotos.value : allPhotos.value.slice(0, 7)))

const sortedReviews = computed(() => {
  const list = [...reviews.value]
  if (sortBy.value === 'highest') {
    return list.sort((a, b) => (b.rating - a.rating) || compareDate(b.createTime, a.createTime))
  }
  if (sortBy.value === 'recent') {
    return list.sort((a, b) => compareDate(b.createTime, a.createTime))
  }
  return list.sort((a, b) => {
    const imageDelta = (b.images?.length || 0) - (a.images?.length || 0)
    if (imageDelta !== 0) {
      return imageDelta
    }
    return compareDate(b.createTime, a.createTime)
  })
})

const compareDate = (left: string, right: string) =>
  new Date(left || 0).getTime() - new Date(right || 0).getTime()

const distributionPercent = (star: number) => {
  if (!summary.value.totalReviews) {
    return 0
  }
  return ((summary.value.ratingDistribution?.[star] || 0) / summary.value.totalReviews) * 100
}

const reviewerInitial = (value: string) =>
  String(value || '?')
    .trim()
    .charAt(0)
    .toUpperCase()

const formatDate = (value: string) => {
  if (!value) {
    return 'Just now'
  }

  try {
    return new Intl.DateTimeFormat('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    }).format(new Date(value))
  } catch {
    return value
  }
}

const resetDraft = () => {
  draft.rating = 5
  draft.title = ''
  draft.content = ''
  draft.images = ['']
}

const loadSummary = async () => {
  if (!props.productId) {
    return
  }

  const res = await useHttp(`/api/review/product/${props.productId}/summary`, {
    showError: false,
  })
  if (res?.code !== 200) {
    return
  }

  summary.value = {
    averageRating: Number(res.data?.averageRating || 0),
    totalReviews: Number(res.data?.totalReviews || 0),
    ratingDistribution: {
      1: Number(res.data?.ratingDistribution?.[1] || 0),
      2: Number(res.data?.ratingDistribution?.[2] || 0),
      3: Number(res.data?.ratingDistribution?.[3] || 0),
      4: Number(res.data?.ratingDistribution?.[4] || 0),
      5: Number(res.data?.ratingDistribution?.[5] || 0),
    },
  }

  emit('summary-change', summary.value)
}

const loadReviews = async (page = 1, append = false) => {
  if (!props.productId) {
    reviews.value = []
    totalReviewsCount.value = 0
    return
  }

  loading.value = true
  try {
    const res = await useHttp(`/api/review/product/${props.productId}`, {
      query: {
        pageNum: page,
        pageSize,
      },
      showError: false,
    })
    if (res?.code !== 200) {
      return
    }

    const records = Array.isArray(res.data?.records) ? res.data.records : []
    reviews.value = append ? [...reviews.value, ...records] : records
    totalReviewsCount.value = Number(res.data?.total || records.length || 0)
    currentPage.value = page
  } finally {
    loading.value = false
  }
}

const refresh = async () => {
  await Promise.all([loadSummary(), loadReviews(1, false)])
}

const loadMore = async () => {
  if (!hasMore.value || loading.value) {
    return
  }
  await loadReviews(currentPage.value + 1, true)
}

const openComposer = async () => {
  await session.fetchMe()
  if (!session.isLoggedIn.value) {
    await navigateTo('/login')
    return
  }
  composerOpen.value = true
}

const submitReview = async () => {
  if (!props.productId || submitting.value) {
    return
  }

  submitting.value = true
  try {
    const payload = {
      rating: draft.rating,
      title: draft.title.trim(),
      content: draft.content.trim(),
      images: draft.images.map((item) => item.trim()).filter(Boolean),
    }

    const res = await useHttp(`/api/review/product/${props.productId}`, {
      method: 'POST',
      body: payload,
      successMsg: 'Review published',
      handleAuthError: false,
    })

    if (res?.code === 200) {
      composerOpen.value = false
      resetDraft()
      await refresh()
    }
  } finally {
    submitting.value = false
  }
}

watch(
  () => props.productId,
  async () => {
    if (!process.client) {
      return
    }
    showAllPhotos.value = false
    activePreview.value = ''
    await refresh()
  },
  { immediate: true },
)

onMounted(async () => {
  await session.fetchMe()
})
</script>

<style scoped lang="scss">
.product-reviews {
  padding: 18px 0 0;
}

.reviews-shell {
  border-top: 1px solid #e8edf4;
  padding-top: 42px;
}

.reviews-title {
  margin: 0 0 28px;
  text-align: center;
  font-size: clamp(30px, 3vw, 40px);
  line-height: 1;
  letter-spacing: -0.04em;
  color: #0c111d;
}

.reviews-summary {
  display: grid;
  grid-template-columns: 1.1fr 1.6fr 1fr;
  gap: 24px;
  align-items: stretch;
}

.summary-panel {
  padding: 28px;
  border-radius: 28px;
  border: 1px solid #e5ecf4;
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
}

.average-panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
}

.average-stars,
.review-stars {
  display: flex;
  gap: 4px;
}

.star-icon,
.mini-star {
  color: #d4dbe6;
  fill: transparent;
}

.star-icon {
  width: 19px;
  height: 19px;
}

.star-icon.small {
  width: 16px;
  height: 16px;
}

.mini-star {
  width: 12px;
  height: 12px;
}

.star-icon.filled,
.mini-star.filled {
  color: #f4b843;
  fill: #f4b843;
}

.average-score {
  margin: 14px 0 6px;
  font-size: 22px;
  font-weight: 800;
  color: #0c111d;
  letter-spacing: -0.03em;
}

.based-on,
.summary-note {
  margin: 0;
  color: #607086;
  font-size: 14px;
  line-height: 1.7;
}

.distribution-panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
}

.rating-bar-row {
  display: grid;
  grid-template-columns: 72px 1fr 34px;
  align-items: center;
  gap: 14px;
}

.stars-label {
  display: flex;
  justify-content: flex-end;
  gap: 1px;
}

.progress-bar {
  height: 10px;
  border-radius: 999px;
  background: #e7edf4;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 999px;
  background: linear-gradient(90deg, #f0b548 0%, #ffd98e 100%);
}

.count-label {
  color: #536479;
  font-size: 13px;
  font-weight: 700;
}

.summary-cta {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 12px;
  background:
    radial-gradient(circle at top right, rgba(121, 188, 255, 0.2), transparent 42%),
    linear-gradient(180deg, #0e1628 0%, #131f37 100%);
  border-color: transparent;
}

.write-review-btn,
.primary-btn,
.load-more-btn {
  min-height: 52px;
  padding: 0 24px;
  border: none;
  border-radius: 999px;
  background: #111;
  color: #fff;
  font-size: 14px;
  font-weight: 800;
  cursor: pointer;
  transition:
    transform 0.2s ease,
    opacity 0.2s ease,
    background 0.2s ease;
}

.summary-cta .write-review-btn {
  background: #fff;
  color: #101626;
}

.write-review-btn:hover,
.primary-btn:hover,
.load-more-btn:hover {
  transform: translateY(-1px);
}

.summary-cta .summary-note {
  color: rgba(255, 255, 255, 0.74);
}

.customer-media {
  margin-top: 26px;
  padding: 24px 0 0;
  border-top: 1px solid #e8edf4;
}

.media-title {
  margin: 0 0 16px;
  color: #69788b;
  font-size: 14px;
  font-weight: 700;
}

.media-strip {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 10px;
}

.media-thumb,
.media-more,
.review-image-btn {
  border: none;
  background: none;
  padding: 0;
  cursor: pointer;
}

.media-thumb img {
  width: 96px;
  height: 96px;
  object-fit: cover;
  border-radius: 18px;
  display: block;
}

.media-more {
  min-width: 96px;
  min-height: 96px;
  padding: 16px;
  border-radius: 18px;
  background: #f2f6fb;
  color: #0d1524;
  font-size: 13px;
  font-weight: 700;
}

.reviews-toolbar {
  margin-top: 26px;
  padding-top: 20px;
  border-top: 1px solid #e8edf4;
}

.sort-label {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  color: #5b6b80;
  font-size: 14px;
  font-weight: 700;
}

.sort-select {
  min-height: 42px;
  padding: 0 16px;
  border-radius: 999px;
  border: 1px solid #dde6f0;
  background: #fff;
  color: #0d1524;
  font-weight: 700;
}

.reviews-empty {
  padding: 40px 0 8px;
  color: #607086;
  font-size: 15px;
}

.reviews-list {
  display: flex;
  flex-direction: column;
}

.review-card {
  padding: 28px 0;
  border-bottom: 1px solid #e8edf4;
}

.review-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 10px;
}

.avatar-pill {
  width: 42px;
  height: 42px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: linear-gradient(180deg, #eff4fb 0%, #e7edf6 100%);
  color: #0d1524;
  font-size: 15px;
  font-weight: 800;
}

.review-meta-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.reviewer-line {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.reviewer-name {
  color: #111827;
  font-size: 15px;
  font-weight: 700;
}

.verified-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 8px;
  border-radius: 999px;
  background: #111827;
  color: #fff;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.review-date {
  color: #6d7a8d;
  font-size: 13px;
}

.review-title {
  margin: 18px 0 8px;
  color: #0c111d;
  font-size: 20px;
  line-height: 1.2;
  letter-spacing: -0.03em;
}

.review-content,
.merchant-body {
  margin: 0;
  color: #46566b;
  font-size: 15px;
  line-height: 1.8;
  white-space: pre-line;
}

.review-images {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
}

.review-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 16px;
  display: block;
}

.merchant-reply {
  margin-top: 18px;
  padding: 18px 20px;
  border-radius: 22px;
  background: #f2f4f7;
}

.merchant-title {
  margin: 0 0 8px;
  color: #111827;
  font-size: 13px;
  font-weight: 800;
}

.load-more-row {
  display: flex;
  justify-content: center;
  padding-top: 24px;
}

.review-modal,
.preview-lightbox {
  position: fixed;
  inset: 0;
  z-index: 220;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: rgba(8, 15, 28, 0.66);
  backdrop-filter: blur(6px);
}

.review-modal-card,
.preview-shell {
  width: min(680px, 100%);
  max-height: calc(100vh - 48px);
  overflow: auto;
  border-radius: 28px;
  background: #fff;
  box-shadow: 0 30px 90px rgba(7, 16, 32, 0.28);
}

.preview-shell {
  width: min(920px, 100%);
  padding: 20px;
  position: relative;
}

.preview-image {
  width: 100%;
  max-height: calc(100vh - 120px);
  object-fit: contain;
  border-radius: 20px;
  display: block;
}

.modal-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
  padding: 28px 28px 0;
}

.modal-eyebrow {
  margin: 0 0 8px;
  color: #1b6fb9;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.modal-header h3 {
  margin: 0;
  color: #0c111d;
  font-size: 28px;
  line-height: 1.05;
  letter-spacing: -0.04em;
}

.modal-close,
.preview-close,
.remove-image-btn,
.inline-action,
.secondary-btn {
  border: none;
  background: none;
  cursor: pointer;
}

.modal-close,
.preview-close {
  width: 40px;
  height: 40px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: #f2f5fa;
  color: #0f1728;
}

.preview-close {
  position: absolute;
  top: 16px;
  right: 16px;
}

.review-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 24px 28px 28px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.field span,
.field-heading {
  color: #0d1524;
  font-size: 14px;
  font-weight: 800;
}

.field input,
.field textarea {
  width: 100%;
  padding: 14px 16px;
  border-radius: 18px;
  border: 1px solid #dfe8f2;
  background: #fbfdff;
  color: #0d1524;
  font: inherit;
  resize: vertical;
}

.field-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.inline-action,
.remove-image-btn,
.secondary-btn {
  color: #1b6fb9;
  font-size: 13px;
  font-weight: 700;
}

.rating-picker {
  display: flex;
  gap: 10px;
}

.rating-star-btn {
  width: 46px;
  height: 46px;
  display: grid;
  place-items: center;
  border-radius: 14px;
  border: 1px solid #dde6f0;
  background: #fff;
  cursor: pointer;
}

.rating-star-btn.active {
  border-color: rgba(244, 184, 67, 0.55);
  background: rgba(244, 184, 67, 0.12);
}

.rating-star-icon {
  width: 20px;
  height: 20px;
  color: #d6deea;
  fill: transparent;
}

.rating-star-btn.active .rating-star-icon {
  color: #f4b843;
  fill: #f4b843;
}

.image-inputs {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.image-input-row {
  display: flex;
  gap: 12px;
  align-items: center;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 4px;
}

.secondary-btn {
  min-height: 52px;
  padding: 0 18px;
  border-radius: 999px;
  background: #eef3f8;
  color: #0d1524;
}

.primary-btn:disabled,
.load-more-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

@media (max-width: 1024px) {
  .reviews-summary {
    grid-template-columns: 1fr;
  }

  .summary-panel {
    padding: 22px;
  }
}

@media (max-width: 640px) {
  .review-modal,
  .preview-lightbox {
    padding: 16px;
  }

  .modal-header,
  .review-form {
    padding-left: 20px;
    padding-right: 20px;
  }

  .image-input-row {
    flex-direction: column;
    align-items: stretch;
  }

  .modal-actions {
    flex-direction: column-reverse;
  }

  .primary-btn,
  .secondary-btn {
    width: 100%;
  }
}
</style>
