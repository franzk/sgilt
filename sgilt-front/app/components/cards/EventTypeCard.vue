<template>
  <button type="button" class="event-type-card" :class="type">
    <div class="circle">
      <component :is="icon" class="icon" />
    </div>
    <p class="label">{{ label }}</p>
  </button>
</template>

<script setup lang="ts">
import type { Component } from 'vue'

defineProps<{
  type: string
  label: string
  icon: Component
}>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.event-type-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: $spacing-xs;
  padding: $spacing-m $spacing-xs;
  border: none;
  font: inherit;
  cursor: pointer;
  border-radius: $radius-lg;
  box-shadow: 0 1px 4px rgba(47, 42, 37, 0.05);

  background: rgba(255, 255, 255, 0.55);
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);

  .circle {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 2.5rem;
    height: 2.5rem;
    border-radius: 50%;
    color: $text-inverted;

    .icon {
      width: 1.125rem;
      height: 1.125rem;
    }
  }

  .label {
    margin: 0;
    font-size: $font-size-sm;
    font-weight: $font-weight-medium;
    color: $text-primary;
    text-align: center;
  }

  &.mariage .circle {
    background: $event-mariage;
  }

  &.anniversaire .circle {
    background: $event-anniversaire;
  }

  &.soiree_privee .circle {
    background: $event-soiree-privee;
  }

  &.fete_entreprise .circle {
    background: $event-fete-entreprise;
  }

  &.evenement_public .circle {
    background: $event-evenement-public;
  }

  &.autre .circle {
    background: $event-autre;
  }
}
</style>
