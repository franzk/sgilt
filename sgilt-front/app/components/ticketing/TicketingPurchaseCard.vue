<!--
  Bloc d'achat de billets de la fiche événement (/e/[slug]), selon l'état de la billetterie : pas
  encore ouverte, ouverte (accès à la commande), complète ou fermée. Le statut vient du back ;
  « complet » en est déduit (billetterie ouverte sans place restante). Hors billetterie ouverte,
  pas de bouton : aucune action possible, un message explique pourquoi. La quantité se choisit sur
  l'écran de commande, pas ici. Absent quand l'événement n'a pas de billetterie (géré par la fiche).
  Deux rendus : carte en colonne sur desktop ; sur mobile, barre compacte fixée en bas de l'écran
  par PublicEventView (billetterie ouverte : prix à gauche, bouton à droite, sans badge ; sinon
  prix + badge, puis le message).
-->
<template>
  <section class="purchase-card" :class="stateKey">
    <div class="price-row">
      <div v-if="state !== 'CLOSED'" class="price">
        <span class="amount">{{ unitPriceLabel }}</span>
        <span class="details">
          <span class="unit">{{ $t('ticketing.event.per-ticket') }}</span>
          <span v-if="ticketing.priceNote" class="note">{{ ticketing.priceNote }}</span>
        </span>
      </div>
      <span class="status-badge" :class="stateKey">
        <span class="dot" aria-hidden="true" />
        {{ $t(`ticketing.event.status.${stateKey}`) }}
      </span>
    </div>

    <button v-if="state === 'OPEN'" type="button" class="cta" @click="onBuyClick">
      {{ $t('ticketing.event.cta') }}
      <ArrowRightIcon class="icon" aria-hidden="true" />
    </button>

    <p v-else class="status-message">{{ $t(`ticketing.event.status-message.${stateKey}`) }}</p>
  </section>
</template>

<script setup lang="ts">
import { ArrowRightIcon } from '@remixicons/vue/line'
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

// Mobile d'abord : barre compacte (cf. PublicEventView) ; desktop : carte en colonne.
.purchase-card {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  @media (min-width: $breakpoint-desktop) {
    gap: $spacing-m;
  }

  // Billetterie ouverte sur mobile : prix à gauche, bouton à droite, sur une ligne.
  &.open {
    flex-direction: row;
    align-items: center;
    gap: $spacing-m;

    .price-row {
      flex: 1;
      min-width: 0;
      justify-content: center;

      // Bloc prix centré dans la zone à gauche du bouton : montant, « par place », puis la
      // mention de prix (ex. « tout compris »), une ligne chacun.
      .price {
        align-items: center;
        text-align: center;

        .details {
          flex-direction: column;
          align-items: center;

          .note::before {
            content: none;
          }
        }
      }

      .status-badge {
        display: none;
      }
    }

    @media (min-width: $breakpoint-desktop) {
      flex-direction: column;
      align-items: stretch;
      gap: $spacing-m;

      .price-row {
        flex: none;
        justify-content: space-between;

        .price {
          align-items: flex-start;
          text-align: left;

          .details {
            flex-direction: row;
            align-items: baseline;

            .note::before {
              content: '·';
            }
          }
        }

        .status-badge {
          display: inline-flex;
        }
      }

    }
  }

  // Badge aligné sur la ligne du montant, pas centré sur tout le bloc prix.
  .price-row {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: $spacing-s;

    // Montant seul sur sa ligne, puis « par place · tout compris » en dessous.
    .price {
      display: flex;
      flex-direction: column;
      min-width: 0;

      .details {
        display: flex;
        flex-wrap: wrap;
        align-items: baseline;
        gap: 0 0.3rem;

        .unit {
          white-space: nowrap;
        }

        .note::before {
          content: '·';
          margin-right: 0.3rem;
        }
      }

      .amount {
        font-size: $font-size-xl;
        font-weight: $font-weight-bold;
        line-height: 1.2;
        color: $text-primary;

        @media (min-width: $breakpoint-desktop) {
          font-size: $font-size-2xl;
        }
      }

      .unit,
      .note {
        font-size: $font-size-xs;
        color: $text-secondary;

        @media (min-width: $breakpoint-desktop) {
          font-size: $font-size-sm;
        }
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

      // Centré sur la ligne du montant (plus haute sur desktop).
      @media (min-width: $breakpoint-desktop) {
        margin-top: 0.3rem;
      }

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
    flex-shrink: 0;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 0.5em;
    height: 3rem;
    padding: 0 $spacing-l;
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

    @media (min-width: $breakpoint-desktop) {
      width: 100%;
      height: 3.25rem;
    }

    .icon {
      width: 1.1rem;
      height: 1.1rem;
    }
  }

  .status-message {
    margin: 0;
    font-size: $font-size-sm;
    color: $text-secondary;

    @media (min-width: $breakpoint-desktop) {
      font-size: $font-size-md;
    }
  }
}
</style>
