<template>
  <div class="faq-page">
    <section class="container faq-shell">
      <div class="faq-head">
        <h1>FAQ's</h1>
        <p>Welcome to our FAQ, we are so happy to have you here and as a client.</p>
        <p>We have tried to answer the most common questions.</p>
      </div>

      <div class="faq-layout">
        <div class="faq-groups">
          <article
            v-for="group in visibleGroups"
            :key="group.title"
            class="faq-group"
          >
            <div class="faq-group__header">
              <h2>{{ group.title }}</h2>
              <p>{{ group.description }}</p>
            </div>

            <details
              v-for="question in group.items"
              :key="question.question"
              class="faq-item"
              :open="question.defaultOpen"
            >
              <summary>
                <span>{{ question.question }}</span>
                <span class="faq-item__icon" aria-hidden="true">+</span>
              </summary>
              <div class="faq-item__answer" v-html="question.answer" />
            </details>
          </article>

          <div v-if="showMore" class="faq-extra">
            <h3>More help</h3>
            <p>
              If you still need support, send your order number and a short note
              to <a href="mailto:support@isinwheel.com">support@isinwheel.com</a>.
            </p>
            <div class="faq-extra__links">
              <a
                href="https://www.isinwheel.com/pages/shipping-policy"
                target="_blank"
                rel="noreferrer"
              >
                Shipping policy
              </a>
              <a
                href="https://www.17track.net/en"
                target="_blank"
                rel="noreferrer"
              >
                Track package
              </a>
            </div>
          </div>

          <div class="faq-actions">
            <button type="button" class="faq-toggle" @click="showMore = !showMore">
              {{ showMore ? 'Show Less' : 'Show More' }}
            </button>
          </div>
        </div>

        <aside class="faq-contact">
          <h2>Didn't find your answer?</h2>
          <p>Please feel free to contact us</p>

          <form class="faq-contact__form" @submit.prevent="submitted = true">
            <input v-model="form.name" type="text" placeholder="Name" />
            <input v-model="form.email" type="email" placeholder="Email" />
            <textarea
              v-model="form.message"
              rows="6"
              placeholder="Message"
            />
            <button type="submit">Send message</button>
          </form>

          <p v-if="submitted" class="faq-contact__success">
            Your message is ready to be handed off to support.
          </p>
        </aside>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'

useSeoMeta({
  title: "FAQ's",
  description:
    'Browse shipping, warranty, order, and product questions from the isinwheel FAQ page.',
})

const showMore = ref(false)
const submitted = ref(false)

const form = reactive({
  name: '',
  email: '',
  message: '',
})

const faqGroups = [
  {
    title: 'Shipping & Returns',
    description: 'Shipping, Returns, Warranty, Refund, and Exchanges',
    items: [
      {
        question: 'How long does it take to deliver?',
        answer:
          'Due to the nature of the item, ground shipping will be the only option. For more details click <a href="https://www.isinwheel.com/pages/shipping-policy" target="_blank" rel="noreferrer">here</a>.',
        defaultOpen: false,
      },
      {
        question: 'What is the warranty with this product?',
        answer:
          'We offer 12 months for the scooter. We take care of quality-related issues with replacement parts.',
        defaultOpen: false,
      },
      {
        question: 'What is your return policy?',
        answer:
          '30 Days Money Back: For 30 days after the date of purchase, return your undamaged and unused isinwheel product and receive a refund for any reason. Shipping costs for non-quality returns are not covered.',
        defaultOpen: false,
      },
    ],
  },
  {
    title: 'Orders',
    description: 'General questions and common questions about orders',
    items: [
      {
        question: 'Cancelling Your Order',
        answer:
          'If you would like to cancel your order, please contact us immediately via <a href="mailto:support@isinwheel.com">support@isinwheel.com</a> and include your order number. If the parcel has not left the warehouse, a refund can usually be issued right away.',
        defaultOpen: false,
      },
      {
        question: 'How long does it takes my order to ship?',
        answer:
          'Orders are usually fulfilled within 1 business day. Delivery normally takes 2 to 5 business days depending on your destination and carrier.',
        defaultOpen: false,
      },
      {
        question: 'How do I track my order?',
        answer:
          'You can track shipments via <a href="https://www.seur.com/en/" target="_blank" rel="noreferrer">SEUR</a> or <a href="https://www.17track.net/en" target="_blank" rel="noreferrer">17TRACK</a>.',
        defaultOpen: false,
      },
    ],
  },
  {
    title: 'Products',
    description: 'Electric scooters and electric bike related questions',
    items: [
      {
        question: 'How old do you need to be to ride an adult electric scooter?',
        answer:
          'We recommend all riders are at least 12 years of age before riding an electric scooter.',
        defaultOpen: false,
      },
      {
        question: 'Can you use an electric scooter in the rain?',
        answer:
          'This depends on the model IP rating. A rating of IP5 or higher indicates water resistance, meaning the scooter can handle rainy conditions more confidently.',
        defaultOpen: false,
      },
      {
        question: 'My Electric scooter is faulty/damaged?',
        answer:
          'If your scooter arrives damaged or develops a manufacturing fault within 30 days of delivery, you may be entitled to a repair, replacement, or refund. Contact <a href="mailto:support@isinwheel.com">support@isinwheel.com</a> to begin the process.',
        defaultOpen: false,
      },
      {
        question: 'Can I take my isinwheel products on an airplane?',
        answer:
          'Please check directly with your airline. Most scooters use batteries larger than 160Wh, which are generally not permitted on passenger flights.',
        defaultOpen: false,
      },
    ],
  },
]

