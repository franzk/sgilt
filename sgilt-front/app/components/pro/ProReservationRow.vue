<!--
  Ligne compacte d'une réservation côté prestataire (blocs de l'accueil /pro) : pavé de date
  (jour + mois), titre de l'événement, badge de statut et chevron. Toute la ligne ouvre la fiche.
-->
<template>
  <NuxtLink
    :to="`/pro/reservations/${reservation.id}`"
    class="pro-reservation-row"
    :style="{ '--status-color': statusColor }"
  >
    <div class="date">
      <span class="day">{{ reservation.date.getDate() }}</span>
      <span class="month">{{ month }}</span>
    </div>

    <div class="body">
      <span class="title">{{ reservation.titre }}</span>
      <span class="status">{{ $t(`reservation.statut.${reservation.statut}`) }}</span>
    </div>

    <ArrowRightSIcon class="chevron" />
  </NuxtLink>
</template>

<script setup lang="ts">
import { ArrowRightSIcon } from '@remixicons/vue/line'
import { RESERVATION_STATUS_CONFIG } from '~/constants/reservation-status'
import type { ProReservationSummary } from '~/data/reservation/domain/ProReservationSummary'

const props = defineProps<{
  reservation: ProReservationSummary
}>()

const month = computed<string>(() =>
  props.reservation.date.toLocaleDateString('fr-FR', { month: 'short' }),
)

const statusColor = computed<string>(() => RESERVATION_STATUS_CONFIG[props.reservation.statut].color)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.pro-reservation-row {
  display: flex;
  align-items: center;
  gap: $spacing-s;
  padding: $spacing-s $spacing-m;
  text-decoration: none;
  color: $text-primary;

  .date {
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 3.5rem;
    height: 3.5rem;
    border-radius: $radius-md;
    background: $brand-subtle;

    .day {
      font-size: 1.375rem;
      font-weight: 600;
      line-height: 1;
    }

    .month {
      margin-top: 0.125rem;
      font-size: 0.8125rem;
      color: $text-secondary;
    }
  }

  .body {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 0.375rem;

    .title {
      max-width: 100%;
      font-size: 1rem;
      font-weight: 600;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .status {
      padding: 0.1875rem 0.75rem;
      border-radius: 2rem;
      background: color-mix(in srgb, var(--status-color) 12%, $surface-white);
      color: var(--status-color);
      font-size: 0.8125rem;
      font-weight: 600;
    }
  }

  .chevron {
    flex-shrink: 0;
    width: 1.375rem;
    height: 1.375rem;
    color: $text-secondary;
  }
}
</style>
