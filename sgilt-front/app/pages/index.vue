<template>
  <div class="home">
    <section class="search-form">
      <!-- title -->
      <div class="hero">
        <p class="title">
          <span class="title-thin">{{ $t('home.search-banner.title-part-1') }}</span>
          <span class="title-bold">{{ $t('home.search-banner.title-part-2') }}</span>
        </p>
        <h3 class="tagline">
          <span class="tagline-mobile">{{ $t('home.search-banner.tagline-mobile') }}</span>
          <span class="tagline-desktop">{{ $t('home.search-banner.tagline-desktop') }}</span>
        </h3>
      </div>

      <!-- composants -->
      <div class="inputs">
        <SgiltDatePicker placeholder="Votre date" v-model="dateModel" />

        <SgiltSelect :options="selectOptions" :model-value="selectedOption">
          <template v-slot:left-icon> <IconRocket /> </template>
        </SgiltSelect>
        <SgiltButton class="submit_button" @click="navigateTo('/search')">
          C'est parti !
        </SgiltButton>
      </div>
    </section>
    <section class="photo-layer" aria-hidden="true"></section>
    <div class="app-background"></div>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import SgiltSelect from '~/components/basics/inputs/SgiltSelect.vue'
import IconRocket from '~/components/icons/IconRocket.vue'
import { useDevice } from '~/composables/useDevice'

useSeoMeta({
  title: '',
  description: '',
})

useHead({
  title: 'Sgilt (Beta)',
  meta: [{ name: 'theme-color', content: '#ffffff' }],
})

const { isMobile } = useDevice()

const selectOptions = [
  { value: '-1', label: 'Votre événement' },
  { value: '0', label: 'Mariage' },
  { value: '1', label: 'Anniversaire' },
  { value: '2', label: "Fête d'entreprise" },
  { value: '3', label: 'Autre' },
]

const selectedOption = ref(selectOptions[0]!.value)

const { dateModel, showOnboarding } = useSearchUi()
showOnboarding.value = true // on affiche l'onboarding à la suite de la page d'accueil
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$background: white;
$padding-top: 0.75rem;

$hero-color: #000000;

$search-form-padding: $spacing-l $spacing-m 0;
$search-form-gap: clamp(1rem, 3vh, 3rem);

$title-thin-font-weight: 600;
$title-thin-font-size: 2.5rem;
$title-thin-line-height: 2.75rem;

$title-bold-font-weight: 900;
$title-bold-font-size: 3.2rem;
$title-bold-line-height: 3rem;
$title-bold-letter-spacing: 0.02em;
$title-bold-margin-bottom: 0.875rem;

$tagline-font-weight: 500;
$tagline-font-size: 1.125rem;
$tagline-line-height: 1.25;
$tagline-color: $text-secondary;
$tagline-max-width: 36rem;

$inputs-gap: 0.875rem;
$inputs-width: 90%;
$inputs-max-width: 30rem;
$inputs-font-size: 1.125rem;
$inputs-height: 3rem;

$photo-filter: brightness(1.03) contrast(1.03) saturate(1.06);

.home {
  position: relative;
  width: 100%;
  min-height: calc(100dvh - $app-header-height); // pour compenser la hauteur du header fixe
  overflow: hidden;
  background: $background;
  padding-top: $padding-top;

  // cacher l'ombre du header sur mobile
  @media (max-width: 849px) {
    :global(.app-header) {
      box-shadow: none !important;
    }
  }

  // 1 grille = 2 zones
  display: grid;
  grid-template-rows: 50% 50%;
  grid-template-columns: 1fr;
  grid-template-areas:
    'content'
    'visual';

  .search-form {
    z-index: 2;
    grid-area: content;
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: $search-form-padding;
    justify-content: center;
    gap: $search-form-gap;

    .hero {
      text-align: center;
      color: $color-primary;

      display: flex;
      flex-direction: column;
      align-items: center;
    }

    .title {
      display: flex;
      flex-direction: column;
      color: $hero-color !important;
    }

    .title-thin {
      font-weight: $title-thin-font-weight;
      font-size: $title-thin-font-size;
      line-height: $title-thin-line-height;
      font-family: 'Cormorant Garamond', serif;
    }

    .title-bold {
      font-weight: $title-bold-font-weight;
      font-size: $title-bold-font-size;
      line-height: $title-bold-line-height;
      letter-spacing: $title-bold-letter-spacing;
      margin-bottom: $title-bold-margin-bottom;
    }

    .tagline {
      font-weight: $tagline-font-weight;
      font-size: $tagline-font-size;
      line-height: $tagline-line-height;
      color: $tagline-color;
      max-width: $tagline-max-width;
      .tagline-desktop {
        display: none;
      }
    }

    .inputs {
      margin: 0 auto;

      display: flex;
      flex-direction: column;
      align-items: center;
      gap: $inputs-gap;
      width: $inputs-width;
      max-width: $inputs-max-width;
      font-size: $inputs-font-size;

      .sgilt-button {
        width: 100%;
      }

      > * {
        height: $inputs-height;
      }
    }
  }

  .photo-layer {
    grid-area: visual;
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
  }

  .app-background {
    position: absolute;
    z-index: 0;
    inset: 0;

    mask-image: none !important;
    -webkit-mask-image: none !important;

    pointer-events: none;
  }
}

