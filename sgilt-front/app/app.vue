<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
</template>

<script setup lang="ts">
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

:root {
  color-scheme: light;
  * {
    -webkit-tap-highlight-color: transparent;
    outline: none;
  }
}
</style>
