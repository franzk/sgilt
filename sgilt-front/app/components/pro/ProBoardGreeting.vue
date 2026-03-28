<template>
  <div class="pro-greeting">
    <p class="pro-greeting__title">Bonjour DJ Animation !</p>
    <div v-if="loading" class="pro-greeting__skeleton skeleton-text" />
    <p v-else class="pro-greeting__subtitle">{{ subtitle }}</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{ subtitle: string; loading?: boolean }>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.pro-greeting {
  display: flex;
  flex-direction: column;

  @media (min-width: $breakpoint-desktop) {
    gap: $spacing-l;
  }
}

.pro-greeting__title {
  font-family: 'Cormorant Garamond', serif;
  font-size: 2.1rem;
  font-weight: 700;
  color: $brand-primary;
  margin: 0;
  line-height: 1.1;
}

.pro-greeting__subtitle {
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  color: $text-secondary;
  margin: 0;
  margin-top: 4px;
}

.pro-greeting__skeleton {
  margin-top: 6px;
  height: 0.8rem;
  width: 220px;
  border-radius: 4px;
}
</style>
