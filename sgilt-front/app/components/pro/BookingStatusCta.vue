<template>
  <div class="booking-cta">
    <!-- nouvelle : Contact effectué + Refuser -->
    <template v-if="status === 'nouvelle'">
      <button
        class="booking-cta__btn booking-cta__btn--confirm"
        type="button"
        :disabled="loading"
        @click="$emit('confirm')"
      >
        Contact effectué ✓
      </button>
      <button
        class="booking-cta__btn booking-cta__btn--refuse"
        type="button"
        @click="$emit('refuse')"
      >
        Refuser la demande
      </button>
    </template>

    <!-- en_discussion : Confirmer + Refuser -->
    <template v-else-if="status === 'en_discussion'">
      <button
        class="booking-cta__btn booking-cta__btn--confirm"
        type="button"
        :disabled="loading"
        @click="$emit('confirm')"
      >
        Confirmer la réservation
      </button>
      <button
        class="booking-cta__btn booking-cta__btn--refuse"
        type="button"
        @click="$emit('refuse')"
      >
        Refuser la demande
      </button>
    </template>
  </div>
</template>

<script setup lang="ts">
import type { ReservationStatus } from '~/types/event'

defineProps<{
  status: ReservationStatus
  loading?: boolean
}>()

defineEmits<{ confirm: []; refuse: [] }>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;

.booking-cta {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
}

.booking-cta__btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  border-radius: $radius-md;
  font-family: 'Inter', sans-serif;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 150ms ease;
  padding: 12px $spacing-m;
  font-size: 0.875rem;

  @media (min-width: $desktop) {
    padding: 10px $spacing-m;
    font-size: 0.8rem;
  }

  &:disabled {
    opacity: 0.5;
    cursor: default;
  }

  &:active:not(:disabled) {
    opacity: 0.8;
  }

  &--confirm {
    background: #2e7d32;
    color: #fff;
    border: none;
  }

  &--refuse {
    background: none;
    border: none;
    color: #c0392b;
    font-weight: 400;
    font-size: 0.8rem;
    padding: 4px 0;
    justify-content: center;

    @media (min-width: $desktop) {
      padding: 2px 0;
    }
  }
}
</style>
