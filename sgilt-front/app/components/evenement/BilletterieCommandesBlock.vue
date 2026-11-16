<!--
  Contenu de l'onglet Commandes de l'écran Billetterie du board, et de l'onglet Récap du scan des
  e-billets : toutes les commandes de la billetterie, les plus récentes d'abord (ordre du back),
  filtrables (Toutes, Payées, Entrées, Annulées, Paiement en cours, Abandonnées, chacun avec son
  nombre), Payées par défaut. Entrées : les commandes dont l'e-billet a été scanné à l'entrée. Une
  carte par commande : acheteur, email, nombre de places, montant, date et heure, badge de statut,
  et l'heure d'entrée si l'e-billet a été scanné. Lecture seule.
-->
<template>
  <section class="commandes-block">
    <Sk v-if="loading" class="skeleton" />

    <div v-else-if="loadFailed" class="load-error" role="alert">
      <p>{{ $t('evenement.billetterie.commandes.errors.LOAD') }}</p>
      <SgiltButton variant="secondary" @click="$emit('retry')">{{ $t('evenement.billetterie.retry') }}</SgiltButton>
    </div>

    <template v-else>
      <div class="filters">
        <button
          v-for="filter in filters"
          :key="filter.key ?? ALL_FILTER_KEY"
          type="button"
          class="filter"
          :class="{ active: activeFilter === filter.key }"
          @click="activeFilter = filter.key"
        >
          {{ filter.label }} · {{ filter.count }}
        </button>
      </div>

      <p v-if="!filteredCommandes.length" class="empty">
        {{
          activeFilter === ENTERED_FILTER
            ? $t('evenement.billetterie.commandes.empty-entered')
            : activeFilter
              ? $t('evenement.billetterie.commandes.empty-status')
              : $t('evenement.billetterie.commandes.empty')
        }}
      </p>

      <ul v-else class="list">
        <li v-for="commande in filteredCommandes" :key="commande.id" class="card">
          <div class="head">
            <span class="buyer">{{ commande.buyerFirstName }} {{ commande.buyerLastName }}</span>
            <span class="badge" :class="commande.status.toLowerCase()">
              {{ $t(`evenement.billetterie.commandes.status.${commande.status}`) }}
            </span>
          </div>
          <span class="email">{{ commande.buyerEmail }}</span>
          <span v-if="commande.scannedAt" class="entered">
            {{ $t('evenement.billetterie.commandes.entered-at', { time: formatTimeOfDay(commande.scannedAt) }) }}
          </span>
          <div class="foot">
            <span class="amount">
              {{ $t('evenement.billetterie.commandes.tickets', commande.quantity) }}
              · {{ formatPrice(commande.totalCents / 100) }}
            </span>
            <span class="date">{{ formatDateTime(commande.createdAt) }}</span>
          </div>
        </li>
      </ul>
    </template>
  </section>
</template>

<script setup lang="ts">
import Sk from '~/components/basics/Sk.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import {
  ORGANIZER_COMMANDE_STATUSES,
  type OrganizerCommande,
  type OrganizerCommandeStatus,
} from '~/data/ticketing/domain/Commande'
import { formatDateTime, formatTimeOfDay } from '~/utils/dateUtils'
import { formatPrice } from '~/utils/priceUtils'

// Clé du filtre « Toutes » (pas de statut).
const ALL_FILTER_KEY = 'ALL'
// Filtre « Entrées » : les commandes dont l'e-billet a été scanné (pas un statut de commande).
const ENTERED_FILTER = 'ENTERED'

// Filtre de la liste : un statut, les entrées, ou null pour toutes.
type CommandeFilter = OrganizerCommandeStatus | typeof ENTERED_FILTER | null

// Ordre des filtres après « Toutes » : les entrées juste après les payées, dont elles font partie.
const FILTERS: Exclude<CommandeFilter, null>[] = [
  'PAID',
  ENTERED_FILTER,
  ...ORGANIZER_COMMANDE_STATUSES.filter((status) => status !== 'PAID'),
]

