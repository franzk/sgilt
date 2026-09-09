<template>
  <section class="home-local">
    <div class="photo" role="img" :aria-label="$t('landing-page.local.photo-alt')">
      <!-- Mobile uniquement : le texte s'affiche en surimpression sur la photo
           plutôt qu'en dessous (voir .text-content pour la version desktop). -->
      <div class="photo-overlay">
        <h2 class="title">{{ $t('landing-page.local.title') }}</h2>
        <p>{{ $t('landing-page.local.paragraph-1') }}</p>
        <p>{{ $t('landing-page.local.paragraph-2') }}</p>
        <p class="closing-line">{{ $t('landing-page.local.closing-line') }}</p>
      </div>
    </div>

    <div class="text">
      <div class="text-content">
        <h2 class="title">{{ $t('landing-page.local.title') }}</h2>
        <p>{{ $t('landing-page.local.paragraph-1') }}</p>
        <p>{{ $t('landing-page.local.paragraph-2') }}</p>
        <p class="closing-line">{{ $t('landing-page.local.closing-line') }}</p>
      </div>

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
    position: relative;
    min-height: 24rem;
    background-image: url('/images/taennel.png');
    background-size: cover;

    // Bord droit en diagonale (même logique que le hero, miroir horizontal
    // puisque la photo est à gauche ici et non à droite).
    @media (min-width: $breakpoint-desktop) {
      min-height: 16rem;
      clip-path: polygon(0 0, 100% 0, 80% 100%, 0 100%);
    }
  }

  // Mobile uniquement : texte en surimpression sur la photo, scrim sombre pour
  // la lisibilité — voir .text-content pour l'équivalent desktop (à côté, pas dessus).
  .photo-overlay {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    padding: $spacing-xl $section-padding-x;
    background: linear-gradient(to top, rgba(0, 0, 0, 0.65) 0%, rgba(0, 0, 0, 0.1) 60%, transparent 100%);

    .title,
    p {
      color: $text-inverted;
    }

    p {
      margin: 0 0 $spacing-s;
      font-size: $font-size-sm;
      line-height: $line-height-relaxed;
    }

    @media (min-width: $breakpoint-desktop) {
      display: none;
    }
  }

  .text {
    padding: $spacing-m $section-padding-x $spacing-xxl;
    display: flex;
    flex-direction: column;
    justify-content: center;

    @media (min-width: $breakpoint-desktop) {
      padding: $spacing-xxxl;
    }
  }

  // Desktop uniquement : texte dans le panneau à côté de la photo, pas dessus.
  .text-content {
    display: none;

    @media (min-width: $breakpoint-desktop) {
      display: block;
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
    flex-direction: row;
    gap: $spacing-m;

    @media (min-width: $breakpoint-desktop) {
      gap: $spacing-l;
    }

    li {
      flex: 1;
      min-width: 0;
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      gap: $spacing-xs;
    }

    .feature-icon {
      width: 2rem;
      height: 2rem;
      color: $brand-accent;
      flex-shrink: 0;
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
