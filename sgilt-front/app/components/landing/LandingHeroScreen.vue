<template>
  <div class="home">
    <section class="search-form">
      <!-- title -->
      <div class="hero">
        <PageHeroTitle :eyebrow="eyebrow" :title="title" :highlight="highlightedSubtext" />
        <p v-if="subtitle" class="tagline">{{ subtitle }}</p>
      </div>

      <div class="action-zone">
        <slot />
      </div>
    </section>
    <div class="app-background"></div>
  </div>
</template>

<script setup lang="ts">
import PageHeroTitle from './PageHeroTitle.vue'

defineProps<{
  title: string
  highlightedSubtext: string
  subtitle?: string
  eyebrow?: string
}>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// ─── Tokens locaux ────────────────────────────────────────────────────────────
$background: white;

$search-form-gap: clamp(0.5rem, 1.5vh, 1.5rem);

$tagline-font-size: 1.05rem;
$tagline-line-height: 1.5;

// ─── Composant ────────────────────────────────────────────────────────────────
.home {
  position: relative;
  width: 100%;
  // min-height (pas height) : la page grandit avec son contenu plutôt que
  // d'être plafonnée à la hauteur du viewport — le scroll se fait au niveau de la page entière
  min-height: $viewport-below-header;
  overflow: clip;
  background: $background;

  display: flex;
  flex-direction: column;

  @media (max-width: #{$breakpoint-desktop - 1px}) {
    // Cacher l'ombre du header uniquement sur mobile
    :global(.app-header) {
      box-shadow: none;
    }
  }

  // ── Zone de recherche ──────────────────────────────────────────────────────
  .search-form {
    z-index: 2;
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    // Même padding que .wrap + .header dans fete.vue (côtés $section-padding-x,
    // haut $spacing-s) pour garder le même emplacement à l'écran entre les deux
    // pages du tunnel.
    padding: $spacing-s $section-padding-x 0;
    justify-content: center;
    gap: $search-form-gap;
    min-height: 0;

    // Mobile uniquement
    @media (max-width: #{$breakpoint-desktop - 1px}) {
      flex: 0 70%;
      justify-content: space-around;
    }

    // Desktop : titre en pleine largeur, zone d'action pleine largeur en-dessous
    @media (min-width: $breakpoint-desktop) {
      align-items: center;
      justify-content: flex-start;
      flex-direction: column;
      width: 100%;
      max-width: $container-max-width;
      margin: 0 auto;
      padding: $spacing-m $section-padding-x $spacing-l;
      gap: $spacing-l;
      // conditionne l'affichage à la taille de la page
      flex: 1;
    }
  }

  // ── Zone d'action (contenu spécifique à chaque écran) ────────────────────────
  .action-zone {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    min-height: 0;
  }

  // ── Hero (titre + sous-titre) ─────────────────────────────────────────────────
  .hero {
    text-align: center;
    color: $color-primary;
    display: flex;
    flex-direction: column;
    align-items: center;

    @media (min-width: $breakpoint-desktop) {
      gap: $spacing-m;
    }
  }

  // Eyebrow + titre : voir PageHeroTitle.vue (composant partagé avec
  // fete.vue, pour garantir le même gabarit entre les deux écrans du tunnel).

  .tagline {
    display: none;
    margin: 0;
    color: $text-secondary;

    // Pas de max-width étroit : la tagline occupe la largeur disponible et
    // wrap sur plusieurs lignes si l'écran est trop étroit
    @media (min-width: $breakpoint-desktop) {
      display: block;
      font-size: $tagline-font-size;
      line-height: $tagline-line-height;
      max-width: none;
    }
  }

  // ── Background décoratif ───────────────────────────────────────────────────
  .app-background {
    position: absolute;
    z-index: 0;
    inset: 0;
    mask-image: none;
    -webkit-mask-image: none;
    pointer-events: none;
  }
}
</style>
