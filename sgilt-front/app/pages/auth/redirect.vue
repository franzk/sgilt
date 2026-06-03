<template>
  <div class="splash">
    <img class="logo" src="/favicon-512.png" alt="Sgilt" />
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: false })
</script>

<style lang="scss" scoped>
@use '@/assets/styles/colors' as *;

.splash {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;

  .logo {
    width: 96px;
    height: 96px;
    animation: breathe 1.6s ease-in-out infinite;
  }
}

@keyframes breathe {
  0%,
  100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.08);
    opacity: 0.75;
  }
}
</style>
