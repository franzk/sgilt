<template>
  <div class="partner-category-button">
    <p class="icon">{{ icon }}</p>
    <p class="label">{{ category }}</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  category: string
  icon: string
}>()
</script>

<style lang="scss" scoped>
.partner-category-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 1px solid black;
  .icon {
    font-size: 2rem;
    margin: 0;
  }
}
</style>
