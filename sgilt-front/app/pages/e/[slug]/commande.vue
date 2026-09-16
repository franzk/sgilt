<template>
  <div class="order-page">
    <header class="order-header">
      <button type="button" class="back-btn" @click="router.back()">
        <ArrowLeftSIcon class="icon" />
        {{ $t('ticketing.order.back') }}
      </button>
      <h1 class="title">{{ $t('ticketing.order.title') }}</h1>
      <span class="secure-badge">
        <LockIcon class="icon" />
        {{ $t('ticketing.order.secure-payment') }}
      </span>
    </header>

    <section class="recap-card">
      <SgiltImage class="thumb" :src="event.heroImage" :alt="event.title" />
      <div class="info">
        <p class="event-title">{{ event.title }}</p>
        <p class="event-subtitle">{{ event.subtitle }}</p>
        <div class="meta-row">
          <CalendarEventIcon class="icon" />
          <span>{{ event.dateLabel }} · {{ event.time }}</span>
        </div>
        <div class="meta-row">
          <MapPin2Icon class="icon" />
          <span>{{ event.venue }} · {{ event.city }}</span>
        </div>
        <div class="meta-row">
          <TeamIcon class="icon" />
          <span>{{ event.audienceLabel }}</span>
        </div>
      </div>
    </section>

    <section class="quantity-section">
      <div class="quantity-header">
        <span class="label">{{ $t('ticketing.order.quantity-label') }}</span>
        <span class="unit-price"
          ><strong>{{ unitPriceLabel }}</strong> {{ $t('ticketing.event.per-ticket') }}</span
        >
      </div>
      <div class="stepper">
        <button
          type="button"
          class="step-btn"
          :aria-label="$t('ticketing.event.decrement-aria')"
          @click="decrement"
        >
          <SubtractIcon />
        </button>
        <span class="value">{{ quantity }}</span>
        <button
          type="button"
          class="step-btn"
          :aria-label="$t('ticketing.event.increment-aria')"
          @click="increment"
        >
          <AddIcon />
        </button>
      </div>
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
          <label class="label" for="lastName"
            >{{ $t('ticketing.order.last-name') }} <span class="required">*</span></label
          >
          <input
            id="lastName"
            v-model="form.lastName"
            type="text"
            class="input"
            :class="{ 'input-error': errors.lastName }"
            autocomplete="family-name"
          />
          <p v-if="errors.lastName" class="field-error">{{ errors.lastName }}</p>
        </div>
        <div class="field">
          <label class="label" for="firstName"
            >{{ $t('ticketing.order.first-name') }} <span class="required">*</span></label
          >
          <input
            id="firstName"
            v-model="form.firstName"
            type="text"
            class="input"
            :class="{ 'input-error': errors.firstName }"
            autocomplete="given-name"
          />
          <p v-if="errors.firstName" class="field-error">{{ errors.firstName }}</p>
        </div>
      </div>

      <div class="field">
        <label class="label" for="email"
          >{{ $t('ticketing.order.email') }} <span class="required">*</span></label
        >
        <input
          id="email"
          v-model="form.email"
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

      <label class="cgv-row">
        <input v-model="form.acceptCgv" type="checkbox" class="cgv-checkbox" />
        <span class="cgv-label">
          {{ $t('ticketing.order.cgv-accept') }}
          <NuxtLink to="/m/cgu" target="_blank" class="cgv-link" @click.stop>{{
            $t('ticketing.order.cgv-link')
          }}</NuxtLink>
          <span class="required">*</span>
        </span>
      </label>
      <p v-if="errors.cgv" class="field-error">{{ errors.cgv }}</p>

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
import SgiltImage from '~/components/basics/media/SgiltImage.vue'
import {
  CalendarEventIcon,
  MapPin2Icon,
  TeamIcon,
  TicketIcon,
  InformationIcon,
  ArrowLeftSIcon,
  ArrowRightSIcon,
  LockIcon,
  AddIcon,
  SubtractIcon,
} from '@remixicons/vue/line'
import { mockEvent } from '~/data/ticketing/mockEvent'

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const slug = route.params.slug as string

const event = mockEvent

useHead({ title: `${t('ticketing.order.title')} · ${event.title} · Sgilt` })

// ── Sélecteur de quantité ─────────────────────────────────────────────────────
// Repris depuis la fiche événement (query `qty`) pour ne pas faire ressaisir la quantité choisie.
// Plafonné par le nombre de places restantes (domaine), pas une limite UI arbitraire.
const MIN_QUANTITY = 1

const initialQty = Number(route.query.qty)
const quantity = ref(
  Number.isInteger(initialQty) && initialQty >= MIN_QUANTITY && initialQty <= event.remainingTickets
    ? initialQty
    : MIN_QUANTITY,
)

