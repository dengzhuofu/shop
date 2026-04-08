<template>
  <section
    v-if="story"
    ref="featureSectionRef"
    class="feature-showcase"
    :class="{ 'is-visible': hasEnteredView }"
  >
    <div class="feature-copy">
      <span class="feature-eyebrow">{{ story.eyebrow }}</span>
      <h2 class="feature-title">{{ story.title }}</h2>
      <p class="feature-subtitle">{{ story.subtitle }}</p>
    </div>

    <article class="feature-hero">
      <img
        :src="story.heroImage"
        :alt="story.title"
        class="feature-hero-image motion-image motion-center"
        :style="heroMotionStyle"
      />
      <div class="feature-hero-overlay" />
      <div class="feature-hero-content">
        <div class="feature-hero-text">
          <span class="hero-kicker">S Nova Story</span>
          <h3>Commuter comfort that still feels distinctive.</h3>
          <p>
            The reference page leans on oversized marketing visuals. This section restores that rhythm so the detail page
            does not end right after the buy box.
          </p>
        </div>

        <div class="feature-metrics">
          <div v-for="metric in story.heroMetrics" :key="metric.label" class="metric-card">
            <span class="metric-value">{{ metric.value }}</span>
            <span class="metric-label">{{ metric.label }}</span>
          </div>
        </div>
      </div>
    </article>

    <div class="feature-panel-grid">
      <article
        v-for="panel in story.panels"
        :key="panel.id"
        class="feature-panel"
        :class="[
          panel.size ? `is-${panel.size}` : 'is-standard',
          panel.tone ? `tone-${panel.tone}` : 'tone-light',
        ]"
      >
        <img
          v-if="panel.image"
          :src="panel.image"
          :alt="panel.title"
          class="feature-panel-image motion-image"
          :style="panelMotionStyle(panel.id)"
        />
        <div class="feature-panel-overlay" />
        <div class="feature-panel-body">
          <h3>{{ panel.title }}</h3>
          <p>{{ panel.body }}</p>

          <div v-if="panel.metrics?.length" class="panel-metrics">
            <div v-for="metric in panel.metrics" :key="metric.label" class="panel-metric">
              <span class="panel-metric-value">{{ metric.value }}</span>
              <span class="panel-metric-label">{{ metric.label }}</span>
            </div>
          </div>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import type { FeatureStory } from '~/utils/productFeatureStory'

const props = defineProps<{
  story: FeatureStory | null
}>()

const featureSectionRef = ref<HTMLElement | null>(null)
const hasEnteredView = ref(false)

const motionPresets = [
  { x: -84, y: 42, delay: 0.08 },
  { x: 28, y: 72, delay: 0.14 },
  { x: 92, y: 28, delay: 0.2 },
  { x: -56, y: 88, delay: 0.26 },
  { x: 54, y: 82, delay: 0.32 },
  { x: 0, y: 64, delay: 0.38 },
] as const

const heroMotionStyle = computed(() => ({
  '--enter-x': '0px',
  '--enter-y': '58px',
  '--enter-scale': '1.1',
  '--enter-delay': '0s',
}))

const panelMotionStyle = (panelId: string) => {
  const panels = props.story?.panels || []
  const index = Math.max(
    panels.findIndex((panel) => panel.id === panelId),
    0,
  )
  const preset = motionPresets[index % motionPresets.length]
  return {
    '--enter-x': `${preset.x}px`,
    '--enter-y': `${preset.y}px`,
    '--enter-scale': '1.12',
    '--enter-delay': `${preset.delay}s`,
  }
}

let sectionObserver: IntersectionObserver | null = null

onMounted(() => {
  if (!process.client || !featureSectionRef.value) return

  sectionObserver = new IntersectionObserver(
    ([entry]) => {
      if (!entry?.isIntersecting) return
      hasEnteredView.value = true
      sectionObserver?.disconnect()
      sectionObserver = null
    },
    {
      threshold: 0.22,
      rootMargin: '0px 0px -10% 0px',
    },
  )

  sectionObserver.observe(featureSectionRef.value)
})

onBeforeUnmount(() => {
  sectionObserver?.disconnect()
  sectionObserver = null
})
</script>

<style scoped lang="scss">
.feature-showcase {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.motion-image {
  transform:
    translate3d(var(--enter-x, 0px), var(--enter-y, 0px), 0)
    scale(var(--enter-scale, 1.08));
  opacity: 0.01;
  filter: blur(12px);
  transition:
    transform 0.95s cubic-bezier(0.22, 1, 0.36, 1),
    opacity 0.7s ease,
    filter 0.85s ease;
  transition-delay: var(--enter-delay, 0s);
  will-change: transform, opacity, filter;
}

.feature-showcase.is-visible .motion-image {
  transform: translate3d(0, 0, 0) scale(1);
  opacity: 1;
  filter: blur(0);
}

.feature-copy {
  max-width: 760px;
}

.feature-eyebrow {
  display: inline-flex;
  align-items: center;
  padding: 7px 14px;
  border-radius: 999px;
  background: rgba(71, 160, 255, 0.12);
  color: #0f5ea8;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.feature-title {
  margin: 16px 0 10px;
  font-size: clamp(32px, 4vw, 52px);
  line-height: 0.98;
  letter-spacing: -0.04em;
  color: #0c111d;
}

.feature-subtitle {
  margin: 0;
  color: #526073;
  font-size: 17px;
  line-height: 1.7;
}

.feature-hero,
.feature-panel {
  position: relative;
  overflow: hidden;
  border-radius: 32px;
}

.feature-hero {
  min-height: 600px;
  background: #091324;
}

.feature-hero-image,
.feature-panel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.feature-hero-image {
  position: absolute;
  inset: 0;
}

.feature-hero-overlay {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, rgba(7, 13, 28, 0.86) 0%, rgba(7, 13, 28, 0.46) 36%, rgba(7, 13, 28, 0.12) 100%),
    radial-gradient(circle at top right, rgba(110, 197, 255, 0.28), transparent 42%);
}

