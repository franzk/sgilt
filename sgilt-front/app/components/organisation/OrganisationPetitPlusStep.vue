<template>
  <div class="petit-plus-step">
    <textarea
      v-model="state.description"
      class="textarea"
      rows="5"
      :maxlength="DESCRIPTION_MAX_LENGTH"
      :placeholder="$t('organisation.steps.petit-plus.placeholder')"
    />
    <span class="counter">{{ state.description.length }}/{{ DESCRIPTION_MAX_LENGTH }}</span>
  </div>
</template>

<script setup lang="ts">
import { useDemande } from '~/composables/useDemande'

const DESCRIPTION_MAX_LENGTH = 500

const { state } = useDemande()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.petit-plus-step {
  position: relative;

  .textarea {
    display: block;
    width: 100%;
    min-height: 9rem;
    padding: $spacing-m $spacing-m 2rem;
    border: 1.5px solid $divider-color;
    border-radius: $radius-lg;
    background: $surface-white;
    color: $text-primary;
    font-family: inherit;
    font-size: $font-size-sm;
    line-height: $line-height-normal;
    resize: none;
    outline: none;
    box-sizing: border-box;
    transition: border-color 180ms ease;

    &:focus {
      border-color: $brand-accent;
    }

    &::placeholder {
      color: $text-secondary;
      opacity: 0.6;
    }
  }

  .counter {
    position: absolute;
    right: $spacing-m;
    bottom: $spacing-s;
    color: $text-secondary;
    font-size: $font-size-xs;
    pointer-events: none;
  }
}
</style>
