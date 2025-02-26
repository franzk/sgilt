<template>
  <div class="price-item">
    <span class="price-item-icon"><SgiltIcon :icon="icon" /></span>
    <span class="price-item-label">{{ label }}</span>
  </div>
</template>

<script setup lang="ts">
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'

defineProps<{
  icon: string
  label: string
}>()
</script>

<style scoped lang="scss">
.price-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  background: #f8f8f8;
  font-size: 0.9rem;
  font-weight: 500;
  color: #333;
  box-shadow: inset 0px -2px 3px rgba(0, 0, 0, 0.1);

  .price-icon {
    font-size: 1.2rem;
  }
}
</style>
