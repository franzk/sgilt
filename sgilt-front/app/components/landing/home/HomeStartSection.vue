<template>
  <section class="home-start">
    <div class="text">
      <h2 class="title">{{ $t('landing-page.start.title') }}</h2>
      <p class="lede">{{ $t('landing-page.start.lede') }}</p>
      <p class="description">{{ $t('landing-page.start.description') }}</p>
      <p class="hint">{{ $t('landing-page.start.hint') }}</p>

      <HomeCtaButton />
    </div>

    <ol class="steps">
      <li class="step">
        <div class="marker"><FileList3Icon class="icon" /></div>
        <h3>{{ $t('landing-page.start.step-1-title') }}</h3>
        <p>{{ $t('landing-page.start.step-1-description') }}</p>
      </li>
      <li class="step">
        <div class="marker"><CalendarEventIcon class="icon" /></div>
        <h3>{{ $t('landing-page.start.step-2-title') }}</h3>
        <p>{{ $t('landing-page.start.step-2-description') }}</p>
      </li>
      <li class="step">
        <div class="marker"><CheckboxCircleIcon class="icon" /></div>
        <h3>{{ $t('landing-page.start.step-3-title') }}</h3>
        <p>{{ $t('landing-page.start.step-3-description') }}</p>
      </li>
    </ol>
  </section>
</template>

<script setup lang="ts">
import { FileList3Icon, CalendarEventIcon, CheckboxCircleIcon } from '@remixicons/vue/line'
import HomeCtaButton from './HomeCtaButton.vue'
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.home-start {
  display: grid;
  grid-template-columns: 1fr;
  gap: $spacing-xxl;
  padding: $spacing-xxxl $section-padding-x;
  background: $surface-soft;

  @media (min-width: $breakpoint-desktop) {
    grid-template-columns: 1fr 1.2fr;
    max-width: $container-max-width;
    margin: 0 auto;
    align-items: center;
  }

  .text {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: $spacing-m;
    max-width: 46ch;
  }

  .title {
    margin: 0;
    font-family: 'Cormorant Garamond', serif;
    font-weight: $font-weight-medium;
    font-size: clamp(1.8rem, 4vw, 2.5rem);
    color: $text-primary;
  }

  .lede {
    margin: 0;
    color: $text-primary;
    font-weight: $font-weight-semibold;
    font-size: $font-size-lg;
  }

  .description,
  .hint {
    margin: 0;
    color: $text-secondary;
    font-size: $font-size-md;
    line-height: $line-height-relaxed;
  }

  // ── 3 étapes ──────────────────────────────────────────────────────────────
  .steps {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    grid-template-columns: 1fr;
    gap: $spacing-xl;

    @media (min-width: $breakpoint-desktop) {
      grid-template-columns: repeat(3, 1fr);
      gap: $spacing-l;
    }
  }

  .step {
    position: relative;
    text-align: center;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: $spacing-xs;

    // Ligne pointillée reliant les étapes (desktop uniquement)
    @media (min-width: $breakpoint-desktop) {
      &:not(:last-child)::after {
        content: '';
        position: absolute;
        top: 2.25rem;
        left: calc(50% + 3rem);
        width: calc(100% - 6rem);
        border-top: 2px dashed $divider-color;
      }
    }

    h3 {
      margin: 0;
      font-size: $font-size-md;
      font-weight: $font-weight-semibold;
      color: $text-primary;
    }

    p {
      margin: 0;
      max-width: 26ch;
      color: $text-secondary;
      font-size: $font-size-sm;
      line-height: $line-height-relaxed;
    }
  }

  .marker {
    position: relative;
    z-index: 1;
    width: 4.5rem;
    height: 4.5rem;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba($brand-accent, 0.18);

    .icon {
      width: 2rem;
      height: 2rem;
      color: $brand-primary;
    }
  }
}
</style>
