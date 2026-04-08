<template>
  <div class="layout-blank">
    <slot />
  </div>
</template>

<script setup>
</script>

<style scoped>
.layout-blank {
  min-height: 100svh;
  height: 100svh;
  background-color: #fff;
  overflow: hidden;
}
</style>
