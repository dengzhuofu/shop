<template>
  <footer class="app-footer">
    <section class="footer-community">
      <div class="container community-shell">
        <div class="service-grid">
          <article
            v-for="item in serviceCards"
            :key="item.title"
            class="service-card"
          >
            <div class="service-icon">
              <img :src="item.icon" alt="" loading="lazy" />
            </div>
            <div>
              <h3>{{ item.title }}</h3>
              <p>{{ item.description }}</p>
            </div>
          </article>
        </div>

        <div class="community-copy">
          <h2>{{ copy.communityTitle }}</h2>
          <a
            class="community-button"
            href="https://www.facebook.com/groups/isinwheel"
            target="_blank"
            rel="noreferrer"
          >
            <span class="community-button__icon">+</span>
            <span>{{ copy.communityCta }}</span>
            <ArrowRightIcon class="community-button__arrow" />
          </a>
        </div>
      </div>
    </section>

    <section class="footer-main">
      <div class="container footer-main__inner">
        <div class="footer-main__left">
          <div class="footer-link-columns">
            <div
              v-for="group in footerNavGroups"
              :key="group.title"
              class="footer-link-group"
            >
              <h3>{{ group.title }}</h3>
              <div class="footer-link-list">
                <NuxtLink
                  v-for="link in group.links"
                  :key="link.label"
                  :to="link.to"
                >
                  {{ link.label }}
                </NuxtLink>
              </div>
            </div>
          </div>

          <div class="footer-contact__meta">
            <p>{{ copy.hours }}</p>
            <a href="tel:+18882935788">+1(888)293-5788</a>
            <a href="mailto:support@isinwheel.com">support@isinwheel.com</a>
          </div>
        </div>

        <div class="footer-main__right">
          <div class="footer-newsletter">
            <h3>{{ copy.newsletterTitle }}</h3>
            <div class="footer-newsletter__form">
              <input
                type="email"
                :placeholder="copy.newsletterPlaceholder"
                aria-label="Email"
              />
              <button type="button" aria-label="Subscribe newsletter">
                <ArrowRightIcon />
              </button>
            </div>
          </div>

          <div class="footer-socials">
            <a
              href="https://www.facebook.com/groups/isinwheel"
              target="_blank"
              rel="noreferrer"
              aria-label="Facebook"
            >
              <FacebookIcon />
            </a>
            <a
              href="https://www.instagram.com/isinwheel/"
              target="_blank"
              rel="noreferrer"
              aria-label="Instagram"
            >
              <InstagramIcon />
            </a>
            <a
              href="https://www.youtube.com/@isinwheel"
              target="_blank"
              rel="noreferrer"
              aria-label="YouTube"
            >
              <YoutubeIcon />
            </a>
            <a
              href="https://www.tiktok.com/"
              target="_blank"
              rel="noreferrer"
              aria-label="TikTok"
              class="footer-socials__text"
            >
              t
            </a>
          </div>
        </div>
      </div>
    </section>

    <section class="footer-bottom">
      <div class="container footer-bottom__inner">
        <div class="footer-bottom__legal">
          <p>{{ copy.copyright }}</p>
          <div class="footer-bottom__links">
            <NuxtLink to="/account/profile">{{ copy.refund }}</NuxtLink>
            <NuxtLink to="/account/profile">{{ copy.privacy }}</NuxtLink>
            <NuxtLink to="/account/profile">{{ copy.terms }}</NuxtLink>
            <NuxtLink to="/pages/contact-us">{{ copy.contact }}</NuxtLink>
          </div>
        </div>

        <button type="button" class="footer-region">
          <span>United States (USD $)</span>
          <ChevronDownIcon />
        </button>

        <div class="footer-payments">
          <span
            v-for="method in paymentMethods"
            :key="method"
            class="payment-badge"
          >
            {{ method }}
          </span>
        </div>
      </div>
    </section>
  </footer>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  ArrowRightIcon,
  ChevronDownIcon,
  FacebookIcon,
  InstagramIcon,
  YoutubeIcon,
} from 'lucide-vue-next'

const { lang } = useShopLocale()

