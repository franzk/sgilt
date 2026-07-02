<template>
  <div class="verify-page">
    <div v-if="status === 'idle' || status === 'pending'" class="state-center">
      <p class="state-text">{{ $t('common.loading') }}</p>
    </div>

    <div v-else-if="pageError" class="state-center">
      <p class="state-text state-text--error">{{ pageError }}</p>
    </div>

    <template v-else>
      <div class="verify-content">
        <!-- ── Hero ────────────────────────────────────────────────────────────── -->
        <div class="hero">
          <div class="hero__top">
            <div class="hero__left">
              <span class="badge">
                <CheckboxCircleIcon class="badge__icon" />
                {{ $t('confirmation.badge') }}
              </span>
              <h1 class="hero__title">
                {{ $t('confirmation.hero-title-line1') }}<br />{{
                  $t('confirmation.hero-title-line2')
                }}
              </h1>
            </div>

            <div class="hero__illustration" aria-hidden="true">
              <OnboardingSuccess />
            </div>
          </div>

          <p class="hero__sub">{{ $t('confirmation.hero-sub') }}</p>
        </div>

        <!-- ── Card formulaire ─────────────────────────────────────────────────── -->
        <div class="card-wrapper">
          <form class="card" @submit.prevent="submit">
            <div class="card__header">
              <h2 class="card__title">{{ $t('confirmation.form.card-title') }}</h2>
              <p class="card__subtitle">{{ $t('confirmation.form.card-subtitle') }}</p>
            </div>

            <div class="email-row">
              <UserIcon class="email-row__icon" />
              <div class="email-row__text">
                <span class="email-row__label">{{
                  $t('confirmation.form.email-connected-label')
                }}</span>
                <span class="email-row__value">{{ email }}</span>
              </div>
            </div>

            <div class="field">
              <label class="label">{{ $t('confirmation.form.password-label') }}</label>
              <div class="input-wrapper">
                <input
                  v-model="password"
                  class="input"
                  :type="showPassword ? 'text' : 'password'"
                  required
                  autocomplete="new-password"
                  @blur="passwordTouched = true"
                />
                <button
                  type="button"
                  class="eye-btn"
                  :aria-label="
                    showPassword
                      ? $t('confirmation.form.hide-password')
                      : $t('confirmation.form.show-password')
                  "
                  @click="showPassword = !showPassword"
                >
                  <EyeOffIcon v-if="showPassword" />
                  <EyeIcon v-else />
                </button>
              </div>
              <ul v-if="passwordTouched" class="password-rules">
                <li
                  v-for="rule in passwordRules.filter((r) => !r.met)"
                  :key="rule.key"
                  class="rule"
                >
                  {{ $t(rule.key) }}
                </li>
              </ul>
            </div>

            <div class="field">
              <label class="label">{{ $t('confirmation.form.password-confirm-label') }}</label>
              <div class="input-wrapper">
                <input
                  v-model="passwordConfirm"
                  class="input"
                  :type="showPasswordConfirm ? 'text' : 'password'"
                  required
                  autocomplete="new-password"
                />
                <button
                  type="button"
                  class="eye-btn"
                  :aria-label="showPasswordConfirm ? 'Masquer' : 'Afficher'"
                  @click="showPasswordConfirm = !showPasswordConfirm"
                >
                  <EyeOffIcon v-if="showPasswordConfirm" />
                  <EyeIcon v-else />
                </button>
              </div>
            </div>

            <label class="terms">
              <input v-model="acceptedTerms" type="checkbox" class="terms__checkbox" />
              <i18n-t keypath="confirmation.form.terms-label" tag="span" scope="global">
                <template #cgu>
                  <a href="/m/cgu" target="_blank" rel="noopener">{{
                    $t('confirmation.form.terms-cgu')
                  }}</a>
                </template>
                <template #privacy>
                  <a href="/m/confidentialite" target="_blank" rel="noopener">{{
                    $t('confirmation.form.terms-privacy')
                  }}</a>
                </template>
              </i18n-t>
            </label>

            <p v-if="submitError" class="submit-error">{{ submitError }}</p>

            <button class="btn-submit" type="submit" :disabled="submitting || !acceptedTerms">
              ✨ {{ submitting ? $t('common.saving') : $t('confirmation.form.cta') }}
            </button>

            <p class="hint">{{ $t('confirmation.form.hint') }}</p>
          </form>

          <p class="security-note">🔒 {{ $t('confirmation.form.security-note') }}</p>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { FetchError } from 'ofetch'
