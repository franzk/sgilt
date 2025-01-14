import { defineProps } from "vue"

<template>
  <button
    class="sgilt-simple-button"
    :class="{ secondary: variant === 'secondary' }"
    @click="$emit('click')"
  >
    <slot></slot>
  </button>
</template>

<script setup lang="ts">
defineEmits(['click'])
defineProps<{
  variant?: string
}>()
</script>

<style scoped lang="scss">
.sgilt-simple-button {
  cursor: pointer;

  font-size: 1em;
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

.secondary {
  background-color: white;
  color: $color-accent;
  border: $border-width-s solid $color-accent;

  &:hover {
    background-color: darken($color-secondary, 10%);
  }
}
</style>
