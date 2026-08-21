<template>
  <div class="home">
    <section class="search-form">
      <!-- title -->
      <div class="hero">
        <p class="title">
          <span class="title-thin">{{ $t('landing.search-banner.title-part-1') }}</span>
          <span class="title-bold"
            >{{ $t('landing.search-banner.title-part-2')
            }}<span class="title-mark">{{ $t('landing.search-banner.title-mark') }}</span></span
          >
        </p>
      </div>

      <div class="event-types">
        <div class="event-type-grid">
          <EventTypeCard
            v-for="eventType in EVENT_TYPE_CATALOG"
            :key="eventType.key"
            :type="eventType.key"
            :label="eventType.label"
            :icon="eventType.icon"
          />
        </div>

        <p class="inspire-link">{{ $t('landing.search-banner.inspire-link') }}</p>
      </div>
    </section>
    <section class="photo-layer" aria-hidden="true"></section>
    <div class="app-background"></div>
  </div>
</template>

<script setup lang="ts">
import EventTypeCard from '~/components/cards/EventTypeCard.vue'
import { EVENT_TYPE_CATALOG } from '~/utils/eventTypes'

useHead({ titleTemplate: '%s' })

useSeoMeta({
  title: 'Sgilt - Organisez votre événement en Alsace',

  description:
    "Trouvez les bons prestataires en Alsace pour votre mariage, anniversaire ou événement d'entreprise.",

  ogTitle: 'Organisez votre événement en Alsace avec Sgilt',

  ogDescription:
    'Trouvez les bons prestataires, échangez avec eux et suivez vos réservations au même endroit.',

  ogImage: 'https://sgilt.alsace/images/sgilt-social.png',

  twitterCard: 'summary_large_image',
  twitterTitle: 'Organisez votre événement en Alsace avec Sgilt',
  twitterDescription:
    'Trouvez les bons prestataires et suivez vos réservations au même endroit.',
  twitterImage: 'https://sgilt.alsace/images/sgilt-social.png',
})

const { showOnboarding } = useSearchUi()

onMounted(() => {
  showOnboarding.value = true
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

$event-types-width: 100%;
$event-types-max-width: 30rem;
$event-type-grid-gap: $spacing-s;

$photo-filter: brightness(1.03) contrast(1.03) saturate(1.06);

// ─── Composant ────────────────────────────────────────────────────────────────
.home {
  position: relative;
  width: 100%;
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

  @media (min-width: $breakpoint-desktop) {
    justify-content: flex-start;
    padding-top: 0;
    color: #fff;
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
      inset: 0;
      z-index: 0;
      background-image: url('/images/hero-party-desktop.png');
      -webkit-mask-image: none;
      mask-image: none;
      filter: brightness(1) contrast(1.03) saturate(1.06);

      &::after {
        content: '';
        position: absolute;
        inset: 0;
        pointer-events: none;
      }
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

    @media (min-width: $breakpoint-desktop) {
      min-height: 50vh;
      padding-top: 6%;
      padding-bottom: 3%;
      justify-content: space-between;
      border-radius: 0 0 10px 10px;
      overflow: clip;

      // Glassmorphism
      background: linear-gradient(
        180deg,
        rgba(255, 255, 255, 0.25) 0%,
        rgba(255, 255, 255, 0.1) 100%
      );
      backdrop-filter: blur(3px) saturate(1.15);
      -webkit-backdrop-filter: blur(3px) saturate(1.15);
      border: 1px solid rgba(255, 255, 255, 0.42);
      box-shadow:
        0 10px 40px rgba(0, 0, 0, 0.15),
        inset 0 1px 0 rgba(255, 255, 255, 0.4);

      &::before {
        content: '';
        position: absolute;
        inset: 0;
        background: radial-gradient(
          900px 220px at 50% 0%,
          rgba($color-accent, 0.12),
          transparent 60%
        );
        pointer-events: none;
      }
    }
  }

  // ── Types d'événement (grille de cartes + échappatoire) ──────────────────────
  .event-types {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: $spacing-l;
  }

  // ── Hero (titre + tagline) ─────────────────────────────────────────────────
  .hero {
    text-align: center;
    color: $color-primary;
    display: flex;
    flex-direction: column;
    align-items: center;

    @media (min-width: $breakpoint-desktop) {
      transform: translateY(-0.5rem);
      width: 100%;
      color: #fff;
      text-shadow:
        0 2px 10px rgba(0, 0, 0, 0.35),
        0 14px 40px rgba(0, 0, 0, 0.22);
      padding: 0;
      margin: 0;
      gap: 0.6rem;
    }
  }

  .title {
    display: flex;
    flex-direction: column;
    align-items: center;
    color: $hero-color;

    @media (min-width: $breakpoint-desktop) {
      flex-direction: row;
      color: inherit;
      align-items: baseline;
      gap: 0.8rem;
      margin: 0;
      text-shadow: 0 6px 18px rgba(0, 0, 0, 0.18);
    }
  }

  .title-thin {
    font-weight: $title-thin-font-weight;
    font-size: $title-thin-font-size;
    line-height: $title-thin-line-height;
    font-family: 'Cormorant Garamond', serif;

    @media (min-width: $breakpoint-desktop) {
      font-size: 3.2rem;
      line-height: 1.05;
      color: $color-primary;
    }
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
      display: inline;
      font-weight: 800;
      font-size: 4rem;
      line-height: 1.05;
      letter-spacing: 0.02rem;
      margin-top: 0.2rem;
      margin-bottom: 0;
      color: #fff7dd; // $color-accent;
      text-shadow:
        0 0 12px rgba(255, 210, 0, 0.4),
        0 8px 24px rgba(0, 0, 0, 0.49);
    }
  }

  // Le "?" reprend le style du début de phrase, seul "FÊTE" garde la grosse typo accent
  .title-mark {
    font-weight: $title-thin-font-weight;
    font-size: $title-thin-font-size;
    line-height: $title-thin-line-height;
    font-family: 'Cormorant Garamond', serif;
    letter-spacing: normal;
    color: $hero-color;

    @media (min-width: $breakpoint-desktop) {
      font-size: 3.2rem;
      line-height: 1.05;
      color: $color-primary;
      text-shadow: 0 6px 18px rgba(0, 0, 0, 0.18);
    }
  }

  // ── Grille des types d'événement ──────────────────────────────────────────
  .event-type-grid {
    margin: 0 auto;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: $event-type-grid-gap;
    width: $event-types-width;
    max-width: $event-types-max-width;
  }

  // ── Échappatoire ("voir les prestataires") ─────────────────────────────────
  .inspire-link {
    margin: 0;
    padding: $spacing-xs $spacing-m;
    background: rgba(255, 255, 255, 0.55);
    backdrop-filter: blur(6px);
    -webkit-backdrop-filter: blur(6px);
    border-radius: 999px;
    box-shadow: 0 1px 4px rgba(47, 42, 37, 0.08);
    color: $text-secondary;
    font-size: $font-size-md;
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
