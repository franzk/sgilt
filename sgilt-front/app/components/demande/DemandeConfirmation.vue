<template>
  <div class="confirmation">
    <div class="confirmation__icon">✓</div>
    <h2 class="confirmation__title">Demande envoyée&nbsp;!</h2>
    <p class="confirmation__text">
      {{ prestataireName }} va recevoir votre demande et vous recontactera rapidement.
    </p>
    <p class="confirmation__recap">
      Un récapitulatif a été envoyé à <strong>{{ email }}</strong>.
    </p>
    <button class="confirmation__close" type="button" @click="$emit('close')">
      Fermer
    </button>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  prestataireName: string
  email: string
}>()

defineEmits<{ (e: 'close'): void }>()
</script>

<style scoped lang="scss">
.confirmation {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: $spacing-xxl $spacing-l;
  gap: $spacing-m;

  &__icon {
    width: 4rem;
    height: 4rem;
    border-radius: 50%;
    background: $state-success;
    color: #fff;
    font-size: 1.5rem;
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: $spacing-s;
  }

  &__title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.8rem;
    font-weight: 500;
    color: $text-primary;
    margin: 0;
  }

  &__text {
    font-size: 1rem;
    color: $text-secondary;
    line-height: 1.6;
    margin: 0;
    max-width: 28rem;
  }

  &__recap {
    font-size: 0.85rem;
    color: $text-secondary;
    opacity: 0.7;
    margin: 0;
  }

  &__close {
    margin-top: $spacing-m;
    padding: $spacing-s $spacing-xl;
    border: 1.5px solid $divider-color;
    border-radius: 2rem;
    background: #fff;
    font-size: 0.95rem;
    font-family: inherit;
    color: $text-primary;
    cursor: pointer;
    transition: background 150ms ease;

    &:hover {
      background: $surface-soft;
    }
  }
}
</style>
