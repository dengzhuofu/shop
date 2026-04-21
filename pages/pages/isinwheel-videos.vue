<template>
  <div class="videos-page">
    <section class="container videos-shell">
      <div class="videos-head">
        <h1>Isinwheel Videos</h1>
      </div>

      <div class="videos-grid">
        <a
          v-for="video in videos"
          :key="video.id"
          :href="video.url"
          target="_blank"
          rel="noreferrer"
          class="video-card"
        >
          <div class="video-card__media">
            <img :src="video.thumbnail" :alt="video.title" loading="lazy" />
            <span v-if="video.badge" class="video-card__badge">{{ video.badge }}</span>
            <span class="video-card__play" aria-hidden="true">
              <PlayIcon :size="26" />
            </span>
          </div>
        </a>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { PlayIcon } from 'lucide-vue-next'

useSeoMeta({
  title: 'Isinwheel Videos',
  description:
    'Watch a restored video gallery of isinwheel product demos, riding clips, and community highlights.',
})

const videos = [
  {
    id: 'WG-6IIdj_PA',
    title: 'E-bike',
    badge: 'e-bike',
    thumbnail:
      'https://www.isinwheel.com/cdn/shop/files/hqdefault_26.jpg?v=1693910484&width=480',
    url: 'https://www.youtube.com/watch?v=WG-6IIdj_PA',
  },
  {
    id: 'fmB27dU63OU',
    title: 'E-scooter',
    badge: 'e-scooter',
    thumbnail:
      'https://www.isinwheel.com/cdn/shop/files/hqdefault_2_d749bde1-4044-4f55-9952-be3b2384e091.jpg?v=1693910452&width=480',
    url: 'https://www.youtube.com/watch?v=fmB27dU63OU',
  },
  {
    id: '2GaT1OfpUEo',
    title: 'S9 Max',
    badge: '',
    thumbnail:
      'https://www.isinwheel.com/cdn/shop/files/hqdefault1_11fbad44-b6b0-424a-9bb4-2c87b9cc294b.jpg?v=1693910856&width=480',
    url: 'https://www.youtube.com/watch?v=2GaT1OfpUEo',
  },
  {
    id: 'wQXg4ZbveU8',
    title: 'S9 Max demo',
    badge: '',
    thumbnail:
      'https://www.isinwheel.com/cdn/shop/files/hqdefault_1_eee63f18-e330-4d71-846c-ce0100535d00.jpg?v=1693911490&width=480',
    url: 'https://www.youtube.com/watch?v=wQXg4ZbveU8',
  },
  {
    id: 'j320HeFhV0s',
    title: 'Electric scooter practical',
    badge: '',
    thumbnail:
      'https://www.isinwheel.com/cdn/shop/files/hqdefault_27.jpg?v=1693910573&width=480',
    url: 'https://www.youtube.com/watch?v=j320HeFhV0s',
  },
  {
    id: 'c843eQ3iZDc',
    title: 'Ride test',
    badge: '',
    thumbnail:
      'https://www.isinwheel.com/cdn/shop/files/hqdefault_18_bacacd8d-2b31-40a3-9bf0-d0e1f9bc14b7.jpg?v=1693909983&width=480',
    url: 'https://www.youtube.com/watch?v=c843eQ3iZDc',
  },
  {
    id: 'zW2yeOUa5f4',
    title: 'V8 demo',
    badge: '',
    thumbnail:
      'https://www.isinwheel.com/cdn/shop/files/hqdefault_16_efc731c9-5f5f-4eab-b90a-9a6e8aadc8a6.jpg?v=1693911606&width=480',
    url: 'https://www.youtube.com/watch?v=zW2yeOUa5f4',
  },
  {
    id: 'Ck6aSMuiaiY',
    title: 'V8 overview',
    badge: '',
    thumbnail:
      'https://www.isinwheel.com/cdn/shop/files/hqdefault_17_939f7dc5-8722-47a0-a90e-a4db67db9aee.jpg?v=1693910529&width=480',
    url: 'https://www.youtube.com/watch?v=Ck6aSMuiaiY',
  },
  {
    id: 'LLKHkJ5P1Z0',
    title: 'V8 feature clip',
    badge: '',
    thumbnail:
      'https://www.isinwheel.com/cdn/shop/files/hqdefault_29.jpg?v=1693910658&width=480',
    url: 'https://www.youtube.com/watch?v=LLKHkJ5P1Z0',
  },
  {
    id: 'NrsjcX8XvY8',
    title: 'GT2',
    badge: '',
    thumbnail:
      'https://www.isinwheel.com/cdn/shop/files/hqdefault_1_2.jpg?v=1693910613&width=480',
    url: 'https://www.youtube.com/watch?v=NrsjcX8XvY8',
  },
  {
    id: 'XaUYqNR3UzM',
    title: 'GT2 45KM/H',
    badge: '',
    thumbnail:
      'https://www.isinwheel.com/cdn/shop/files/hqdefault_5_d1e29184-5d83-48ff-969c-fb040590d962.jpg?v=1693911019&width=480',
    url: 'https://www.youtube.com/watch?v=XaUYqNR3UzM',
  },
  {
    id: 'orkgEymVeCk',
    title: 'GT2 ride',
    badge: '',
    thumbnail:
      'https://www.isinwheel.com/cdn/shop/files/hqdefault_4_ab90928f-3b63-432b-b969-3a99df0eda3b.jpg?v=1693911879&width=480',
    url: 'https://www.youtube.com/watch?v=orkgEymVeCk',
  },
]
</script>

<style scoped lang="scss">
.videos-page {
  background: #fff;
}

.videos-shell {
  padding-top: 30px;
  padding-bottom: 54px;
}

.videos-head {
  margin-bottom: 18px;
  text-align: center;

  h1 {
    margin: 0;
    font-size: clamp(34px, 3.9vw, 48px);
    line-height: 1;
    font-weight: 800;
    color: #151515;
  }
}

.videos-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.video-card {
  display: block;
  text-decoration: none;
}

.video-card__media {
  position: relative;
  overflow: hidden;
  border-radius: 12px;
  background: #dde4ea;

  img {
    display: block;
    width: 100%;
    height: auto;
    aspect-ratio: 16 / 9;
    object-fit: cover;
    transition:
      transform 0.34s ease,
      filter 0.34s ease;
  }
}

.video-card__badge {
  position: absolute;
  left: 14px;
  top: 14px;
  z-index: 1;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: rgba(18, 18, 18, 0.72);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  line-height: 28px;
  text-transform: lowercase;
}

.video-card__play {
  position: absolute;
  left: 50%;
  top: 50%;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 62px;
  height: 62px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.92);
  color: #111;
  transform: translate(-50%, -50%);
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.18);
}

.video-card:hover {
  .video-card__media img {
    transform: scale(1.03);
    filter: saturate(1.05);
  }
}

@media (max-width: 980px) {
  .videos-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .videos-shell {
    padding-top: 24px;
    padding-bottom: 44px;
  }

  .videos-grid {
    grid-template-columns: 1fr;
  }

  .video-card__play {
    width: 54px;
    height: 54px;
  }
}
</style>
