<template>
  <section class="home-closing">
    <div class="wrap">
      <h2 class="title">{{ $t('landing-page.closing.title') }}</h2>

      <p class="lines">
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
  padding: $spacing-xxxl 0;
  background: $brand-primary;

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

  .question {
    margin: 0 0 $spacing-s;
    color: $text-inverted;
    font-weight: $font-weight-semibold;
    font-size: $font-size-lg;
  }
}
</style>
