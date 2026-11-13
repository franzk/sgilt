<!--
  Étape 2 du parcours d'achat (/e/[slug]/paiement) : récapitulatif de la commande et paiement.
  Le widget de paiement est le Payment Element de Stripe, monté sans PaymentIntent (mode différé) :
  « Payer » valide la saisie, crée la commande côté back (blocage des places et PaymentIntent),
  puis confirme le paiement auprès de Stripe.
-->
<template>
  <div class="order-page">
    <TicketingOrderHeader :title="$t('ticketing.payment.title')" />

    <TicketingEventRecap :event="event" />

    <section class="summary-card">
      <h2 class="summary-title">{{ $t('ticketing.payment.summary-title') }}</h2>
      <div class="summary-row">
        <div class="summary-item">
          <span class="item-label">{{ $t('ticketing.payment.tickets-label') }}</span>
          <span class="item-detail">{{ ticketsDetailLabel }}</span>
        </div>
        <span class="item-value">{{ totalLabel }}</span>
      </div>
      <div class="total-row">
        <span class="label">{{ $t('ticketing.event.total-label') }}</span>
        <span class="value">{{ totalLabel }}</span>
      </div>
    </section>

    <section class="payment-section">
      <h2 class="section-title">{{ $t('ticketing.payment.method-title') }}</h2>
      <p class="section-subtitle">{{ $t('ticketing.payment.method-subtitle') }}</p>

      <div ref="paymentElementRef" class="payment-element" />

      <TicketingCgvCheckbox v-model="acceptCgv" :error="cgvError" />

      <button
        type="button"
        class="submit-cta"
        :disabled="paying"
        :aria-busy="paying"
        @click="onPay"
      >
        <span v-if="paying" class="spinner" aria-hidden="true" />
        <LockIcon v-else class="icon" />
        {{ $t('ticketing.payment.cta', { amount: totalLabel }) }}
        <ArrowRightSIcon class="chevron" aria-hidden="true" />
      </button>

      <Transition name="fade">
        <p v-if="payError" class="submit-error">{{ payError }}</p>
      </Transition>
    </section>
  </div>
</template>

<script setup lang="ts">
import TicketingOrderHeader from '~/components/ticketing/TicketingOrderHeader.vue'
import TicketingEventRecap from '~/components/ticketing/TicketingEventRecap.vue'
import TicketingCgvCheckbox from '~/components/ticketing/TicketingCgvCheckbox.vue'
import { ArrowRightSIcon, LockIcon } from '@remixicons/vue/line'
import { usePublicEvent } from '~/composables/usePublicEvent'
import { useTicketOrder } from '~/composables/useTicketOrder'
import { createCommande, toCommandeFailure } from '~/data/ticketing/service/commandeService'
import { loadStripe } from '@stripe/stripe-js'
import type { Stripe, StripeElements, StripeError } from '@stripe/stripe-js'

definePageMeta({ middleware: 'ticketing' })

const { t } = useI18n()
const route = useRoute()
const slug = String(route.params.slug)
const event = await usePublicEvent(slug)
const { order, attachCommande, markPaid, unitPriceLabel, totalCents, totalLabel } = useTicketOrder(
  slug,
  event.ticketing,
)

useHead({ title: `${t('ticketing.payment.title')} · ${event.title}` })

// ── Récapitulatif ─────────────────────────────────────────────────────────────
const ticketsDetailLabel = computed(() => `${order.value.quantity} × ${unitPriceLabel}`)

// ── Widget Stripe ─────────────────────────────────────────────────────────────
// Moyen de paiement accepté : la carte (Apple Pay et Google Pay en découlent, sur les appareils et
// domaines compatibles). Pas de Link, qui redemande les coordonnées de l'acheteur. Même liste que
// celle du PaymentIntent côté sgilt-pay.
const ALLOWED_PAYMENT_METHOD_TYPES = ['card']
const CURRENCY = 'eur'

const { stripePublishableKey } = useRuntimeConfig().public
const paymentElementRef = ref<HTMLElement | null>(null)
let stripe: Stripe | null = null
let elements: StripeElements | null = null

// Page en SPA : Stripe.js se charge et le widget se monte côté client seulement.
onMounted(async () => {
  try {
    stripe = await loadStripe(stripePublishableKey)
  } catch {
    stripe = null
  }
  if (!stripe || !paymentElementRef.value) {
    payError.value = t('ticketing.payment.errors.TECHNICAL')
    return
  }
  elements = stripe.elements({
    mode: 'payment',
    amount: totalCents.value,
    currency: CURRENCY,
    allowedPaymentMethodTypes: ALLOWED_PAYMENT_METHOD_TYPES,
    locale: 'fr',
  })
  // Link s'invite dans le formulaire carte même hors des moyens autorisés : masqué explicitement.
  elements.create('payment', { wallets: { link: 'never' } }).mount(paymentElementRef.value)
})

