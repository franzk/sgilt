<template>
  <nav class="reservation-tabs">
    <NuxtLink
      v-for="tab in RESERVATION_TABS"
      :key="tab"
      :to="reservationTabPath(reservationPath, tab)"
      class="tab"
      :class="{ active: tab === active }"
      replace
    >
      <component :is="TAB_ICONS[tab]" class="icon" aria-hidden="true" />
      <span class="label">{{ $t(`reservation.tabs.${tab}`) }}</span>
    </NuxtLink>
  </nav>
</template>

<script setup lang="ts">
// Barre d'onglets de la fiche d'une réservation, commune au client et au prestataire. Chaque onglet
// est une URL (sous le chemin de la réservation) : rechargement et retour arrière le gardent.
import { FileList3Icon, FolderIcon, HistoryIcon, Store2Icon } from '@remixicons/vue/line'
import type { Component } from 'vue'
import {
  RESERVATION_TABS,
  reservationTabPath,
  type ReservationTab,
} from '~/constants/reservation-tabs'

defineProps<{
  // Chemin de la réservation (onglet Suivi).
  reservationPath: string
  active: ReservationTab
}>()

const TAB_ICONS: Record<ReservationTab, Component> = {
  suivi: HistoryIcon,
  details: FileList3Icon,
  documents: FolderIcon,
  prestataire: Store2Icon,
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.reservation-tabs {
  display: flex;
  overflow-x: auto;
  border-bottom: 1px solid $divider-color;
  background: $surface-white;
  scrollbar-width: none;

  &::-webkit-scrollbar {
    display: none;
  }

  .tab {
    display: flex;
    flex: 1;
    align-items: center;
    justify-content: center;
    gap: $spacing-xs;
    padding: $spacing-s $spacing-m;
    border-bottom: 3px solid transparent;
    color: $text-secondary;
    font-size: $font-size-sm;
    font-weight: $font-weight-medium;
    text-decoration: none;
    white-space: nowrap;

    &.active {
      border-bottom-color: $brand-accent;
      color: $text-primary;
      font-weight: $font-weight-semibold;
    }

    .icon {
      flex-shrink: 0;
      width: 1.25rem;
      height: 1.25rem;
    }
  }
}
</style>
