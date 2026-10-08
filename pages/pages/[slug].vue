<template>
  <div class="policy-page">
    <div class="container policy-shell">
      <nav class="breadcrumbs"><NuxtLink to="/">{{ isZh ? '首页' : 'Home' }}</NuxtLink><span>/</span><span>{{ page.title }}</span></nav>
      <header class="policy-header"><span class="eyebrow">CBJJ · {{ isZh ? '服务与政策' : 'Support & policies' }}</span><h1>{{ page.title }}</h1><p>{{ page.intro }}</p><small>{{ isZh ? '生效日期：2026年9月17日' : 'Effective September 17, 2026' }}</small></header>
      <div class="policy-layout">
        <aside><nav :aria-label="isZh ? '页面目录' : 'On this page'"><a v-for="(item, index) in page.sections" :key="index" :href="`#section-${index}`">{{ item.title }}</a></nav><NuxtLink to="/pages/contact-us" class="support-link">{{ isZh ? '联系 CBJJ 客服' : 'Contact CBJJ support' }} →</NuxtLink></aside>
        <article>
          <img v-if="slug === 'klarna'" src="/cbjj/payments/klarna.svg" alt="Klarna" class="klarna-logo" />
          <section v-for="(item, index) in page.sections" :id="`section-${index}`" :key="index"><h2>{{ item.title }}</h2><p v-for="text in item.text" :key="text">{{ text }}</p><ul v-if="item.links"><li v-for="link in item.links" :key="link.href"><a :href="link.href" target="_blank" rel="noopener noreferrer">{{ link.label }} ↗</a></li></ul></section>
          <div class="policy-contact"><strong>CBJJ</strong><span>{{ brand.operator }}</span><a :href="`mailto:${brand.email}`">{{ brand.email }}</a></div>
        </article>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { policies } from '~/data/policies'
import { brand } from '~/data/brand'
const route = useRoute()
const { isZh } = useShopLocale()
const slug = computed(() => String(route.params.slug))
if (!policies[slug.value]) throw createError({ statusCode: 404, statusMessage: 'Page not found' })
const page = computed(() => policies[slug.value][isZh.value ? 'zh' : 'en'])
useSeoMeta({ title: () => `${page.value.title} | CBJJ`, description: () => page.value.intro })
</script>
<style scoped lang="scss">
.policy-layout > * { min-width: 0; }
@media(max-width:768px) { .policy-layout { grid-template-columns:minmax(0,1fr) !important; } .policy-layout aside nav { max-width:100%; } }
.policy-page{background:#f7f8f6}.policy-shell{padding-top:28px;padding-bottom:72px}.breadcrumbs{display:flex;gap:12px;font-size:13px;color:#586153}.policy-header{padding:42px 0;max-width:850px}.eyebrow{text-transform:uppercase;letter-spacing:2px;font-size:12px;font-weight:700;color:#397c0c}.policy-header h1{font-size:clamp(32px,5vw,52px);line-height:1.15;margin:12px 0 20px}.policy-header p{font-size:18px;line-height:1.65;color:#4d5749;margin-bottom:14px}.policy-header small{color:#586153}.policy-layout{display:grid;grid-template-columns:250px minmax(0,1fr);gap:42px}.policy-layout aside{position:sticky;top:130px;align-self:start}.policy-layout aside nav{display:grid;gap:4px}.policy-layout aside a{padding:10px 12px;font-size:14px;border-radius:8px}.policy-layout aside a:hover{background:#e9efdf}.support-link{display:block;margin-top:24px;color:#286507;font-weight:700}.policy-layout article{background:#fff;padding:clamp(24px,4vw,48px);border:1px solid #e2e7de;border-radius:16px;min-width:0}.policy-layout section{scroll-margin-top:150px;margin-bottom:36px}.policy-layout h2{font-size:23px;line-height:1.4;margin-bottom:14px}.policy-layout p{font-size:16px;line-height:1.85;color:#434b3f;margin-bottom:12px;overflow-wrap:anywhere}.policy-layout ul{padding-left:22px;display:grid;gap:12px}.policy-layout article a{color:#286507;text-decoration:underline;text-underline-offset:4px}.policy-contact{border-top:1px solid #e2e7de;padding-top:24px;display:grid;gap:8px;font-size:14px}.policy-contact strong{font-size:24px}.klarna-logo{width:120px;height:auto;margin-bottom:32px}a:focus-visible{outline:3px solid #397c0c;outline-offset:3px}@media(max-width:768px){.policy-layout{grid-template-columns:1fr;gap:20px}.policy-layout aside{position:static}.policy-layout aside nav{display:flex;overflow:auto;padding-bottom:8px}.policy-layout aside nav a{white-space:nowrap;border:1px solid #dbe2d4}.support-link{margin-top:8px}.policy-header{padding:28px 0}}
</style>
