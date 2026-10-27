<!--
  Bloc d'achat de billets de la fiche événement (/e/[slug]), selon l'état de la billetterie : pas
  encore ouverte, ouverte (accès à la commande), complète ou fermée. Le statut vient du back ;
  « complet » en est déduit (billetterie ouverte sans place restante). Hors billetterie ouverte,
  pas de bouton : aucune action possible, un message explique pourquoi. La quantité se choisit sur
  l'écran de commande, pas ici. Absent quand l'événement n'a pas de billetterie (géré par la fiche).
-->
<template>
  <section class="purchase-card">
    <div class="price-row">
      <div v-if="state !== 'CLOSED'" class="price">
        <span class="amount">{{ unitPriceLabel }}</span>
        <span class="unit">{{ $t('ticketing.event.per-ticket') }}</span>
      </div>
      <span class="status-badge" :class="stateKey">
        <span class="dot" aria-hidden="true" />
        {{ $t(`ticketing.event.status.${stateKey}`) }}
      </span>
    </div>

    <template v-if="state === 'OPEN'">
      <button type="button" class="cta" @click="onBuyClick">
        <TicketIcon class="icon" />
        {{ $t('ticketing.event.cta') }}
        <ArrowRightSIcon class="chevron" aria-hidden="true" />
      </button>

      <p class="reassurance">
        <LockIcon class="icon" aria-hidden="true" />
        {{ $t('ticketing.event.reassurance') }} <span class="brand">Sgilt</span>
      </p>
    </template>

    <p v-else class="status-message">{{ $t(`ticketing.event.status-message.${stateKey}`) }}</p>
  </section>
</template>

<script setup lang="ts">
import { TicketIcon, ArrowRightSIcon, LockIcon } from '@remixicons/vue/line'
import type { PublicEventTicketing } from '~/data/ticketing/domain/PublicEvent'
import { useTicketOrder } from '~/composables/useTicketOrder'
import { isSoldOut } from '~/utils/ticketingUtils'

const props = defineProps<{ slug: string; ticketing: PublicEventTicketing }>()

type PurchaseState = 'NOT_OPEN' | 'OPEN' | 'SOLD_OUT' | 'CLOSED'

// Suffixe des clés i18n et classe du badge, par état.
const STATE_KEYS: Record<PurchaseState, string> = {
  NOT_OPEN: 'not-open',
  OPEN: 'open',
  SOLD_OUT: 'sold-out',
  CLOSED: 'closed',
}

const state = computed<PurchaseState>(() =>
  isSoldOut(props.ticketing) ? 'SOLD_OUT' : props.ticketing.status,
)
const stateKey = computed(() => STATE_KEYS[state.value])

const { buy, unitPriceLabel } = useTicketOrder(props.slug, props.ticketing)

// Reprend la commande en cours de l'événement, ou en démarre une nouvelle (cf. useTicketOrder).
function onBuyClick(): void {
  buy()
  navigateTo(`/e/${props.slug}/commande`)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.purchase-card {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;

  .price-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $spacing-s;

    .price {
      display: flex;
      align-items: baseline;
      gap: 0.4rem;

      .amount {
        font-size: $font-size-2xl;
        font-weight: $font-weight-bold;
        color: $text-primary;
      }

      .unit {
        font-size: $font-size-sm;
        color: $text-secondary;
      }
    }

    .status-badge {
      display: inline-flex;
      align-items: center;
      gap: 0.4rem;
      padding: 0.35rem 0.8rem;
      border-radius: 2rem;
      font-size: $font-size-xs;
      font-weight: $font-weight-semibold;
      white-space: nowrap;

      .dot {
        width: 0.375rem;
        height: 0.375rem;
        border-radius: 50%;
        background: currentColor;
        flex-shrink: 0;
      }

      &.not-open {
        background: rgba($state-warning, 0.1);
        color: $state-warning;
      }

      &.open {
        background: rgba($state-available, 0.1);
        color: $state-available;
      }

      &.sold-out {
        background: rgba($state-danger, 0.1);
        color: $state-danger;
      }

      &.closed {
        background: rgba($text-secondary, 0.1);
        color: $text-secondary;
      }
    }
  }

  .cta {
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

    .icon,
    .chevron {
      width: 1.1rem;
      height: 1.1rem;
    }
  }

  .reassurance {
    margin: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $spacing-xs;
    color: $text-secondary;
    font-size: $font-size-xs;

    .icon {
      width: 0.9rem;
      height: 0.9rem;
      flex-shrink: 0;
    }

    .brand {
      text-decoration: underline;
      font-weight: $font-weight-medium;
    }
  }

  .status-message {
    margin: 0;
    font-size: $font-size-md;
    color: $text-secondary;
  }
}
</style>
