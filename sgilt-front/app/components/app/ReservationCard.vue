<template>
  <button class="reservation-card" type="button" @click="$emit('click')">
    <!-- Photo / fallback initiales -->
    <div class="card-media">
      <img
        v-if="reservation.prestatairePhoto"
        :src="reservation.prestatairePhoto"
        :alt="reservation.prestataireName"
        class="card-media__img"
      />
      <span v-else class="card-media__fallback">{{ initials }}</span>
    </div>

    <!-- Catégorie + Nom -->
    <div class="card-info">
      <span class="card-info__category">{{ reservation.category }}</span>
      <span class="card-info__name">{{ reservation.prestataireName }}</span>
    </div>

    <!-- Badge état + point notes -->
    <div class="card-status">
      <span class="card-status__badge" :class="`card-status__badge--${reservation.status}`">
        {{ STATUS_LABELS[reservation.status] }}
      </span>
      <span v-if="reservation.hasNotes" class="card-status__notes-dot" />
    </div>

    <!-- Chevron -->
    <span class="card-chevron" aria-hidden="true">›</span>
  </button>
</template>

<script setup lang="ts">
import type { Reservation, ReservationStatus } from '~/types/event'

const props = defineProps<{ reservation: Reservation }>()
defineEmits<{ click: [] }>()

const STATUS_LABELS: Record<ReservationStatus, string> = {
  brouillon: 'Brouillon',
  envoyee: 'Envoyée',
  recontactee: 'Recontactée',
  confirmee: 'Confirmée',
  annulee: 'Annulée',
  cloturee: 'Clôturée',
  terminee: 'Terminée',
}

const initials = computed(() =>
  props.reservation.prestataireName
    .split(' ')
    .slice(0, 2)
    .map((w) => w[0]?.toUpperCase() ?? '')
    .join(''),
)
</script>

<style scoped lang="scss">
.reservation-card {
  width: 100%;
  display: flex;
  align-items: center;
  gap: $spacing-s;
  padding: 12px 14px;
  border-radius: 16px;
  border: 0.5px solid $brand-border;
  background: #fff;
  cursor: pointer;
  text-align: left;
  font-family: inherit;
  transition: background 120ms ease;

  &:active {
    background: $surface-soft;
  }
}

// ─── Photo ────────────────────────────────────────────────────────────────────
.card-media {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  overflow: hidden;
  background: $brand-subtle;
  display: flex;
  align-items: center;
  justify-content: center;

  &__img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  &__fallback {
    font-family: 'Cormorant Garamond', serif;
    font-size: 17px;
    font-weight: 500;
    color: $text-secondary;
    line-height: 1;
  }
}

// ─── Info ─────────────────────────────────────────────────────────────────────
.card-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;

  &__category {
    font-family: 'Inter', sans-serif;
    font-size: 10px;
    font-weight: 600;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: $brand-muted;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  &__name {
    font-family: 'Cormorant Garamond', serif;
    font-size: 17px;
    font-weight: 500;
    color: $brand-primary;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

// ─── Badge état ───────────────────────────────────────────────────────────────
.card-status {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;

  &__badge {
    font-size: 0.7rem;
    font-weight: 600;
    padding: 3px 8px;
    border-radius: 2rem;
    white-space: nowrap;

    &--brouillon,
    &--terminee {
      background: #f0efee;
      color: #888;
    }

    &--envoyee {
      background: #e8f0fe;
      color: #2c5cc5;
    }

    &--recontactee {
      background: rgba($brand-accent, 0.15);
      color: darken(#e6b800, 20%);
    }

    &--confirmee {
      background: rgba($state-available, 0.12);
      color: $state-available;
    }

    &--annulee,
    &--cloturee {
      background: rgba($state-error, 0.1);
      color: $state-error;
    }
  }

  &__notes-dot {
    display: block;
    width: 7px;
    height: 7px;
    border-radius: 50%;
    background: $brand-accent;
  }
}

// ─── Chevron ──────────────────────────────────────────────────────────────────
.card-chevron {
  flex-shrink: 0;
  font-size: 1.2rem;
  color: $brand-muted;
  line-height: 1;
  margin-left: 2px;
}
</style>