const serviceCards = computed(() =>
  lang.value === 'zh'
    ? [
        {
          title: '快速可靠配送',
          description: '2-5 个工作日 UPS/FedEx 送达',
          icon: 'https://www.isinwheel.com/cdn/shop/files/lQLPJw6ECy5xnu8yMrDe9EqOZZlckwkTWuPGFHcC_50_50.png?v=1765524567',
        },
        {
          title: '1 年保修服务',
          description: '由 isinwheel 有限保修提供支持',
          icon: 'https://www.isinwheel.com/cdn/shop/files/lQLPJxLr0S33Hu8yMrCQIEE417wfBAkTWuPGFHcE_50_50.png?v=1765524567',
        },
        {
          title: '美国本地支持',
          description: '在线聊天与电话服务',
          icon: 'https://www.isinwheel.com/cdn/shop/files/lQLPKG07iq9XX-8yMrAo-xE02uGvNAkTWuPGFHcD_50_50.png?v=1765524567',
        },
        {
          title: '安全支付',
          description: '支持 PayPal / 信用卡 / 分期付款',
          icon: 'https://www.isinwheel.com/cdn/shop/files/lQLPJxnibT25nu8yMrD6FoVHdv_MjQkTWuPGFHcF_50_50.png?v=1765524566',
        },
      ]
    : [
        {
          title: 'Fast & Reliable Shipping',
          description: '2-5 Business Days UPS/FedEx Delivery',
          icon: 'https://www.isinwheel.com/cdn/shop/files/lQLPJw6ECy5xnu8yMrDe9EqOZZlckwkTWuPGFHcC_50_50.png?v=1765524567',
        },
        {
          title: '1-Year Warranty Service',
          description: 'Backed by isinwheel Limited Warranty',
          icon: 'https://www.isinwheel.com/cdn/shop/files/lQLPJxLr0S33Hu8yMrCQIEE417wfBAkTWuPGFHcE_50_50.png?v=1765524567',
        },
        {
          title: 'US-Based Support',
          description: 'Live Chat and Phone Service',
          icon: 'https://www.isinwheel.com/cdn/shop/files/lQLPKG07iq9XX-8yMrAo-xE02uGvNAkTWuPGFHcD_50_50.png?v=1765524567',
        },
        {
          title: 'Secure Payments',
          description: 'PayPal / Credit Card / Installment Payments',
          icon: 'https://www.isinwheel.com/cdn/shop/files/lQLPJxnibT25nu8yMrD6FoVHdv_MjQkTWuPGFHcF_50_50.png?v=1765524566',
        },
      ],
)

const footerNavGroups = computed(() => [
  {
    title: 'EXPLORE',
    links: [
      { label: 'All Electric Scooter', to: '/collections/electric-scooters' },
      { label: 'Off Road Electric Scooter', to: '/collections/electric-scooters' },
      { label: 'Commuter Electric Scooter', to: '/collections/electric-scooters' },
      { label: 'Electric Bicycle', to: '/collections/electric-bike' },
      { label: 'Electric Skateboards', to: '/collections/electric-skateboard' },
      { label: 'Kids Scooter', to: '/collections/electric-scooters' },
      { label: 'Accessories', to: '/collections/accessories' },
      { label: 'Exclusive Discount', to: '/pages/contact-us' },
    ],
  },
  {
    title: 'SUPPORT',
    links: [
      { label: 'Shipping Policy', to: '/pages/contact-us' },
      { label: 'Refund & Return Policy', to: '/pages/contact-us' },
      { label: 'Warranty', to: '/pages/contact-us' },
      { label: 'Order Status', to: '/account/orders' },
      { label: 'Payment Methods', to: '/pages/contact-us' },
      { label: 'Buy Now Pay Later', to: '/pages/contact-us' },
      { label: 'Unauthorized Warning', to: '/pages/contact-us' },
      { label: 'FAQ', to: '/pages/support-faq' },
    ],
  },
  {
    title: 'COMPANY',
    links: [
      { label: 'About Us', to: '/pages/about-us-1' },
      { label: 'Contact Us', to: '/pages/contact-us' },
      { label: 'Terms Of Conditions', to: '/account/profile' },
      { label: 'Privacy Policy', to: '/account/profile' },
      { label: 'Become A Dealer', to: '/account/profile' },
      { label: 'Affiliate Program', to: '/account/profile' },
    ],
  },
])

const paymentMethods = [
  'Amex',
  'Apple Pay',
  'Discover',
  'Google Pay',
  'Mastercard',
  'PayPal',
  'Shop Pay',
  'Visa',
]

