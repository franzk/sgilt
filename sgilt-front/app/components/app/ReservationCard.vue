<template>
  <button class="reservation-card" type="button">
    <!-- La pastille compte les notes non lues : seul signal qu'un prestataire a écrit. -->
    <BadgeableComponent class="thumb" :count="reservation.unreadNotesCount">
      <SgiltImage
        class="image"
        :src="reservation.prestatairePhoto"
        :alt="reservation.prestataireName"
      />
    </BadgeableComponent>
    <span class="text">
      <span class="name">{{ reservation.prestataireName }}</span>
      <StatusBadge :status="reservation.status" context="client" />
    </span>
    <ArrowRightSIcon class="chevron" aria-hidden="true" />
  </button>
</template>

<script setup lang="ts">
// Réservation d'un événement, en ligne : page rubrique de l'espace connecté (et, à venir, liste
// des réservations de l'événement).
import { ArrowRightSIcon } from '@remixicons/vue/line'
import BadgeableComponent from '~/components/basics/BadgeableComponent.vue'
import SgiltImage from '~/components/basics/media/SgiltImage.vue'
import StatusBadge from '~/components/basics/StatusBadge.vue'
import type { ReservationSummary } from '~/data/reservation/domain/ReservationSummary'

defineProps<{
  reservation: ReservationSummary
}>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.reservation-card {
  appearance: none;
  display: flex;
  align-items: center;
  gap: $spacing-m;
  width: 100%;
  padding: $spacing-s;
  border: none;
  border-radius: $radius-lg;
  background: $surface-white;
  box-shadow: 0 0.125rem 0.75rem rgba(0, 0, 0, 0.08);
  font-family: 'Inter', sans-serif;
  text-align: left;
  color: $text-primary;
  cursor: pointer;
  transition: background 120ms ease;

  &:active {
    background: $surface-soft;
  }

  .thumb {
    flex-shrink: 0;
    width: 4.5rem;
    height: 4.5rem;

    .image {
      border-radius: $radius-md;
    }
  }

  .text {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: $spacing-xxs;
    min-width: 0;

    .name {
      max-width: 100%;
      overflow: hidden;
      font-size: 0.95rem;
      font-weight: 600;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .chevron {
    flex-shrink: 0;
    width: 1.125rem;
    height: 1.125rem;
    color: $text-secondary;
  }
}
</style>