const visibleGroups = computed(() => faqGroups)
</script>

<style scoped lang="scss">
.faq-page {
  background: #fff;
}

.faq-shell {
  padding-top: 36px;
  padding-bottom: 58px;
}

.faq-head {
  margin-bottom: 28px;

  h1 {
    margin: 0 0 12px;
    font-size: clamp(30px, 3.8vw, 46px);
    line-height: 1;
    font-weight: 800;
    color: #121212;
  }

  p {
    margin: 0;
    color: #666;
    font-size: 13px;
    line-height: 1.7;
  }
}

.faq-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.42fr) minmax(270px, 0.58fr);
  gap: 28px;
  align-items: start;
}

.faq-groups {
  display: grid;
  gap: 22px;
}

.faq-group,
.faq-extra {
  border: 1px solid #ebebeb;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 14px 34px rgba(17, 17, 17, 0.04);
}

.faq-group__header,
.faq-extra {
  padding: 24px 24px 0;
}

.faq-group__header {
  h2 {
    margin: 0;
    font-size: 28px;
    line-height: 1;
    font-weight: 800;
    color: #141414;
  }

  p {
    margin: 8px 0 0;
    font-size: 12px;
    color: #9b9b9b;
  }
}

.faq-item {
  margin-top: 8px;
  border-top: 1px solid #f1f1f1;

  &:first-of-type {
    margin-top: 18px;
  }

  summary {
    list-style: none;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    padding: 18px 24px;
    cursor: pointer;
    font-size: 15px;
    font-weight: 600;
    color: #1c1c1c;
  }

  summary::-webkit-details-marker {
    display: none;
  }

  &[open] .faq-item__icon {
    transform: rotate(45deg);
  }
}

.faq-item__icon {
  flex: none;
  font-size: 22px;
  line-height: 1;
  color: #8b8b8b;
  transition: transform 0.22s ease;
}

.faq-item__answer {
  padding: 0 24px 20px;
  color: #5e5e5e;
  font-size: 14px;
  line-height: 1.8;

  :deep(a) {
    color: #111;
    text-decoration: underline;
  }
}

.faq-extra {
  padding-bottom: 24px;

  h3 {
    margin: 0 0 10px;
    font-size: 20px;
    font-weight: 800;
    color: #151515;
  }

  p {
    margin: 0;
    color: #5d5d5d;
    line-height: 1.75;
  }

  a {
    color: #111;
    font-weight: 600;
  }
}

.faq-extra__links {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;

  a {
    display: inline-flex;
    align-items: center;
    min-height: 40px;
    padding: 0 16px;
    border-radius: 999px;
    background: #f5f7fa;
    text-decoration: none;
  }
}

.faq-actions {
  display: flex;
  justify-content: center;
}

.faq-toggle {
  min-width: 112px;
  min-height: 36px;
  padding: 0 18px;
  border: 1px solid #d6d6d6;
  border-radius: 999px;
  background: #fff;
  font-size: 12px;
  font-weight: 700;
  color: #111;
  transition:
    border-color 0.2s ease,
    transform 0.2s ease;

  &:hover {
    border-color: #111;
    transform: translateY(-1px);
  }
}

.faq-contact {
  padding-top: 10px;

  h2 {
    margin: 0;
    font-size: 28px;
    line-height: 1.05;
    font-weight: 800;
    color: #1a1a1a;
  }

  p {
    margin: 10px 0 0;
    font-size: 13px;
    color: #7b7b7b;
  }
}

.faq-contact__form {
  display: grid;
  gap: 12px;
  margin-top: 18px;

  input,
  textarea {
    width: 100%;
    border: 1px solid #f0f0f0;
    border-radius: 10px;
    background: #fafafa;
    padding: 14px 15px;
    font: inherit;
    color: #1e1e1e;
    outline: none;
    transition:
      border-color 0.2s ease,
      background-color 0.2s ease;

    &:focus {
      border-color: #a2bdf5;
      background: #fff;
    }
  }

  textarea {
    resize: vertical;
    min-height: 152px;
  }

  button {
    width: fit-content;
    min-height: 38px;
    padding: 0 20px;
    border-radius: 999px;
    background: #111;
    color: #fff;
    font-size: 12px;
    font-weight: 700;
    transition:
      opacity 0.2s ease,
      transform 0.2s ease;

    &:hover {
      opacity: 0.94;
      transform: translateY(-1px);
    }
  }
}

.faq-contact__success {
  color: #2f7d32;
  font-size: 12px;
  font-weight: 600;
}

@media (max-width: 980px) {
  .faq-layout {
    grid-template-columns: 1fr;
  }

  .faq-contact {
    padding-top: 0;
  }
}

@media (max-width: 640px) {
  .faq-shell {
    padding-top: 28px;
    padding-bottom: 48px;
  }

  .faq-group__header,
  .faq-extra {
    padding-left: 18px;
    padding-right: 18px;
  }

  .faq-item summary,
  .faq-item__answer {
    padding-left: 18px;
    padding-right: 18px;
  }

  .faq-group__header h2,
  .faq-contact h2 {
    font-size: 24px;
  }
}
</style>
