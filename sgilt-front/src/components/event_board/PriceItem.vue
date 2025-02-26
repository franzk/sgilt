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
/*.price-item {
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
}*/
.price-item {
  display: flex;
  align-items: center;
  gap: $spacing-l;

  .price-item-icon {
    color: $color-white;
    font-size: 1.2rem;
    svg {
      // border: 1px solid black;
      border-radius: 100%;
      background: #4a4a4a;
      padding: 0.1rem;
    }
  }

  span {
    font-size: 0.95rem;
    color: #333333;
  }
}
</style>
