<template>
  <div class="news-page">
    <section class="container news-shell">
      <div class="news-head">
        <h1>News</h1>
      </div>

      <div class="news-featured">
        <article class="news-card news-card--hero">
          <a
            :href="featuredPost.url"
            target="_blank"
            rel="noreferrer"
            class="news-card__link"
          >
            <div class="news-card__media">
              <img :src="featuredPost.image" :alt="featuredPost.title" loading="lazy" />
            </div>
            <div class="news-card__content">
              <p class="news-card__date">{{ featuredPost.date }}</p>
              <h2>{{ featuredPost.title }}</h2>
              <p class="news-card__excerpt">{{ featuredPost.excerpt }}</p>
              <span class="news-card__cta">Read now</span>
            </div>
          </a>
        </article>

        <div class="news-side">
          <article
            v-for="post in sidePosts"
            :key="post.title"
            class="news-card news-card--side"
          >
            <a
              :href="post.url"
              target="_blank"
              rel="noreferrer"
              class="news-card__link"
            >
              <div class="news-card__media">
                <img :src="post.image" :alt="post.title" loading="lazy" />
              </div>
              <div class="news-card__content">
                <p class="news-card__date">{{ post.date }}</p>
                <h3>{{ post.title }}</h3>
                <p class="news-card__excerpt">{{ post.excerpt }}</p>
                <span class="news-card__cta">Read now</span>
              </div>
            </a>
          </article>
        </div>
      </div>

      <div class="news-grid">
        <article v-for="post in gridPosts" :key="post.title" class="news-card">
          <a
            :href="post.url"
            target="_blank"
            rel="noreferrer"
            class="news-card__link"
          >
            <div class="news-card__media">
              <img :src="post.image" :alt="post.title" loading="lazy" />
            </div>
            <div class="news-card__content">
              <p class="news-card__date">{{ post.date }}</p>
              <h3>{{ post.title }}</h3>
              <p class="news-card__excerpt">{{ post.excerpt }}</p>
              <span class="news-card__cta">Read now</span>
            </div>
          </a>
        </article>
      </div>

      <div class="news-pagination" aria-label="Pagination">
        <button type="button" class="is-active">1</button>
        <button type="button">2</button>
        <button type="button">3</button>
        <span>...</span>
        <button type="button">21</button>
        <button type="button" class="news-pagination__next">Next</button>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
useSeoMeta({
  title: 'News',
  description:
    'Browse the restored isinwheel blog index with featured stories, latest product articles, and community content.',
})

const posts = [
  {
    title:
      'Dual Motor Electric Scooter: Why More Riders Are Upgrading to the isinwheel GT1 Dual',
    date: 'Apr 01, 2026',
    image:
      'https://www.isinwheel.com/cdn/shop/articles/lQLPJwiURzqLvyHNBgDNCsCws2POS7RnumEJo0vM6VbiAQ_2752_1536.png?v=1775010099&width=1920',
    url: 'https://www.isinwheel.com/blogs/news/dual-motor-electric-scooter-why-more-riders-are-upgrading-to-the-isinwheel-gt1-dual',
    excerpt:
      'The isinwheel GT1 Dual is a dual motor electric scooter with 800W x 2 power, 32 MPH speed, strong hill climbing, and off-road capability for daily and adventure riding.',
  },
  {
    title:
      '3 Best Beginner E-Scooters for Commuting: Maximizing Long Battery Range and Power',
    date: 'Mar 31, 2026',
    image:
      'https://www.isinwheel.com/cdn/shop/articles/DSC4168.jpg?v=1774950769&width=1200',
    url: 'https://www.isinwheel.com/blogs/news/best-beginner-e-scooters-for-commuting-long-range-power',
    excerpt:
      'Tired of watching your battery meter nosedive halfway to work? This guide compares beginner-friendly commuter scooters that balance range, usable power, and everyday comfort.',
  },
  {
    title:
      'Best Suspension for Value Off-Road Scooters: What Matters on Rough Paths',
    date: 'Mar 26, 2026',
    image:
      'https://www.isinwheel.com/cdn/shop/articles/DSC6845.jpg?v=1774522515&width=1200',
    url: 'https://www.isinwheel.com/blogs/news/best-suspension-value-off-road-scooters-rough-paths',
    excerpt:
      'When the trail gets rutted and the bars start buzzing, suspension quality matters more than spec-sheet hype. Here is what actually improves control and comfort.',
  },
  {
    title:
      '3 Best Value-for-Money Electric Scooters with the Best Off-Road Suspension',
    date: 'Mar 17, 2026',
    image:
      'https://www.isinwheel.com/cdn/shop/articles/DSC7098_876b1752-80a0-4ff5-b8d8-a62c6807ba3c.jpg?v=1773740416&width=1200',
    url: 'https://www.isinwheel.com/blogs/news/3-best-value-off-road-suspension-electric-scooters',
    excerpt:
      'Cracks, potholes, and curb ramps quickly expose weak suspension. These off-road-ready picks focus on value without sacrificing ride quality.',
  },
  {
    title:
      'Neighborhood Errands Under $500: 4 Best Scooters for Families Who Want an Easy-to-Ride Vehicle',
    date: 'Mar 11, 2026',
    image:
      'https://www.isinwheel.com/cdn/shop/articles/DSC1251.jpg?v=1773225289&width=1200',
    url: 'https://www.isinwheel.com/blogs/news/neighborhood-errands-under-500-family-scooters',
    excerpt:
      'For grocery runs, school pickup, and short neighborhood loops, easy handling beats flashy specs. This roundup focuses on stable, simple family-friendly choices.',
  },
  {
    title: '5 Easy-to-Ride Electric Scooters Under $800 for Beginners: A Checklist',
    date: 'Mar 10, 2026',
    image:
      'https://www.isinwheel.com/cdn/shop/articles/isinwheel_c66ce650-cbe4-4a35-a307-51b9e864b27f.jpg?v=1774605498&width=1200',
    url: 'https://www.isinwheel.com/blogs/news/5-easy-to-ride-electric-scooters-under-800-for-beginners-a-checklist',
    excerpt:
      'If your first ride feels twitchy, the wrong setup may be to blame. This checklist narrows down beginner scooters that feel predictable from day one.',
  },
  {
    title:
      '3 Durable Electric Scooters for Off-Road Conditions for Beginner (Tested & Rated)',
    date: 'Mar 05, 2026',
    image:
      'https://www.isinwheel.com/cdn/shop/articles/DSC4868.jpg?v=1773224273&width=1200',
    url: 'https://www.isinwheel.com/blogs/news/3-durable-off-road-scooters-for-beginners-tested-rated',
    excerpt:
      'Loose gravel, surprise ruts, and poor braking punish small mistakes fast. These beginner-focused off-road scooters prioritize durability where it counts.',
  },
  {
    title:
      'How to Choose a Daily Electric Scooter for Your Child: 5 Essential Tips for Parents',
    date: 'Mar 02, 2026',
    image:
      'https://www.isinwheel.com/cdn/shop/articles/DSC1109.jpg?v=1772442065&width=1200',
    url: 'https://www.isinwheel.com/blogs/news/choose-daily-electric-scooter-for-child',
    excerpt:
      'A daily school run sounds simple until safety enters the picture. This parent guide covers balance, braking habits, battery size, and ride confidence.',
  },
  {
    title:
      '5 Steps to Select an Electric Scooter for Outdoor Adventures and Rough Terrain 2026',
    date: 'Mar 01, 2026',
    image:
      'https://www.isinwheel.com/cdn/shop/articles/6_a6c2794b-7b1c-4838-9dc7-42025746b75b.png?v=1773331599&width=1200',
    url: 'https://www.isinwheel.com/blogs/news/5-steps-select-off-road-electric-scooter-2026',
    excerpt:
      'Rough terrain exposes weak components fast. This step-by-step guide focuses on motor strength, tire size, braking, and frame confidence for trail use.',
  },
]

