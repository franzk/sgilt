<template>
  <section class="home-start">
    <div class="wrap">
      <h2 class="title">{{ $t('landing-page.start.title') }}</h2>
      <p class="lede">{{ $t('landing-page.start.lede') }}</p>
      <p class="description">{{ $t('landing-page.start.description') }}</p>
      <p class="hint">{{ $t('landing-page.start.hint') }}</p>

      <HomeCtaButton />
    </div>
  </section>
</template>

<script setup lang="ts">
import HomeCtaButton from './HomeCtaButton.vue'
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.home-start {
  padding: $spacing-xxxl 0;
  background: $surface-soft;

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
    max-width: 52ch;
  }

  .home-cta {
    margin-top: $spacing-s;
  }
}
</style>
