<script setup lang="ts">
import TheHeader from '@/components/TheHeader.vue'
</script>

<template>
  <TheHeader />
  <main>
    <RouterView />
  </main>
</template>

<style scoped>
main {
  padding-top: 4.5rem;
  flex: 1;
}
</style>
