<template>
  <div class="content-card" :class="{ clickable }">
    <slot />
  </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    clickable?: boolean
  }>(),
  {
    clickable: false,
  },
)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.content-card {
  background: #fff;
  border: 1px solid $divider-color;
  border-radius: $radius-lg;
  box-shadow: 0 2px 8px $shadow-s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 24px $shadow-m;
  }

  &.clickable {
    cursor: pointer;
    transition:
      transform 200ms ease,
      box-shadow 200ms ease;
  }
}
</style>
