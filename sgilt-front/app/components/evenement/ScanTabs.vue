<!--
  Barre d'onglets de l'écran de scan des e-billets d'un événement : Scan, Récap. Chaque onglet est
  une URL (sous le chemin du scan) : rechargement et retour arrière le gardent. Même forme que les
  onglets de la billetterie.
-->
<template>
  <nav class="scan-tabs">
    <NuxtLink
      v-for="tab in SCAN_TABS"
      :key="tab"
      :to="scanTabPath(scanPath, tab)"
      class="tab"
      :class="{ active: tab === active }"
      replace
    >
      <component :is="TAB_ICONS[tab]" class="icon" aria-hidden="true" />
      <span class="label">{{ $t(`evenement.scan.tabs.${tab}`) }}</span>
    </NuxtLink>
  </nav>
</template>

<script setup lang="ts">
import { FileList3Icon, QrScan2Icon } from '@remixicons/vue/line'
import type { Component } from 'vue'
import { SCAN_TABS, scanTabPath, type ScanTab } from '~/constants/scan-tabs'

defineProps<{
  // Chemin du scan (onglet Scan).
  scanPath: string
  active: ScanTab
}>()

const TAB_ICONS: Record<ScanTab, Component> = {
  scan: QrScan2Icon,
  recap: FileList3Icon,
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.scan-tabs {
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
