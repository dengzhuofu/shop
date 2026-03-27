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
    required: true
  }
})
</script>

<style lang="scss" scoped>
.nav-dropdown {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  background: $white;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  padding: 16px 0;
  min-width: 200px;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  z-index: 100;
  pointer-events: none;
  margin-top: -10px; // 根据需要调整，因为 nav-item 的 padding 撑大了高度

  &::before {
    content: '';
    position: absolute;
    top: -20px;
    left: 0;
    width: 100%;
    height: 20px;
  }

  ul {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      a {
        display: block;
        padding: 10px 24px;
        color: $text-color;
        text-decoration: none;
        font-size: 15px;
        font-weight: 500;
        transition: all 0.2s ease;

        &:hover {
          color: #58cc02;
          background: rgba(88, 204, 2, 0.05);
        }
      }
    }
  }
}

// 配合父组件的 hover 效果
.nav-item:hover .nav-dropdown {
  opacity: 1;
  visibility: visible;
  transform: translateX(-50%) translateY(0);
  pointer-events: auto;
}
</style>
