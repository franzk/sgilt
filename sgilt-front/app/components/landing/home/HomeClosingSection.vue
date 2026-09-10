<template>
  <section class="home-closing">
    <div class="wrap">
      <h2 class="title">{{ $t('landing-page.closing.title') }}</h2>

      <p class="lines script">
        {{ $t('landing-page.closing.line-1') }}<br />
        {{ $t('landing-page.closing.line-2') }}
      </p>

      <p class="question">{{ $t('landing-page.closing.question') }}</p>

      <HomeCtaButton />
    </div>
  </section>
</template>

<script setup lang="ts">
import HomeCtaButton from './HomeCtaButton.vue'
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.home-closing {
  padding: $spacing-xl 0 $spacing-xl;
  background: $brand-primary;

  @media (min-width: $breakpoint-desktop) {
    padding: $spacing-xxxl 0;
  }

  .wrap {
    max-width: 640px;
    margin: 0 auto;
    padding: 0 $section-padding-x;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: $spacing-m;
  }

  .title {
    margin: 0;
    font-family: 'Cormorant Garamond', serif;
    font-weight: $font-weight-medium;
    font-size: clamp(1.8rem, 4vw, 2.5rem);
    color: $text-inverted;
  }

  .lines {
    margin: 0;
    color: rgba(255, 255, 255, 0.75);
    font-size: $font-size-md;
    line-height: $line-height-relaxed;
  }

  .script {
    font-family: 'Caveat', cursive;
    font-size: clamp(1.4rem, 2.5vw, 1.8rem);
    line-height: 1.3;
    color: rgba(255, 255, 255, 0.85);
  }

  .question {
    margin: 0 0 $spacing-s;
    color: $text-inverted;
    font-weight: $font-weight-semibold;
    font-size: $font-size-lg;
  }
}
</style>
