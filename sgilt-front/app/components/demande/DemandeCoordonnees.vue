<template>
  <div class="coordonnees">
    <div class="column">
      <DemandeHeader
        class="header"
        :prestataire-name="prestataireName"
        :prestataire-image="prestataireImage"
        @back="$emit('back')"
      />

      <h1 class="title">{{ $t('tunnel.coordonnees.title') }}</h1>
      <p class="subtitle">
        {{ $t('tunnel.coordonnees.subtitle', { name: prestataireName }) }}
      </p>

      <!-- ── Synthèse prestataire + événement ────────────────────────────────── -->
      <div class="summary">
        <div class="summary-prestataire">
          <SgiltImage
            class="summary-avatar"
            :src="prestataireImage"
            :alt="prestataireName"
            width="128"
            height="128"
          />
          <span class="summary-text">
            <span class="summary-name">{{ prestataireName }}</span>
            <span class="summary-category">{{ categoryLine }}</span>
          </span>
        </div>
        <div class="summary-event">
          <span class="summary-info">
            <CalendarEventIcon class="icon" aria-hidden="true" />
            <span class="info-lines">
              <span v-if="eventTypeLabel">{{ eventTypeLabel }}</span>
              <span>{{ formatDate(localEvent.date) }}</span>
            </span>
          </span>
          <span v-if="localEvent.nbInvites" class="summary-info">
            <GroupIcon class="icon" aria-hidden="true" />
            {{ $t('tunnel.coordonnees.invites-value', { value: localEvent.nbInvites }) }}
          </span>
          <span class="summary-info">
            <MapPin2Icon class="icon" aria-hidden="true" />
            {{ localEvent.ville }}
          </span>
        </div>
      </div>

      <!-- ── Vos coordonnées ─────────────────────────────────────────────────── -->
      <section class="section">
        <h2 class="section-title">{{ $t('tunnel.coordonnees.contact-title') }}</h2>
        <p class="section-subtitle">{{ $t('tunnel.coordonnees.contact-subtitle') }}</p>

        <div class="name-row">
          <label class="field-group">
            <span class="field-label">
              {{ $t('tunnel.coordonnees.field-prenom') }}<span class="required">*</span>
            </span>
            <span class="field" :class="{ invalid: errors.prenom }">
              <UserIcon class="icon" aria-hidden="true" />
              <input
                ref="prenomInput"
                v-model="state.prenom"
                class="input"
                type="text"
                name="given-name"
                autocomplete="given-name"
                :placeholder="$t('tunnel.coordonnees.prenom-placeholder')"
              />
            </span>
            <span v-if="errors.prenom" class="error">{{ errors.prenom }}</span>
          </label>

          <label class="field-group">
            <span class="field-label">
              {{ $t('tunnel.coordonnees.field-nom') }}<span class="required">*</span>
            </span>
            <span class="field" :class="{ invalid: errors.nom }">
              <UserIcon class="icon" aria-hidden="true" />
              <input
                ref="nomInput"
                v-model="state.nom"
                class="input"
                type="text"
                name="family-name"
                autocomplete="family-name"
                :placeholder="$t('tunnel.coordonnees.nom-placeholder')"
              />
            </span>
            <span v-if="errors.nom" class="error">{{ errors.nom }}</span>
          </label>
        </div>

        <label class="field-group">
          <span class="field-label">
            {{ $t('tunnel.coordonnees.field-email') }}<span class="required">*</span>
          </span>
          <span class="field" :class="{ invalid: errors.email }">
            <MailIcon class="icon" aria-hidden="true" />
            <input
              ref="emailInput"
              v-model="state.email"
              class="input"
              type="email"
              name="email"
              autocomplete="email"
              :placeholder="$t('tunnel.coordonnees.email-placeholder')"
            />
          </span>
          <span v-if="errors.email" class="error">{{ errors.email }}</span>
        </label>

        <label class="field-group">
          <span class="field-label">
            {{ $t('tunnel.coordonnees.field-phone') }}<span class="required">*</span>
          </span>
          <span class="field" :class="{ invalid: errors.telephone }">
            <PhoneIcon class="icon" aria-hidden="true" />
            <input
              ref="telephoneInput"
              v-model="state.telephone"
              class="input"
              type="tel"
              name="tel"
              autocomplete="tel"
              :placeholder="$t('tunnel.coordonnees.phone-placeholder')"
            />
          </span>
          <span v-if="errors.telephone" class="error">{{ errors.telephone }}</span>
        </label>
      </section>

      <!-- ── Votre message ───────────────────────────────────────────────────── -->
      <section class="section">
        <h2 class="section-title">{{ $t('tunnel.coordonnees.message-title') }}</h2>
        <p class="section-subtitle">
          {{ $t('tunnel.coordonnees.message-subtitle', { name: prestataireName }) }}
        </p>
        <span class="message-field">
          <Chat3Icon class="icon" aria-hidden="true" />
          <textarea
            v-model="state.prestataireMessage"
            class="textarea"
            rows="4"
            :maxlength="MESSAGE_MAX_LENGTH"
            :aria-label="$t('tunnel.coordonnees.message-title')"
            :placeholder="$t('tunnel.coordonnees.message-placeholder')"
          />
          <span class="counter">
            {{ (state.prestataireMessage ?? '').length }}/{{ MESSAGE_MAX_LENGTH }}
          </span>
        </span>
      </section>

      <!-- ── Actions ─────────────────────────────────────────────────────────── -->
      <button class="cta primary" type="button" @click="onSubmit">
        <SendPlaneIcon class="icon" aria-hidden="true" />
        {{ $t('tunnel.coordonnees.submit') }}
      </button>
      <button class="cta secondary" type="button" @click="$emit('back')">
        <ArrowLeftIcon class="icon" aria-hidden="true" />
        {{ $t('tunnel.coordonnees.back-to-recap') }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  ArrowLeftIcon,
  CalendarEventIcon,
  Chat3Icon,
  GroupIcon,
  MailIcon,
  MapPin2Icon,
  PhoneIcon,
  SendPlaneIcon,
  UserIcon,
} from '@remixicons/vue/line'
import SgiltImage from '~/components/basics/media/SgiltImage.vue'
import DemandeHeader from '~/components/demande/DemandeHeader.vue'
import { useDemande } from '~/composables/useDemande'
import { APP_CATEGORIES } from '~/utils/constants'
import { validateEmail, validatePhone } from '~/utils/contactValidation'
import { formatDate } from '~/utils/dateUtils'

