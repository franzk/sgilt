<template>
  <section class="home-local">
    <div class="photo" role="img" :aria-label="$t('landing-page.local.photo-alt')"></div>

    <div class="text">
      <h2 class="title">{{ $t('landing-page.local.title') }}</h2>
      <p>{{ $t('landing-page.local.paragraph-1') }}</p>
      <p>{{ $t('landing-page.local.paragraph-2') }}</p>
      <p class="closing-line">{{ $t('landing-page.local.closing-line') }}</p>

      <ul class="features">
        <li>
          <MapPin2Icon class="feature-icon" aria-hidden="true" />
          <div>
            <p class="label">{{ $t('landing-page.local.feature-local-label') }}</p>
            <p class="description">{{ $t('landing-page.local.feature-local-description') }}</p>
          </div>
        </li>
        <li>
          <LeafIcon class="feature-icon" aria-hidden="true" />
          <div>
            <p class="label">{{ $t('landing-page.local.feature-authentique-label') }}</p>
            <p class="description">{{ $t('landing-page.local.feature-authentique-description') }}</p>
          </div>
        </li>
        <li>
          <HeartsIcon class="feature-icon" aria-hidden="true" />
          <div>
            <p class="label">{{ $t('landing-page.local.feature-humain-label') }}</p>
            <p class="description">{{ $t('landing-page.local.feature-humain-description') }}</p>
          </div>
        </li>
      </ul>
    </div>
  </section>
</template>

<script setup lang="ts">
import { MapPin2Icon, LeafIcon, HeartsIcon } from '@remixicons/vue/line'
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.home-local {
  display: grid;
  grid-template-columns: 1fr;

  @media (min-width: $breakpoint-desktop) {
    grid-template-columns: 1.15fr 1fr;
    min-height: 34rem;
  }

  .photo {
    min-height: 16rem;
    background-image: url('/images/taennel.png');
    background-size: cover;

    // Bord droit en diagonale (même logique que le hero, miroir horizontal
    // puisque la photo est à gauche ici et non à droite).
    @media (min-width: $breakpoint-desktop) {
      clip-path: polygon(0 0, 100% 0, 80% 100%, 0 100%);
    }
  }

  .text {
    padding: $spacing-xxl $section-padding-x;
    display: flex;
    flex-direction: column;
    justify-content: center;

    @media (min-width: $breakpoint-desktop) {
      padding: $spacing-xxxl;
    }

    p {
      margin: 0 0 $spacing-s;
      max-width: 48ch;
      color: $text-secondary;
      font-size: $font-size-md;
      line-height: $line-height-relaxed;
    }
  }

  .title {
    margin: 0 0 $spacing-l;
    font-family: 'Cormorant Garamond', serif;
    font-weight: $font-weight-medium;
    font-size: clamp(1.8rem, 4vw, 2.5rem);
    color: $text-primary;
  }

  .closing-line {
    color: $text-primary;
    font-weight: $font-weight-semibold;
  }

  .features {
    list-style: none;
    margin: $spacing-l 0 0;
    padding: 0;
    display: flex;
    flex-direction: column;
    gap: $spacing-m;

    @media (min-width: $breakpoint-desktop) {
      flex-direction: row;
      gap: $spacing-l;
    }

    li {
      display: flex;
      align-items: flex-start;
      gap: $spacing-xs;
    }

    .feature-icon {
      width: 1.25rem;
      height: 1.25rem;
      color: $brand-accent;
      flex-shrink: 0;
      margin-top: 0.15rem;
    }

    .label {
      margin: 0;
      color: $text-primary;
      font-weight: $font-weight-semibold;
      font-size: $font-size-sm;
    }

    .description {
      margin: 0;
      color: $text-secondary;
      font-size: $font-size-xs;
    }
  }
}
</style>
