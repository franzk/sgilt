<template>
  <div class="finalisation">
    <button class="close" type="button" @click="$emit('close')">
      ✕
    </button>

    <div class="hero">
      <div class="icon">✉️</div>
      <h2 class="title">Plus qu'une étape&nbsp;!</h2>
      <div class="cta">
        <p class="cta-text">
          Vérifiez vos emails et cliquez sur le lien de confirmation. Votre demande sera alors
          transmise à {{ prestataireName }}.
        </p>
      </div>
    </div>

    <div class="recap">
      <DemandeRecap />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ prestataireName: string }>()
defineEmits<{ close: [] }>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.finalisation {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: $spacing-xl;

  .hero {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: $spacing-m;
    padding: $spacing-l 0;
  }

  .icon {
    font-size: 3rem;
    line-height: 1;
  }

  .title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.6rem;
    font-weight: 500;
    color: $text-primary;
    margin: 0;
    line-height: 1.3;
  }

  .cta {
    background: rgba($brand-accent, 0.08);
    border: 1.5px solid rgba($brand-accent, 0.3);
    border-radius: $radius-md;
    padding: $spacing-m $spacing-l;

    @media (min-width: $breakpoint-desktop) {
      max-width: 500px;
    }
  }

  .cta-text {
    font-size: 0.95rem;
    color: $text-primary;
    line-height: 1.6;
    margin: 0;
  }

  .close {
    display: none;

    @media (min-width: $breakpoint-desktop) {
      position: absolute;
      top: $spacing-m;
      right: $spacing-l;
      width: 2rem;
      height: 2rem;
      display: flex;
      align-items: center;
      justify-content: center;
      background: none;
      border: none;
      font-size: 1rem;
      color: $text-secondary;
      cursor: pointer;
      border-radius: $radius-sm;
      transition: color 150ms ease, background 150ms ease;

      &:hover {
        color: $text-primary;
        background: $surface-soft;
      }
    }
  }

  .recap {
    width: 600px;

    @media (max-width: $breakpoint-desktop) {
      width: 90%;
    }
    align-self: center;
  }
}
</style>
