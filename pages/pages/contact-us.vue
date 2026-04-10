<template>
  <div class="contact-page">
    <section class="container contact-shell">
      <div class="contact-intro">
        <h1>Contact Us</h1>
        <p class="contact-intro__lead">
          Welcome to isinwheel. If you have any questions, please feel free to
          contact us. We will be happy to serve you better.
        </p>
        <div class="contact-intro__details">
          <p><strong>Hours:</strong> 10AM-6PM(PDT)</p>
          <p><strong>Open:</strong> Monday to Friday</p>
          <p><strong>Tel:</strong> +1(888)293-5788</p>
          <p><strong>Email:</strong> support@isinwheel.com</p>
          <p><strong>Add:</strong> 840 F AVE, PLANO, TX 75074</p>
          <p>
            Contact us via email or tel, and our team will get back to you as
            soon as possible.
          </p>
          <p class="contact-intro__note">
            Note: To get your email faster processed, please indicate the order
            number in the title of your email. If you experience a product
            failure, please film a video and attach it to the email.
          </p>
        </div>
      </div>

      <div class="contact-grid">
        <form class="contact-form" @submit.prevent="handleSubmit">
          <div class="contact-form__grid">
            <input v-model="form.name" type="text" placeholder="Name" />
            <input v-model="form.email" type="email" placeholder="Email" />
            <input
              v-model="form.phone"
              type="text"
              placeholder="Phone number"
            />
            <select v-model="form.subject">
              <option value="General inquiry">Subject</option>
              <option value="Order support">Order support</option>
              <option value="Warranty">Warranty</option>
              <option value="Partnership">Partnership</option>
            </select>
            <textarea
              v-model="form.message"
              rows="7"
              placeholder="Message"
            />
          </div>

          <button class="contact-form__button" type="submit">
            Send message
          </button>
          <p v-if="submitted" class="contact-form__success">
            Thanks. Your message has been staged locally for this restore page.
          </p>
          <p class="contact-form__policy">
            This site is protected by hCaptcha and the hCaptcha Privacy Policy
            and Terms of Service apply.
          </p>
        </form>

        <aside class="contact-sidebar">
          <div class="contact-sidebar__block">
            <p class="contact-sidebar__label">Address</p>
            <p>840 F AVE,</p>
            <p>PLANO, TX 75074</p>
          </div>

          <div class="contact-sidebar__block">
            <p class="contact-sidebar__label">Email</p>
            <a href="mailto:support@isinwheel.com">support@isinwheel.com</a>
          </div>

          <div class="contact-sidebar__block">
            <p class="contact-sidebar__label">Phone</p>
            <p>Mon - Fri</p>
            <a href="tel:+18882935788">+1(888)293-5788</a>
          </div>

          <div class="contact-sidebar__block">
            <p class="contact-sidebar__label">Follow us</p>
            <div class="contact-sidebar__socials">
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
                class="contact-sidebar__social-text"
              >
                t
              </a>
            </div>
          </div>
        </aside>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { FacebookIcon, InstagramIcon, YoutubeIcon } from 'lucide-vue-next'

useSeoMeta({
  title: 'Contact Us',
  description:
    'Get in touch with isinwheel support for orders, warranty requests, and product questions.',
})

const submitted = ref(false)
const form = reactive({
  name: '',
  email: '',
  phone: '',
  subject: 'General inquiry',
  message: '',
})

const handleSubmit = () => {
  submitted.value = true
}
</script>

<style scoped lang="scss">
.contact-page {
  background: #fff;
}

.contact-shell {
  padding-top: 42px;
  padding-bottom: 60px;
}

.contact-intro {
  max-width: 980px;
  margin-bottom: 34px;

  h1 {
    font-family: inherit;
    font-size: clamp(32px, 4vw, 48px);
    line-height: 1;
    font-weight: 800;
    margin-bottom: 16px;
  }
}

.contact-intro__lead,
.contact-intro__details p {
  font-size: 14px;
  line-height: 1.8;
  color: #2d2d2d;
}

.contact-intro__lead {
  font-weight: 600;
}

.contact-intro__details {
  display: grid;
  gap: 2px;
  margin-top: 8px;
}

.contact-intro__note {
  margin-top: 4px;
  font-weight: 700;
}

.contact-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 280px;
  gap: 28px;
  align-items: start;
}

.contact-form {
  padding-top: 6px;
}

.contact-form__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;

  input,
  select,
  textarea {
    width: 100%;
    border: 1px solid #f0f0f0;
    background: #fafafa;
    border-radius: 10px;
    padding: 15px 16px;
    font: inherit;
    color: #202020;
    outline: none;
    transition:
      border-color 0.2s ease,
      background-color 0.2s ease;

    &:focus {
      border-color: #8eb6f7;
      background: #fff;
    }
  }

  textarea {
    grid-column: 1 / -1;
    resize: vertical;
    min-height: 150px;
  }

  select {
    appearance: none;
  }
}

.contact-form__button {
  margin-top: 18px;
  padding: 13px 24px;
  border-radius: 999px;
  background: #111;
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  transition:
    transform 0.2s ease,
    opacity 0.2s ease;

  &:hover {
    transform: translateY(-1px);
    opacity: 0.94;
  }
}

.contact-form__success,
.contact-form__policy {
  margin-top: 12px;
  font-size: 11px;
  line-height: 1.6;
  color: #777;
}

.contact-form__success {
  color: #2c8b2d;
  font-weight: 600;
}

.contact-sidebar {
  padding: 8px 0 0 18px;
  border-left: 1px solid #f1f1f1;
  display: grid;
  gap: 22px;
}

.contact-sidebar__block {
  display: grid;
  gap: 4px;

  p,
  a {
    color: #2d2d2d;
    font-size: 13px;
    line-height: 1.7;
  }

  a:hover {
    color: #000;
  }
}

.contact-sidebar__label {
  color: #a3a3a3 !important;
  font-size: 11px !important;
  font-weight: 700;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.contact-sidebar__socials {
  display: flex;
  align-items: center;
  gap: 14px;

  a {
    width: 18px;
    height: 18px;
    display: grid;
    place-items: center;
    color: #111;
  }

  svg {
    width: 18px;
    height: 18px;
  }
}

.contact-sidebar__social-text {
  font-size: 14px !important;
  font-weight: 800;
  text-transform: lowercase;
}

@media (max-width: 900px) {
  .contact-grid {
    grid-template-columns: 1fr;
  }

  .contact-sidebar {
    padding-left: 0;
    border-left: 0;
    border-top: 1px solid #f1f1f1;
    padding-top: 22px;
  }
}

@media (max-width: 640px) {
  .contact-form__grid {
    grid-template-columns: 1fr;
  }
}
</style>
