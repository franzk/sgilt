<template>
  <div class="app-banner" :class="`tone-${tone}`">
    <div class="inner">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
// Coque commune des bandeaux fixés sous le header (ContextBanner, EvenementBanner) :
// position, hauteur, conteneur et teinte. Le contenu est fourni par le slot.
// Le décalage du contenu de la page est géré par les layouts (has-banner).
withDefaults(defineProps<{ tone?: 'dark' | 'light' }>(), { tone: 'light' })
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.app-banner {
  position: fixed;
  top: $app-header-height;
  left: 0;
  right: 0;
  z-index: $z-header - 1;
  height: $banner-h;

  &.tone-dark {
    background: $brand-primary;
    color: #fff;
  }

  &.tone-light {
    background: $surface-soft;
    border-bottom: 1px solid $divider-color;
    color: $text-primary;
  }

  .inner {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $spacing-xs;
    height: 100%;
    max-width: 75rem;
    margin: 0 auto;
    padding: 0 $spacing-m;
  }
}
</style>
