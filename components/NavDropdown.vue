<template>
  <div class="nav-dropdown">
    <ul>
      <li v-for="link in links" :key="link.title">
        <NuxtLink :to="link.url">{{ link.title }}</NuxtLink>
      </li>
    </ul>
  </div>
</template>

<script setup>
defineProps({
  links: {
    type: Array,
    required: true,
  },
})
</script>

<style lang="scss" scoped>
.nav-dropdown {
  position: absolute;
  top: 100%;
  left: 50%;
  overflow: hidden;
  min-width: 256px;
  padding: 18px 0 20px;
  border: 1px solid rgba(17, 17, 17, 0.04);
  border-radius: 28px;
  background: #fff;
  box-shadow:
    0 18px 34px rgba(15, 23, 42, 0.1),
    0 4px 14px rgba(15, 23, 42, 0.06);
  opacity: 0;
  visibility: hidden;
  pointer-events: none;
  z-index: 140;
  margin-top: 10px;
  clip-path: inset(0 100% 0 0 round 28px);
  transform: translateX(-50%) translateY(0);
  transform-origin: top left;
  transition:
    clip-path 0.34s cubic-bezier(0.22, 1, 0.36, 1),
    opacity 0.22s ease,
    visibility 0.22s ease;

  &::before {
    content: '';
    position: absolute;
    top: -16px;
    left: 0;
    width: 100%;
    height: 16px;
  }

  ul {
    margin: 0;
    padding: 0;
    list-style: none;
  }

  li {
    opacity: 0;
    transform: translateX(-14px);
    transition:
      opacity 0.24s ease,
      transform 0.28s cubic-bezier(0.22, 1, 0.36, 1);
  }

  li:nth-child(1) { transition-delay: 0.08s; }
  li:nth-child(2) { transition-delay: 0.1s; }
  li:nth-child(3) { transition-delay: 0.12s; }
  li:nth-child(4) { transition-delay: 0.14s; }
  li:nth-child(5) { transition-delay: 0.16s; }
  li:nth-child(6) { transition-delay: 0.18s; }
  li:nth-child(7) { transition-delay: 0.2s; }
  li:nth-child(8) { transition-delay: 0.22s; }
  li:nth-child(9) { transition-delay: 0.24s; }
  li:nth-child(10) { transition-delay: 0.26s; }

  li + li {
    margin-top: 1px;
  }

  a {
    display: block;
    padding: 8px 28px;
    color: #1f1f1f;
    text-decoration: none;
    font-size: 15px;
    font-weight: 500;
    line-height: 1.34;
    transition:
      color 0.2s ease,
      padding-left 0.2s ease,
      transform 0.2s ease;

    &:hover {
      color: #111;
      padding-left: 31px;
      transform: translateX(1px);
    }
  }
}

.nav-item:hover .nav-dropdown {
  opacity: 1;
  visibility: visible;
  pointer-events: auto;
  clip-path: inset(0 0 0 0 round 28px);
}

.nav-item:hover .nav-dropdown li {
  opacity: 1;
  transform: translateX(0);
}
</style>
