<template>
  <h2><SgiltIcon :icon="icon" mobile class="icon" /> {{ label }}</h2>
</template>

<script setup lang="ts">
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'

defineProps<{
  icon: string
  label: string
}>()
</script>

<style scoped lang="scss">
h2 {
  display: flex;
  align-items: center;
  justify-content: left;
  gap: $spacing-s;

  font-size: 1.5rem;
  margin: 0 0 1em 0;
  font-weight: 500;
  text-transform: uppercase;

  .icon {
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, 0.1);
    border-radius: 50%;
    padding: $spacing-s;
  }
}
</style>
