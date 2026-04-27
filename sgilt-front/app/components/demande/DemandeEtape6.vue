<template>
  <div class="etape">
    <h2 class="question">
      {{
        isNewEventFlow ? $t('tunnel.etape6.question-message') : $t('tunnel.etape6.question-contact')
      }}
    </h2>

    <!-- Coordonnées (masquées en flow new-event, récupérées depuis le profil) -->
    <template v-if="!isNewEventFlow">
      <div class="fields">
        <div class="field-group">
          <label class="field-label">
            {{ $t('tunnel.etape6.field-prenom') }} <span class="required">*</span>
          </label>
          <input
            v-model="state.prenom"
            class="field-input"
            :class="{ 'field-input--error': errors.prenom }"
            type="text"
            :placeholder="$t('tunnel.etape6.prenom-placeholder')"
            autocomplete="given-name"
            @blur="touched.prenom = true"
          />
          <p v-if="errors.prenom" class="field-error">{{ errors.prenom }}</p>
        </div>

        <div class="field-group">
          <label class="field-label">
            {{ $t('tunnel.etape6.field-nom') }} <span class="required">*</span>
          </label>
          <input
            v-model="state.nom"
            class="field-input"
            :class="{ 'field-input--error': errors.nom }"
            type="text"
            :placeholder="$t('tunnel.etape6.nom-placeholder')"
            autocomplete="family-name"
            @blur="touched.nom = true"
          />
          <p v-if="errors.nom" class="field-error">{{ errors.nom }}</p>
        </div>

        <div class="field-group">
          <label class="field-label">
            {{ $t('tunnel.etape6.field-email') }} <span class="required">*</span>
          </label>
          <input
            v-model="state.email"
            class="field-input"
            :class="{ 'field-input--error': errors.email }"
            type="email"
            :placeholder="$t('tunnel.etape6.email-placeholder')"
            autocomplete="email"
            @blur="touched.email = true"
          />
          <p v-if="errors.email" class="field-error">{{ errors.email }}</p>
        </div>

        <div class="field-group">
          <label class="field-label">
            {{ $t('tunnel.etape6.field-phone') }} <span class="required">*</span>
          </label>
          <input
            v-model="state.telephone"
            class="field-input"
            :class="{ 'field-input--error': errors.telephone }"
            type="tel"
            :placeholder="$t('tunnel.etape6.phone-placeholder')"
            autocomplete="tel"
            @blur="touched.telephone = true"
          />
          <p v-if="errors.telephone" class="field-error">{{ errors.telephone }}</p>
        </div>
      </div>
    </template>

    <div class="fields">
      <div class="field-group">
        <textarea
          v-model="state.prestataireMessage"
          class="description-textarea"
          :placeholder="$t('tunnel.etape6.message-placeholder')"
          rows="6"
        />
      </div>

      <div class="field-group submit">
        <SgiltButton type="primary" :disabled="!formValid" :loading="submitting" @click="handleSubmit">
          {{ $t('tunnel.etape6.submit') }}
        </SgiltButton>
        <p v-if="submitError" class="submit-error">{{ submitError }}</p>
      </div>
    </div>

    <!-- Récapitulatif complet -->
    <DemandeRecap v-if="showRecap" />
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import { useDemande } from '~/composables/useDemande'

defineProps<{ showRecap?: boolean }>()

const { t } = useI18n()

const { state, submit, submitting, submitError } = useDemande()

const { currentFlow } = useFlow()
const isNewEventFlow = computed(() => currentFlow.value === 'new-event')

// ── Validation ────────────────────────────────────────────────────────────────

const touched = reactive({ prenom: false, nom: false, email: false, telephone: false })

function validateEmail(v: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v.trim())
}

function validatePhone(v: string): boolean {
  const digits = v.replace(/[\s\-.()\/+]/g, '')
  return /^\d{7,15}$/.test(digits)
}

const errors = computed(() => ({
  prenom: touched.prenom && !state.prenom.trim() ? t('tunnel.etape6.error-required') : null,
  nom: touched.nom && !state.nom.trim() ? t('tunnel.etape6.error-required') : null,
  email: touched.email
    ? !state.email.trim()
      ? t('tunnel.etape6.error-required')
      : !validateEmail(state.email)
        ? t('tunnel.etape6.error-email')
        : null
    : null,
  telephone: touched.telephone
    ? !state.telephone.trim()
      ? t('tunnel.etape6.error-required')
      : !validatePhone(state.telephone)
        ? t('tunnel.etape6.error-phone')
        : null
    : null,
}))

const formValid = computed(() => {
  if (isNewEventFlow.value) return true
  return (
    !!state.prenom.trim() &&
    !!state.nom.trim() &&
    validateEmail(state.email) &&
    validatePhone(state.telephone)
  )
})

function handleSubmit() {
  if (!formValid.value) {
    touched.prenom = true
    touched.nom = true
    touched.email = true
    touched.telephone = true
    return
  }
  submit()
}
</script>

<style scoped lang="scss">
.etape {
  display: flex;
  flex-direction: column;
  gap: 1rem;

  .question {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.5rem;
    font-weight: 500;
    color: $text-primary;
    margin: 0;
    line-height: 1.3;
  }
}

.fields {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}

.submit-error {
  font-size: 0.85rem;
  color: $state-error;
  margin: 0;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: $spacing-xxs;
}

.field-label {
  font-size: 0.85rem;
  font-weight: 600;
  color: $text-primary;
}

.required {
  color: $state-error;
  margin-left: 2px;
}

.field-error {
  font-size: 0.8rem;
  color: $state-error;
  margin: 0;
}

.field-input {
  width: 100%;
  padding: $spacing-s $spacing-m;
  border: 1.5px solid $divider-color;
  border-radius: $radius-md;
  font-size: 0.95rem;
  font-family: inherit;
  color: $text-primary;
  background: #fff;
  outline: none;
  transition: border-color 180ms ease;
  box-sizing: border-box;

  &:focus {
    border-color: $brand-accent;
  }

  &--error {
    border-color: $state-error;
  }

  &::placeholder {
    color: $text-secondary;
    opacity: 0.5;
  }
}

.description-textarea {
  width: 100%;
  padding: $spacing-m;
  border: 1.5px solid $divider-color;
  border-radius: $radius-md;
  font-size: 0.95rem;
  font-family: inherit;
  color: $text-primary;
  background: #fff;
  resize: vertical;
  outline: none;
  transition: border-color 180ms ease;
  box-sizing: border-box;
  line-height: 1.6;

  &:focus {
    border-color: $brand-accent;
  }

  &::placeholder {
    color: $text-secondary;
    opacity: 0.5;
  }
}
</style>
