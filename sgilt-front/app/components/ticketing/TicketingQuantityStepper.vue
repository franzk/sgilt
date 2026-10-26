<!--
  Sélecteur de quantité de billets (boutons − / +), sur l'écran de commande (/e/[slug]/commande).
  Purement visuel : la quantité et ses bornes sont portées par la page.
-->
<template>
  <div class="quantity-stepper">
    <button
      type="button"
      class="step-btn"
      :aria-label="$t('ticketing.event.decrement-aria')"
      @click="$emit('decrement')"
    >
      <SubtractIcon />
    </button>
    <span class="value">{{ quantity }}</span>
    <button
      type="button"
      class="step-btn"
      :aria-label="$t('ticketing.event.increment-aria')"
      @click="$emit('increment')"
    >
      <AddIcon />
    </button>
  </div>
</template>

<script setup lang="ts">
import { AddIcon, SubtractIcon } from '@remixicons/vue/line'

defineProps<{ quantity: number }>()
defineEmits<{ decrement: []; increment: [] }>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.quantity-stepper {
  display: flex;
  align-items: center;
  gap: $spacing-xl;

  .step-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 2.75rem;
    height: 2.75rem;
    border: none;
    border-radius: 50%;
    background: $surface-soft;
    color: $text-primary;
    cursor: pointer;
    transition: background 150ms ease;

    &:hover {
      background: $brand-subtle;
    }

    svg {
      width: 1.1rem;
      height: 1.1rem;
    }
  }

  .value {
    min-width: 2rem;
    text-align: center;
    font-size: $font-size-2xl;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }
}
</style>
