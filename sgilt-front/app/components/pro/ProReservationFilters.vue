<!--
  Filtres de la liste des réservations du prestataire (/pro/reservations) : À contacter et
  À conclure avec leur nombre de réservations, puis Toutes (sans nombre). Le filtre actif est porté
  par la page (URL).
-->
<template>
  <div class="pro-reservation-filters">
    <button
      v-for="filter in PRO_RESERVATION_FILTERS"
      :key="filter.id"
      class="pill"
      :class="{ active: modelValue === filter.id }"
      :style="pillStyle(filter.statut)"
      type="button"
      @click="modelValue = filter.id"
    >
      {{ $t(`pro.reservations.filters.${filter.id}`) }}
      <span v-if="counts[filter.id] !== undefined" class="count">{{ counts[filter.id] }}</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { PRO_RESERVATION_FILTERS, RESERVATION_STATUS_CONFIG } from '~/constants/reservation-status'
import type { ProReservationFilter, ReservationStatut } from '~/constants/reservation-status'

defineProps<{
  /** Nombre de réservations par filtre ; un filtre absent s'affiche sans nombre. */
  counts: Partial<Record<ProReservationFilter, number>>
}>()

const modelValue = defineModel<ProReservationFilter>({ required: true })

function pillStyle(statut: ReservationStatut | null): Record<string, string> {
  if (!statut) return {}
  const cfg = RESERVATION_STATUS_CONFIG[statut]
  return { '--pill-color': cfg.color, '--pill-bg': cfg.bgColor }
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.pro-reservation-filters {
  display: flex;
  gap: $spacing-xs;
  overflow-x: auto;
  scrollbar-width: none;
  padding-bottom: 2px;

  &::-webkit-scrollbar {
    display: none;
  }

  .pill {
    position: relative;
    flex-shrink: 0;
    display: inline-flex;
    align-items: center;
    gap: 0.375rem;
    padding: 0.3125rem 0.75rem;
    margin-bottom: 2px;
    border-radius: 2rem;
    border: none;
    font-family: 'Inter', sans-serif;
    font-size: 0.75rem;
    font-weight: 500;
    cursor: pointer;
    color: var(--pill-color, $text-secondary);
    background: var(--pill-bg, transparent);
    transition:
      background 120ms ease,
      color 120ms ease;

    &.active {
      font-weight: 600;

      &::after {
        content: '';
        position: absolute;
        bottom: -0.3125rem;
        left: 0;
        right: 0;
        height: 2px;
        border-radius: 999px;
        background: $brand-accent;
      }
    }

    .count {
      font-size: 0.6875rem;
      opacity: 0.8;
    }
  }
}
</style>