function matches(commande: OrganizerCommande, filter: Exclude<CommandeFilter, null>): boolean {
  return filter === ENTERED_FILTER ? !!commande.scannedAt : commande.status === filter
}

const props = defineProps<{
  commandes: OrganizerCommande[]
  loading: boolean
  loadFailed: boolean
}>()

defineEmits<{
  retry: []
}>()

const { t } = useI18n()

// null : toutes les commandes. Payées par défaut : ce sont elles qui comptent (ventes, entrées).
const activeFilter = ref<CommandeFilter>('PAID')

const filters = computed<{ key: CommandeFilter; label: string; count: number }[]>(() => [
  { key: null, label: t('evenement.billetterie.commandes.filters.ALL'), count: props.commandes.length },
  ...FILTERS.map((filter) => ({
    key: filter,
    label: t(`evenement.billetterie.commandes.filters.${filter}`),
    count: props.commandes.filter((commande) => matches(commande, filter)).length,
  })),
])

const filteredCommandes = computed(() => {
  const filter = activeFilter.value
  return filter === null ? props.commandes : props.commandes.filter((commande) => matches(commande, filter))
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.commandes-block {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;

  .skeleton {
    height: 8rem;
    border-radius: $radius-lg;
  }

  .load-error {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: $spacing-s;

    p {
      margin: 0;
      font-size: $font-size-sm;
      color: $text-primary;
    }
  }

  // Défile horizontalement sur mobile plutôt que de passer à la ligne.
  .filters {
    display: flex;
    gap: $spacing-xs;
    overflow-x: auto;
    scrollbar-width: none;

    &::-webkit-scrollbar {
      display: none;
    }

    .filter {
      flex-shrink: 0;
      padding: 0.3rem 0.75rem;
      border: 1px solid $divider-color;
      border-radius: 9999px;
      background: $surface-white;
      font-family: 'Inter', sans-serif;
      font-size: $font-size-xs;
      font-weight: 500;
      color: $text-secondary;
      cursor: pointer;
      transition:
        background 120ms ease,
        color 120ms ease;

      &.active {
        border-color: $brand-primary;
        background: $brand-primary;
        color: $surface-white;
        font-weight: 600;
      }
    }
  }

  .empty {
    margin: 0;
    padding: $spacing-m;
    border-radius: $radius-lg;
    background: $surface-soft;
    font-size: $font-size-sm;
    color: $text-secondary;
  }

  .list {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
    margin: 0;
    padding: 0;
    list-style: none;

    .card {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
      padding: $spacing-s $spacing-m;
      border-radius: $radius-lg;
      background: $surface-soft;

      .head {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: $spacing-s;

        .buyer {
          min-width: 0;
          font-size: 0.95rem;
          font-weight: 600;
          color: $text-primary;
          overflow-wrap: anywhere;
        }

        .badge {
          flex-shrink: 0;
          padding: 0.125rem $spacing-xs;
          border-radius: $radius-sm;
          font-size: 0.7rem;
          font-weight: 600;
          white-space: nowrap;
          background: rgba(0, 0, 0, 0.05);
          color: $text-secondary;

          &.paid {
            background: rgba($state-success, 0.12);
            color: $state-success;
          }

          &.pending {
            background: rgba($state-warning, 0.12);
            color: $state-warning;
          }

          // Abandonnée (EXPIRED) garde le gris par défaut.
          &.cancelled {
            background: rgba($state-danger, 0.12);
            color: $state-danger;
          }
        }
      }

      .email {
        font-size: $font-size-sm;
        color: $text-secondary;
        overflow-wrap: anywhere;
      }

      .entered {
        font-size: $font-size-sm;
        font-weight: 600;
        color: $state-success;
      }

      .foot {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        gap: 0.25rem $spacing-s;
        font-size: $font-size-sm;

        .amount {
          font-weight: 600;
          color: $text-primary;
        }

        .date {
          color: $text-secondary;
        }
      }
    }
  }
}
</style>
