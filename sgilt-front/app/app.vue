<template>
  <AppHeader />
  <section class="app-content">
    <NuxtPage />
  </section>
</template>

<script setup lang="ts">
import AppHeader from '@/components/AppHeader.vue'
useHead({
  meta: [
    { name: 'robots', content: 'noindex, nofollow' },
    { name: 'theme-color', content: '#ffffff' },
    { name: 'color-scheme', content: 'light' },
  ],
})
</script>

<style lang="scss">
@use '@/assets/styles/base' as *;

body {
  margin: 0;
  padding: 0;
}

.app-content {
  padding-top: $app-header-height; // pour compenser la hauteur du header fixe
}

:root {
  color-scheme: light;
  * {
    -webkit-tap-highlight-color: transparent;
  }
}
</style>
