<template>
  <div class="contact-card">
    <!-- Ligne 1 : icône à gauche, bouton copy à droite -->
    <div class="header">
      <span class="icon">
        <slot name="icon" />
      </span>
      <button
        class="copy"
        type="button"
        :class="{ copied: copied }"
        :aria-label="copied ? 'Copié' : 'Copier'"
        @click="copy"
      >
        <CheckIcon v-if="copied" class="copy-icon" />
        <FileCopyIcon v-else class="copy-icon" />
      </button>
    </div>

    <!-- Ligne 2 : label : valeur -->
    <div class="info">
      <span class="label"><slot name="title" /></span>
      <span class="sep">:</span>
      <span class="value"><slot name="content" /></span>
    </div>

    <!-- Séparateur -->
    <hr class="divider" />

    <!-- Ligne 3 : CTA -->
    <div class="cta">
      <slot name="cta" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { CheckIcon, FileCopyIcon } from '@remixicons/vue/line'

const props = defineProps<{
  copyValue: string
}>()

const copied = ref(false)

async function copy() {
  await navigator.clipboard.writeText(props.copyValue)
  copied.value = true
  setTimeout(() => (copied.value = false), 2000)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.contact-card {
  display: flex;
  flex-direction: column;
  background: #fff;
  border: 1px solid $color-accent;
  border-radius: $radius-md;
  padding: $spacing-s $spacing-m;
  box-shadow: 0 2px 10px rgba($color-accent, 0.18);

  .header {
    display: flex;
    justify-content: space-between;
    margin-bottom: $spacing-xs;
  }

  .icon {
    display: flex;
    align-items: center;
    flex-shrink: 0;
    width: 4rem;
    height: 4rem;
    color: $color-accent;

    :deep(svg) {
      width: 100%;
      height: 100%;
    }
  }

  .copy {
    flex-shrink: 0;
    width: 2rem;
    height: 2rem;
    border-radius: $radius-sm;
    border: 1px solid $divider-color;
    background: none;
    color: $text-secondary;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition:
      background 120ms ease,
      color 120ms ease;

    &.copied {
      color: $color-primary;
      border-color: $color-accent;
    }
  }

  .copy-icon {
    width: 100%;
    height: 100%;
  }

  .info {
    display: flex;
    align-items: baseline;
    gap: 4px;
    min-width: 0;
    margin-bottom: $spacing-xs;
  }

  .label {
    flex-shrink: 0;
    font-family: 'Inter', sans-serif;
    font-size: 0.7rem;
    font-weight: 600;
    letter-spacing: 0.07em;
    text-transform: uppercase;
    color: $text-secondary;
  }

  .sep {
    flex-shrink: 0;
    font-family: 'Inter', sans-serif;
    font-size: 0.7rem;
    color: $text-secondary;
  }

  .value {
    flex: 1;
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    color: $text-primary;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .divider {
    border: none;
    border-top: 1px solid $divider-color;
    margin: 0 0 $spacing-xs;
  }

  .cta {
    display: flex;

    :deep(a),
    :deep(button) {
      display: flex;
      flex: 1;
      align-items: center;
      justify-content: center;
      gap: 5px;
      padding-bottom: 2px;
      border: none;
      background: none;
      font-family: 'Inter', sans-serif;
      font-size: 0.8rem;
      font-weight: 600;
      color: $brand-primary;
      text-decoration: none;
      cursor: pointer;
      transition: opacity 150ms ease;

      &:hover { opacity: 0.7; }
      @include pressable;
    }
  }
}
</style>
