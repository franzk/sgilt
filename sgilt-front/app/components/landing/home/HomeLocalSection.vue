<template>
  <section class="home-local">
    <div class="wrap">
      <div class="text">
        <h2 class="title">{{ $t('landing-page.local.title') }}</h2>
        <p>{{ $t('landing-page.local.paragraph-1') }}</p>
        <p>{{ $t('landing-page.local.paragraph-2') }}</p>
        <p class="closing-line">{{ $t('landing-page.local.closing-line') }}</p>
      </div>

      <div class="photos">
        <div class="photo-placeholder">
          <ImageIcon class="icon" />
          <span class="sr-only">{{ $t('landing-page.local.photo-domaine-alt') }}</span>
        </div>
        <div class="photo-placeholder">
          <ImageIcon class="icon" />
          <span class="sr-only">{{ $t('landing-page.local.photo-musique-alt') }}</span>
        </div>
        <div class="photo-placeholder">
          <ImageIcon class="icon" />
          <span class="sr-only">{{ $t('landing-page.local.photo-prestation-alt') }}</span>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ImageIcon } from '@remixicons/vue/line'
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.home-local {
  padding: $spacing-xxxl 0;
  background: $surface-soft;

  .wrap {
    max-width: $container-max-width;
    margin: 0 auto;
    padding: 0 $section-padding-x;
    display: flex;
    flex-direction: column;
    gap: $spacing-xl;
    text-align: center;
    align-items: center;
  }

  .text {
    max-width: 60ch;

    p {
      margin: 0 0 $spacing-s;
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
    margin: $spacing-m 0 0;
    color: $text-primary;
    font-weight: $font-weight-semibold;
  }

  .photos {
    width: 100%;
    display: grid;
    grid-template-columns: 1fr;
    gap: $spacing-s;

    @media (min-width: $breakpoint-desktop) {
      grid-template-columns: repeat(3, 1fr);
    }
  }

  .photo-placeholder {
    aspect-ratio: 4 / 3;
    border-radius: $radius-lg;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, $brand-accent 0%, $brand-primary 140%);

    .icon {
      width: 2.5rem;
      height: 2.5rem;
      color: rgba(255, 255, 255, 0.7);
    }
  }

  .sr-only {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    white-space: nowrap;
    border: 0;
  }
}
</style>
