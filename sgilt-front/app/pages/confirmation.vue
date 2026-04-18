<template>
  <div class="confirmation-page">
    <div v-if="status === 'idle' || status === 'pending'" class="state">
      <p class="state-text">{{ $t('common.loading') }}</p>
    </div>

    <div v-else-if="pageError" class="state">
      <p class="state-text">{{ pageError }}</p>
    </div>

    <form v-else class="form" @submit.prevent="submit">
      <div class="field">
        <label class="label">{{ $t('confirmation.form.email-label') }}</label>
        <input class="input" type="email" :value="email" disabled />
      </div>

      <div class="field">
        <label class="label">{{ $t('confirmation.form.password-label') }}</label>
        <input
          v-model="password"
          class="input"
          type="password"
          required
          autocomplete="new-password"
        />
      </div>

      <div class="field">
        <label class="label">{{ $t('confirmation.form.password-confirm-label') }}</label>
        <input
          v-model="passwordConfirm"
          class="input"
          type="password"
          required
          autocomplete="new-password"
        />
      </div>

      <p v-if="submitError" class="submit-error">{{ submitError }}</p>

      <button class="btn" type="submit" :disabled="submitting">
        {{ submitting ? $t('common.saving') : $t('confirmation.form.submit') }}
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { FetchError } from 'ofetch'

definePageMeta({ layout: 'app' })

const route = useRoute()
const router = useRouter()
const { t } = useI18n()

interface ConfirmationResponse {
  email: string
  setPasswordToken: string
}

const token = Array.isArray(route.query.token) ? route.query.token[0] : route.query.token

const {
  data,
  error: fetchError,
  status,
} = useApiFetch<ConfirmationResponse>('/confirmation', {
  query: { token },
})

const email = computed(() => data.value?.email ?? null)
const setPasswordToken = computed(() => data.value?.setPasswordToken ?? null)

const pageError = computed<string | null>(() => {
  if (!fetchError.value) return null
  const code = fetchError.value.statusCode
  if (code === 400) return t('confirmation.error.invalid')
  if (code === 403) return t('confirmation.error.already-used')
  if (code === 410) return t('confirmation.error.expired')
  return t('confirmation.error.server')
})

const password = ref('')
const passwordConfirm = ref('')
const submitting = ref(false)
const submitError = ref<string | null>(null)

async function submit(): Promise<void> {
  if (password.value !== passwordConfirm.value) {
    submitError.value = t('confirmation.submit.error.password-mismatch')
    return
  }

  submitting.value = true
  submitError.value = null

  try {
    await apiFetch('/onboarding/confirm-account', {
      method: 'POST',
      body: {
        setPasswordToken: setPasswordToken.value,
        password: password.value,
      },
    })
    await router.push('/app')
  } catch (e: unknown) {
    if (e instanceof FetchError) {
      if (e.statusCode === 400) {
        submitError.value = t('confirmation.submit.error.invalid-token')
      } else if (e.statusCode === 410) {
        submitError.value = t('confirmation.submit.error.session-expired')
      } else {
        submitError.value = t('confirmation.error.server')
      }
    } else {
      submitError.value = t('confirmation.error.server')
    }
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.confirmation-page {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: $spacing-l $spacing-m;

  .state {
    .state-text {
      font-size: $font-size-md;
      color: $text-secondary;
    }
  }

  .form {
    display: flex;
    flex-direction: column;
    gap: $spacing-m;
    width: 100%;
    max-width: 420px;

    .field {
      display: flex;
      flex-direction: column;
      gap: $spacing-xxs;

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

        &:focus {
          border-color: $input-focus-border-color;
          box-shadow: $input-focus-box-shadow;
        }

        &:disabled {
          background-color: $surface-soft;
          color: $text-secondary;
          cursor: not-allowed;
        }
      }
    }

    .submit-error {
      font-size: $font-size-sm;
      color: $color-error;
    }

    .btn {
      font-size: $font-size-md;
      font-weight: $font-weight-medium;
      font-family: inherit;
      padding: $spacing-s $spacing-l;
      background-color: $color-primary;
      color: $text-inverted;
      border: none;
      border-radius: $border-radius-xxl;
      cursor: pointer;

      &:disabled {
        opacity: 0.6;
        cursor: not-allowed;
      }
    }
  }
}
</style>