const featuredPost = posts[0]
const sidePosts = posts.slice(1, 3)
const gridPosts = posts.slice(3)
</script>

<style scoped lang="scss">
.news-page {
  background: #fff;
}

.news-shell {
  padding-top: 34px;
  padding-bottom: 54px;
}

.news-head {
  margin-bottom: 22px;
  text-align: center;

  h1 {
    margin: 0;
    font-size: clamp(34px, 3.8vw, 48px);
    line-height: 1;
    font-weight: 800;
    color: #161616;
  }
}

.news-featured {
  display: grid;
  grid-template-columns: minmax(0, 1.45fr) minmax(280px, 0.72fr);
  gap: 22px;
}

.news-side {
  display: grid;
  gap: 18px;
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 24px 22px;
  margin-top: 28px;
}

.news-card {
  overflow: hidden;
  border-radius: 14px;
  background: #fff;
  box-shadow: 0 10px 28px rgba(15, 15, 15, 0.05);
}

.news-card__link {
  display: block;
  color: inherit;
  text-decoration: none;
}

.news-card__media {
  overflow: hidden;
  background: #eef1f4;

  img {
    display: block;
    width: 100%;
    height: auto;
    aspect-ratio: 16 / 11;
    object-fit: cover;
    transition: transform 0.36s ease;
  }
}

.news-card__content {
  padding: 18px 18px 20px;
}

.news-card__date {
  margin: 0 0 10px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #7a7a7a;
}

.news-card h2,
.news-card h3 {
  margin: 0;
  color: #171717;
  line-height: 1.15;
  font-weight: 800;
}

.news-card h2 {
  font-size: clamp(28px, 3vw, 40px);
}

.news-card h3 {
  font-size: 22px;
}

.news-card__excerpt {
  margin: 12px 0 0;
  color: #646464;
  font-size: 14px;
  line-height: 1.75;
}

.news-card__cta {
  display: inline-flex;
  margin-top: 14px;
  font-size: 12px;
  font-weight: 700;
  color: #111;
}

.news-card:hover img {
  transform: scale(1.03);
}

.news-card--hero .news-card__content {
  padding: 22px 22px 24px;
}

.news-card--side {
  .news-card__link {
    display: grid;
    grid-template-columns: 160px minmax(0, 1fr);
    align-items: stretch;
  }

  .news-card__media img {
    height: 100%;
    aspect-ratio: auto;
  }

  h3 {
    font-size: 20px;
  }

  .news-card__content {
    padding: 16px;
  }
}

.news-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-top: 30px;

  button,
  span {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 34px;
    height: 34px;
    border-radius: 999px;
    font-size: 12px;
    font-weight: 700;
    color: #444;
  }

  button {
    border: 1px solid #d8d8d8;
    background: #fff;
  }

  .is-active {
    border-color: #111;
    background: #111;
    color: #fff;
  }
}

.news-pagination__next {
  padding: 0 14px;
}

@media (max-width: 1080px) {
  .news-featured {
    grid-template-columns: 1fr;
  }

  .news-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .news-shell {
    padding-top: 28px;
    padding-bottom: 46px;
  }

  .news-grid {
    grid-template-columns: 1fr;
  }

  .news-card--side .news-card__link {
    grid-template-columns: 1fr;
  }

  .news-card h2 {
    font-size: 28px;
  }

  .news-card h3 {
    font-size: 20px;
  }

  .news-pagination {
    gap: 8px;
    flex-wrap: wrap;
  }
}
</style>