import { CheckboxCircleIcon, UserIcon, EyeIcon, EyeOffIcon } from '@remixicons/vue/line'
import { confirmOnboardingAccount } from '~/data/onboarding/api/onboardingApi'
import type { VerifyTokenResponseDto } from '~/data/onboarding/dto/OnboardingDto'
import OnboardingSuccess from '~/assets/svg/OnboardingSuccess.vue'

const route = useRoute()
const { t } = useI18n()

definePageMeta({ layout: 'app' })
useHead(() => ({ title: t('confirmation.page-title') }))

const token = Array.isArray(route.query.token) ? route.query.token[0] : route.query.token

const {
  data,
  error: fetchError,
  status,
} = useApiFetch<VerifyTokenResponseDto>('/onboarding/verify', {
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
const passwordTouched = ref(false)
const showPassword = ref(false)
const showPasswordConfirm = ref(false)
const acceptedTerms = ref(false)
const submitting = ref(false)
const submitError = ref<string | null>(null)

const passwordRules = computed(() => [
  { key: 'confirmation.form.password-rules.length', met: password.value.length >= 8 },
  { key: 'confirmation.form.password-rules.upper', met: /[A-Z]/.test(password.value) },
  { key: 'confirmation.form.password-rules.lower', met: /[a-z]/.test(password.value) },
  { key: 'confirmation.form.password-rules.digit', met: /[0-9]/.test(password.value) },
  { key: 'confirmation.form.password-rules.special', met: /[^A-Za-z0-9]/.test(password.value) },
  {
    key: 'confirmation.form.password-rules.not-email',
    met: !!password.value && password.value.toLowerCase() !== email.value?.toLowerCase(),
  },
])

const passwordValid = computed(() => passwordRules.value.every((r) => r.met))

async function submit(): Promise<void> {
  if (!passwordValid.value) {
    passwordTouched.value = true
    return
  }

  if (password.value !== passwordConfirm.value) {
    submitError.value = t('confirmation.submit.error.password-mismatch')
    return
  }

  if (!acceptedTerms.value) {
    return
  }

  submitting.value = true
  submitError.value = null

  try {
    const response = await confirmOnboardingAccount({
      setPasswordToken: setPasswordToken.value!,
      password: password.value,
      acceptedTerms: acceptedTerms.value,
    })
    window.location.href = response.loginUrl
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
@use '@/assets/styles/base' as *;

.verify-page {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  background: $surface-soft;

  @media (min-width: $breakpoint-desktop) {
    align-items: center;
    justify-content: center;
    padding: $spacing-xl $spacing-m;
  }
}

// ── Wrapper desktop deux colonnes ─────────────────────────────────────────────
.verify-content {
  display: contents;

  @media (min-width: $breakpoint-desktop) {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: $spacing-xxl;
    align-items: center;
    width: 100%;
    max-width: 960px;
  }
}

// ── États chargement / erreur ─────────────────────────────────────────────────
.state-center {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-xl $spacing-m;

  .state-text {
    font-size: $font-size-md;
    color: $text-secondary;
    text-align: center;

    &--error {
      color: $state-error;
    }
  }
}

// ── Hero ──────────────────────────────────────────────────────────────────────
.hero {
  display: flex;
  flex-direction: column;
  padding: $spacing-l $spacing-m calc(#{$spacing-xxl} + #{$spacing-l});
  gap: $spacing-s;

  @media (min-width: $breakpoint-desktop) {
    padding: 0;
    gap: $spacing-l;
    justify-content: center;
  }

  &__top {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: $spacing-s;

    @media (min-width: $breakpoint-desktop) {
      flex-direction: column-reverse;
      align-items: flex-start;
      gap: $spacing-l;
    }
  }

  &__left {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
  }

  &__title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 2.4rem;
    font-weight: 700;
    color: $brand-primary;
    margin: 0;
    line-height: 1.1;

    @media (min-width: $breakpoint-desktop) {
      font-size: 3.2rem;
    }
  }

  &__sub {
    font-size: $font-size-sm;
    color: $text-secondary;
    margin: 0;
    line-height: 1.55;

    @media (min-width: $breakpoint-desktop) {
      font-size: $font-size-md;
      max-width: 380px;
    }
  }

  &__illustration {
    flex: 1;
    display: flex;
    justify-content: center;

    margin-top: $spacing-xs;

    @media (min-width: $breakpoint-desktop) {
      width: 200px;
      margin-top: 0;
      justify-content: unset;
    }

    img {
      width: 100%;
      height: auto;
      display: block;
    }
  }
}

// ── Badge ─────────────────────────────────────────────────────────────────────
.badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 4px 10px 4px 8px;
  background: rgba($state-success, 0.12);
  color: $state-success;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 600;
  width: fit-content;

  &__icon {
    width: 14px;
    height: 14px;
  }
}

// ── Card formulaire ───────────────────────────────────────────────────────────
.card-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0 $spacing-m $spacing-xl;
  gap: $spacing-m;

  @media (max-width: #{$breakpoint-desktop - 1px}) {
    margin-top: calc(-1 * #{$spacing-xxl});
  }

  @media (min-width: $breakpoint-desktop) {
    flex: none;
    padding: 0;
  }
}

.card {
  background: $surface-white;
  border-radius: $radius-lg;
  box-shadow: 0 4px 24px $shadow-m;
  padding: $spacing-l $spacing-m;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;

  @media (max-width: #{$breakpoint-desktop - 1px}) {
    border-radius: $radius-lg $radius-lg 0 0;
    box-shadow: 0 -4px 32px rgba(0, 0, 0, 0.08);
    padding: $spacing-l $spacing-m $spacing-xxl;
  }

  @media (min-width: $breakpoint-desktop) {
    padding: $spacing-xl $spacing-l;
  }

  &__header {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.35rem;
    font-weight: 700;
    color: $brand-primary;
    margin: 0;
  }

  &__subtitle {
    font-size: $font-size-sm;
    color: $text-secondary;
    margin: 0;
  }
}

// ── Email row ─────────────────────────────────────────────────────────────────
.email-row {
  display: flex;
  align-items: center;
  gap: $spacing-s;
  padding: $spacing-s $spacing-m;
  background: $surface-soft;
  border-radius: $radius-md;

  &__icon {
    width: 1.1rem;
    height: 1.1rem;
    color: $text-secondary;
    flex-shrink: 0;
  }

  &__text {
    display: flex;
    flex-direction: column;
    gap: 1px;
  }

  &__label {
    font-size: 0.7rem;
    color: $text-secondary;
    text-transform: uppercase;
    letter-spacing: 0.04em;
    font-weight: 500;
  }

  &__value {
    font-size: $font-size-sm;
    color: $text-primary;
    font-weight: 500;
  }
}

// ── Champ ─────────────────────────────────────────────────────────────────────
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

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;

  .input {
    width: 100%;
    padding-right: 2.75rem;
  }

  .eye-btn {
    position: absolute;
    right: $spacing-s;
    display: flex;
    align-items: center;
    justify-content: center;
    background: none;
    border: none;
    cursor: pointer;
    color: $text-secondary;
    padding: 0;

    svg {
      width: 1.1rem;
      height: 1.1rem;
    }

    &:hover {
      color: $text-primary;
    }
  }
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
}

// ── Acceptation CGU ───────────────────────────────────────────────────────────
.terms {
  display: flex;
  align-items: flex-start;
  gap: $spacing-xs;
  font-size: $font-size-sm;
  color: $text-secondary;
  line-height: 1.5;
  cursor: pointer;

  &__checkbox {
    margin-top: 3px;
    width: 1rem;
    height: 1rem;
    flex-shrink: 0;
    cursor: pointer;
    accent-color: $brand-primary;
  }

  a {
    color: $text-primary;
    text-decoration: underline;
    text-decoration-color: $brand-accent;
    text-underline-offset: 2px;

    &:hover {
      color: $brand-accent;
    }
  }
}

.password-rules {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: $spacing-xxs;

  .rule {
    font-size: $font-size-sm;
    color: $state-error;
  }
}

// ── Bouton submit ─────────────────────────────────────────────────────────────
.btn-submit {
  font-size: $font-size-md;
  font-weight: $font-weight-semibold;
  font-family: inherit;
  padding: $spacing-s $spacing-l;
  background: $brand-primary;
  color: $text-inverted;
  border: none;
  border-radius: $border-radius-xxl;
  cursor: pointer;
  width: 100%;
  letter-spacing: 0.01em;

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

// ── Textes secondaires ────────────────────────────────────────────────────────
.submit-error {
  font-size: $font-size-sm;
  color: $state-error;
  margin: 0;
}

.hint {
  font-size: 0.75rem;
  color: $text-secondary;
  margin: 0;
  line-height: 1.5;
  text-align: center;
}

.security-note {
  font-size: 0.75rem;
  color: $text-secondary;
  text-align: center;
  margin: 0;
}
</style>
