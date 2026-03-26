<template>
  <div class="contact-card">
    <!-- Ligne 1 : icône à gauche, bouton copy à droite -->
    <div class="contact-card__header">
      <span class="contact-card__icon">
        <slot name="icon" />
      </span>
      <button
        class="contact-card__copy"
        type="button"
        :class="{ 'contact-card__copy--copied': copied }"
        :aria-label="copied ? 'Copié' : 'Copier'"
        @click="copy"
      >
        <CheckIcon v-if="copied" class="contact-card__copy-icon" />
        <FileCopyIcon v-else class="contact-card__copy-icon" />
      </button>
    </div>

    <!-- Ligne 2 : label : valeur -->
    <div class="contact-card__info">
      <span class="contact-card__label"><slot name="title" /></span>
      <span class="contact-card__sep">:</span>
      <span class="contact-card__value"><slot name="content" /></span>
    </div>

    <!-- Séparateur -->
    <hr class="contact-card__divider" />

    <!-- Ligne 3 : CTA -->
    <div class="contact-card__cta">
      <slot name="cta" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { CheckIcon, FileCopyIcon } from '@remixicons/vue/line'

const props = defineProps<{
  copyValue: string
}>()

const copied = ref(false)

async function copy() {
  await navigator.clipboard.writeText(props.copyValue)
  copied.value = true
  setTimeout(() => (copied.value = false), 2000)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.contact-card {
  display: flex;
  flex-direction: column;
  background: #fff;
  border: 1px solid $color-accent;
  border-radius: $radius-md;
  padding: $spacing-s $spacing-m;
  box-shadow: 0 2px 10px rgba($color-accent, 0.18);

  &__header {
    display: flex;
    justify-content: space-between;
    margin-bottom: $spacing-xs;
  }

  &__icon {
    display: flex;
    align-items: center;
    flex-shrink: 0;
    width: 4rem;
    height: 4rem;
    color: $color-accent;

    :deep(svg) {
      width: 100%;
      height: 100%;
    }
  }

  &__copy {
    flex-shrink: 0;
    width: 2rem;
    height: 2rem;
    border-radius: $radius-sm;
    border: 1px solid $divider-color;
    background: none;
    color: $text-secondary;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition:
      background 120ms ease,
      color 120ms ease;

    &--copied {
      color: $color-primary;
      border-color: $color-accent;
    }
  }

  &__copy-icon {
    width: 100%;
    height: 100%;
  }

  &__info {
    display: flex;
    align-items: baseline;
    gap: 4px;
    min-width: 0;
    margin-bottom: $spacing-xs;
  }

  &__label {
    flex-shrink: 0;
    font-family: 'Inter', sans-serif;
    font-size: 0.7rem;
    font-weight: 600;
    letter-spacing: 0.07em;
    text-transform: uppercase;
    color: $text-secondary;
  }

  &__sep {
    flex-shrink: 0;
    font-family: 'Inter', sans-serif;
    font-size: 0.7rem;
    color: $text-secondary;
  }

  &__value {
    flex: 1;
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    color: $text-primary;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__divider {
    border: none;
    border-top: 1px solid $divider-color;
    margin: 0 0 $spacing-xs;
  }

  &__cta {
    display: flex;

    :deep(a),
    :deep(button) {
      display: flex;
      flex: 1;
      align-items: center;
      justify-content: center;
      padding-bottom: 2px;
      border: none;
      background: none;
      font-family: 'Inter', sans-serif;
      font-size: 0.8rem;
      font-weight: 600;
      color: $brand-primary;
      text-decoration: none;
      cursor: pointer;
      transition: opacity 150ms ease;

      &:hover { opacity: 0.7; }
      @include pressable;
    }
  }
}
</style>
