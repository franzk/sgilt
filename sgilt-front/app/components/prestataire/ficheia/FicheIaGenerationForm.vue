<template>
  <div class="ia-form">
    <p class="intro">{{ $t('provider.edit.ia.intro') }}</p>

    <div class="field">
      <label class="label" for="ia-url">{{ $t('provider.edit.ia.url-label') }}</label>
      <input
        id="ia-url"
        v-model="url"
        class="input"
        type="url"
        :disabled="generating"
        :placeholder="$t('provider.edit.ia.url-placeholder')"
      />
      <p v-if="urlError" class="error">{{ urlError }}</p>
    </div>

    <SgiltButton :loading="generating" @click="onGenerate">
      {{ $t('provider.edit.ia.submit') }}
    </SgiltButton>

    <p v-if="generating" class="hint">{{ $t('provider.edit.ia.generating') }}</p>
    <p v-if="generationError === 'quota'" class="error">
      {{ $t('provider.edit.ia.error-quota') }}
    </p>
    <p v-if="generationError === 'technical'" class="error">
      {{ $t('provider.edit.ia.error-technical') }}
    </p>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'

const { t } = useI18n()
const { result, generating, generationError, generate } = useFicheIa()

const emit = defineEmits<{ generated: [] }>()

const url = ref('')
const urlError = ref<string | null>(null)

function isValidHttpUrl(value: string): boolean {
  return value.includes('.')
}

async function onGenerate(): Promise<void> {
  if (generationError.value === 'quota') {
    // Le message est déjà affiché sous le formulaire — inutile de relancer un appel.
    return
  }

  const trimmedUrl = url.value.trim()
  if (!trimmedUrl) {
    urlError.value = t('provider.edit.ia.url-required')
    return
  }
  if (!isValidHttpUrl(trimmedUrl)) {
    urlError.value = t('provider.edit.ia.url-invalid')
    return
  }
  urlError.value = null

  await generate(trimmedUrl)
  if (result.value) emit('generated')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.ia-form {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  max-width: 480px;
  margin: 0 auto;

  .intro {
    font-size: $font-size-sm;
    color: $text-secondary;
    margin: 0;
  }

  .field {
    display: flex;
    flex-direction: column;
    gap: $spacing-xxs;
  }

  .label {
    font-size: $font-size-sm;
    font-weight: $font-weight-medium;
    color: $text-secondary;
  }

  .input {
    font-size: $font-size-md;
    padding: $spacing-s $spacing-m;
    border: $input-border;
    border-radius: $input-border-radius;
    box-shadow: $input-box-shadow;
    outline: none;
    width: 100%;
    font-family: inherit;
    background: $surface-white;

    &:focus {
      border-color: $input-focus-border-color;
      box-shadow: $input-focus-box-shadow;
    }

    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }

  .hint {
    font-size: $font-size-sm;
    color: $text-secondary;
    margin: 0;
  }

  .error {
    font-size: $font-size-sm;
    color: $state-error;
    margin: 0;
  }
}
</style>
