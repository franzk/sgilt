<template>
  <div class="finalisation">
    <div class="finalisation__hero">
      <div class="finalisation__icon">✉️</div>
      <h2 class="finalisation__title">Plus qu'une étape&nbsp;!</h2>
      <div class="finalisation__cta">
        <p class="finalisation__cta-text">
          Vérifiez vos emails et cliquez sur le lien de confirmation. Votre demande sera alors
          transmise à {{ prestataireName }}.
        </p>
      </div>
    </div>

    <div class="finalisation-recap">
      <DemandeRecap />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ prestataireName: string }>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.finalisation {
  display: flex;
  flex-direction: column;
  gap: $spacing-xl;

  &__hero {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: $spacing-m;
    padding: $spacing-l 0;
  }

  &__icon {
    font-size: 3rem;
    line-height: 1;
  }

  &__title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.6rem;
    font-weight: 500;
    color: $text-primary;
    margin: 0;
    line-height: 1.3;
  }

  &__cta {
    background: rgba($brand-accent, 0.08);
    border: 1.5px solid rgba($brand-accent, 0.3);
    border-radius: $radius-md;
    padding: $spacing-m $spacing-l;

    @media (min-width: $breakpoint-desktop) {
      max-width: 500px;
    }
  }

  &__cta-text {
    font-size: 0.95rem;
    color: $text-primary;
    line-height: 1.6;
    margin: 0;
  }
}

.finalisation-recap {
  width: 600px;

  @media (max-width: $breakpoint-desktop) {
    width: 90%;
  }
  align-self: center;
}
</style>
