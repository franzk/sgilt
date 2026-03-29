<template>
  <div class="sheet-header">
    <button
      v-if="etape > 1 && !submitted"
      class="btn"
      type="button"
      aria-label="Étape précédente"
      @click="$emit('back')"
    >
      ←
    </button>
    <span v-else class="btn-placeholder" />

    <DemandeStepper v-if="!submitted" :etape="etape" @go-to="$emit('go-to', $event)" />

    <button class="btn" type="button" aria-label="Fermer" @click="$emit('close')">
      ✕
    </button>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  etape: number
  submitted: boolean
}>()

defineEmits<{
  (e: 'back'): void
  (e: 'close'): void
  (e: 'go-to', n: number): void
}>()
</script>

<style scoped lang="scss">
.sheet-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-s $spacing-m 0;
  flex-shrink: 0;
  gap: $spacing-xs;
  overflow: hidden;

  .btn {
    width: 2.2rem;
    height: 2.2rem;
    border-radius: 50%;
    border: 1px solid $divider-color;
    background: #fff;
    font-size: 1rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    transition: background 150ms ease;
    color: $text-primary;

    &:hover {
      background: $surface-soft;
    }
  }

  .btn-placeholder {
    width: 2.2rem;
    height: 2.2rem;
    flex-shrink: 0;
  }
}
</style>
