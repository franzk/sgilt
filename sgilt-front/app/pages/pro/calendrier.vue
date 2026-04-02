<template>
  <div class="calendrier-page">
    <div class="calendrier-page__header">
      <h1 class="calendrier-page__title">Calendrier</h1>
      <p class="calendrier-page__hint">
        Touchez un jour disponible pour le bloquer, touchez-le à nouveau pour le libérer.
      </p>
    </div>

    <ProCalendar />
  </div>
</template>

<script setup lang="ts">
import ProCalendar from '~/components/pro/ProCalendar.vue'

definePageMeta({ layout: 'pro' })
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.calendrier-page {
  max-width: 480px;
  margin: 0 auto;
  padding: $spacing-l $spacing-m $spacing-xxxl;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;

  &__title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.6rem;
    font-weight: 500;
    color: $text-primary;
    margin: 0;
  }

  &__hint {
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    color: $text-secondary;
    margin: 0;
    line-height: 1.5;
  }
}
</style>
