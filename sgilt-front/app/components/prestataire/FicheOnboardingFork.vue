<template>
  <div class="fork">
    <div v-if="step === 'choice'" class="choice">
      <h1 class="title">{{ $t('provider.edit.fork.title') }}</h1>
      <p class="subtitle">{{ $t('provider.edit.fork.subtitle') }}</p>

      <div class="options">
        <button type="button" class="option" @click="step = 'ia-form'">
          <span class="option-title">{{ $t('provider.edit.fork.ia-title') }}</span>
          <span class="option-description">{{ $t('provider.edit.fork.ia-description') }}</span>
        </button>
        <button type="button" class="option" @click="chooseManual">
          <span class="option-title">{{ $t('provider.edit.fork.manual-title') }}</span>
          <span class="option-description">{{ $t('provider.edit.fork.manual-description') }}</span>
        </button>
      </div>
    </div>

    <div v-else-if="step === 'ia-form'" class="ia-step">
      <FicheIaGenerationForm @generated="onGenerated" />
      <button type="button" class="back-link" @click="step = 'choice'">
        {{ $t('common.back-arrow') }}
      </button>
    </div>

    <div v-else class="applying">
      <p v-if="!applyError">{{ $t('provider.edit.fork.applying') }}</p>
      <template v-else>
        <p class="error">{{ $t('provider.edit.ia.apply-error') }}</p>
        <SgiltButton @click="runApply">{{ $t('provider.edit.retry') }}</SgiltButton>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import FicheIaGenerationForm from '~/components/prestataire/ficheia/FicheIaGenerationForm.vue'

const emit = defineEmits<{ 'choose-manual': [] }>()

const { applyAll, applyError } = useFicheIa()

type Step = 'choice' | 'ia-form' | 'applying'
const step = ref<Step>('choice')

async function chooseManual(): Promise<void> {
  emit('choose-manual')
  await navigateTo('/pro/page-edition/edition')
}

async function onGenerated(): Promise<void> {
  step.value = 'applying'
  await runApply()
}

async function runApply(): Promise<void> {
  await applyAll()
  if (!applyError.value) {
    await navigateTo('/pro/page-edition/edition')
  }
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.fork {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100dvh - $app-header-height);
  padding: $spacing-l $spacing-m;
}

.choice {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-l;
  max-width: 640px;
  text-align: center;

  .title {
    font-family: 'Cormorant Garamond', serif;
    font-size: clamp(1.6rem, 4vw, 2.2rem);
    font-weight: 700;
    color: $color-primary;
    margin: 0;
  }

  .subtitle {
    font-size: $font-size-md;
    color: $text-secondary;
    margin: 0;
  }
}

.options {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  width: 100%;

  @media (min-width: $breakpoint-desktop) {
    flex-direction: row;
  }
}

.option {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
  flex: 1;
  padding: $spacing-l;
  background: $surface-white;
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: $radius-md;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  font-family: inherit;
  text-align: left;
  cursor: pointer;
  transition:
    border-color 150ms ease,
    box-shadow 150ms ease;

  &:hover {
    border-color: $color-primary;
    box-shadow: 0 4px 24px rgba(0, 0, 0, 0.1);
  }

  .option-title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.2rem;
    font-weight: 700;
    color: $color-primary;
  }

  .option-description {
    font-size: $font-size-sm;
    color: $text-secondary;
    line-height: 1.5;
  }
}

.ia-step {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  width: 100%;
  max-width: 480px;
}

.back-link {
  align-self: flex-start;
  background: none;
  border: none;
  padding: 0;
  font-family: inherit;
  font-size: $font-size-sm;
  color: $text-secondary;
  cursor: pointer;

  &:hover {
    color: $color-primary;
  }
}

.applying {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-m;
  text-align: center;

  p {
    margin: 0;
    color: $text-secondary;
  }

  .error {
    color: $state-error;
  }
}
</style>
