<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import SgiltSelect from '~/components/basics/inputs/SgiltSelect.vue'

useSeoMeta({
  title: '',
  description: '',
})
</script>

<template>
  <div class="home">
    <!-- background -->
    <div class="app-background">
      <div class="photo-layer" aria-hidden="true"></div>
    </div>

    <!-- title -->
    <div class="title-text">
      <span class="title-thin">{{ $t('home.search-banner.title-part-1') }}</span>
      <span class="title-bold">{{ $t('home.search-banner.title-part-2') }}</span>
      <h3 class="tagline">{{ $t('home.search-banner.tagline') }}</h3>
    </div>

    <!-- composants -->
    <section class="inputs">
      <SgiltDatePicker />
      <SgiltSelect
        :options="[
          { value: '-1', label: 'Votre événement' },
          { value: '0', label: 'Mariage' },
          { value: '1', label: 'Anniversaire' },
          { value: '2', label: 'Fête d\'entreprise' },
          { value: '3', label: 'Autre' },
        ]"
      />
      <SgiltButton class="submit_button">C'est parti !</SgiltButton>
    </section>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.home {
  position: relative;
  width: 100%;
  min-height: calc(100dvh - $app-header-height); // pour compenser la hauteur du header fixe
  overflow: hidden;
  background-color: $brand-background;

  & > :not(.app-background) {
    position: relative;
    z-index: 2;
  }

  .app-background {
    position: absolute;
    z-index: 0;
    inset: 0;

    background-color: $brand-background;
    background-image: url('/textures/paper-grain.png');
    background-repeat: repeat;
    background-size: 700px;

    @media (min-width: 1024px) {
      background-size: 900px;
    }
  }

  .photo-layer {
    position: absolute;
    z-index: 1;
    inset: auto 0 0 0;
    top: 40%;

    // filter: brightness(1.06) contrast(1.03) saturate(1.06);

    background-image: url('/images/hero-party.png');
    background-size: cover;
    background-repeat: no-repeat;
    background-position: 50% 30%;

    @media (min-width: 768px) {
      top: 50%;
      background-position: 50% 35%;
      background-image: url('/images/hero-party-desktop.png');
    }

    -webkit-mask-image: linear-gradient(to bottom, transparent 0%, #000 18%, #000 100%);
    mask-image: linear-gradient(to bottom, transparent 0%, #000 28%, #000 100%);
    -webkit-mask-repeat: no-repeat;
    mask-repeat: no-repeat;
    -webkit-mask-size: 100% 100%;
    mask-size: 100% 100%;

    pointer-events: none;
  }

  .title-text {
    text-align: center;
    padding: $spacing-xl $spacing-l 0;
    color: $color-primary;
    // text-shadow: 0 6px 24px rgba(47, 42, 37, 0.18);

    display: flex;
    flex-direction: column;
    align-items: center;
    gap: $spacing-xxs;
  }

  .title-thin {
    font-weight: $font-weight-medium;
    font-size: 2rem;
    line-height: $line-height-tight;
  }

  .title-bold {
    font-weight: $font-weight-bold;
    font-size: 4rem;
    line-height: $line-height-tight;
    letter-spacing: 0.02em;
  }

  .tagline {
    margin: $spacing-s 0 0;
    font-weight: $font-weight-regular;
    font-size: 1.25rem;
    color: $text-secondary;
    max-width: 36rem;
  }

  .inputs {
    margin: 10px auto 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: $spacing-m;
    width: 90%;

    .submit_button {
      width: 100%;
    }
  }
}
</style>
