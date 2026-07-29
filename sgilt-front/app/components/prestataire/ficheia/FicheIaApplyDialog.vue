<template>
  <SgiltDialog v-model:open="open" :title="$t('provider.edit.ia.apply-dialog.title')" max-width="480px">
    <div class="apply-dialog">
      <template v-if="!isList">
        <p class="message">{{ $t('provider.edit.ia.apply-dialog.simple-message') }}</p>
        <div class="actions">
          <SgiltButton variant="secondary" @click="open = false">
            {{ $t('common.cancel') }}
          </SgiltButton>
          <SgiltButton @click="onConfirm">
            {{ $t('provider.edit.ia.apply-dialog.confirm') }}
          </SgiltButton>
        </div>
      </template>

      <template v-else>
        <div class="option">
          <p class="option-desc">{{ $t('provider.edit.ia.apply-dialog.replace-desc') }}</p>
          <SgiltButton variant="secondary" @click="onReplace">
            {{ $t('provider.edit.ia.action.replace') }}
          </SgiltButton>
        </div>
        <div class="option">
          <p class="option-desc">{{ $t('provider.edit.ia.apply-dialog.add-desc') }}</p>
          <SgiltButton variant="secondary" @click="onAdd">
            {{ $t('provider.edit.ia.action.add') }}
          </SgiltButton>
        </div>
        <button type="button" class="cancel-link" @click="open = false">
          {{ $t('common.cancel') }}
        </button>
      </template>
    </div>
  </SgiltDialog>
</template>

<script setup lang="ts">
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'

const open = defineModel<boolean>('open', { required: true })

defineProps<{
  /** Champ liste (choix Remplacer/Ajouter) vs champ simple (confirmation unique). */
  isList: boolean
}>()

const emit = defineEmits<{ confirm: []; replace: []; add: [] }>()

function onConfirm() {
  open.value = false
  emit('confirm')
}
function onReplace() {
  open.value = false
  emit('replace')
}
function onAdd() {
  open.value = false
  emit('add')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.apply-dialog {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: $spacing-m $spacing-m $spacing-l;
}

.message {
  font-size: 0.95rem;
  color: $text-secondary;
  margin: 0;
  line-height: 1.5;
}

.actions {
  display: flex;
  gap: $spacing-s;
  justify-content: flex-end;
}

.option {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  .option-desc {
    font-size: 0.9rem;
    color: $text-secondary;
    margin: 0;
    line-height: 1.5;
  }
}

.cancel-link {
  align-self: center;
  background: none;
  border: none;
  padding: 0;
  font-family: inherit;
  font-size: $font-size-sm;
  color: $text-secondary;
  text-decoration: underline;
  cursor: pointer;
}
</style>