/* =========================
   DESKTOP HERO FULLSCREEN
   ========================= */
@media (min-width: 850px) {
  .home {
    display: flex !important;
    flex-direction: column !important;
    justify-content: start !important;
    min-height: calc(100dvh - $app-header-height);
    padding-top: 0; // le hero doit coller sous le header
    color: #fff !important;
  }

  // image fullscreen
  .photo-layer {
    position: absolute;
    inset: 0;
    z-index: 0;

    background-image: url('/images/hero-party-desktop.png') !important; // ou hero-party si tu gardes un seul asset
    background-size: cover;
    background-repeat: no-repeat;

    // on enlève le masque mobile
    -webkit-mask-image: none !important;
    mask-image: none !important;

    filter: brightness(1) contrast(1.03) saturate(1.06);
    pointer-events: none;

    // voile de lisibilité
    &::after {
      content: '';
      position: absolute;
      inset: 0;
      pointer-events: none;
    }
  }

  // zone texte + form centrée (style "hero")
  .search-form {
    position: relative;
    z-index: 2;

    height: 50vh !important; // pour éviter que le hero soit trop haut sur grand écran
    display: flex;
    flex-direction: column;
    padding-top: 6% !important;
    padding-bottom: 3% !important;
    justify-content: space-between !important;

    border-radius: 0 0 10px 10px !important;

    // glassmorphism
    background: linear-gradient(
      180deg,
      rgba(255, 255, 255, 0.25) 0%,
      rgba(255, 255, 255, 0.1) 100%
    );

    // blur (à doser)
    backdrop-filter: blur(3px) saturate(1.15);
    -webkit-backdrop-filter: blur(3px) saturate(1.15);

    // bord + reflets
    border: 1px solid rgba(255, 255, 255, 0.42) !important;
    box-shadow:
      0 10px 40px rgba(0, 0, 0, 0.15),
      inset 0 1px 0 rgba(255, 255, 255, 0.4);

    // micro-contraste sur le fond (évite l’effet “bande”)
    position: relative;
    overflow: hidden;

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

  .hero {
    transform: translateY(-0.5rem);
    text-align: center;
    width: 100% !important;

    color: #fff !important;
    text-shadow:
      0 2px 10px rgba(0, 0, 0, 0.35),
      0 14px 40px rgba(0, 0, 0, 0.22);
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    gap: 0.6rem;
    width: 100% !important;
  }

  .title {
    display: flex;
    flex-direction: row !important;
    color: inherit;
    align-items: baseline;
    gap: 0.8rem !important;
    margin: 0;
    text-shadow: 0 6px 18px rgba(0, 0, 0, 0.18);
  }

  .title-thin {
    font-weight: 600;
    font-size: 3.2rem !important;
    line-height: 1.05 !important;
    color: $color-primary !important;
  }

  .title-bold {
    font-weight: 800 !important;
    font-size: 4rem !important;
    line-height: 1.05 !important;
    letter-spacing: 0.02rem !important;
    margin-top: 0.2rem !important;
    color: $color-accent !important;

    text-shadow:
      0 0 12px rgba(255, 210, 0, 0.4),
      0 8px 24px rgba(0, 0, 0, 0.2) !important;
  }

  .tagline {
    margin: 0;
    padding: 0;
    width: 100% !important;
    max-width: 100% !important;
    font-weight: 400 !important;
    font-size: clamp(1rem, 1.2vw, 1.15rem);
    line-height: 1.3;
    opacity: 0.88 !important;
    color: rgba(0, 0, 0, 0.75) !important;

    .tagline-mobile {
      display: none !important;
    }

    .tagline-desktop {
      display: block !important;
      letter-spacing: 1px;
    }
  }

  .inputs {
    font-size: 1rem;
    display: flex !important;
    flex-direction: row !important;
    gap: 1.1rem;
    align-items: center !important;
    justify-content: center !important;
    height: unset !important;
    width: 100% !important;
    max-width: unset !important;
    padding: 0.3rem !important;

    width: unset !important;

    border-radius: 2rem !important;

    background: radial-gradient(
      1200px 200px at 50% -20%,
      rgba(255, 190, 60, 0.18),
      transparent 60%
    );
    border: 1px solid rgba(255, 255, 255, 0.42) !important;
    box-shadow:
      0 18px 50px rgba(0, 0, 0, 0.18),
      inset 0 1px 0 rgba(255, 255, 255, 0.55) !important;

    // un poil de relief sous les composants
    filter: drop-shadow(0 14px 40px rgba(0, 0, 0, 0.22));
    > * {
      min-width: 17rem !important;
      max-width: 17rem !important;
      font-size: 1.1rem !important;
      backdrop-filter: blur(6px);
    }
  }

  .sgilt-button {
    box-shadow:
      0 8px 20px rgba(0, 0, 0, 0.25),
      0 2px 6px rgba(0, 0, 0, 0.15) !important;
  }
}
</style>
