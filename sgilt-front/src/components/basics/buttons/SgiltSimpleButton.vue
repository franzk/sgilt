<template>
  <button class="sgilt-simple-button" @click="$emit('click')">
    <slot></slot>
  </button>
</template>

<script setup lang="ts">
defineEmits(['click'])
</script>

<style scoped lang="scss">
.sgilt-simple-button {
  cursor: pointer;

  font-size: $font-size-base;
  font-weight: bold;

  background-color: $color-accent;
  color: $color-secondary;

  border: none;
  border-radius: $border-radius-s;

  padding: $spacing-mm $spacing-l;

  &:hover {
    background-color: lighten($color-accent, 10%);
  }
}
</style>
