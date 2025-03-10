<template>
  <h2 class="reservations-title"><SgiltIcon :icon="icon" />{{ title }}</h2>
</template>

<script setup lang="ts">
defineProps<{
  icon: string
  title: string
}>()
</script>

<style scoped lang="scss">
.reservations-title {
  @include respond-to(not-mobile) {
    display: none;
  }

  text-transform: uppercase;
  letter-spacing: 1px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-s;
  position: relative;
  padding: $spacing-s;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.1);

  svg {
    width: 2rem;
    height: 2rem;
  }

  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    width: 50%;
    height: 2px;
    background: linear-gradient(90deg, #ff9800, #ff5722);
    left: 50%;
    transform: translateX(-50%);
  }
}
</style>
