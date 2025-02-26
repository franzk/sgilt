<template>
  <h3 class="partner-title">
    <img :src="partner?.imageUrl" class="partner-image" />
    <span class="partner-name">{{ partner?.title }}</span>
  </h3>
</template>

<script setup lang="ts">
import type { Partner } from '@/data/domain/Partner'

defineProps<{
  partner?: Partner
}>()
</script>

<style scoped lang="scss">
.partner-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: $spacing-s $spacing-s 0 $spacing-s;
  margin: 0;
  .partner-image {
    height: 3rem;
    aspect-ratio: 1;
    border-radius: 100%;
    object-fit: cover;
  }
}
</style>