.feature-hero-content {
  position: relative;
  z-index: 1;
  min-height: 600px;
  padding: 42px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 40px;
}

.feature-hero-text {
  max-width: 520px;
  color: #f5f7fb;
}

.hero-kicker {
  display: inline-block;
  margin-bottom: 16px;
  color: #8fd0ff;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.2em;
  text-transform: uppercase;
}

.feature-hero-text h3 {
  margin: 0 0 14px;
  font-size: clamp(32px, 4vw, 58px);
  line-height: 0.95;
  letter-spacing: -0.05em;
}

.feature-hero-text p {
  margin: 0;
  color: rgba(245, 247, 251, 0.82);
  font-size: 16px;
  line-height: 1.7;
}

.feature-metrics {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  width: 100%;
}

.metric-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 18px 20px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.14);
  color: #f5f7fb;
}

.metric-value {
  font-size: 22px;
  font-weight: 800;
  letter-spacing: -0.03em;
}

.metric-label {
  font-size: 12px;
  font-weight: 700;
  color: rgba(245, 247, 251, 0.68);
  text-transform: uppercase;
  letter-spacing: 0.12em;
}

.feature-panel-grid {
  display: grid;
  grid-template-columns: repeat(12, minmax(0, 1fr));
  gap: 18px;
}

.feature-panel {
  min-height: 340px;
  grid-column: span 4;
  background: #f4f7fb;
}

.feature-panel.is-wide {
  min-height: 420px;
  grid-column: span 12;
}

.feature-panel.is-tall {
  min-height: 460px;
  grid-column: span 5;
}

.feature-panel-image {
  position: absolute;
  inset: 0;
}

.feature-panel:hover .motion-image,
.feature-hero:hover .motion-image {
  transform: scale(1.03);
}

.feature-panel-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(12, 17, 29, 0.06) 0%, rgba(12, 17, 29, 0.72) 100%);
}

.feature-panel-body {
  position: relative;
  z-index: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  gap: 14px;
  padding: 28px;
  color: #f7fafc;
}

.feature-panel.tone-light .feature-panel-body {
  color: #0d1524;
}

.feature-panel.tone-light .feature-panel-overlay {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.1) 0%, rgba(244, 248, 252, 0.9) 100%);
}

.feature-panel.tone-accent .feature-panel-overlay {
  background:
    linear-gradient(180deg, rgba(8, 20, 38, 0.2) 0%, rgba(6, 15, 32, 0.78) 100%),
    linear-gradient(135deg, rgba(63, 174, 255, 0.36) 0%, rgba(113, 109, 255, 0.14) 100%);
}

.feature-panel h3 {
  margin: 0;
  font-size: clamp(24px, 2.5vw, 34px);
  line-height: 1;
  letter-spacing: -0.04em;
}

.feature-panel p {
  margin: 0;
  max-width: 560px;
  font-size: 15px;
  line-height: 1.7;
}

.panel-metrics {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.panel-metric {
  min-width: 110px;
  padding: 12px 14px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(10px);
}

.feature-panel.tone-light .panel-metric {
  background: rgba(15, 23, 42, 0.08);
}

.panel-metric-value {
  display: block;
  font-size: 16px;
  font-weight: 800;
}

.panel-metric-label {
  display: block;
  margin-top: 4px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  opacity: 0.72;
}

@media (max-width: 1024px) {
  .feature-hero {
    min-height: 520px;
  }

  .feature-hero-content {
    min-height: 520px;
    padding: 32px;
  }

  .feature-metrics {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .feature-panel,
  .feature-panel.is-tall,
  .feature-panel.is-wide {
    grid-column: span 12;
    min-height: 320px;
  }
}

@media (max-width: 640px) {
  .feature-title {
    font-size: 34px;
  }

  .feature-subtitle {
    font-size: 15px;
  }

  .feature-hero {
    min-height: 440px;
  }

  .feature-hero-content {
    min-height: 440px;
    padding: 24px;
    gap: 24px;
  }

  .feature-metrics {
    grid-template-columns: 1fr;
  }

  .feature-panel-body {
    padding: 22px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .motion-image {
    transform: none;
    opacity: 1;
    filter: none;
    transition: none;
  }
}
</style>
