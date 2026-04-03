<template>
  <DrawerRoot v-model:open="isOpen">
    <DrawerTrigger v-if="$slots.trigger" as-child>
      <slot name="trigger" />
    </DrawerTrigger>

    <DrawerPortal>
      <DrawerOverlay v-if="overlay" class="sgilt-sheet__overlay" />
      <DrawerContent class="sgilt-sheet__content">
        <!-- Accessibilité -->
        <DrawerTitle class="sr-only">{{ title }}</DrawerTitle>
        <DrawerDescription v-if="description" class="sr-only">{{ description }}</DrawerDescription>

        <!-- Handle -->
        <div class="sgilt-sheet__handle" aria-hidden="true" />

        <!-- Header : slot personnalisé ou titre par défaut -->
        <slot name="header">
          <div class="sgilt-sheet__header">
            <span class="sgilt-sheet__title">{{ title }}</span>
          </div>
        </slot>

        <!-- Corps -->
        <div class="sgilt-sheet__body">
          <slot />
        </div>
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
@use '@/assets/styles/base' as *;

// ── Overlay ────────────────────────────────────────────────────────────────────
.sgilt-sheet__overlay {
  position: fixed;
  inset: 0;
  z-index: $z-overlay;
  background: rgba(10, 10, 12, 0.48);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
}

// ── Contenu ────────────────────────────────────────────────────────────────────
.sgilt-sheet__content {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  max-height: calc(100vh - $app-header-height);
  z-index: $z-modal;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  outline: none;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  background:
    radial-gradient(
      1100px 480px at 50% -5%,
      rgba($brand-accent, 0.16) 0%,
      rgba(255, 255, 255, 0) 55%
    ),
    linear-gradient(180deg, #fffdf6 0%, #ffffff 50%);
  border-top: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 -12px 48px rgba(0, 0, 0, 0.18);
}

// ── Handle ─────────────────────────────────────────────────────────────────────
.sgilt-sheet__handle {
  width: 36px;
  height: 4px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.12);
  margin: 12px auto 0;
  flex-shrink: 0;
}

// ── Header (titre par défaut) ──────────────────────────────────────────────────
.sgilt-sheet__header {
  padding: $spacing-s $spacing-m $spacing-xs;
  text-align: center;
  flex-shrink: 0;
}

.sgilt-sheet__title {
  font-family: 'Cormorant Garamond', serif;
  font-size: 1.4rem;
  font-weight: 600;
  color: $brand-primary;
  letter-spacing: -0.01em;

  &::after {
    content: '';
    display: block;
    width: 48px;
    height: 3px;
    border-radius: 999px;
    margin: 6px auto 0;
    background: linear-gradient(
      90deg,
      rgba($brand-accent, 0) 0%,
      rgba($brand-accent, 1) 40%,
      rgba($brand-accent, 1) 60%,
      rgba($brand-accent, 0) 100%
    );
  }
}

// ── Corps ──────────────────────────────────────────────────────────────────────
.sgilt-sheet__body {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

// ── Accessibilité ──────────────────────────────────────────────────────────────
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
