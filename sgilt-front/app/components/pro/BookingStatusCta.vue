<template>
  <div class="booking-cta" :class="layout === 'row' ? 'row' : ''">
    <!-- Refuser — seule action pro restante sur nouvelle/en_discussion, l'avancement du statut
    (contact effectué, confirmation) est désormais déclaré par le client -->
    <button class="btn refuse" type="button" @click="$emit('refuse')">
      <span class="label">Non, je refuse cette prestation</span>
    </button>
    <button class="refuse-link" type="button" @click="$emit('refuse')">
      Non, je refuse cette prestation.
    </button>
  </div>
</template>

<script setup lang="ts">
import type { ReservationStatus } from '~/types/event'

defineProps<{
  status: ReservationStatus
  layout?: 'row' | 'column'
}>()

defineEmits<{ refuse: [] }>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;

.booking-cta {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  &.row {
    @media (min-width: $desktop) {
      flex-direction: row;
    }
  }

  .btn {
    display: flex;
    flex: 1;
    flex-direction: row;
    align-items: center;
    justify-content: center;
    gap: 8px;
    width: 100%;
    border-radius: $radius-md;
    font-family: 'Inter', sans-serif;
    font-weight: 500;
    cursor: pointer;
    min-height: 40px;

    &.refuse {
      background: #fff;
      border: none;
      color: #c0392b;
      font-weight: 600;

      span {
        font-weight: 500;
        text-transform: none;
      }

      @media (max-width: #{$desktop - 1px}) {
        display: none;
      }
    }
  }

  // Texte de refus — affiché uniquement sur mobile
  .refuse-link {
    display: none;

    @media (max-width: #{$desktop - 1px}) {
      display: block;
      width: 100%;
      padding: $spacing-s 0;
      border: none;
      background: none;
      font-family: 'Inter', sans-serif;
      font-size: 0.8rem;
      color: rgba(192, 57, 43, 0.65);
      text-align: center;
      cursor: pointer;
      text-decoration: underline;
      text-decoration-color: rgba(192, 57, 43, 0.3);
      text-underline-offset: 3px;
    }
  }

  .label {
    font-size: 0.8rem;
    line-height: 1.2;
    text-transform: uppercase;
  }

  // ── Overrides en layout row (desktop) ────────────────────────────────────────
  &.row {
    @media (min-width: $desktop) {
      .btn.refuse {
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
}
</style>
