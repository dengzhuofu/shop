<template>
  <div class="layout-default">
    <AppHeader />
    <main class="page-shell">
      <slot />
    </main>
    <AppFooter />
    <CartSidebar />
  </div>
</template>

<script setup lang="ts"></script>

<style scoped lang="scss">
.layout-default {
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(15, 118, 110, 0.08), transparent 28%),
    radial-gradient(circle at right 20%, rgba(249, 115, 22, 0.08), transparent 30%),
    #f8f7f1;
}

.page-shell {
  min-height: calc(100vh - 160px);
}
</style>
