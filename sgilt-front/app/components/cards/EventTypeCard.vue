<template>
  <button type="button" class="event-type-card" @click="$emit('select')">
    <span class="inner">
      <span class="illustration" :style="{ backgroundImage: `url(${image})` }" />

      <span class="text">
        <span class="name">{{ label }}</span>
        <span class="tagline">{{ tagline }}</span>
      </span>

      <ArrowRightSIcon class="chevron" aria-hidden="true" />
    </span>
  </button>
</template>

<script setup lang="ts">
import { ArrowRightSIcon } from '@remixicons/vue/line'

defineProps<{
  label: string
  tagline: string
  image: string
}>()

defineEmits<{ select: [] }>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.event-type-card {
  display: block;
  width: 100%;
  max-width: 9rem;
  margin: 0 auto;
  padding: 0;
  border: 2px solid transparent;
  border-radius: $radius-xl;
  background: $surface-white;
  box-shadow: 0 2px 8px rgba(47, 42, 37, 0.06);
  font: inherit;
  text-align: center;
  cursor: pointer;
  overflow: hidden;
  transition:
    border-color 160ms ease,
    box-shadow 160ms ease;

  &:focus-visible {
    outline: 3px solid $brand-accent;
    outline-offset: 2px;
  }

  .inner {
    display: flex;
    flex-direction: column;
    aspect-ratio: 1 / 1;
    padding: $spacing-xs;

    // L'illustration remplit tout l'espace laissé libre par le texte (hauteur fixe, une ligne)
    .illustration {
      display: block;
      flex: 1;
      min-height: 0;
      padding: $spacing-m;
      background-size: contain;
      background-repeat: no-repeat;
      background-position: center;
      background-origin: content-box;
    }

    .text {
      display: block;
      flex-shrink: 0;
      margin-top: $spacing-xs;

      .name {
        display: block;
        margin: 0;
        font-family: 'Cormorant Garamond', serif;
        font-weight: $font-weight-medium;
        font-size: $font-size-md;
        line-height: $line-height-tight;
        color: $text-primary;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      // Masquée sur mobile (carte carrée, un seul libellé) — visible seulement
      // dans la tuile horizontale desktop.
      .tagline {
        display: none;
        margin: 0;
      }
    }

    // Chevron desktop uniquement (le clic navigue directement, voir fete.vue).
    .chevron {
      display: none;
    }
  }

  // Desktop : tuile horizontale (icône à gauche, libellé + tagline à droite)
  // — plus d'aspect-ratio ni de largeur plafonnée, la hauteur vient du contenu.
  @media (min-width: $breakpoint-desktop) {
    max-width: none;
    text-align: left;

    .inner {
      aspect-ratio: auto;
      flex-direction: row;
      align-items: center;
      gap: $spacing-m;
      padding: $spacing-m $spacing-l;

      .illustration {
        flex: 0 0 auto;
        width: 3.5rem;
        height: 3.5rem;
        padding: 0;
      }

      .text {
        flex: 1;
        min-width: 0;
        margin-top: 0;

        .name {
          font-size: $font-size-lg;
        }

        .tagline {
          display: block;
          margin-top: $spacing-xxs;
          color: $text-secondary;
          font-size: $font-size-sm;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }

      .chevron {
        display: block;
        flex-shrink: 0;
        width: 1.25rem;
        height: 1.25rem;
        color: $text-secondary;
      }
    }
  }
}
</style>
