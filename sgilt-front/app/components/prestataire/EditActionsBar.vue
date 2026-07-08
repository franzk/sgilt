<template>
  <div class="edit-actions">
    <SgiltButton class="btn-full" :loading="saving" @click="onSave">
      {{ saved ? $t('provider.edit.saved') : $t('provider.edit.save') }}
    </SgiltButton>
    <p v-if="saveError" class="error">{{ $t('provider.edit.save-error') }}</p>

    <SgiltButton
      v-if="prestataire?.status === 'DRAFT'"
      class="btn-full"
      variant="secondary"
      :loading="submitting"
      @click="onSubmit"
    >
      {{ $t('provider.edit.submit') }}
    </SgiltButton>
    <p v-if="submitError" class="error">{{ $t('provider.edit.submit-error') }}</p>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'

const { prestataire, save, saving, saved, saveError, submit, submitting, submitError } = usePrestataire()

async function onSave() {
  await save()
}

async function onSubmit() {
  await submit()
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.edit-actions {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
  width: 100%;
}

.btn-full {
  width: 100%;
}

.error {
  font-size: 0.8rem;
  color: $state-error;
  margin: 0;
}
</style>
