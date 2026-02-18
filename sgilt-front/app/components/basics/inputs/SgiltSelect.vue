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
