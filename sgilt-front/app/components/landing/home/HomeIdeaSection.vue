<template>
  <section class="home-idea">
    <div class="wrap">
      <h2 class="title">{{ $t('landing-page.idea.title') }}</h2>

      <p class="lines">
        {{ $t('landing-page.idea.line-mariage') }}<br />
        {{ $t('landing-page.idea.line-anniversaire') }}<br />
        {{ $t('landing-page.idea.line-soiree') }}<br />
        {{ $t('landing-page.idea.line-pro') }}
      </p>

      <p class="conclusion">{{ $t('landing-page.idea.conclusion') }}</p>
    </div>
  </section>
</template>

<script setup lang="ts"></script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.home-idea {
  padding: $spacing-xxxl 0;

  .wrap {
    max-width: 640px;
    margin: 0 auto;
    padding: 0 $section-padding-x;
    text-align: center;
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

  .conclusion {
    margin: 0;
    color: $text-primary;
    font-weight: $font-weight-semibold;
    font-size: $font-size-lg;
    line-height: $line-height-relaxed;
  }
}
</style>
