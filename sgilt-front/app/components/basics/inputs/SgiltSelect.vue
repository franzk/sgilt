<template>
  <ClientOnly>
    <div class="sgilt-select">
      <SgiltSelectMobile v-if="isMobile" v-model="modelValue" :options="options">
        <template #left-icon>
          <slot name="left-icon" />
        </template>
      </SgiltSelectMobile>

      <SgiltSelectDesktop v-else v-model="modelValue" :options="options">
        <template #left-icon>
          <slot name="left-icon" />
        </template>
      </SgiltSelectDesktop>
    </div>

    <!-- SSR fallback -->
    <template #fallback>
      <div class="select-skeleton">
        <div class="skeleton-icon" />
        <div class="skeleton-text" />
      </div>
    </template>
  </ClientOnly>
</template>

<script setup lang="ts">
import SgiltSelectDesktop from './SgiltSelectDesktop.vue'
import SgiltSelectMobile from './SgiltSelectMobile.vue'
import { useDevice } from '~/composables/useDevice'

const modelValue = defineModel<string>()

defineProps<{
  options: { value: string; label: string }[]
}>()

const { isMobile } = useDevice()
</script>

<style scoped lang="scss">
// input
$input-background: white;
$input-letter-spacing: 0.02rem;

// input with value
$input-has-value-font-weight: 500;
$input-has-value-color: rgba($color-primary, 0.8);

// icon
$left-icon-color: $color-accent;
$left-icon-width: 2.5rem;

// style
.sgilt-select {
  width: 100%;
}

// Style du bouton qui ouvre le menu
.sgilt-select :deep(.select-trigger) {
  position: relative;
  width: 100%;
  display: flex;
  flex-direction: row;
  background: $input-background;
  border: $input-border;
  border-radius: $input-border-radius;
  font-size: inherit;
  letter-spacing: $input-letter-spacing;
  height: inherit;
  padding: 0;

  box-shadow: $input-box-shadow;

  &:focus-visible {
    outline: none;
    border-color: $input-focus-border-color;

    box-shadow: $input-focus-box-shadow;
  }
}

// Style du contenu du bouton (zone cliquable)
.sgilt-select :deep(.trigger-content) {
  position: relative;
  width: 100%;
  height: inherit;
  border-radius: inherit;
  background: transparent;

  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  cursor: pointer;

  .value {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .has-value {
    font-weight: $input-has-value-font-weight;
    color: $input-has-value-color;
  }
}

// Style de l'icône à gauche
.sgilt-select :deep(.trigger-content .left-icon) {
  position: absolute;
  left: 0;
  display: flex;
  flex-wrap: wrap;
  width: $left-icon-width;
  align-content: center;
  justify-content: center;
  color: $left-icon-color;
  svg path {
    stroke: currentColor;
  }
}
</style>
