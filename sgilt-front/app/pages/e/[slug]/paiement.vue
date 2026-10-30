<!--
  Étape 2 du parcours d'achat (/e/[slug]/paiement) : récapitulatif de la commande et paiement.
  « Payer » crée la commande côté back (blocage des places) juste avant le paiement. Le widget de
  paiement (Stripe) n'est pas encore intégré : la commande créée est considérée payée.
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

      <div class="widget-placeholder">
        <span class="widget-icon" aria-hidden="true"><BankCard2Icon /></span>
        <p class="widget-title">{{ $t('ticketing.payment.widget-title') }}</p>
        <p class="widget-subtitle">{{ $t('ticketing.payment.widget-subtitle') }}</p>
        <span class="widget-badge">{{ $t('ticketing.payment.widget-badge') }}</span>
      </div>

      <div class="security-notice">
        <InformationIcon class="icon" />
        <p>{{ $t('ticketing.payment.security-notice') }}</p>
      </div>

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

      <p class="reassurance">{{ $t('ticketing.payment.reassurance') }}</p>
    </section>
  </div>
</template>

<script setup lang="ts">
import TicketingOrderHeader from '~/components/ticketing/TicketingOrderHeader.vue'
import TicketingEventRecap from '~/components/ticketing/TicketingEventRecap.vue'
import TicketingCgvCheckbox from '~/components/ticketing/TicketingCgvCheckbox.vue'
import { BankCard2Icon, InformationIcon, ArrowRightSIcon, LockIcon } from '@remixicons/vue/line'
import { usePublicEvent } from '~/composables/usePublicEvent'
import { useTicketOrder } from '~/composables/useTicketOrder'
import { createCommande, toCommandeFailure } from '~/data/ticketing/service/commandeService'

definePageMeta({ middleware: 'ticketing' })

const { t } = useI18n()
const route = useRoute()
const slug = String(route.params.slug)
const event = await usePublicEvent(slug)
const { order, attachCommande, markPaid, unitPriceLabel, totalLabel } = useTicketOrder(
  slug,
  event.ticketing,
)

useHead({ title: `${t('ticketing.payment.title')} · ${event.title}` })

// ── Récapitulatif ─────────────────────────────────────────────────────────────
const ticketsDetailLabel = computed(() => `${order.value.quantity} × ${unitPriceLabel}`)

// ── Paiement ──────────────────────────────────────────────────────────────────
// Une saisie incomplète ne désactive pas le bouton : elle se traduit par une erreur explicite au
// clic. Seule la création de la commande le désactive, spinner à l'appui, pour ne pas en créer deux.
// Le widget de paiement n'étant pas encore intégré, la commande créée est aussitôt considérée
// payée ; le paiement Stripe viendra s'insérer entre sa création et markPaid.
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
  payError.value = null
  paying.value = true
  try {
    const commande = await createCommande(slug, order.value.quantity, order.value.buyer)
    attachCommande(commande.id)
    markPaid()
    await navigateTo(`/e/${slug}/confirmation`)
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

.widget-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-xs;
  padding: $spacing-xl $spacing-m;
  border: 1.5px dashed $divider-color;
  border-radius: $radius-lg;
  background: $surface-soft;
  text-align: center;

  .widget-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 3.5rem;
    height: 3.5rem;
    margin-bottom: $spacing-xs;
    border-radius: 50%;
    background: $surface-white;
    color: $text-primary;

    svg {
      width: 1.5rem;
      height: 1.5rem;
    }
  }

  .widget-title {
    margin: 0;
    font-size: $font-size-md;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }

  .widget-subtitle {
    margin: 0;
    font-size: $font-size-sm;
    color: $text-secondary;
  }

  .widget-badge {
    margin-top: $spacing-xs;
    padding: 0.35rem 0.9rem;
    border-radius: 2rem;
    background: $brand-accent;
    color: $brand-primary;
    font-size: $font-size-xs;
    font-weight: $font-weight-semibold;
  }
}

.security-notice {
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

  p {
    margin: 0;
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

.reassurance {
  margin: 0;
  text-align: center;
  font-size: $font-size-sm;
  color: $text-secondary;
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
