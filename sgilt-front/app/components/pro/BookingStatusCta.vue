<template>
  <div class="booking-cta" :class="layout === 'row' ? 'booking-cta--row' : ''">
    <!-- nouvelle : Contact effectué + Refuser -->
    <template v-if="status === 'nouvelle'">
      <button
        class="booking-cta__btn booking-cta__btn--confirm"
        type="button"
        :disabled="loading"
        @click="$emit('confirm')"
      >
        <span class="booking-cta__icon">✓</span>
        <span class="booking-cta__label">Contact effectué</span>
      </button>
      <button
        class="booking-cta__btn booking-cta__btn--refuse"
        type="button"
        @click="$emit('refuse')"
      >
        <span class="booking-cta__icon">✗</span>
        <span class="booking-cta__label">Refuser</span>
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
        <span class="booking-cta__icon">✓</span>
        <span class="booking-cta__label">Confirmer la réservation</span>
      </button>
      <button
        class="booking-cta__btn booking-cta__btn--refuse"
        type="button"
        @click="$emit('refuse')"
      >
        <span class="booking-cta__icon">✗</span>
        <span class="booking-cta__label">Refuser</span>
      </button>
    </template>
  </div>
</template>

<script setup lang="ts">
import type { ReservationStatus } from '~/types/event'

defineProps<{
  status: ReservationStatus
  loading?: boolean
  layout?: 'row' | 'column'
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

  &--row {
    @media (min-width: $desktop) {
      flex-direction: row;
    }
  }
}

.booking-cta__btn {
  display: flex;
  flex: 1;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  width: 100%;
  border-radius: $radius-md;
  font-family: 'Inter', sans-serif;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 150ms ease;
  padding: 16px $spacing-m;

  &:disabled { opacity: 0.5; cursor: default; }
  &:active:not(:disabled) { opacity: 0.8; }

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
    padding: 8px 0;
  }
}

.booking-cta__icon {
  font-size: 1.25rem;
  line-height: 1;
  font-weight: 700;
}

.booking-cta__label {
  font-size: 0.8rem;
  line-height: 1.2;
}

// ── Overrides en layout row (desktop) ──────────────────────────────────────────
.booking-cta--row {
  @media (min-width: $desktop) {
    .booking-cta__btn--confirm {
      letter-spacing: 0.03em;
    }

    .booking-cta__btn--refuse {
      border: 1.5px solid #c0392b;
      background: #fff;
      font-weight: 700;
      padding: 16px $spacing-m;
      transition:
        background 120ms ease,
        border-color 120ms ease;

      &:hover {
        background: rgba(192, 57, 43, 0.04);
        border-color: rgba(192, 57, 43, 0.6);
      }
    }
  }
}
</style>
