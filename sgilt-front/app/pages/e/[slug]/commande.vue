<!--
  Étape 1 du parcours d'achat (/e/[slug]/commande) : quantité, coordonnées de l'acheteur et
  acceptation des CGV, puis passage au paiement.
-->
<template>
  <div class="order-page">
    <TicketingOrderHeader :title="$t('ticketing.order.title')" />

    <TicketingEventRecap :event="event" />

    <section class="quantity-section">
      <div class="quantity-header">
        <span class="label">{{ $t('ticketing.order.quantity-label') }}</span>
        <span class="unit-price"
          ><strong>{{ unitPriceLabel }}</strong> {{ $t('ticketing.event.per-ticket') }}</span
        >
      </div>
      <TicketingQuantityStepper
        :quantity="quantity"
        large
        @decrement="decrement"
        @increment="increment"
      />
      <div class="total-row">
        <span class="label">{{ $t('ticketing.event.total-label') }}</span>
        <span class="value">{{ totalLabel }}</span>
      </div>
    </section>

    <section class="info-section">
      <h2 class="section-title">{{ $t('ticketing.order.info-title') }}</h2>
      <p class="section-subtitle">{{ $t('ticketing.order.info-subtitle') }}</p>

      <div class="name-row">
        <div class="field">
          <label class="label" for="firstName"
            >{{ $t('ticketing.order.first-name') }} <span class="required">*</span></label
          >
          <input
            id="firstName"
            v-model="buyer.firstName"
            type="text"
            class="input"
            :class="{ 'input-error': errors.firstName }"
            autocomplete="given-name"
          />
          <p v-if="errors.firstName" class="field-error">{{ errors.firstName }}</p>
        </div>
        <div class="field">
          <label class="label" for="lastName"
            >{{ $t('ticketing.order.last-name') }} <span class="required">*</span></label
          >
          <input
            id="lastName"
            v-model="buyer.lastName"
            type="text"
            class="input"
            :class="{ 'input-error': errors.lastName }"
            autocomplete="family-name"
          />
          <p v-if="errors.lastName" class="field-error">{{ errors.lastName }}</p>
        </div>
      </div>

      <div class="field">
        <label class="label" for="email"
          >{{ $t('ticketing.order.email') }} <span class="required">*</span></label
        >
        <input
          id="email"
          v-model="buyer.email"
          type="email"
          class="input"
          :class="{ 'input-error': errors.email }"
          autocomplete="email"
        />
        <p v-if="errors.email" class="field-error">{{ errors.email }}</p>
      </div>

      <div class="email-notice">
        <TicketIcon class="icon" />
        <div>
          <p class="notice-title">{{ $t('ticketing.order.email-notice-title') }}</p>
          <p class="notice-sub">{{ $t('ticketing.order.email-notice-sub') }}</p>
        </div>
      </div>

      <div class="important-info">
        <InformationIcon class="icon" />
        <div>
          <p class="important-title">{{ $t('ticketing.order.important-info-title') }}</p>
          <ul>
            <li v-for="line in event.importantInfo" :key="line">{{ line }}</li>
          </ul>
        </div>
      </div>

      <TicketingCgvCheckbox v-model="acceptCgv" :error="errors.cgv" />

      <button type="button" class="submit-cta" @click="onSubmit">
        <LockIcon class="icon" />
        {{ $t('ticketing.order.submit') }}
        <ArrowRightSIcon class="chevron" aria-hidden="true" />
      </button>

      <Transition name="fade">
        <p v-if="submitError" class="submit-error">{{ submitError }}</p>
      </Transition>

      <p class="contact-link">
        {{ $t('ticketing.order.question') }}
        <NuxtLink to="/m/nous-contacter">{{ $t('ticketing.order.contact-us') }}</NuxtLink>
      </p>
    </section>
  </div>
</template>

<script setup lang="ts">
import TicketingOrderHeader from '~/components/ticketing/TicketingOrderHeader.vue'
import TicketingEventRecap from '~/components/ticketing/TicketingEventRecap.vue'
import TicketingCgvCheckbox from '~/components/ticketing/TicketingCgvCheckbox.vue'
import TicketingQuantityStepper from '~/components/ticketing/TicketingQuantityStepper.vue'
import { TicketIcon, InformationIcon, ArrowRightSIcon, LockIcon } from '@remixicons/vue/line'
import { mockEvent } from '~/data/ticketing/mockEvent'
import { useTicketOrder } from '~/composables/useTicketOrder'

const { t } = useI18n()
const route = useRoute()
const slug = route.params.slug as string

const event = mockEvent

useHead({ title: `${t('ticketing.order.title')} · ${event.title} · Sgilt` })

// ── Sélecteur de quantité ─────────────────────────────────────────────────────
const { quantity, buyer, decrement, increment, unitPriceLabel, totalLabel } = useTicketOrder(
  slug,
  event.unitPrice,
  event.remainingTickets,
)

// ── Formulaire ────────────────────────────────────────────────────────────────
const acceptCgv = ref(false)

const errors = reactive({
  lastName: '',
  firstName: '',
  email: '',
  cgv: '',
})

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

function validate(): boolean {
  errors.lastName = buyer.lastName.trim() ? '' : t('ticketing.order.error-required')
  errors.firstName = buyer.firstName.trim() ? '' : t('ticketing.order.error-required')
  errors.email = EMAIL_PATTERN.test(buyer.email) ? '' : t('ticketing.order.error-email')
  errors.cgv = acceptCgv.value ? '' : t('ticketing.order.error-cgv')
  return !errors.lastName && !errors.firstName && !errors.email && !errors.cgv
}

