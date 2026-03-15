<template>
  <!-- ── Mobile : bottom sheet ────────────────────────────────────────────────── -->
  <SgiltBottomSheet
    v-if="isMobile"
    v-model:open="open"
    :title="title"
    :description="description"
    overlay
  >
    <slot />
  </SgiltBottomSheet>

  <!-- ── Desktop : modal centrée ──────────────────────────────────────────────── -->
  <Teleport v-if="!isMobile" to="body">
    <Transition name="sgilt-dialog">
      <div
        v-if="open"
        class="sgilt-dialog__overlay"
        role="dialog"
        aria-modal="true"
        :aria-label="title"
        @click.self="open = false"
      >
        <div class="sgilt-dialog__panel">
          <div class="sgilt-dialog__header">
            <span class="sgilt-dialog__title">{{ title }}</span>
            <button
              class="sgilt-dialog__close"
              type="button"
              :aria-label="`Fermer ${title}`"
              @click="open = false"
            >
              ✕
            </button>
          </div>
          <div class="sgilt-dialog__body">
            <slot />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import SgiltBottomSheet from '~/components/basics/sheets/SgiltBottomSheet.vue'
import { useDevice } from '~/composables/useDevice'

const open = defineModel<boolean>('open', { required: true })

defineProps<{
  title: string
  description?: string
}>()

const { isMobile } = useDevice()
</script>

<style scoped lang="scss">
// ── Overlay ────────────────────────────────────────────────────────────────────
.sgilt-dialog__overlay {
  position: fixed;
  inset: 0;
  z-index: $z-modal;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-m;
}

// ── Panel ──────────────────────────────────────────────────────────────────────
.sgilt-dialog__panel {
  background: #fff;
  border-radius: $radius-lg;
  width: 100%;
  max-width: 480px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
  overflow: hidden;
}

.sgilt-dialog__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-s;
  padding: $spacing-m $spacing-m $spacing-s;
  flex-shrink: 0;
  border-bottom: 1px solid $divider-color;
}

.sgilt-dialog__title {
  font-family: 'Cormorant Garamond', serif;
  font-size: 1.15rem;
  font-weight: 600;
  color: $brand-primary;
}

.sgilt-dialog__close {
  background: none;
  border: none;
  font-size: 0.9rem;
  color: $text-secondary;
  cursor: pointer;
  padding: 4px 6px;
  border-radius: $radius-sm;
  line-height: 1;
  flex-shrink: 0;
  transition:
    color 150ms ease,
    background 150ms ease;

  &:hover {
    color: $text-primary;
    background: $surface-soft;
  }
}

.sgilt-dialog__body {
  overflow-y: auto;
  flex: 1;
}

// ── Transition ─────────────────────────────────────────────────────────────────
.sgilt-dialog-enter-active,
.sgilt-dialog-leave-active {
  transition: opacity 200ms ease;
}
.sgilt-dialog-enter-from,
.sgilt-dialog-leave-to {
  opacity: 0;
}
</style>
