<template>
  <div class="brand-page about-page">
    <section
      class="about-hero"
      :style="{ backgroundImage: `url(${heroImage})` }"
    >
      <div class="about-hero__overlay" />
      <div class="container about-hero__content">
        <div>
          <p class="about-hero__eyebrow">About isinwheel</p>
          <h1>BRING FUN TO YOUR WAY</h1>
        </div>
      </div>
    </section>

    <section class="container about-section">
      <div class="section-heading">
        <h2>Timeline</h2>
        <p>Key milestones that shaped isinwheel into a global smart-mobility brand.</p>
      </div>

      <div class="timeline-track">
        <article
          v-for="item in timeline"
          :key="item.year"
          class="timeline-card"
        >
          <img :src="item.image" :alt="item.title" loading="lazy" />
          <div class="timeline-card__copy">
            <p class="timeline-card__year">{{ item.year }}</p>
            <h3>{{ item.title }}</h3>
            <p>{{ item.text }}</p>
          </div>
        </article>
      </div>

      <div class="timeline-years" aria-hidden="true">
        <span v-for="item in timeline" :key="`${item.year}-label`">{{ item.year }}</span>
      </div>
    </section>

    <section class="container about-video">
      <a
        class="about-video__frame"
        href="https://www.youtube.com/watch?v=zWQf94JzZbM"
        target="_blank"
        rel="noreferrer"
      >
        <img :src="videoPreview" alt="Bring fun to your way" loading="lazy" />
        <div class="about-video__overlay" />
        <div class="about-video__title">BRING FUN TO YOUR WAY</div>
        <div class="about-video__play">
          <PlayIcon />
        </div>
      </a>
    </section>

    <section
      v-for="(section, index) in storySections"
      :key="section.title"
      class="container about-story"
      :class="{ 'about-story--reverse': index % 2 === 1 }"
    >
      <div class="about-story__media">
        <img :src="section.image" :alt="section.title" loading="lazy" />
      </div>

      <div class="about-story__copy">
        <h2 class="scribble-title">{{ section.title }}</h2>
        <p
          v-for="paragraph in section.paragraphs"
          :key="paragraph"
        >
          {{ paragraph }}
        </p>
      </div>
    </section>

    <section class="container about-moments">
      <div class="about-moments__grid">
        <img
          v-for="(image, index) in momentImages"
          :key="`${image}-${index}`"
          :src="image"
          alt="isinwheel moments"
          loading="lazy"
        />
      </div>
    </section>

    <section class="about-banner">
      <img :src="bottomBanner" alt="isinwheel banner" loading="lazy" />
    </section>
  </div>
</template>

<script setup lang="ts">
import { PlayIcon } from 'lucide-vue-next'

useSeoMeta({
  title: 'About Us',
  description:
    'Learn the isinwheel story, brand motto, vision, and mission through a restored brand page experience.',
})

const heroImage =
  'https://www.isinwheel.com/cdn/shop/files/890C85E0-E55B-466d-ADDE-67FDE5677466.png?v=1746761293&width=1920'
const videoPreview =
  'https://www.isinwheel.com/cdn/shop/files/preview_images/0894b62123324ae1992ebac0e10078b9.thumbnail.0000000000.jpg?v=1696560243&width=1600'
const bottomBanner =
  'https://www.isinwheel.com/cdn/shop/files/355310f54d909d2ce96acd61e3d8a719.png?v=1745572686&width=1920'

const timeline = [
  {
    year: '2018',
    title: 'S9 SERIES',
    image:
      'https://www.isinwheel.com/cdn/shop/files/D3E916FC-5A02-440a-AD3E-0FDF91744194.png?v=1746770408&width=1200',
    text:
      'In 2018, isinwheel launched the S9Pro and S9Max in the US, focusing on affordable, high-quality green transportation with strong value for everyday riders.',
  },
  {
    year: '2022',
    title: 'GT2',
    image:
      'https://www.isinwheel.com/cdn/shop/files/12BB4B8A-B201-48e0-B840-C2C291A15512.png?v=1746770582&width=1200',
    text:
      'In 2022, isinwheel introduced the GT2, a flagship off-road scooter designed, developed, and manufactured in-house with a 1000W motor and 15.6Ah battery.',
  },
  {
    year: '2023',
    title: 'Ebikes',
    image:
      'https://www.isinwheel.com/cdn/shop/files/D759D6C4-BA9D-45df-8150-E3702B79D189.png?v=1746770732&width=1200',
    text:
      'In 2023, the brand expanded into electric bicycles, adding the M10, U1, U2, and U5 to give more riders smart and stylish mobility options.',
  },
  {
    year: '2024',
    title: 'S10MAX',
    image:
      'https://www.isinwheel.com/cdn/shop/files/22D64724-F71D-4cd7-8210-725CA16A0AEF.png?v=1746770882&width=1200',
    text:
      'In 2024, isinwheel launched the S10MAX across Europe, North America, and South America, earning quick recognition for performance, durability, and value.',
  },
]

