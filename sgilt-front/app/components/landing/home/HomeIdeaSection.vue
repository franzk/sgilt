<template>
  <section class="home-idea">
    <div class="text">
      <h2 class="title">
        {{ $t('landing-page.idea.eyebrow') }}<br />
        {{ $t('landing-page.idea.title') }}
      </h2>

      <p class="lines">
        {{ $t('landing-page.idea.line-mariage') }}<br />
        {{ $t('landing-page.idea.line-anniversaire') }}<br />
        {{ $t('landing-page.idea.line-soiree') }}<br />
        {{ $t('landing-page.idea.line-pro') }}
      </p>

      <p class="conclusion">{{ $t('landing-page.idea.conclusion') }}</p>
    </div>

    <div class="visual">
      <img src="/images/neons-recti.png" alt="" class="leaves leaves--top" />
      <img src="/images/neons-courbe.png" alt="" class="leaves leaves--bottom" />
      <SparklingIcon class="sparkle" aria-hidden="true" />

      <div class="polaroid polaroid--back">
        <div class="photo photo--salle" role="img" aria-label="Une salle prête pour un événement Sgilt"></div>
      </div>
      <div class="polaroid polaroid--front">
        <div class="photo" role="img" aria-label="Un événement professionnel Sgilt"></div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { SparklingIcon } from '@remixicons/vue/line'
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.home-idea {
  display: grid;
  grid-template-columns: 1fr;
  gap: $spacing-xl;
  padding: $spacing-m $section-padding-x $spacing-xxxl;
  align-items: center;

  @media (min-width: $breakpoint-desktop) {
    grid-template-columns: 1fr 1fr;
    max-width: $container-max-width;
    margin: 0 auto;
    gap: $spacing-xxxl;
    padding: $spacing-xxxl $section-padding-x;
  }

  .text {
    max-width: 52ch;
  }

  .title {
    margin: 0 0 $spacing-l;
    font-family: 'Cormorant Garamond', serif;
    font-weight: $font-weight-medium;
    font-size: clamp(1.8rem, 4vw, 2.5rem);
    color: $text-primary;
  }

  .lines {
    margin: 0 0 $spacing-l;
    color: $text-secondary;
    font-size: $font-size-lg;
    line-height: $line-height-relaxed;
  }

  p.conclusion {
    margin: 0;
    color: $text-primary;
    font-weight: 600;
    font-size: $font-size-lg;
    line-height: $line-height-relaxed;
  }

  // ── Duo de vignettes façon polaroid (placeholders — vraies photos à venir) ───
  .visual {
    position: relative;
    height: 16rem;

    @media (min-width: $breakpoint-desktop) {
      height: 19rem;
    }
  }

  .sparkle {
    position: absolute;
    top: -1rem;
    right: -0.5rem;
    width: 1.5rem;
    height: 1.5rem;
    color: $brand-accent;
    z-index: 1;
  }

  // Décors néon dans l'espace libre autour du cluster de photos — pas dessous,
  // comme le sparkle top-right.
  .leaves {
    position: absolute;

    object-fit: contain;
    z-index: 0;

    &--top {
      top: -2rem;
      left: -2rem;
      width: 8rem;
      height: 8rem;
    }

    &--bottom {
      bottom: -2.5rem;
      right: -1.5rem;
      width: 6.5rem;
      height: 6rem;
    }
  }

  .polaroid {
    position: absolute;
    width: 46%;
    aspect-ratio: 4 / 5;
    padding: $spacing-xs $spacing-xs $spacing-m;
    background: $surface-white;
    border-radius: $radius-sm;
    box-shadow: 0 12px 28px $shadow-m;

    .photo {
      width: 100%;
      height: 100%;
      border-radius: $radius-sm;
      background-image: url('/images/corpo.png');
      background-size: cover;
      background-position: center;

      &--salle {
        background-image: url('/images/salle-prete.jpg');
      }
    }

    &--back {
      top: 0;
      left: 0;
      z-index: 1;
      transform: rotate(-6deg);
    }

    &--front {
      bottom: 0;
      right: 0;
      width: 60%;
      aspect-ratio: 5 / 4;
      transform: rotate(4deg);
    }
  }
}
</style>
