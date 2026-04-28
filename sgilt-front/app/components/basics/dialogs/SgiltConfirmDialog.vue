<template>
  <SgiltDialog v-model:open="open" :title="title ?? ''" :max-width="maxWidth">
    <div class="confirm-content">
      <p class="confirm-message">{{ message }}</p>
      <div class="confirm-actions">
        <SgiltButton variant="secondary" @click="handleCancel">
          {{ cancelLabel ?? $t('common.cancel') }}
        </SgiltButton>
        <span :class="{ 'confirm-destructive': destructive }">
          <SgiltButton :loading="confirmLoading" @click="handleConfirm">
            {{ confirmLabel ?? $t('common.confirm') }}
          </SgiltButton>
        </span>
      </div>
    </div>
  </SgiltDialog>
</template>

<script setup lang="ts">
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'

const open = defineModel<boolean>('open', { required: true })

defineProps<{
  title?: string
  message: string
  confirmLabel?: string
  cancelLabel?: string
  destructive?: boolean
  confirmLoading?: boolean
  maxWidth?: string
}>()

const emit = defineEmits<{ confirm: []; cancel: [] }>()

function handleConfirm() {
  open.value = false
  emit('confirm')
}

function handleCancel() {
  open.value = false
  emit('cancel')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.confirm-content {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: $spacing-m $spacing-m $spacing-l;
}

.confirm-message {
  font-size: 0.95rem;
  color: $text-secondary;
  margin: 0;
  line-height: 1.5;
}

.confirm-actions {
  display: flex;
  gap: $spacing-s;
  justify-content: flex-end;
}

.confirm-destructive {
  :deep(.btn) {
    background: $state-error;
    box-shadow: 0 2px 6px rgba($state-error, 0.25);
    border-top-color: rgba(255, 255, 255, 0.2);

    &:hover:not(:disabled) {
      filter: brightness(1.08);
    }
  }
}
</style>
