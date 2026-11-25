<!--
  Layout de l'espace prestataire (/pro) : header commun, contenu, et navigation basse mobile
  (ProBottomNav). L'édition de la fiche (/pro/page-edition) est en plein écran, hors de ce layout.
-->
<template>
  <AppHeader />
  <main class="pro-content">
    <slot />
  </main>
  <ProBottomNav />
</template>

<script setup lang="ts">
import AppHeader from '~/components/AppHeader.vue'
import ProBottomNav from '~/components/pro/ProBottomNav.vue'
</script>

<style lang="scss">
@use '@/assets/styles/base' as *;

.pro-content {
  padding-top: $app-header-height;
  padding-bottom: calc($bottom-nav-h + env(safe-area-inset-bottom, 0px));
  flex: 1;
  display: flex;
  flex-direction: column;

  @media (min-width: $breakpoint-desktop) {
    padding-bottom: 0;
  }
}
</style>
