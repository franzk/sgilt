<template>
  <DrawerRoot v-model:open="isOpen">
    <DrawerTrigger v-if="$slots.trigger" as-child>
      <slot name="trigger" />
    </DrawerTrigger>

    <DrawerPortal>
      <DrawerOverlay v-if="overlay" class="sgilt-sheet__overlay" />
      <DrawerContent class="sgilt-sheet__content">
        <DrawerTitle class="sr-only">{{ title }}</DrawerTitle>
        <DrawerDescription v-if="description" class="sr-only">{{ description }}</DrawerDescription>
        <slot />
      </DrawerContent>
    </DrawerPortal>
  </DrawerRoot>
</template>

<script setup lang="ts">
import {
  DrawerRoot,
  DrawerContent,
  DrawerOverlay,
  DrawerPortal,
  DrawerTrigger,
  DrawerTitle,
  DrawerDescription,
} from 'vaul-vue'

const isOpen = defineModel<boolean>('open', { required: true })

defineProps<{
  title: string
  description?: string
  overlay?: boolean
}>()
</script>

<style scoped lang="scss">
.sgilt-sheet__overlay {
  position: fixed;
  inset: 0;
  z-index: $z-overlay;
  background: rgba(10, 10, 12, 0.48);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
}

.sgilt-sheet__content {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: $z-modal;
  border-top-left-radius: 16px;
  border-top-right-radius: 16px;
  outline: none;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  justify-content: end;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}
</style>
