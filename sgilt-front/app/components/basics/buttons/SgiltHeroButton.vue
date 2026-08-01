import { defineProps } from "vue"

<template>
  <button
    class="sgilt-button"
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
@use 'sass:color';

// global
$background: linear-gradient(
  to bottom,
  color.adjust($color-accent, $lightness: 12%) 0%,
  $color-accent 100%
);

// font
$font-size: 1.125rem;
$font-weight: 750;
$color: #fff;
$text-shadow: 0 1px 0 rgba(0, 0, 0, 0.1);

// border
$border-radius: 1.75rem;
$border: 0.0625rem solid rgba(255, 255, 255, 0.35);
$box-shadow:
  0 0.25rem 0.5rem rgba(0, 0, 0, 0.14),
  0 0.75rem 1.75rem rgba(0, 0, 0, 0.08),
  0 0.75rem 2rem rgba($color-accent, 0.18);

// hover & active
$hover-text-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);

// style
.sgilt-button {
  cursor: pointer;

  height: 100%;
  min-height: 2rem;

  font-size: $font-size;
  font-weight: $font-weight;

  background: $background;
  color: $color;
  text-shadow: $text-shadow;
  border: $border;
  border-radius: $border-radius;

  &:hover {
    text-shadow: $hover-text-shadow;
  }

  &:focus-visible {
    border-color: $input-focus-border-color;
    box-shadow: $input-focus-box-shadow;
  }

  box-shadow: $box-shadow;
}

.secondary {
  background: white;
  color: $text-primary;
  font-weight: 500;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12);

  &:hover {
    background-color: color.adjust($color-secondary, $lightness: -10%);
  }
}
</style>