function decrement(): void {
  quantity.value = Math.max(MIN_QUANTITY, quantity.value - 1)
}

function increment(): void {
  quantity.value = Math.min(event.remainingTickets, quantity.value + 1)
}

const unitPriceLabel = computed(() => `${event.unitPrice} €`)
const totalLabel = computed(() => `${event.unitPrice * quantity.value} €`)

// ── Formulaire ────────────────────────────────────────────────────────────────
const form = reactive({
  lastName: '',
  firstName: '',
  email: '',
  acceptCgv: false,
})

const errors = reactive({
  lastName: '',
  firstName: '',
  email: '',
  cgv: '',
})

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

function validate(): boolean {
  errors.lastName = form.lastName.trim() ? '' : t('ticketing.order.error-required')
  errors.firstName = form.firstName.trim() ? '' : t('ticketing.order.error-required')
  errors.email = EMAIL_PATTERN.test(form.email) ? '' : t('ticketing.order.error-email')
  errors.cgv = form.acceptCgv ? '' : t('ticketing.order.error-cgv')
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
    ...form,
  })
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

// ─── Header ───────────────────────────────────────────────────────────────────
.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-s;

  .back-btn {
    display: inline-flex;
    align-items: center;
    gap: 0.2rem;
    padding: 0;
    border: none;
    background: none;
    font-family: inherit;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    color: $text-primary;
    cursor: pointer;
    flex-shrink: 0;

    .icon {
      width: 1.25rem;
      height: 1.25rem;
    }
  }

  .title {
    margin: 0;
    font-size: $font-size-md;
    font-weight: $font-weight-bold;
    color: $text-primary;
    text-align: center;
  }

  .secure-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.3rem;
    flex-shrink: 0;
    font-size: $font-size-xs;
    color: $text-secondary;
    white-space: nowrap;

    .icon {
      width: 0.9rem;
      height: 0.9rem;
    }
  }
}

// ─── Récap événement ─────────────────────────────────────────────────────────
.recap-card {
  display: flex;
  gap: $spacing-m;
  padding: $spacing-m;
  background: $surface-soft;
  border-radius: $radius-lg;

  .thumb {
    flex-shrink: 0;
    width: 5rem;
    height: 5rem;
    border-radius: $radius-md;
    overflow: hidden;
  }

  .info {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 0.2rem;
  }

  .event-title {
    margin: 0;
    font-size: $font-size-lg;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }

  .event-subtitle {
    margin: 0 0 0.2rem;
    font-size: $font-size-sm;
    color: $text-secondary;
  }

  .meta-row {
    display: flex;
    align-items: center;
    gap: 0.4rem;
    font-size: $font-size-sm;
    color: $text-primary;

    .icon {
      flex-shrink: 0;
      width: 1rem;
      height: 1rem;
      color: $text-secondary;
    }
  }
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

.stepper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-xl;
}

.step-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2.75rem;
  height: 2.75rem;
  border: none;
  border-radius: 50%;
  background: $surface-soft;
  color: $text-primary;
  cursor: pointer;
  transition: background 150ms ease;

  &:hover {
    background: $brand-subtle;
  }

  svg {
    width: 1.1rem;
    height: 1.1rem;
  }
}

.stepper .value {
  min-width: 2rem;
  text-align: center;
  font-size: $font-size-2xl;
  font-weight: $font-weight-bold;
  color: $text-primary;
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

// ─── CGV ─────────────────────────────────────────────────────────────────────
.cgv-row {
  display: flex;
  align-items: flex-start;
  gap: $spacing-s;
  cursor: pointer;
}

.cgv-checkbox {
  flex-shrink: 0;
  appearance: none;
  display: grid;
  place-content: center;
  width: 1.5rem;
  height: 1.5rem;
  margin: 0;
  border: 1.5px solid $divider-color;
  border-radius: $radius-sm;
  background: $surface-white;
  cursor: pointer;
  transition:
    background 150ms ease,
    border-color 150ms ease;

  &::after {
    content: '';
    width: 0.4rem;
    height: 0.75rem;
    border: solid $brand-primary;
    border-width: 0 2px 2px 0;
    transform: rotate(45deg) translate(-1px, -2px);
    opacity: 0;
  }

  &:checked {
    background: $brand-accent;
    border-color: $brand-accent;

    &::after {
      opacity: 1;
    }
  }
}

.cgv-label {
  font-size: $font-size-sm;
  color: $text-primary;

  .required {
    color: $state-error;
  }
}

.cgv-link {
  color: $text-primary;
  text-decoration: underline;
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
