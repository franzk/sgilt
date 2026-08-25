<template>
  <div class="home">
    <section class="search-form">
      <!-- title -->
      <div class="hero">
        <p class="title">
          <span class="title-thin">{{ titleParts.prefix }}</span>
          <span class="title-bold"
            >{{ highlightedSubtext }}<span class="title-mark">{{ titleParts.suffix }}</span></span
          >
        </p>
        <p v-if="subtitle" class="tagline">{{ subtitle }}</p>
      </div>

      <div class="action-zone">
        <slot />
      </div>
    </section>
    <section class="photo-layer" aria-hidden="true"></section>
    <div class="app-background"></div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  title: string
  highlightedSubtext: string
  subtitle?: string
}>()

// Sépare `title` autour de `highlightedSubtext` pour appliquer le style accent
// à ce seul segment — si la sous-chaîne n'est pas trouvée, tout part en style
// "thin" plutôt que de planter.
const titleParts = computed(() => {
  const idx = props.title.indexOf(props.highlightedSubtext)
  if (idx === -1) return { prefix: props.title, suffix: '' }
  return {
    prefix: props.title.slice(0, idx),
    suffix: props.title.slice(idx + props.highlightedSubtext.length),
  }
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// ─── Tokens locaux ────────────────────────────────────────────────────────────
$background: white;
$padding-top: 0.75rem;
$hero-color: #000000;

$search-form-padding: $spacing-l $spacing-m 0;
$search-form-gap: clamp(0.5rem, 1.5vh, 1.5rem);

$title-thin-font-weight: 600;
$title-thin-font-size: 2.5rem;
$title-thin-line-height: 2.75rem;

$title-bold-font-weight: 900;
$title-bold-font-size: 3.2rem;
$title-bold-line-height: 3rem;
$title-bold-letter-spacing: 0.02em;
$title-bold-margin-bottom: 0.875rem;

$tagline-font-size: 1.05rem;
$tagline-line-height: 1.5;

$photo-filter: brightness(1.03) contrast(1.03) saturate(1.06);

// ─── Composant ────────────────────────────────────────────────────────────────
.home {
  position: relative;
  width: 100%;
  // min-height (pas height) : la page grandit avec son contenu plutôt que
  // d'être plafonnée à la hauteur du viewport — le scroll se fait au niveau de la page entière
  min-height: calc(100dvh - $app-header-height);
  overflow: clip;
  background: $background;
  padding-top: $padding-top;

  display: flex;
  flex-direction: column;

  @media (max-width: #{$breakpoint-desktop - 1px}) {
    // Cacher l'ombre du header uniquement sur mobile
    :global(.app-header) {
      box-shadow: none;
    }
  }

  // ── Photo de fond ──────────────────────────────────────────────────────────
  .photo-layer {
    position: absolute;
    inset: 70% 0 0 0;
    z-index: 1;
    filter: $photo-filter;
    background-image: url('/images/hero-party-desktop.png');
    background-size: cover;
    background-repeat: no-repeat;
    background-position: 50% 30%;
    -webkit-mask-image: linear-gradient(to bottom, transparent 0%, #000 18%, #000 100%);
    mask-image: linear-gradient(to bottom, transparent 0%, #000 28%, #000 100%);
    -webkit-mask-repeat: no-repeat;
    mask-repeat: no-repeat;
    -webkit-mask-size: 100% 100%;
    mask-size: 100% 100%;
    pointer-events: none;

    @media (min-width: $breakpoint-desktop) {
      inset: 40% 0 0 0;
    }
  }

  // ── Zone de recherche ──────────────────────────────────────────────────────
  .search-form {
    z-index: 2;
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: $search-form-padding;
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
      padding: $spacing-xl $spacing-xl $spacing-l;
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

  .title {
    display: flex;
    flex-direction: column;
    align-items: center;
    color: $hero-color;

    // Desktop : titre sur une seule ligne
    @media (min-width: $breakpoint-desktop) {
      flex-direction: row;
      align-items: baseline;
      gap: 0.5rem;
    }
  }

  .title-thin {
    font-weight: $title-thin-font-weight;
    font-size: $title-thin-font-size;
    line-height: $title-thin-line-height;
    font-family: 'Cormorant Garamond', serif;
  }

  .title-bold {
    display: inline-flex;
    align-items: center;
    font-weight: $title-bold-font-weight;
    font-size: $title-bold-font-size;
    line-height: $title-bold-line-height;
    letter-spacing: $title-bold-letter-spacing;
    margin-bottom: $title-bold-margin-bottom;
    color: $color-accent;

    @media (min-width: $breakpoint-desktop) {
      margin-bottom: 0;
    }
  }

  // Le "?" reprend le style du début de phrase, seul le mot accentué garde la grosse typo
  .title-mark {
    font-weight: $title-thin-font-weight;
    font-size: $title-thin-font-size;
    line-height: $title-thin-line-height;
    font-family: 'Cormorant Garamond', serif;
    letter-spacing: normal;
    color: $hero-color;
  }

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
