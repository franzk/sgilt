<!--
  Sélecteur de quantité de billets (boutons − / +), utilisé sur la fiche événement (/e/[slug]) et
  sur l'écran de commande. Purement visuel : la quantité et ses bornes sont portées par
  useTicketQuantity. Variante `large` pour l'écran de commande, où le sélecteur est mis en avant.
-->
<template>
  <div class="quantity-stepper" :class="{ large }">
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

withDefaults(defineProps<{ quantity: number; large?: boolean }>(), { large: false })
defineEmits<{ decrement: []; increment: [] }>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.quantity-stepper {
  display: flex;
  align-items: center;
  gap: $spacing-m;

  .step-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 2.25rem;
    height: 2.25rem;
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
      width: 1rem;
      height: 1rem;
    }
  }

  .value {
    min-width: 1.5rem;
    text-align: center;
    font-size: $font-size-lg;
    font-weight: $font-weight-semibold;
    color: $text-primary;
  }

  &.large {
    gap: $spacing-xl;

    .step-btn {
      width: 2.75rem;
      height: 2.75rem;

      svg {
        width: 1.1rem;
        height: 1.1rem;
      }
    }

    .value {
      min-width: 2rem;
      font-size: $font-size-2xl;
      font-weight: $font-weight-bold;
    }
  }
}
</style>
