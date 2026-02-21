<template>
  <ClientOnly>
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
@use '@/assets/styles/base' as *;

$border-radius: 1.4rem;

// Style du bouton qui ouvre le menu
:global(.select-trigger) {
  position: relative;
  width: 100%;
  display: flex;
  flex-direction: row;
  // align-items: center;
  // justify-content: space-between;
  // padding: 1rem 1.25rem;
  background: white;
  border: 1px solid $shadow-m; // ou ta variable
  border-radius: $border-radius;
  font-size: inherit;
  letter-spacing: 0.02rem;
  height: inherit;
  padding: 0;

  box-shadow:
    0 0.0625rem 0 rgba(0, 0, 0, 0.04),
    0 0.5rem 1.25rem rgba(0, 0, 0, 0.05);
}

:global(.trigger-content) {
  position: relative;
  width: 100%;
  height: inherit;
  border-radius: $border-radius;
  background: white;
  border: 1px solid $shadow-m;
  box-shadow:
    0 0.0625rem 0 rgba(0, 0, 0, 0.04),
    0 0.5rem 1.25rem rgba(0, 0, 0, 0.05);

  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  gap: 0.75rem;

  cursor: pointer;
  -webkit-tap-highlight-color: transparent;

  /* &:focus-visible {
      outline: none;
      border-color: rgba($color-accent, 0.55);
      box-shadow:
        0 10px 24px rgba(0, 0, 0, 0.06),
        0 0 0 4px rgba($color-accent, 0.22);
    } */

  .value {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .has-value {
    color: $text-primary;
    font-weight: 500;
  }

  /* * {
      width: 1.5em;
      height: 1.5em;
    } */
}

:global(.trigger-content .left-icon) {
  position: absolute;
  left: 0;
  display: flex;
  flex-wrap: wrap;
  width: 2.5rem;
  align-content: center;
  justify-content: center;
  color: $color-accent;
  svg path {
    stroke: currentColor;
  }
}
</style>
