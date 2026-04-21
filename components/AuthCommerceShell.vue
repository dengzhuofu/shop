<template>
  <div class="auth-shell">
    <section class="auth-showcase">
      <NuxtLink to="/" class="auth-brand">iSinwheel</NuxtLink>

      <div class="auth-showcase__copy">
        <p class="auth-showcase__eyebrow">{{ heroEyebrow }}</p>
        <h1 class="auth-showcase__title">{{ heroTitle }}</h1>
        <p class="auth-showcase__body">{{ heroBody }}</p>
      </div>

      <div class="auth-showcase__media">
        <img :src="heroImage" :alt="heroImageAlt || heroTitle" class="auth-showcase__image" />
      </div>

      <div class="auth-showcase__footer">
        <ul v-if="features.length" class="auth-features">
          <li v-for="feature in features" :key="feature.title" class="auth-features__item">
            <span class="auth-features__title">{{ feature.title }}</span>
            <span class="auth-features__description">{{ feature.description }}</span>
          </li>
        </ul>

        <div v-if="metrics.length" class="auth-metrics">
          <div v-for="metric in metrics" :key="`${metric.label}-${metric.value}`" class="auth-metrics__item">
            <strong>{{ metric.value }}</strong>
            <span>{{ metric.label }}</span>
          </div>
        </div>
      </div>
    </section>

    <section class="auth-panel">
      <div class="auth-panel__top">
        <p class="auth-panel__eyebrow">{{ panelEyebrow }}</p>
        <NuxtLink to="/" class="auth-panel__store-link">{{ backToStoreText }}</NuxtLink>
      </div>

      <div class="auth-panel__body">
        <h2 class="auth-panel__title">{{ panelTitle }}</h2>
        <p class="auth-panel__subtitle">{{ panelSubtitle }}</p>
        <slot />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
interface AuthFeature {
  title: string
  description: string
}

interface AuthMetric {
  value: string
  label: string
}

withDefaults(
  defineProps<{
    heroEyebrow: string
    heroTitle: string
    heroBody: string
    heroImage: string
    heroImageAlt?: string
    features?: AuthFeature[]
    metrics?: AuthMetric[]
    panelEyebrow: string
    panelTitle: string
    panelSubtitle: string
    backToStoreText: string
  }>(),
  {
    heroImageAlt: '',
    features: () => [],
    metrics: () => [],
  },
)
</script>