// Même limite que la validation back (InitOnboardingRequest.prestataireMessage).
const MESSAGE_MAX_LENGTH = 1000

const props = defineProps<{
  prestataireName: string
  prestataireImage: string
  // Libellé de la catégorie du prestataire et clés de ses sous-catégories.
  prestataireCategory: string
  prestataireSubcats: string[]
}>()

defineEmits<{
  back: []
}>()

const { t } = useI18n()
const { state } = useDemande()
const { localEvent, eventTypeLabel } = useLocalEvent()

// ── Synthèse ──────────────────────────────────────────────────────────────────
// « Restauration · Traiteur » : catégorie puis libellés des sous-catégories.
const categoryLine = computed(() => {
  const subcatNames = APP_CATEGORIES.flatMap((category) => category.subcategories)
    .filter((subcat) => props.prestataireSubcats.includes(subcat.key))
    .map((subcat) => subcat.name)
  return [props.prestataireCategory, ...subcatNames].join(' · ')
})

// ── Validation ────────────────────────────────────────────────────────────────
// Erreurs affichées au clic sur « Envoyer », puis mises à jour en direct.

type ContactField = 'prenom' | 'nom' | 'email' | 'telephone'

// Ordre d'affichage : le défilement va à la première erreur.
const CONTACT_FIELDS: ContactField[] = ['prenom', 'nom', 'email', 'telephone']

const submitAttempted = ref(false)

const errors = computed<Record<ContactField, string | null>>(() => {
  if (!submitAttempted.value) return { prenom: null, nom: null, email: null, telephone: null }
  const required = t('tunnel.coordonnees.error-required')
  return {
    prenom: state.prenom.trim() ? null : required,
    nom: state.nom.trim() ? null : required,
    email: !state.email.trim()
      ? required
      : validateEmail(state.email)
        ? null
        : t('tunnel.coordonnees.error-email'),
    telephone: !state.telephone.trim()
      ? required
      : validatePhone(state.telephone)
        ? null
        : t('tunnel.coordonnees.error-phone'),
  }
})

const inputs = {
  prenom: useTemplateRef<HTMLInputElement>('prenomInput'),
  nom: useTemplateRef<HTMLInputElement>('nomInput'),
  email: useTemplateRef<HTMLInputElement>('emailInput'),
  telephone: useTemplateRef<HTMLInputElement>('telephoneInput'),
}