const copy = computed(() =>
  lang.value === 'zh'
    ? {
        communityTitle: '加入我们的社区',
        communityCta: '立即加入',
        hours: '周一至周五 10AM-6PM(PDT)',
        copyright: '© 2026 isinwheel 官方商店',
        refund: '退款政策',
        privacy: '隐私政策',
        terms: '服务条款',
        contact: '联系我们',
        newsletterTitle: '订阅我们的每周资讯，获取最新动态',
        newsletterPlaceholder: '输入你的邮箱',
      }
    : {
        communityTitle: 'Join Our Community',
        communityCta: 'Join Now',
        hours: 'Mon-Fri: 10AM-6PM(PDT)',
        copyright: '© 2026 isinwheel Official Store.',
        refund: 'Refund policy',
        privacy: 'Privacy policy',
        terms: 'Terms of service',
        contact: 'Contact information',
        newsletterTitle: 'Stay in the loop with our weekly newsletter',
        newsletterPlaceholder: 'Enter your email',
      },
)
</script>

<style scoped lang="scss">
.app-footer {
  color: #fff;
  background: #1f1f1f;
}

.footer-community {
  position: relative;
  overflow: hidden;
  background:
    linear-gradient(90deg, rgba(44, 92, 179, 0.94), rgba(127, 200, 243, 0.72)),
    url('https://www.isinwheel.com/cdn/shop/files/8_da6db915-51f1-4fa5-8ac2-0a26b20c2281.png?v=1765433912')
      center center / cover no-repeat;

  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(90deg, rgba(15, 43, 88, 0.36), rgba(15, 43, 88, 0.04));
    pointer-events: none;
  }
}

.community-shell {
  position: relative;
  z-index: 1;
  min-height: 340px;
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(320px, 0.9fr);
  align-items: center;
  gap: 48px;
  padding-top: 28px;
  padding-bottom: 28px;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24px;
}

.service-card {
  display: flex;
  align-items: center;
  gap: 20px;
  min-height: 126px;
  padding: 22px 18px;
  border: 1px solid rgba(255, 255, 255, 0.74);
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(8px);

  h3 {
    font-size: 18px;
    font-weight: 700;
    margin-bottom: 6px;
    line-height: 1.3;
  }

  p {
    color: rgba(255, 255, 255, 0.92);
    font-size: 13px;
    line-height: 1.5;
  }
}

.service-icon {
  width: 74px;
  height: 74px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  background: rgba(255, 255, 255, 0.96);
  flex-shrink: 0;

  img {
    width: 38px;
    height: 38px;
    object-fit: contain;
  }
}

.community-copy {
  justify-self: end;
  max-width: 500px;

  h2 {
    font-size: clamp(40px, 5vw, 72px);
    line-height: 0.94;
    font-weight: 700;
    margin-bottom: 30px;
    text-shadow: 0 10px 30px rgba(0, 0, 0, 0.16);
  }
}

.community-button {
  display: inline-flex;
  align-items: center;
  gap: 14px;
  padding: 10px 20px 10px 10px;
  border-radius: 999px;
  color: #101010;
  background: #fff;
  font-weight: 700;
  box-shadow: 0 12px 25px rgba(17, 29, 56, 0.16);
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 16px 30px rgba(17, 29, 56, 0.22);
  }
}

.community-button__icon {
  width: 40px;
  height: 40px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  background: #6fd43c;
  color: #fff;
  font-size: 24px;
  line-height: 1;
  font-weight: 500;
}

.community-button__arrow {
  width: 16px;
  height: 16px;
}

.footer-main {
  background: #1f1f1f;
}

.footer-main__inner {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(320px, 0.95fr);
  align-items: start;
  gap: 42px;
  padding-top: 18px;
  padding-bottom: 14px;
}

.footer-main__left {
  display: grid;
  gap: 54px;
  min-width: 0;
  padding-right: 42px;
  border-right: 1px solid rgba(255, 255, 255, 0.12);
}

.footer-link-columns {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  align-items: start;
  gap: 28px;
}

.footer-link-group {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  min-width: 0;

  h3 {
    margin-bottom: 24px;
    font-size: 18px;
    font-weight: 800;
  }
}

.footer-link-list {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 14px;
  width: 100%;

  a {
    display: block;
    width: 100%;
    color: rgba(255, 255, 255, 0.9);
    font-size: 14px;
    line-height: 1.25;
    transition: color 0.2s ease;

    &:hover {
      color: #fff;
    }
  }
}

