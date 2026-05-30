<template>
  <ClientOnly>
    <div class="sgilt-select" tabindex="-1">
      <SgiltSelectMobile
        v-if="isMobile"
        v-model="modelValue"
        :options="options"
        :disabled="disabled"
      >
        <template #left-icon>
          <slot name="left-icon" />
        </template>
      </SgiltSelectMobile>

      <SgiltSelectDesktop v-else v-model="modelValue" :options="options" :disabled="disabled">
        <template #left-icon>
          <slot name="left-icon" />
        </template>
      </SgiltSelectDesktop>
    </div>

    <!-- SSR fallback -->
    <template #fallback>
      <div class="select-skeleton">
        <Sk width="1.5rem" height="1.5rem" radius="6px" />
        <Sk />
      </div>
    </template>
  </ClientOnly>
</template>

<script setup lang="ts">
import SgiltSelectDesktop from './SgiltSelectDesktop.vue'
import SgiltSelectMobile from './SgiltSelectMobile.vue'
import { useDevice } from '~/composables/useDevice'
import Sk from '~/components/basics/Sk.vue'

const modelValue = defineModel<string>()

defineProps<{
  options: { value: string; label: string }[]
  disabled?: boolean
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

.select-skeleton {
  width: 100%;
  height: 3.5rem;
  border-radius: 0.875rem;
  background: #f4f4f4;
  border: 1px solid rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0 1rem;
  box-shadow:
    0 0.0625rem 0 rgba(0, 0, 0, 0.04),
    0 0.5rem 1.25rem rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

// Style du bouton qui ouvre le menu
.sgilt-select :deep(.select-trigger:disabled),
.sgilt-select :deep(.select-trigger[data-disabled]) {
  cursor: not-allowed;
  opacity: 0.5;
}

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