<style scoped lang="scss">
.auth-shell {
  --auth-ink: #132016;
  --auth-ink-soft: rgba(19, 32, 22, 0.72);
  --auth-panel: #fbf8f2;
  --auth-panel-line: rgba(19, 32, 22, 0.08);
  --auth-accent: #7aa647;
  --auth-accent-strong: #5c8730;
  --auth-cream: #f4efe5;
  min-height: 100svh;
  height: 100svh;
  display: grid;
  grid-template-columns: minmax(0, 1.16fr) minmax(420px, 500px);
  background:
    radial-gradient(circle at top left, rgba(122, 166, 71, 0.18), transparent 32%),
    linear-gradient(135deg, #f6f1e7 0%, #efe8db 100%);
  color: var(--auth-ink);
  font-family: var(--shop-font-family-sans);
  overflow: hidden;
}

.auth-showcase {
  position: relative;
  min-height: 0;
  height: 100%;
  overflow: hidden;
  padding: clamp(28px, 4vw, 48px);
  display: grid;
  grid-template-rows: auto auto minmax(240px, 1fr) auto;
  gap: 28px;
  background:
    linear-gradient(135deg, rgba(10, 16, 12, 0.9), rgba(20, 30, 22, 0.36)),
    radial-gradient(circle at top right, rgba(122, 166, 71, 0.34), transparent 30%),
    #0f1711;
}

.auth-showcase::before {
  content: '';
  position: absolute;
  inset: 22px 22px auto auto;
  width: clamp(120px, 14vw, 180px);
  aspect-ratio: 1;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  opacity: 0.7;
}

.auth-showcase::after {
  content: '';
  position: absolute;
  inset: auto auto 24px 24px;
  width: clamp(140px, 16vw, 220px);
  height: 1px;
  background: linear-gradient(90deg, rgba(122, 166, 71, 0.8), rgba(255, 255, 255, 0));
}

.auth-brand,
.auth-showcase__copy,
.auth-showcase__footer,
.auth-panel__top,
.auth-panel__body {
  position: relative;
  z-index: 2;
}

.auth-brand {
  width: fit-content;
  color: #fff;
  text-decoration: none;
  font-size: 0.95rem;
  font-weight: 800;
  letter-spacing: 0.22em;
  text-transform: uppercase;
  animation: authRise 0.7s ease both;
}

.auth-showcase__copy {
  max-width: 560px;
  color: #fff;
  display: grid;
  gap: 14px;
  align-self: end;
  animation: authRise 0.8s ease 0.08s both;
}

.auth-showcase__eyebrow {
  margin: 0;
  color: rgba(255, 255, 255, 0.72);
  font-size: 0.82rem;
  font-weight: 700;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.auth-showcase__title {
  margin: 0;
  max-width: 10ch;
  font-size: clamp(2.8rem, 5vw, 5.4rem);
  line-height: 0.94;
  letter-spacing: -0.06em;
}

.auth-showcase__body {
  margin: 0;
  max-width: 28rem;
  color: rgba(255, 255, 255, 0.78);
  font-size: clamp(1rem, 1.5vw, 1.16rem);
  line-height: 1.65;
}

.auth-showcase__media {
  position: relative;
  min-height: 260px;
  align-self: stretch;
  display: flex;
  align-items: end;
  justify-content: end;
  animation: authFloatIn 1s ease 0.18s both;
}

.auth-showcase__media::before {
  content: '';
  position: absolute;
  inset: 12% 0 8% 18%;
  border-radius: 28px;
  background:
    linear-gradient(135deg, rgba(122, 166, 71, 0.18), transparent 60%),
    rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(12px);
}

.auth-showcase__image {
  position: relative;
  z-index: 1;
  width: min(72%, 720px);
  min-width: 320px;
  object-fit: cover;
  object-position: center;
  border-radius: 28px;
  box-shadow: 0 28px 80px rgba(0, 0, 0, 0.28);
}

.auth-showcase__footer {
  display: grid;
  gap: 22px;
  color: #fff;
  animation: authRise 0.85s ease 0.22s both;
}

.auth-features {
  list-style: none;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
  padding: 0;
  margin: 0;
}

.auth-features__item {
  display: grid;
  gap: 6px;
  padding-top: 14px;
  border-top: 1px solid rgba(255, 255, 255, 0.16);
}

.auth-features__title {
  font-size: 0.95rem;
  font-weight: 700;
}

.auth-features__description {
  color: rgba(255, 255, 255, 0.68);
  font-size: 0.9rem;
  line-height: 1.5;
}

.auth-metrics {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
}

.auth-metrics__item {
  min-width: 110px;
  display: grid;
  gap: 2px;
}

.auth-metrics__item strong {
  font-size: 1.55rem;
  font-weight: 800;
  line-height: 1;
}

.auth-metrics__item span {
  color: rgba(255, 255, 255, 0.66);
  font-size: 0.85rem;
  line-height: 1.4;
}

.auth-panel {
  min-height: 0;
  height: 100%;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.78), rgba(255, 255, 255, 0.92)),
    var(--auth-panel);
  border-left: 1px solid var(--auth-panel-line);
  display: grid;
  grid-template-rows: auto minmax(0, 1fr);
  padding: clamp(28px, 4vw, 42px);
  overflow: hidden;
}

.auth-panel__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  animation: authRise 0.75s ease 0.08s both;
}