.footer-contact__meta {
  display: grid;
  gap: 14px;

  p {
    font-size: 22px;
    font-weight: 700;
    color: #fff;
  }

  a {
    width: fit-content;
    font-size: 24px;
    line-height: 1.1;
    color: #fff;
    text-decoration: underline;
    text-underline-offset: 7px;
    transition: opacity 0.2s ease;

    &:hover {
      opacity: 0.84;
    }
  }
}

.footer-main__right {
  display: grid;
  align-content: space-between;
  align-items: start;
  gap: 36px;
  min-width: 0;
  padding-left: 18px;
}

.footer-newsletter {
  width: 100%;
  max-width: 390px;

  h3 {
    margin-bottom: 28px;
    font-size: clamp(34px, 4vw, 58px);
    line-height: 0.98;
    font-weight: 700;
  }
}

.footer-newsletter__form {
  position: relative;
  width: 100%;

  input {
    display: block;
    width: 100%;
    height: 56px;
    padding: 0 72px 0 22px;
    border: none;
    border-radius: 14px;
    background: #2c2c2c;
    color: #fff;
    font-size: 16px;
    outline: none;
  }

  input::placeholder {
    color: rgba(255, 255, 255, 0.56);
  }

  button {
    position: absolute;
    right: 12px;
    top: 50%;
    width: 40px;
    height: 40px;
    display: grid;
    place-items: center;
    border: none;
    border-radius: 999px;
    transform: translateY(-50%);
    background: #fff;
    color: #111;
    cursor: pointer;
  }
}

.footer-socials {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 22px;

  a {
    width: 22px;
    height: 22px;
    display: grid;
    place-items: center;
    color: rgba(255, 255, 255, 0.9);
    transition:
      color 0.2s ease,
      transform 0.2s ease;

    &:hover {
      color: #fff;
      transform: translateY(-1px);
    }
  }

  svg {
    width: 22px;
    height: 22px;
  }
}

.footer-socials__text {
  font-size: 18px;
  font-weight: 800;
  text-transform: lowercase;
}

.footer-bottom {
  background: #141414;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.footer-bottom__inner {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto auto;
  align-items: center;
  gap: 20px;
  padding-top: 16px;
  padding-bottom: 16px;
}

.footer-bottom__legal {
  display: grid;
  gap: 8px;

  p {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.88);
  }
}

.footer-bottom__links {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;

  a {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.66);

    &:hover {
      color: #fff;
    }
  }
}

.footer-region {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 9px 14px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.02);
  color: rgba(255, 255, 255, 0.88);
  font-size: 11px;

  svg {
    width: 14px;
    height: 14px;
  }
}

.footer-payments {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 6px;
}

.payment-badge {
  padding: 4px 7px;
  border-radius: 4px;
  background: #fff;
  color: #111;
  font-size: 10px;
  font-weight: 700;
  line-height: 1;
}

@media (max-width: 1180px) {
  .community-shell,
  .footer-main__inner {
    grid-template-columns: 1fr;
  }

  .community-copy {
    justify-self: start;
    max-width: none;
  }

  .footer-main__left {
    padding-right: 0;
    border-right: none;
    border-bottom: 1px solid rgba(255, 255, 255, 0.12);
    padding-bottom: 28px;
  }

  .footer-main__right {
    padding-left: 0;
  }
}

@media (max-width: 768px) {
  .community-shell {
    min-height: auto;
    gap: 24px;
    padding-top: 28px;
    padding-bottom: 28px;
  }

  .service-grid,
  .footer-link-columns {
    grid-template-columns: 1fr;
  }

  .footer-main__left,
  .footer-main__right {
    gap: 28px;
  }

  .footer-link-group h3 {
    margin-bottom: 14px;
  }

  .service-card {
    min-height: auto;
    padding: 18px 16px;

    h3 {
      font-size: 16px;
    }
  }

  .community-copy h2 {
    margin-bottom: 18px;
  }

  .footer-main__inner,
  .footer-bottom__inner {
    gap: 24px;
  }

  .footer-contact__meta {
    p {
      font-size: 18px;
    }

    a {
      font-size: 18px;
    }
  }

  .footer-newsletter h3 {
    margin-bottom: 20px;
  }

  .footer-bottom__inner {
    grid-template-columns: 1fr;
    justify-items: start;
  }

  .footer-payments {
    justify-content: flex-start;
  }
}
</style>
