<template>
  <div class="edit-actions">
    <SgiltButton class="btn-full" :loading="saving" @click="onSave">
      {{ saved ? $t('provider.edit.saved') : $t('provider.edit.save') }}
    </SgiltButton>
    <p v-if="saveError" class="error">{{ $t('provider.edit.save-error') }}</p>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'

const { save, saving, saved, saveError } = usePrestataire()

async function onSave() {
  await save()
}

function onSubmit() {
  // stub — branché au brief 4b
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
