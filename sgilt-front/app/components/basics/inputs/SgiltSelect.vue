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
@use '@/assets/styles/base' as *;

$input-border-radius: 1.4rem;
$input-background: white;
$input-border: 1px solid $shadow-m;
$input-letter-spacing: 0.02rem;
$input-box-shadow:
  0 0.0625rem 0 rgba(0, 0, 0, 0.04),
  0 0.5rem 1.25rem rgba(0, 0, 0, 0.05);

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
    border-color: rgba($color-accent, 0.55);

    box-shadow:
      0 10px 24px rgba(0, 0, 0, 0.06),
      0 0 0 4px rgba($color-accent, 0.22);
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

  /*&:focus-visible {
    outline: none;
    border-color: rgba($color-accent, 0.55);

    box-shadow:
      0 10px 24px rgba(0, 0, 0, 0.06),
      0 0 0 4px rgba($color-accent, 0.22);
  }*/

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
}

.sgilt-select :deep(.trigger-content .left-icon) {
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
