<template>
  <div class="edit-actions">
    <div v-if="statusText" class="save-status" :class="{ error: saveError }">
      <p>{{ statusText }}</p>
      <SgiltButton v-if="saveError" variant="tertiary" class="retry-btn" @click="retrySave">
        {{ $t('provider.edit.retry') }}
      </SgiltButton>
    </div>

    <SgiltButton
      v-if="prestataire?.status === 'DRAFT'"
      class="btn-submit"
      variant="secondary"
      :loading="submitting"
      @click="onSubmit"
    >
      {{ $t('provider.edit.submit') }}
    </SgiltButton>
    <p v-if="submitError" class="error">{{ $t('provider.edit.submit-error') }}</p>
    <p v-if="submitBlockedMessage" class="error">{{ submitBlockedMessage }}</p>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'

const { t } = useI18n()
const { prestataire, saving, saveError, retrySave, submit, submitting, submitError } =
  usePrestataire()

const submitBlockedMessage = ref<string | null>(null)

const statusText = computed(() => {
  if (saving.value) return t('provider.edit.saving')
  if (saveError.value) return t('provider.edit.save-error')
  return ''
})

async function onSubmit() {
  if (saving.value) {
    submitBlockedMessage.value = t('provider.edit.submit-blocked-saving')
    return
  }
  submitBlockedMessage.value = null
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

.btn-submit {
  width: 15rem;
}

.save-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  font-size: 0.8rem;
  color: $text-secondary;

  p {
    margin: 0;
  }

  &.error {
    color: $state-error;
  }
}

.retry-btn {
  height: auto;
  padding: 0.1rem 0.5rem;
  font-size: 0.8rem;
  text-decoration: underline;
}

.error {
  font-size: 0.8rem;
  color: $state-error;
  margin: 0;
}
</style>