const storySections = [
  {
    title: 'Our Story',
    image:
      'https://www.isinwheel.com/cdn/shop/files/329A62D4-B54B-497e-A959-4BD42C8CC2BD.png?v=1745573177&width=1200',
    paragraphs: [
      "Founded in 2018 with a focus on intelligent short-distance transportation, isinwheel has swiftly become a respected international brand.",
      "Specializing in premium online-drive personal transportation, we epitomize 'Move Smart, Move Fun' by blending ease, safety, and enjoyment into every ride.",
    ],
  },
  {
    title: 'Our Brand Motto',
    image:
      'https://www.isinwheel.com/cdn/shop/files/D5541CE6-8098-459a-BADC-E756380B3B24.png?v=1745573606&width=1200',
    paragraphs: [
      'Move Smart means building intelligent urban mobility through practical technology, efficient design, and rider-friendly experiences.',
      'Move Fun means every product should feel energetic, approachable, and memorable, turning everyday travel into something enjoyable.',
    ],
  },
  {
    title: 'Our Vision',
    image:
      'https://www.isinwheel.com/cdn/shop/files/1_4_fc3c5c36-6218-460e-bdf1-43acb4b05204.jpg?v=1746774506&width=1200',
    paragraphs: [
      'Revolutionizing urban mobility with smart, sustainable, secure, and enjoyable travel solutions.',
    ],
  },
  {
    title: 'Our Mission',
    image:
      'https://www.isinwheel.com/cdn/shop/files/489925757_642957701951063_2952534063022732152_n.jpg?v=1746773963&width=1400',
    paragraphs: [
      'To redefine the experience of every journey, igniting paths of joy and passion for riders around the world.',
    ],
  },
]

const momentImages = [
  'https://www.isinwheel.com/cdn/shop/files/D3E916FC-5A02-440a-AD3E-0FDF91744194.png?v=1746770408&width=800',
  'https://www.isinwheel.com/cdn/shop/files/12BB4B8A-B201-48e0-B840-C2C291A15512.png?v=1746770582&width=800',
  'https://www.isinwheel.com/cdn/shop/files/D759D6C4-BA9D-45df-8150-E3702B79D189.png?v=1746770732&width=800',
  'https://www.isinwheel.com/cdn/shop/files/22D64724-F71D-4cd7-8210-725CA16A0AEF.png?v=1746770882&width=800',
  'https://www.isinwheel.com/cdn/shop/files/329A62D4-B54B-497e-A959-4BD42C8CC2BD.png?v=1745573177&width=800',
  'https://www.isinwheel.com/cdn/shop/files/D5541CE6-8098-459a-BADC-E756380B3B24.png?v=1745573606&width=800',
  'https://www.isinwheel.com/cdn/shop/files/1_4_fc3c5c36-6218-460e-bdf1-43acb4b05204.jpg?v=1746774506&width=800',
  'https://www.isinwheel.com/cdn/shop/files/489925757_642957701951063_2952534063022732152_n.jpg?v=1746773963&width=800',
]
</script>

<style scoped lang="scss">
.about-page {
  padding-bottom: 28px;
}

.about-hero {
  position: relative;
  min-height: clamp(340px, 54vw, 620px);
  background-position: center;
  background-size: cover;
  overflow: hidden;
}

.about-hero__overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(8, 16, 28, 0.28), rgba(8, 16, 28, 0.48));
}

.about-hero__content {
  position: relative;
  z-index: 1;
  min-height: inherit;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: #fff;

  h1 {
    font-family: inherit;
    font-size: clamp(34px, 6vw, 72px);
    line-height: 0.95;
    letter-spacing: 0.06em;
    font-weight: 800;
  }
}

