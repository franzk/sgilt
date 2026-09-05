<template>
  <section class="home-occasions">
    <div class="wrap">
      <h2 class="title">{{ $t('landing-page.occasions.title') }}</h2>

      <ul class="tags">
        <li class="tag">
          <SparklingIcon class="icon" />
          <span>{{ $t('landing-page.occasions.line-oui') }}</span>
        </li>
        <li class="tag">
          <SparklingIcon class="icon" />
          <span>{{ $t('landing-page.occasions.line-bougie') }}</span>
        </li>
        <li class="tag">
          <SparklingIcon class="icon" />
          <span>{{ $t('landing-page.occasions.line-entreprise') }}</span>
        </li>
        <li class="tag">
          <SparklingIcon class="icon" />
          <span>{{ $t('landing-page.occasions.line-revoir') }}</span>
        </li>
        <li class="tag">
          <SparklingIcon class="icon" />
          <span>{{ $t('landing-page.occasions.line-jeudi') }}</span>
        </li>
      </ul>
    </div>
  </section>
</template>

<script setup lang="ts">
import { SparklingIcon } from '@remixicons/vue/line'
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.home-occasions {
  padding: $spacing-xxxl 0;

  .wrap {
    max-width: $container-max-width;
    margin: 0 auto;
    padding: 0 $section-padding-x;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: $spacing-xl;
  }

  .title {
    margin: 0;
    font-family: 'Cormorant Garamond', serif;
    font-weight: $font-weight-medium;
    font-size: clamp(1.8rem, 4vw, 2.5rem);
    color: $text-primary;
    max-width: 28ch;
  }

  .tags {
    width: 100%;
    max-width: 640px;
    margin: 0;
    padding: 0;
    list-style: none;
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: $spacing-s;
  }

  .tag {
    display: inline-flex;
    align-items: center;
    gap: $spacing-xs;
    padding: $spacing-xs $spacing-m;
    border-radius: 9999px;
    background: $surface-soft;
    border: 1px solid $divider-color;
    color: $text-primary;
    font-size: $font-size-sm;
    font-weight: $font-weight-medium;

    .icon {
      width: 1rem;
      height: 1rem;
      color: $brand-accent;
      flex-shrink: 0;
    }
  }
}
</style>
