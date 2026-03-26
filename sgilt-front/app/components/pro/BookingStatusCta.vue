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
        <CheckIcon class="booking-cta__icon" />
        <span class="booking-cta__label">Premier contact effectué</span>
      </button>
      <button
        class="booking-cta__btn booking-cta__btn--refuse"
        type="button"
        @click="$emit('refuse')"
      >
        <CloseIcon class="booking-cta__icon" />
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
        <CheckIcon class="booking-cta__icon" />
        <span class="booking-cta__label">Confirmer la réservation</span>
      </button>
      <button
        class="booking-cta__btn booking-cta__btn--refuse"
        type="button"
        @click="$emit('refuse')"
      >
        <CloseIcon class="booking-cta__icon" />
        <span class="booking-cta__label">Refuser</span>
      </button>
    </template>
  </div>
</template>

<script setup lang="ts">
import type { ReservationStatus } from '~/types/event'
import { CheckIcon, CloseIcon } from '@remixicons/vue/line'

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
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  border-radius: $radius-md;
  font-family: 'Inter', sans-serif;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 150ms ease;
  padding: 16px $spacing-m;

  &:disabled {
    opacity: 0.5;
    cursor: default;
  }
  &:active:not(:disabled) {
    opacity: 0.8;
  }

  &--confirm {
    // background: #2e7d32;
    background: linear-gradient(180deg, #3a8f3e 0%, #2e7d32 100%);
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2);
    color: #fff;
    border: none;
  }

  &--refuse {
    background: #fff;
    border: 1.5px solid #c0392b;
    color: #c0392b;
    font-weight: 600;
  }
}

.booking-cta__icon {
  width: 22px;
  height: 22px;
  flex-shrink: 0;
}

.booking-cta__label {
  font-size: 0.8rem;
  line-height: 1.2;
  text-transform: uppercase;
}

// ── Overrides en layout row (desktop) ──────────────────────────────────────────
.booking-cta--row {
  @media (min-width: $desktop) {
    .booking-cta__btn {
      flex-direction: column;
      gap: 4px;
    }

    .booking-cta__btn--confirm {
      letter-spacing: 0.03em;
    }

    .booking-cta__btn--refuse {
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
