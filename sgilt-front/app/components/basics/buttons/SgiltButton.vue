<template>
  <button
    class="btn"
    :class="[variant, { loading, disabled }]"
    :disabled="disabled || loading"
    type="button"
    @click="$emit('click')"
  >
    <span v-if="loading" class="spinner" aria-hidden="true" />
    <slot />
  </button>
</template>

<script setup lang="ts">
defineEmits(['click'])
defineProps<{
  variant?: 'primary' | 'secondary' | 'tertiary'
  disabled?: boolean
  loading?: boolean
}>()
</script>

<style scoped lang="scss">
$btn-radius: 0.5rem;
$btn-height: 2.5rem;
$btn-padding-y: 9px;
$btn-padding-x: 18px;
$btn-font-size: 14px;
$btn-font-weight: 500;

$btn-primary-bg-from: #fad84a;
$btn-primary-bg-to: #e8b000;

$btn-secondary-border: #e2e8f0;

.btn {
  display: inline-flex;
  width: fit-content;
  height: $btn-height;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: $btn-padding-y $btn-padding-x;
  border-radius: $btn-radius;
  font-family: inherit;
  font-size: $btn-font-size;
  font-weight: $btn-font-weight;
  line-height: 1;
  cursor: pointer;
  border: none;
  background: none;
  white-space: nowrap;
  transition:
    filter 120ms ease,
    transform 80ms ease,
    box-shadow 80ms ease,
    background 120ms ease,
    color 120ms ease;

  &.disabled,
  &:disabled {
    opacity: 0.45;
    cursor: not-allowed;
    pointer-events: none;
  }

  // ── Primary ──────────────────────────────────────────────────────────────────
  &.primary,
  &:not(.secondary):not(.tertiary) {
    background: linear-gradient(180deg, $btn-primary-bg-from 0%, $btn-primary-bg-to 100%);
    border-top: 1px solid rgba(255, 255, 255, 0.3);
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.18);
    color: #fff;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);

    &:hover:not(:disabled) {
      filter: brightness(1.05);
    }

    &:active:not(:disabled) {
      transform: translateY(1px);
      box-shadow: none;
    }
  }

  // ── Secondary ─────────────────────────────────────────────────────────────────
  &.secondary {
    background: transparent;
    border: 1px solid $btn-secondary-border;
    color: $text-secondary;

    &:hover:not(:disabled) {
      background: $surface-soft;
    }
  }

  // ── Tertiary ──────────────────────────────────────────────────────────────────
  &.tertiary {
    padding: 0;
    color: $text-secondary;

    &:hover:not(:disabled) {
      color: $text-primary;
    }
  }
}

// ── Spinner ───────────────────────────────────────────────────────────────────
.spinner {
  width: 13px;
  height: 13px;
  border-radius: 50%;
  border: 2px solid currentColor;
  border-top-color: transparent;
  animation: spin 0.6s linear infinite;
  flex-shrink: 0;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
