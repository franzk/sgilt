<template>
  <button
    type="button"
    class="event-type-card"
    :class="{ selected }"
    :aria-pressed="selected"
    @click="$emit('select')"
  >
    <div class="illustration" :style="{ backgroundImage: `url(${image})` }">
      <div v-if="selected" class="check-badge" aria-hidden="true">
        <CheckIcon class="icon" />
      </div>
    </div>

    <p class="name">{{ label }}</p>
  </button>
</template>

<script setup lang="ts">
import { CheckIcon } from '@remixicons/vue/line'

defineProps<{
  label: string
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

  // Hauteur fixe et prévisible (une seule ligne, taille fixe — pas de clamp
  // lié au viewport) : l'espace restant pour .illustration doit être stable.
  .name {
    flex-shrink: 0;
    margin: $spacing-xs 0 0;
    font-family: 'Cormorant Garamond', serif;
    font-weight: $font-weight-medium;
    font-size: $font-size-md;
    line-height: $line-height-tight;
    color: $text-primary;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