function onSubmit() {
  submitAttempted.value = true
  const firstError = CONTACT_FIELDS.find((field) => errors.value[field])
  if (firstError) {
    const input = inputs[firstError].value
    input?.scrollIntoView({ behavior: 'smooth', block: 'center' })
    input?.focus({ preventScroll: true })
    return
  }
  // Envoi : brancher sur le modèle jeton + synchronisation (chantier back à venir).
  console.log('stay tuned')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.coordonnees {
  display: flex;
  justify-content: center;
  flex: 1;
  background: $surface-white;

  .column {
    display: flex;
    flex-direction: column;
    width: 100%;
    max-width: 30rem;
    padding: $spacing-m $section-padding-x $spacing-xl;

    .header {
      margin-bottom: $spacing-l;
    }

    .title {
      margin: 0;
      color: $text-primary;
      font-family: 'Cormorant Garamond', serif;
      font-size: clamp(1.75rem, 7vw, 2.25rem);
      font-weight: $font-weight-medium;
      line-height: 1.2;
    }

    .subtitle {
      margin: $spacing-xs 0 0;
      color: $text-secondary;
      font-size: $font-size-md;
      line-height: $line-height-normal;
    }

    // ── Synthèse ───────────────────────────────────────────────────────────────
    .summary {
      display: flex;
      flex-direction: column;
      gap: $spacing-s;
      margin-top: $spacing-l;
      padding: $spacing-m;
      border-radius: $radius-lg;
      background: $surface-soft;

      .summary-prestataire {
        display: flex;
        align-items: center;
        gap: $spacing-s;
        padding-bottom: $spacing-s;
        border-bottom: 1px solid $divider-color;

        .summary-avatar {
          flex-shrink: 0;
          width: 3.5rem;
          height: 3.5rem;
          border-radius: 50%;
          overflow: hidden;
        }

        .summary-text {
          display: flex;
          flex-direction: column;
          gap: 0.125rem;
          min-width: 0;

          .summary-name {
            color: $text-primary;
            font-size: $font-size-md;
            font-weight: $font-weight-bold;
          }

          .summary-category {
            color: $text-secondary;
            font-size: $font-size-xs;
          }
        }
      }

      .summary-event {
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        justify-content: space-between;
        gap: $spacing-s;

        .summary-info {
          display: inline-flex;
          align-items: center;
          gap: $spacing-xs;
          color: $text-primary;
          font-size: $font-size-xs;

          .icon {
            flex-shrink: 0;
            width: 1.375rem;
            height: 1.375rem;
          }

          .info-lines {
            display: flex;
            flex-direction: column;
          }
        }
      }
    }

    // ── Sections ───────────────────────────────────────────────────────────────
    .section {
      display: flex;
      flex-direction: column;
      gap: $spacing-s;
      margin-top: $spacing-l;

      .section-title {
        margin: 0;
        color: $text-primary;
        font-size: $font-size-xs;
        font-weight: $font-weight-semibold;
        letter-spacing: 0.14em;
        text-transform: uppercase;
      }

      .section-subtitle {
        margin: 0;
        color: $text-secondary;
        font-size: $font-size-sm;
        line-height: $line-height-normal;
      }

      .name-row {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: $spacing-s;
      }

      .field-group {
        display: flex;
        flex-direction: column;
        gap: $spacing-xs;
        min-width: 0;

        .field-label {
          color: $text-primary;
          font-size: $font-size-sm;
          font-weight: $font-weight-semibold;

          .required {
            margin-left: 0.25rem;
            color: $state-error;
          }
        }

        .field {
          display: flex;
          align-items: center;
          gap: $spacing-s;
          height: 3.25rem;
          padding: 0 $spacing-m;
          border: 1.5px solid $divider-color;
          border-radius: $radius-lg;
          background: $surface-white;
          transition: border-color 180ms ease;

          &:focus-within {
            border-color: $brand-accent;
          }

          &.invalid {
            border-color: $state-error;
          }

          .icon {
            flex-shrink: 0;
            width: 1.25rem;
            height: 1.25rem;
            color: $text-primary;
          }

          .input {
            flex: 1;
            min-width: 0;
            height: 100%;
            border: none;
            background: transparent;
            color: $text-primary;
            font-family: inherit;
            font-size: $font-size-sm;
            outline: none;

            &::placeholder {
              color: $text-secondary;
              opacity: 0.6;
            }
          }
        }

        .error {
          color: $state-error;
          font-size: $font-size-xs;
        }
      }

      .message-field {
        position: relative;
        display: flex;
        gap: $spacing-s;
        padding: $spacing-m $spacing-m 2rem;
        border: 1.5px solid $divider-color;
        border-radius: $radius-lg;
        background: $surface-white;
        transition: border-color 180ms ease;

        &:focus-within {
          border-color: $brand-accent;
        }

        .icon {
          flex-shrink: 0;
          width: 1.25rem;
          height: 1.25rem;
          color: $text-primary;
        }

        .textarea {
          flex: 1;
          min-height: 6rem;
          border: none;
          background: transparent;
          color: $text-primary;
          font-family: inherit;
          font-size: $font-size-sm;
          line-height: $line-height-normal;
          resize: none;
          outline: none;

          &::placeholder {
            color: $text-secondary;
            opacity: 0.6;
          }
        }

        .counter {
          position: absolute;
          right: $spacing-m;
          bottom: $spacing-s;
          color: $text-secondary;
          font-size: $font-size-xs;
          pointer-events: none;
        }
      }
    }

    // ── Actions ────────────────────────────────────────────────────────────────
    .cta {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: $spacing-xs;
      width: 100%;
      height: 3.25rem;
      border-radius: 9999px;
      font-family: inherit;
      cursor: pointer;

      &:focus-visible {
        outline: 3px solid $brand-primary;
        outline-offset: 4px;
      }

      .icon {
        width: 1.25rem;
        height: 1.25rem;
      }

      // Même bouton que le CTA du tunnel /organisation.
      &.primary {
        margin-top: $spacing-l;
        border: none;
        background: $brand-accent;
        color: $brand-primary;
        font-size: $font-size-md;
        font-weight: $font-weight-bold;
        box-shadow: 0 0.25rem 0.625rem rgba($brand-primary, 0.18);
      }

      &.secondary {
        margin-top: $spacing-s;
        border: 1.5px solid $divider-color;
        background: $surface-white;
        color: $text-primary;
        font-size: $font-size-sm;
      }
    }
  }
}
</style>