.about-hero__eyebrow {
  margin-bottom: 14px;
  font-size: 13px;
  letter-spacing: 0.3em;
  text-transform: uppercase;
  opacity: 0.9;
}

.about-section,
.about-story,
.about-video,
.about-moments {
  margin-top: 44px;
}

.section-heading {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 22px;

  h2 {
    font-family: inherit;
    font-size: clamp(30px, 3vw, 46px);
    line-height: 1;
    font-weight: 800;
  }

  p {
    max-width: 540px;
    color: #666;
    font-size: 15px;
    line-height: 1.7;
  }
}

.timeline-track {
  display: grid;
  grid-auto-flow: column;
  grid-auto-columns: minmax(320px, 76vw);
  gap: 20px;
  overflow-x: auto;
  padding-bottom: 10px;
  scroll-snap-type: x proximity;

  &::-webkit-scrollbar {
    height: 8px;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(0, 0, 0, 0.16);
    border-radius: 999px;
  }
}

.timeline-card {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(280px, 0.85fr);
  min-height: 340px;
  background: #f5f5f2;
  border-radius: 22px;
  overflow: hidden;
  scroll-snap-align: start;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.timeline-card__copy {
  padding: 28px 28px 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;

  h3 {
    font-family: inherit;
    font-size: 34px;
    line-height: 1;
    font-weight: 800;
    margin-bottom: 14px;
  }

  p {
    color: #555;
    font-size: 15px;
    line-height: 1.75;
  }
}

.timeline-card__year {
  margin-bottom: 10px;
  color: #7a7a7a;
  font-size: 13px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.timeline-years {
  display: flex;
  gap: 18px;
  margin-top: 16px;
  color: #888;
  font-size: 14px;
  font-weight: 700;
}

.about-video__frame {
  position: relative;
  display: block;
  overflow: hidden;
  border-radius: 22px;

  img {
    width: 100%;
    display: block;
  }
}

.about-video__overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(10, 10, 10, 0.08), rgba(10, 10, 10, 0.36));
}

.about-video__title {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  color: #fff;
  font-size: clamp(30px, 5vw, 60px);
  font-weight: 800;
  letter-spacing: 0.08em;
  text-align: center;
  white-space: nowrap;
}

.about-video__play {
  position: absolute;
  right: 24px;
  bottom: 24px;
  width: 56px;
  height: 56px;
  display: grid;
  place-items: center;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.94);
  color: #121212;

  svg {
    width: 22px;
    height: 22px;
    margin-left: 2px;
  }
}

.about-story {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(320px, 0.72fr);
  gap: 36px;
  align-items: center;
}

.about-story--reverse {
  grid-template-columns: minmax(320px, 0.72fr) minmax(0, 1fr);

  .about-story__media {
    order: 2;
  }

  .about-story__copy {
    order: 1;
  }
}

.about-story__media img {
  width: 100%;
  display: block;
  border-radius: 6px;
}

.about-story__copy {
  p {
    color: #555;
    font-size: 15px;
    line-height: 1.8;
    margin-top: 16px;
  }
}

.scribble-title {
  position: relative;
  display: inline-block;
  font-family: inherit;
  font-size: clamp(30px, 3vw, 44px);
  line-height: 1.05;
  font-weight: 800;

  &::after {
    content: '';
    position: absolute;
    left: 0;
    bottom: -8px;
    width: 100%;
    height: 10px;
    border-radius: 999px;
    background: linear-gradient(90deg, rgba(117, 212, 49, 0.95), rgba(117, 212, 49, 0.45));
    opacity: 0.9;
  }
}

.about-moments__grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;

  img {
    width: 100%;
    aspect-ratio: 1 / 1;
    object-fit: cover;
    border-radius: 4px;
  }
}

.about-banner img {
  width: 100%;
  display: block;
  margin-top: 44px;
}

@media (max-width: 900px) {
  .section-heading,
  .about-story,
  .about-story--reverse,
  .timeline-card {
    grid-template-columns: 1fr;
  }

  .timeline-card {
    min-height: auto;
  }

  .about-story--reverse {
    .about-story__media,
    .about-story__copy {
      order: initial;
    }
  }

  .about-video__title {
    white-space: normal;
    width: calc(100% - 48px);
  }

  .about-moments__grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
