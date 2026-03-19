<template>
  <button
    class="reservation-card"
    type="button"
    :style="{ borderLeftColor: RESERVATION_STATUS_COLORS[reservation.status] }"
    @click="$emit('click')"
  >
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

    <!-- Grille 2 lignes -->
    <div class="card-body">
      <!-- Ligne 1 : catégorie + badge statut -->
      <div class="card-row">
        <span class="card-category">{{ reservation.category }}</span>
        <span class="card-badge" :class="`card-badge--${reservation.status}`">
          {{ STATUS_LABELS[reservation.status] }}
        </span>
      </div>

      <!-- Ligne 2 : nom -->
      <div class="card-row">
        <span class="card-name">{{ reservation.prestataireName }}</span>
      </div>
    </div>

    <!-- Badge notes non lues -->
    <span v-if="reservation.unreadNotesCount > 0" class="card-unread">
      {{ reservation.unreadNotesCount }}
    </span>

    <!-- Chevron -->
    <span class="card-chevron" aria-hidden="true">›</span>
  </button>
</template>

<script setup lang="ts">
import type { Reservation } from '~/types/event'
import {
  RESERVATION_STATUS_LABELS as STATUS_LABELS,
  RESERVATION_STATUS_COLORS,
} from '~/utils/reservationStatus'

const props = defineProps<{ reservation: Reservation }>()
defineEmits<{ click: [] }>()

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
  position: relative;
  gap: $spacing-s;
  padding: 12px 14px;
  border-radius: $border-radius-xs;
  border: 0.5px solid $brand-border;
  border-left-width: 3px;
  background: #fff;
  box-shadow:
    0 2px 8px rgba(0, 0, 0, 0.06),
    0 1px 2px rgba(0, 0, 0, 0.04);
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
  border-radius: 50%;
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

// ─── Grille corps ─────────────────────────────────────────────────────────────
.card-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.card-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-xs;
}

// ─── Ligne 1 ──────────────────────────────────────────────────────────────────
.card-category {
  font-family: 'Inter', sans-serif;
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: $brand-muted;
  white-space: nowrap;
}

.card-badge {
  flex-shrink: 0;
  font-size: 0.625rem; // 10px
  font-weight: 600;
  padding: 2px 7px;
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

// ─── Ligne 2 ──────────────────────────────────────────────────────────────────
.card-name {
  font-family: 'Cormorant Garamond', serif;
  font-size: 17px;
  font-weight: 500;
  color: $brand-primary;
  line-height: 1.2;
}

.card-unread {
  position: absolute;
  top: -0.5rem;
  right: -0.5rem;
  // flex-shrink: 0;
  //align-self: flex-end;
  padding: 3px 8px;
  border-radius: 999px;
  background: $state-danger;
  color: #fff;
  font-family: 'Inter', sans-serif;
  font-size: 0.688rem; // 11px
  font-weight: 500;
  white-space: nowrap;
  line-height: 1.4;
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
