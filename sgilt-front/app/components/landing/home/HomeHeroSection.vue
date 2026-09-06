<template>
  <section class="home-hero">
    <div class="text">
      <p class="eyebrow">{{ $t('landing-page.hero.subtitle-line-1') }}</p>

      <h1 class="title">
        <span class="title-thin">{{ titleParts.prefix }}</span>
        <span class="title-bold"
          >{{ $t('landing-page.hero.title-highlight')
          }}<span class="title-mark">{{ titleParts.suffix }}</span></span
        >
      </h1>

      <p class="subtitle">{{ $t('landing-page.hero.subtitle-line-2') }}</p>

      <HomeCtaButton />

      <p class="tagline-mini">
        <SparklingIcon class="icon" />
        {{ $t('landing-page.hero.tagline-mini') }}
      </p>
    </div>

    <div class="photo" role="img" :aria-label="$t('landing-page.hero.photo-caption')"></div>
  </section>
</template>

<script setup lang="ts">
import { SparklingIcon } from '@remixicons/vue/line'
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

.home-hero {
  display: grid;
  grid-template-columns: 1fr;
  background: $surface-soft;

  @media (min-width: $breakpoint-desktop) {
    grid-template-columns: 1fr 1.1fr;
    min-height: 90dvh;
  }

  .text {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    justify-content: center;
    gap: $spacing-m;
    padding: $spacing-xxl $section-padding-x;

    @media (min-width: $breakpoint-desktop) {
      padding: $spacing-xxxl;
    }
  }

  .eyebrow {
    margin: 0;
    color: $text-secondary;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    letter-spacing: 0.14em;
    text-transform: uppercase;
  }

  .title {
    margin: 0;
    display: flex;
    flex-direction: column;
    color: $brand-primary;
  }

  .title-thin,
  .title-mark {
    font-family: 'Cormorant Garamond', serif;
    font-weight: 500;
    font-size: clamp(2.8rem, 6.5vw, 4rem);
    line-height: 1.05;
  }

  .title-bold {
    font-weight: 700;
    font-size: clamp(2.8rem, 6.5vw, 4rem);
    line-height: 1.05;
    color: $brand-accent;
  }

  .title-mark {
    margin-left: 0.08em;
    color: $brand-primary;
  }

  .subtitle {
    margin: 0;
    max-width: 40ch;
    color: $text-secondary;
    font-size: $font-size-lg;
    line-height: $line-height-relaxed;
  }

  .tagline-mini {
    margin: $spacing-xs 0 0;
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    color: $text-secondary;
    font-size: $font-size-md;

    .icon {
      width: 1.1rem;
      height: 1.1rem;
      color: $brand-accent;
      flex-shrink: 0;
    }
  }

  // ── Photo ─────────────────────────────────────────────────────────────────
  .photo {
    position: relative;
    min-height: 16rem;
    background-image: url('/images/hero-party.png');
    background-size: cover;
    background-repeat: no-repeat;
    background-position: 10% center;
    filter: saturate(1.3) brightness(1.02);

    @media (min-width: $breakpoint-desktop) {
      background-image: url('/images/hero-party-desktop.png');
      // Bord gauche en diagonale (plutôt qu'une colonne droite) avec un léger
      // zigzag — le découpage laisse voir le fond de .home-hero derrière.
      clip-path: polygon(0% 0, 100% 0, 100% 100%, 0 100%, 13% 33%);
    }
  }

}
</style>
