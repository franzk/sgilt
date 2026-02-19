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
@use '@/assets/styles/base' as *;

.sgilt-button {
  cursor: pointer;

  height: 100%;

  font-size: 1.125rem;
  font-weight: 750;

  // background-color: #f2c200;
  background: linear-gradient(to bottom, #ffd84d 0%, #f2c200 100%);
  color: #fff;
  text-shadow: 0 1px 0 rgba(0, 0, 0, 0);
  transition:
    transform 0.15s ease,
    box-shadow 0.2s ease;

  border: none;
  border-radius: 0.875rem;

  // padding: $spacing-m $spacing-l;

  &:hover {
    background-color: darken(#f2c200, 10%);
  }

  &:active {
    transform: translateY(1px);
    box-shadow: 0 0.2rem 0.4rem rgba(0, 0, 0, 0.1);
  }
  border: 0.0625rem solid rgba(255, 255, 255, 0.35);
  box-shadow:
    0 0.25rem 0.5rem rgba(0, 0, 0, 0.14),
    0 0.75rem 1.75rem rgba(0, 0, 0, 0.08),
    0 0.75rem 2rem rgba(242, 194, 0, 0.18);
}

.secondary {
  background-color: white;
  color: $color-accent;
  // border: $border-width-s solid $color-accent;

  &:hover {
    background-color: darken($color-secondary, 10%);
  }
}
</style>
