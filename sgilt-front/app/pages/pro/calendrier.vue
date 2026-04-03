<template>
  <div class="calendrier-page">
    <div class="calendrier-page__header">
      <p class="calendrier-page__hint">
        <template v-if="isMobile">
          Touchez un jour disponible pour le bloquer, touchez-le à nouveau pour le libérer.
        </template>
        <template v-else>
          Cliquez sur un jour disponible pour le bloquer, cliquez à nouveau pour le libérer.
        </template>
      </p>
    </div>

    <ProCalendar />
  </div>
</template>

<script setup lang="ts">
import ProCalendar from '~/components/pro/ProCalendar.vue'

definePageMeta({ layout: 'pro' })

const { isMobile } = useDevice()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;

.calendrier-page {
  padding: $spacing-l $spacing-m $spacing-xxxl;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;

  @media (max-width: $desktop) {
    //padding: $spacing-l 0 $spacing-xxxl;
    gap: 0;
  }

  &__header {
    @media (max-width: $desktop) {
      padding: 0 $spacing-m $spacing-m;
      text-align: center;
    }
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
