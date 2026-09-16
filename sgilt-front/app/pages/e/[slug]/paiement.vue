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

      <button type="button" class="submit-cta" @click="onPay">
        <LockIcon class="icon" />
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
import { mockEvent } from '~/data/ticketing/mockEvent'

const { t } = useI18n()
const route = useRoute()
const slug = route.params.slug as string

const event = mockEvent

useHead({ title: `${t('ticketing.payment.title')} · ${event.title} · Sgilt` })

// ── Récapitulatif ─────────────────────────────────────────────────────────────
// Repris depuis la page commande (query `qty`) — pas de champ dédié tant qu'il n'y a pas de
// panier persisté côté back.
const MIN_QUANTITY = 1

const initialQty = Number(route.query.qty)
const quantity =
  Number.isInteger(initialQty) && initialQty >= MIN_QUANTITY && initialQty <= event.remainingTickets
    ? initialQty
    : MIN_QUANTITY

const unitPriceLabel = computed(() => `${event.unitPrice} €`)
const totalLabel = computed(() => `${event.unitPrice * quantity} €`)
const ticketsDetailLabel = computed(() => `${quantity} × ${unitPriceLabel.value}`)

// ── Paiement ──────────────────────────────────────────────────────────────────
// Le bouton reste toujours cliquable (jamais `disabled`) : une saisie incomplète se traduit par
// une erreur explicite au clic plutôt qu'un blocage silencieux. Le widget Lemonway n'étant pas
// encore intégré, le clic ne fait que valider les CGV — pas de paiement réel à déclencher.
const acceptCgv = ref(false)
const cgvError = ref<string | null>(null)
const payError = ref<string | null>(null)

function onPay(): void {
  cgvError.value = acceptCgv.value ? null : t('ticketing.order.error-cgv')
  if (cgvError.value) {
    payError.value = t('ticketing.order.error-form')
    return
  }
  payError.value = null
  console.log(`Paiement de ${totalLabel.value} pour "${event.title}" (slug=${slug})`)
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

.reassurance {
  margin: 0;
  text-align: center;
  font-size: $font-size-sm;
  color: $text-secondary;
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
