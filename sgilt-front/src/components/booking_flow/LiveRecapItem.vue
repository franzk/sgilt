<template>
  <div class="live-recap-item">
    <p class="item-title">{{ title }}</p>
    <p class="item-value">{{ value }}</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  value: string
}>()
</script>

<style scoped lang="scss">
.live-recap-item {
  display: flex;
  flex-direction: row;
  gap: $spacing-m;
  align-items: center;
  transition: opacity 0.3s ease-in-out;

  .item-title {
    margin: 0;
    padding: 0;
    font-weight: 500;
  }

  .item-value {
    margin: 0;
    padding: 0;
    color: $color-subtext;
  }
}
</style>