// Le bouton reste toujours cliquable (jamais `disabled`) : une saisie incomplète se traduit par
// une erreur explicite au clic plutôt qu'un blocage silencieux.
const submitError = ref<string | null>(null)

function onSubmit(): void {
  if (!validate()) {
    submitError.value = t('ticketing.order.error-form')
    return
  }
  submitError.value = null
  console.log(`Commande de ${quantity.value} billet(s) pour "${event.title}" (slug=${slug})`, {
    ...buyer,
  })
  navigateTo(`/e/${slug}/paiement`)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.order-page {
  width: 100%;
  max-width: 640px;
  margin: 0 auto;
  padding: $spacing-m;
  padding-bottom: $spacing-xxl;
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
}

// ─── Quantité + total ───────────────────────────────────────────────────────
.quantity-section {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding-bottom: $spacing-l;
  border-bottom: 1px solid $divider-color;
}

.quantity-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;

  .label {
    font-size: $font-size-lg;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }

  .unit-price {
    font-size: $font-size-sm;
    color: $text-secondary;

    strong {
      font-weight: $font-weight-bold;
      color: $text-primary;
    }
  }
}

.quantity-stepper {
  align-self: center;
}

.total-row {
  display: flex;
  align-items: center;
  justify-content: space-between;

  .label {
    font-size: $font-size-md;
    font-weight: $font-weight-medium;
    color: $text-primary;
  }

  .value {
    font-size: $font-size-xl;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }
}

// ─── Informations ────────────────────────────────────────────────────────────
.info-section {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}

.section-title {
  margin: 0;
  font-size: $font-size-lg;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.section-subtitle {
  margin: -$spacing-s 0 0;
  font-size: $font-size-sm;
  color: $text-secondary;
}

.name-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: $spacing-m;

  @media (max-width: 420px) {
    grid-template-columns: 1fr;
  }
}

.field {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
}

.label {
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  color: $text-primary;

  .required {
    color: $state-error;
  }
}

.input {
  width: 100%;
  padding: $spacing-s $spacing-m;
  border: 1px solid $divider-color;
  border-radius: $radius-sm;
  font-family: inherit;
  font-size: $font-size-md;
  color: $text-primary;
  background: $surface-white;
  outline: none;
  box-sizing: border-box;
  transition:
    border-color 150ms ease,
    box-shadow 150ms ease;

  &:focus {
    border-color: $input-focus-border-color;
    box-shadow: $input-focus-box-shadow;
  }

  &.input-error {
    border-color: $state-error;
  }
}

.field-error {
  margin: 0;
  font-size: $font-size-xs;
  color: $state-error;
}

// ─── Bandeau e-mail ──────────────────────────────────────────────────────────
.email-notice {
  display: flex;
  align-items: flex-start;
  gap: $spacing-s;
  padding: $spacing-m;
  background: rgba($state-success, 0.08);
  border-radius: $radius-md;
  color: $state-success;

  .icon {
    flex-shrink: 0;
    width: 1.25rem;
    height: 1.25rem;
    margin-top: 0.1rem;
  }

  .notice-title {
    margin: 0;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    color: $text-primary;
  }

  .notice-sub {
    margin: 0;
    font-size: $font-size-xs;
    color: $text-secondary;
  }
}

// ─── Informations importantes ────────────────────────────────────────────────
.important-info {
  display: flex;
  align-items: flex-start;
  gap: $spacing-s;
  padding: $spacing-m;
  background: $surface-soft;
  border-radius: $radius-md;

  .icon {
    flex-shrink: 0;
    width: 1.25rem;
    height: 1.25rem;
    color: $text-secondary;
    margin-top: 0.1rem;
  }

  .important-title {
    margin: 0 0 0.3rem;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    color: $text-primary;
  }

  ul {
    margin: 0;
    padding-left: 1.1rem;
    display: flex;
    flex-direction: column;
    gap: 0.2rem;
  }

  li {
    font-size: $font-size-sm;
    color: $text-secondary;
  }
}

// ─── CTA ─────────────────────────────────────────────────────────────────────
.submit-cta {
  width: 100%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5em;
  height: 3.25rem;
  border: none;
  border-radius: 9999px;
  background: $brand-accent;
  color: $brand-primary;
  font-family: inherit;
  font-size: $font-size-md;
  font-weight: $font-weight-bold;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba($brand-primary, 0.18);
  transition:
    transform 160ms ease,
    box-shadow 160ms ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba($brand-primary, 0.22);
  }

  &:focus-visible {
    outline: 3px solid $brand-primary;
    outline-offset: 4px;
  }

  .icon {
    width: 1.1rem;
    height: 1.1rem;
  }

  .chevron {
    width: 1.1rem;
    height: 1.1rem;
  }
}

.submit-error {
  margin: -$spacing-xs 0 0;
  font-size: $font-size-sm;
  color: $state-error;
  text-align: center;
}

.contact-link {
  margin: 0;
  text-align: center;
  font-size: $font-size-sm;
  color: $text-secondary;

  a {
    color: $text-primary;
    text-decoration: underline;
    font-weight: $font-weight-medium;
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 200ms ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
