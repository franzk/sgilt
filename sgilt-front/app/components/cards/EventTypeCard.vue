<template>
  <button
    type="button"
    class="event-type-card"
    :class="{ selected }"
    :aria-pressed="selected"
    @click="$emit('select')"
  >
    <div v-if="selected" class="check-badge" aria-hidden="true">
      <CheckIcon class="icon" />
    </div>

    <div class="illustration" :style="{ backgroundImage: `url(${image})` }" />

    <div class="text">
      <p class="name">{{ label }}</p>
      <p class="tagline">{{ tagline }}</p>
    </div>

    <ArrowRightSIcon class="chevron" aria-hidden="true" />
  </button>
</template>

<script setup lang="ts">
import { CheckIcon, ArrowRightSIcon } from '@remixicons/vue/line'

defineProps<{
  label: string
  tagline: string
  image: string
  selected?: boolean
}>()

defineEmits<{ select: [] }>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.event-type-card {
  position: relative;
  // Largeur = la colonne de la grille, plafonnée pour ne pas grossir démesurément
  // sur des colonnes larges (peu de colonnes / grand écran) — centrée si la
  // colonne est plus large que ça. Hauteur dérivée via aspect-ratio.
  width: 100%;
  max-width: 9rem;
  margin: 0 auto;
  aspect-ratio: 1 / 1;
  display: flex;
  flex-direction: column;
  padding: $spacing-xs;
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

  &.selected {
    border-color: $brand-accent;
    box-shadow: 0 4px 14px rgba($brand-accent, 0.3);
  }

  &:focus-visible {
    outline: 3px solid $brand-accent;
    outline-offset: 2px;
  }

  // L'illustration remplit tout l'espace laissé libre par le texte (hauteur
  // fixe, une ligne) — pas de recadrage (contain), pas de pourcentage arbitraire.
  .illustration {
    position: relative;
    flex: 1;
    min-height: 0;
    padding: $spacing-m;
    background-size: contain;
    background-repeat: no-repeat;
    background-position: center;
    background-origin: content-box;
  }

  .check-badge {
    position: absolute;
    top: $spacing-xs;
    right: $spacing-xs;
    z-index: 2;
    width: 1.5rem;
    height: 1.5rem;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: $brand-accent;
    color: $brand-primary;

    .icon {
      width: 1rem;
      height: 1rem;
    }
  }

  .text {
    flex-shrink: 0;
    margin-top: $spacing-xs;
  }

  // Hauteur fixe et prévisible (une seule ligne, taille fixe — pas de clamp
  // lié au viewport) : l'espace restant pour .illustration doit être stable.
  .name {
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

  // Chevron desktop uniquement (le clic navigue directement, voir fete.vue).
  .chevron {
    display: none;
  }

  // Desktop : tuile horizontale (icône à gauche, libellé + tagline à droite)
  // — plus d'aspect-ratio ni de largeur plafonnée, la hauteur vient du contenu.
  @media (min-width: $breakpoint-desktop) {
    width: 100%;
    max-width: none;
    aspect-ratio: auto;
    flex-direction: row;
    align-items: center;
    gap: $spacing-m;
    padding: $spacing-m $spacing-l;
    text-align: left;

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
    }

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

    .chevron {
      display: block;
      flex-shrink: 0;
      width: 1.25rem;
      height: 1.25rem;
      color: $text-secondary;
    }
  }
}
</style>
