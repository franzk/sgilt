<template>
  <!-- ── Mobile : bottom sheet ────────────────────────────────────────────────── -->
  <SgiltBottomSheet
    v-if="isMobile"
    v-model:open="open"
    v-model:confirmed="confirmed"
    :title="title"
    :description="description"
    :fullscreen="props.fullscreen"
    overlay
  >
    <slot />
  </SgiltBottomSheet>

  <!-- ── Desktop : modal centrée ──────────────────────────────────────────────── -->
  <Teleport v-if="!isMobile" to="body">
    <Transition name="sgilt-dialog">
      <div
        v-if="open"
        class="sgilt-dialog"
        :class="{ 'is-fullscreen': props.fullscreen }"
        role="dialog"
        aria-modal="true"
        :aria-label="title"
        @mousedown.self="open = false"
      >
        <div
          class="panel"
          :class="{ fullscreen: props.fullscreen }"
          :style="props.maxWidth ? { maxWidth: props.maxWidth } : {}"
        >
          <div class="header">
            <span class="title">{{ title }}</span>
            <button
              class="close"
              type="button"
              :aria-label="`Fermer ${title}`"
              @click="open = false"
            >
              ✕
            </button>
          </div>
          <div class="body">
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
const confirmed = defineModel<boolean>('confirmed', { default: false })

const props = defineProps<{
  title: string
  description?: string
  maxWidth?: string
  fullscreen?: boolean
}>()

const { isMobile } = useDevice()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// ── Overlay ────────────────────────────────────────────────────────────────────
.sgilt-dialog {
  position: fixed;
  inset: 0;
  z-index: $z-modal;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-m;
  // max-height: calc(100vh - $app-header-height);

  &.is-fullscreen {
    padding: 0;
    align-items: stretch;
  }

  // ── Panel ──────────────────────────────────────────────────────────────────────
  .panel {
    background: #fff;
    border-radius: $radius-lg;
    width: 100%;
    max-width: 480px;

    display: flex;
    flex-direction: column;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
    overflow: hidden;

    &.fullscreen {
      height: 100dvh;
      border-radius: 0;
      box-shadow: none;
    }
  }

  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $spacing-s;
    padding: $spacing-m $spacing-m $spacing-s;
    flex-shrink: 0;
    border-bottom: 1px solid $divider-color;
  }

  .title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.15rem;
    font-weight: 600;
    color: $brand-primary;
  }

  .close {
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

  .body {
    overflow-y: auto;
    flex: 1;
    display: flex;
    flex-direction: column;
  }
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
