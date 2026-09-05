<template>
  <section class="home-hero">
    <div class="search-form">
      <div class="hero">
        <h1 class="title">
          <span class="title-thin">{{ titleParts.prefix }}</span>
          <span class="title-bold"
            >{{ $t('landing-page.hero.title-highlight')
            }}<span class="title-mark">{{ titleParts.suffix }}</span></span
          >
        </h1>

        <p class="tagline">
          {{ $t('landing-page.hero.subtitle-line-1') }}
          {{ $t('landing-page.hero.subtitle-line-2') }}
        </p>
      </div>

      <div class="action-zone">
        <HomeCtaButton />
      </div>
    </div>

    <section class="photo-layer" aria-hidden="true"></section>
  </section>
</template>

<script setup lang="ts">
import HomeCtaButton from './HomeCtaButton.vue'

const { t } = useI18n()

// Sépare le titre autour du mot accentué pour appliquer le style accent à ce seul
// segment — si la sous-chaîne n'est pas trouvée, tout part en style "thin" plutôt
// que de planter (même logique que LandingHeroScreen, dupliquée localement pour ne
// pas toucher un composant utilisé par les pages 2/3).
const titleParts = computed(() => {
  const title = t('landing-page.hero.title')
  const highlight = t('landing-page.hero.title-highlight')
  const idx = title.indexOf(highlight)
  if (idx === -1) return { prefix: title, suffix: '' }
  return {
    prefix: title.slice(0, idx),
    suffix: title.slice(idx + highlight.length),
  }
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// Repris à l'identique de LandingHeroScreen.vue (hero de /fete) pour rester iso.
// Deux différences volontaires, commentées plus bas : (1) la tagline reste visible
// sur mobile — ici c'est le texte narratif principal, pas une ligne décorative à
// masquer pour gagner de la place au-dessus d'une grille de cartes ; (2) pas de
// hack globale masquant l'ombre du header en continu — cette page fait 8 sections
// et pas juste un écran, l'ombre doit pouvoir réapparaître au scroll.
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

.home-hero {
  position: relative;
  width: 100%;
  min-height: calc(100dvh - $app-header-height);
  overflow: clip;
  background: $surface-white;
  padding-top: 0.75rem;
  display: flex;
  flex-direction: column;

  // ── Photo de fond (recadrage mobile dédié, desktop au-delà du breakpoint) ────
  .photo-layer {
    position: absolute;
    inset: 70% 0 0 0;
    z-index: 1;
    filter: $photo-filter;
    background-image: url('/images/hero-party.png');
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
      background-image: url('/images/hero-party-desktop.png');
    }
  }

  // ── Zone de contenu (titre + tagline + CTA) ──────────────────────────────────
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

    @media (max-width: #{$breakpoint-desktop - 1px}) {
      flex: 0 70%;
      justify-content: space-around;
    }

    @media (min-width: $breakpoint-desktop) {
      align-items: center;
      justify-content: flex-start;
      flex-direction: column;
      width: 100%;
      max-width: $container-max-width;
      margin: 0 auto;
      padding: $spacing-xl $spacing-xl $spacing-l;
      gap: $spacing-l;
      flex: 1;
    }
  }

  .action-zone {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    min-height: 0;
  }

  .hero {
    text-align: center;
    color: $brand-primary;
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
    color: $brand-accent;

    @media (min-width: $breakpoint-desktop) {
      margin-bottom: 0;
    }
  }

  // Le "?" reprend le style du prefixe ("Qu'est-ce qu'on") : seul le mot accentué
  // garde la grosse typo. margin-left explicite : l'espace naturel de la chaîne
  // ("FÊTE ?") ne suffit pas visuellement ici, contrairement à /fete.
  .title-mark {
    margin-left: 0.2em;
    font-weight: $title-thin-font-weight;
    font-size: $title-thin-font-size;
    line-height: $title-thin-line-height;
    font-family: 'Cormorant Garamond', serif;
    letter-spacing: normal;
    color: $hero-color;
  }

  // Différence volontaire vs /fete : toujours visible sur mobile (voir note en haut).
  .tagline {
    display: block;
    margin: $spacing-m 0 0;
    color: $text-secondary;
    font-size: $font-size-md;
    line-height: $line-height-relaxed;
    max-width: 34ch;

    @media (min-width: $breakpoint-desktop) {
      font-size: $tagline-font-size;
      line-height: $tagline-line-height;
      max-width: none;
    }
  }
}
</style>