.auth-panel__eyebrow {
  margin: 0;
  color: rgba(19, 32, 22, 0.58);
  font-size: 0.82rem;
  font-weight: 700;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.auth-panel__store-link {
  color: var(--auth-ink);
  text-decoration: none;
  font-size: 0.94rem;
  font-weight: 700;
}

.auth-panel__store-link:hover {
  color: var(--auth-accent-strong);
}

.auth-panel__body {
  margin: 0;
  max-width: 380px;
  width: 100%;
  align-self: center;
  max-height: 100%;
  overflow: auto;
  scrollbar-gutter: stable;
  padding: 18px 10px 18px 0;
  animation: authRise 0.85s ease 0.15s both;
}

.auth-panel__title {
  margin: 14px 0 10px;
  font-size: clamp(2rem, 3vw, 2.7rem);
  line-height: 0.98;
  letter-spacing: -0.05em;
}

.auth-panel__subtitle {
  margin: 0 0 30px;
  color: var(--auth-ink-soft);
  font-size: 0.98rem;
  line-height: 1.65;
}

.auth-panel__body :deep(.auth-form) {
  display: grid;
  gap: 18px;
}

.auth-panel__body::-webkit-scrollbar {
  width: 8px;
}

.auth-panel__body::-webkit-scrollbar-thumb {
  border-radius: 999px;
  background: rgba(19, 32, 22, 0.16);
}

.auth-panel__body :deep(.auth-grid) {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.auth-panel__body :deep(.auth-field) {
  display: grid;
  gap: 8px;
}

.auth-panel__body :deep(.auth-label) {
  font-size: 0.9rem;
  font-weight: 700;
  color: rgba(19, 32, 22, 0.82);
}

.auth-panel__body :deep(.auth-input) {
  appearance: none;
  width: 100%;
  border: 1px solid rgba(19, 32, 22, 0.14);
  background: rgba(255, 255, 255, 0.82);
  border-radius: 18px;
  padding: 15px 17px;
  color: var(--auth-ink);
  font: inherit;
  font-size: 0.98rem;
  transition:
    border-color 0.24s ease,
    box-shadow 0.24s ease,
    transform 0.24s ease,
    background-color 0.24s ease;
}

.auth-panel__body :deep(.auth-input::placeholder) {
  color: rgba(19, 32, 22, 0.34);
}

.auth-panel__body :deep(.auth-input:focus) {
  outline: none;
  border-color: rgba(122, 166, 71, 0.82);
  background: #fff;
  box-shadow: 0 0 0 4px rgba(122, 166, 71, 0.14);
  transform: translateY(-1px);
}

.auth-panel__body :deep(.auth-error) {
  border-radius: 16px;
  padding: 12px 14px;
  background: rgba(179, 52, 43, 0.08);
  color: #b3342b;
  font-size: 0.92rem;
  line-height: 1.45;
}

.auth-panel__body :deep(.auth-submit) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: 100%;
  border: none;
  border-radius: 999px;
  background: linear-gradient(135deg, var(--auth-accent) 0%, var(--auth-accent-strong) 100%);
  color: #fff;
  padding: 15px 22px;
  font: inherit;
  font-size: 1rem;
  font-weight: 800;
  cursor: pointer;
  transition:
    transform 0.18s ease,
    box-shadow 0.24s ease,
    filter 0.24s ease;
  box-shadow: 0 16px 28px rgba(122, 166, 71, 0.28);
}

.auth-panel__body :deep(.auth-submit:hover:not(:disabled)) {
  transform: translateY(-1px);
  filter: saturate(1.05);
  box-shadow: 0 20px 36px rgba(122, 166, 71, 0.3);
}

.auth-panel__body :deep(.auth-submit:disabled) {
  cursor: not-allowed;
  filter: grayscale(0.15);
  box-shadow: none;
  opacity: 0.7;
}

.auth-panel__body :deep(.auth-meta-row) {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  flex-wrap: wrap;
  color: rgba(19, 32, 22, 0.56);
  font-size: 0.9rem;
  line-height: 1.5;
}

.auth-panel__body :deep(.auth-inline-note) {
  margin: 0;
  color: rgba(19, 32, 22, 0.56);
  font-size: 0.9rem;
  line-height: 1.6;
}

.auth-panel__body :deep(.auth-switch-link),
.auth-panel__body :deep(.auth-text-link) {
  color: var(--auth-ink);
  text-decoration: none;
  font-weight: 800;
}

.auth-panel__body :deep(.auth-switch-link:hover),
.auth-panel__body :deep(.auth-text-link:hover) {
  color: var(--auth-accent-strong);
}

.auth-panel__body :deep(.auth-footnote) {
  margin: 10px 0 0;
  color: rgba(19, 32, 22, 0.48);
  font-size: 0.82rem;
  line-height: 1.55;
}

@keyframes authRise {
  from {
    opacity: 0;
    transform: translateY(18px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes authFloatIn {
  from {
    opacity: 0;
    transform: translateY(18px) scale(0.985);
  }

  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@media (max-width: 1080px) {
  .auth-shell {
    grid-template-columns: minmax(0, 1fr) minmax(360px, 440px);
  }

  .auth-showcase__copy {
    max-width: 520px;
  }

  .auth-showcase__title {
    max-width: 8ch;
  }

  .auth-features {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 920px) {
  .auth-shell {
    grid-template-columns: 1fr;
    background:
      radial-gradient(circle at top left, rgba(122, 166, 71, 0.18), transparent 34%),
      linear-gradient(180deg, #f7f2e8 0%, #f1eadc 100%);
  }

  .auth-showcase {
    display: none;
  }

  .auth-panel {
    width: min(100%, 560px);
    margin: 0 auto;
    border-left: none;
    background:
      linear-gradient(180deg, rgba(255, 255, 255, 0.74), rgba(255, 255, 255, 0.96)),
      rgba(251, 248, 242, 0.94);
  }

  .auth-panel__body {
    max-width: 100%;
  }
}

@media (max-width: 720px) {
  .auth-shell {
    min-height: 100svh;
    height: 100svh;
  }

  .auth-panel {
    width: 100%;
    padding: 22px 18px 24px;
  }

  .auth-panel__top {
    align-items: center;
    flex-direction: row;
  }

  .auth-panel__title {
    font-size: 2rem;
  }

  .auth-panel__subtitle {
    margin-bottom: 24px;
  }

  .auth-panel__body {
    padding-right: 0;
  }

  .auth-panel__body :deep(.auth-grid) {
    grid-template-columns: 1fr;
  }

  .auth-panel__body :deep(.auth-meta-row) {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
