<template>
  <div class="pro-greeting">
    <p class="title">{{ $t('pro.board.greeting.title') }}</p>
    <div v-if="loading" class="skeleton skeleton-text" />
    <p v-else class="subtitle">{{ subtitle }}</p>
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

  .title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 2.1rem;
    font-weight: 700;
    color: $brand-primary;
    margin: 0;
    line-height: 1.1;
  }

  .subtitle {
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    color: $text-secondary;
    margin: 0;
    margin-top: 4px;
  }

  .skeleton {
    margin-top: 6px;
    height: 0.8rem;
    width: 220px;
    border-radius: 4px;
  }
}
</style>
