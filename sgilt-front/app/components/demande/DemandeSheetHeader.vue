<template>
  <div class="sheet-header">
    <!-- Ligne haute : lien Annuler (droit), masqué après soumission -->
    <div v-if="!submitted" class="top-row">
      <button class="cancel-btn" type="button" @click="showCancelDialog = true">
        {{ $t('tunnel.cancel.label') }}
      </button>
    </div>

    <!-- Ligne basse : ← retour + stepper -->
    <div class="nav-row">
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

      <DemandeStepper v-if="!submitted" :etape="etape" :steps="steps" @go-to="$emit('go-to', $event)" />
    </div>

    <!-- Dialog de confirmation -->
    <SgiltDialog
      v-model:open="showCancelDialog"
      :title="$t('tunnel.cancel.dialog-title')"
    >
      <div class="dialog-content">
        <p class="dialog-body">{{ $t('tunnel.cancel.dialog-body') }}</p>
        <div class="dialog-actions">
          <SgiltButton variant="secondary" @click="showCancelDialog = false">
            {{ $t('tunnel.cancel.back') }}
          </SgiltButton>
          <SgiltButton @click="confirmCancel">
            {{ $t('tunnel.cancel.confirm') }}
          </SgiltButton>
        </div>
      </div>
    </SgiltDialog>
  </div>
</template>

<script setup lang="ts">
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'

defineProps<{
  etape: number
  submitted: boolean
  steps?: number
}>()

const emit = defineEmits<{
  (e: 'back'): void
  (e: 'close'): void
  (e: 'go-to', n: number): void
}>()

const showCancelDialog = ref(false)

function confirmCancel() {
  showCancelDialog.value = false
  emit('close')
}
</script>

<style scoped lang="scss">
.sheet-header {
  display: flex;
  flex-direction: column;
  padding: $spacing-s $spacing-m 0;
  flex-shrink: 0;
  overflow: hidden;
}

.top-row {
  display: flex;
  justify-content: flex-end;
  margin-bottom: $spacing-xxs;
}

.cancel-btn {
  background: none;
  border: none;
  padding: 0;
  font-size: 0.85rem;
  font-family: inherit;
  color: $text-secondary;
  cursor: pointer;
  transition: color 150ms ease;

  &:hover {
    color: $text-primary;
  }
}

.nav-row {
  display: flex;
  align-items: center;
  gap: $spacing-xs;
  overflow: hidden;
}

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

.dialog-content {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: $spacing-m $spacing-m $spacing-l;
}

.dialog-body {
  font-size: 0.95rem;
  color: $text-secondary;
  margin: 0;
  line-height: 1.5;
}

.dialog-actions {
  display: flex;
  gap: $spacing-s;
  justify-content: flex-end;
}
</style>
