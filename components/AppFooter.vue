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

    <section class="footer-contact">
      <div class="container footer-contact__inner">
        <div class="footer-contact__meta">
          <p>{{ copy.hours }}</p>
          <a href="tel:+18882935788">+1(888)293-5788</a>
          <a href="mailto:support@isinwheel.com">support@isinwheel.com</a>
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
          title: '快速配送',
          description: '2-5 个工作日 UPS/FedEx 送达',
          icon: 'https://www.isinwheel.com/cdn/shop/files/lQLPJw6ECy5xnu8yMrDe9EqOZZlckwkTWuPGFHcC_50_50.png?v=1765524567',
        },
        {
          title: '1 年保修服务',
          description: '享受 isinwheel 有限保修支持',
          icon: 'https://www.isinwheel.com/cdn/shop/files/lQLPJxLr0S33Hu8yMrCQIEE417wfBAkTWuPGFHcE_50_50.png?v=1765524567',
        },
        {
          title: '美国本地支持',
          description: '在线聊天和电话服务',
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
    linear-gradient(90deg, rgba(55, 109, 201, 0.94), rgba(105, 180, 233, 0.82)),
    url('https://www.isinwheel.com/cdn/shop/files/8_da6db915-51f1-4fa5-8ac2-0a26b20c2281.png?v=1765433912')
      center bottom / cover no-repeat;

  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(90deg, rgba(15, 43, 88, 0.42), rgba(15, 43, 88, 0));
    pointer-events: none;
  }
}

.community-shell {
  position: relative;
  z-index: 1;
  min-height: 340px;
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(280px, 0.85fr);
  align-items: center;
  gap: 48px;
  padding-top: 38px;
  padding-bottom: 38px;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.service-card {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 20px 18px;
  border: 1px solid rgba(255, 255, 255, 0.45);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(8px);

  h3 {
    font-size: 18px;
    font-weight: 700;
    margin-bottom: 6px;
    line-height: 1.25;
  }

  p {
    color: rgba(255, 255, 255, 0.84);
    font-size: 13px;
    line-height: 1.5;
  }
}

.service-icon {
  width: 58px;
  height: 58px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  background: rgba(255, 255, 255, 0.92);
  flex-shrink: 0;

  img {
    width: 32px;
    height: 32px;
    object-fit: contain;
  }
}

.community-copy {
  justify-self: end;
  max-width: 360px;

  h2 {
    font-family: inherit;
    font-size: clamp(34px, 4vw, 56px);
    line-height: 0.95;
    font-weight: 700;
    margin-bottom: 28px;
    text-shadow: 0 10px 30px rgba(0, 0, 0, 0.18);
  }
}

.community-button {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 8px 18px 8px 8px;
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
  width: 38px;
  height: 38px;
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

.footer-contact {
  background: #212121;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.footer-contact__inner {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  padding-top: 18px;
  padding-bottom: 18px;
}

.footer-contact__meta {
  display: grid;
  gap: 6px;

  p,
  a {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.88);
  }

  a {
    width: fit-content;

    &:hover {
      color: #fff;
    }
  }
}

.footer-socials {
  display: flex;
  align-items: center;
  gap: 16px;

  a {
    width: 18px;
    height: 18px;
    display: grid;
    place-items: center;
    color: rgba(255, 255, 255, 0.86);
    transition:
      color 0.2s ease,
      transform 0.2s ease;

    &:hover {
      color: #fff;
      transform: translateY(-1px);
    }
  }

  svg {
    width: 18px;
    height: 18px;
  }
}

.footer-socials__text {
  font-size: 14px;
  font-weight: 800;
  text-transform: lowercase;
}

.footer-bottom {
  background: #141414;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.footer-bottom__inner {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto auto;
  align-items: center;
  gap: 20px;
  padding-top: 14px;
  padding-bottom: 14px;
}

.footer-bottom__legal {
  display: grid;
  gap: 8px;

  p {
    font-size: 11px;
    color: rgba(255, 255, 255, 0.88);
  }
}

.footer-bottom__links {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;

  a {
    font-size: 11px;
    color: rgba(255, 255, 255, 0.62);

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

@media (max-width: 1100px) {
  .community-shell {
    grid-template-columns: 1fr;
  }

  .community-copy {
    justify-self: start;
    max-width: none;
  }
}

@media (max-width: 768px) {
  .community-shell {
    min-height: auto;
    gap: 24px;
    padding-top: 28px;
    padding-bottom: 28px;
  }

  .service-grid {
    grid-template-columns: 1fr;
  }

  .service-card {
    padding: 18px 16px;

    h3 {
      font-size: 16px;
    }
  }

  .community-copy h2 {
    margin-bottom: 18px;
  }

  .footer-contact__inner,
  .footer-bottom__inner {
    grid-template-columns: 1fr;
    display: grid;
    justify-items: start;
  }

  .footer-payments {
    justify-content: flex-start;
  }
}
</style>