// Message d'une erreur Stripe : celui de Stripe (déjà en français) pour une saisie invalide ou une
// carte refusée, le message générique sinon.
function toStripeErrorMessage(error: StripeError): string {
  if ((error.type === 'card_error' || error.type === 'validation_error') && error.message) {
    return error.message
  }
  return t('ticketing.payment.errors.TECHNICAL')
}

// ── Paiement ──────────────────────────────────────────────────────────────────
// Une saisie incomplète ne désactive pas le bouton : elle se traduit par une erreur explicite au
// clic. Seul le paiement en cours le désactive, spinner à l'appui, pour ne pas payer deux fois.
// Un paiement refusé laisse la commande en attente : un nouvel essai en crée une autre, la
// précédente libère ses places à son expiration.
const acceptCgv = ref(false)
const cgvError = ref<string | null>(null)
const payError = ref<string | null>(null)
const paying = ref(false)

async function onPay(): Promise<void> {
  cgvError.value = acceptCgv.value ? null : t('ticketing.order.error-cgv')
  if (cgvError.value) {
    payError.value = t('ticketing.order.error-form')
    return
  }
  if (!stripe || !elements) {
    payError.value = t('ticketing.payment.errors.TECHNICAL')
    return
  }
  payError.value = null
  paying.value = true
  try {
    // Valide la saisie du widget avant de bloquer des places : les champs en erreur s'y signalent.
    const { error: submitError } = await elements.submit()
    if (submitError) {
      payError.value = toStripeErrorMessage(submitError)
      return
    }
    const commande = await createCommande(slug, order.value.quantity, order.value.buyer)
    attachCommande(commande.id)
    // Sans redirection pour les cartes (le 3D Secure s'ouvre dans une modale) : Stripe ne redirige
    // vers return_url que pour un moyen de paiement qui l'exige.
    const confirmationPath = `/e/${slug}/confirmation?commande=${commande.id}`
    const { error } = await stripe.confirmPayment({
      elements,
      clientSecret: commande.clientSecret,
      confirmParams: { return_url: `${window.location.origin}${confirmationPath}` },
      redirect: 'if_required',
    })
    if (error) {
      payError.value = toStripeErrorMessage(error)
      return
    }
    markPaid()
    await navigateTo(confirmationPath)
  } catch (e: unknown) {
    payError.value = t(`ticketing.payment.errors.${toCommandeFailure(e)}`)
  } finally {
    paying.value = false
  }
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

// ─── Récapitulatif commande ───────────────────────────────────────────────────
.summary-card {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}

.summary-title {
  margin: 0;
  font-size: $font-size-lg;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.summary-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: $spacing-s;
  padding-bottom: $spacing-m;
  border-bottom: 1px solid $divider-color;

  .summary-item {
    display: flex;
    flex-direction: column;
    gap: 0.2rem;
  }

  .item-label {
    font-size: $font-size-md;
    font-weight: $font-weight-medium;
    color: $text-primary;
  }

  .item-detail {
    font-size: $font-size-sm;
    color: $text-secondary;
  }

  .item-value {
    flex-shrink: 0;
    font-size: $font-size-lg;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }
}

.total-row {
  display: flex;
  align-items: center;
  justify-content: space-between;

  .label {
    font-size: $font-size-lg;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }

  .value {
    font-size: $font-size-2xl;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }
}

// ─── Moyen de paiement ────────────────────────────────────────────────────────
.payment-section {
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

.payment-element {
  min-height: 12rem;
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

  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba($brand-primary, 0.22);
  }

  &:focus-visible {
    outline: 3px solid $brand-primary;
    outline-offset: 4px;
  }

  &:disabled {
    cursor: progress;
  }

  .icon {
    width: 1.1rem;
    height: 1.1rem;
  }

  .spinner {
    width: 1.1rem;
    height: 1.1rem;
    border-radius: 50%;
    border: 2px solid currentColor;
    border-top-color: transparent;
    animation: spin 0.6s linear infinite;
    flex-shrink: 0;
    box-sizing: border-box;
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

@keyframes spin {
  to {
    transform: rotate(360deg);
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
