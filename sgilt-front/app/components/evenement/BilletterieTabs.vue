<!--
  Barre d'onglets de l'écran Billetterie d'un événement : Réglages, Commandes. Chaque onglet est une
  URL (sous le chemin de la billetterie) : rechargement et retour arrière le gardent. Même forme que
  les onglets de la fiche d'une réservation.
-->
<template>
  <nav class="billetterie-tabs">
    <NuxtLink
      v-for="tab in BILLETTERIE_TABS"
      :key="tab"
      :to="billetterieTabPath(billetteriePath, tab)"
      class="tab"
      :class="{ active: tab === active }"
      replace
    >
      <component :is="TAB_ICONS[tab]" class="icon" aria-hidden="true" />
      <span class="label">{{ $t(`evenement.billetterie.tabs.${tab}`) }}</span>
    </NuxtLink>
  </nav>
</template>

<script setup lang="ts">
import { FileList3Icon, Settings3Icon } from '@remixicons/vue/line'
import type { Component } from 'vue'
import { BILLETTERIE_TABS, billetterieTabPath, type BilletterieTab } from '~/constants/billetterie-tabs'

defineProps<{
  // Chemin de la billetterie (onglet Réglages).
  billetteriePath: string
  active: BilletterieTab
}>()

const TAB_ICONS: Record<BilletterieTab, Component> = {
  reglages: Settings3Icon,
  commandes: FileList3Icon,
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.billetterie-tabs {
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
