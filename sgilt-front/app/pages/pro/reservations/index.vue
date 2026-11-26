<!--
  Onglet « Réservations » de l'espace prestataire (/pro/reservations) : ses réservations, filtrées
  par ce qu'il a à faire. Le filtre est porté par l'URL : rien (ou ?filtre=a-contacter) pour les
  nouvelles demandes, ?filtre=a-conclure, ?filtre=toutes. Seules les réservations à traiter sont
  chargées d'office ; toutes ne sont chargées qu'à la sélection du filtre « Toutes ».
-->
<template>
  <div class="pro-board">
    <div class="filters">
      <ProReservationFilters v-model="activeFilter" :counts="counts" />
    </div>

    <div class="bookings-list">
      <template v-if="loading">
        <ProBookingCard v-for="i in 4" :key="i" skeleton />
      </template>

      <div v-else-if="error" class="empty">
        <p class="empty-text">{{ $t('pro.reservations.error') }}</p>
      </div>

      <div v-else-if="activeFilter === 'toutes' && filteredReservations.length === 0" class="empty">
        <p class="empty-title">{{ $t('pro.reservations.empty-title') }}</p>
        <i18n-t keypath="pro.reservations.empty-text" tag="p" class="empty-text" scope="global">
          <template #link>
            <NuxtLink to="/pro/page-edition">{{ $t('pro.reservations.empty-link') }}</NuxtLink>
          </template>
        </i18n-t>
      </div>

      <div v-else-if="filteredReservations.length === 0" class="empty">
        <p class="empty-text">{{ $t(`pro.reservations.filters-empty.${activeFilter}`) }}</p>
      </div>

      <ProBookingCard
        v-for="(reservation, index) in filteredReservations"
        v-else
        :key="reservation.id"
        :reservation="reservation"
        :animation-delay="index * 60"
        @click="navigateTo(`/pro/reservations/${reservation.id}`)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useProReservations } from '~/data/reservation/useProReservations'
import {
  fetchProReservations,
  fetchProReservationsToHandle,
} from '~/data/reservation/service/reservationService'
import { PRO_RESERVATION_FILTERS } from '~/constants/reservation-status'
import type { ProReservationFilter } from '~/constants/reservation-status'
import ProReservationFilters from '~/components/pro/ProReservationFilters.vue'
import ProBookingCard from '~/components/pro/ProBookingCard.vue'
import type { ProReservationSummary } from '~/data/reservation/domain/ProReservationSummary'

definePageMeta({ layout: 'pro' })

const route = useRoute()

// ── Filtre (URL) ───────────────────────────────────────────────────────────────
// Valeur absente ou inconnue : le premier filtre (à contacter).
const DEFAULT_FILTER: ProReservationFilter = PRO_RESERVATION_FILTERS[0]!.id

const activeFilter = computed<ProReservationFilter>({
  get: () => PRO_RESERVATION_FILTERS.find((f) => f.id === route.query.filtre)?.id ?? DEFAULT_FILTER,
  set: (id) => navigateTo({ query: id === DEFAULT_FILTER ? {} : { filtre: id } }, { replace: true }),
})

// ── Données ────────────────────────────────────────────────────────────────────
// À traiter : chargées d'office. Toutes : seulement une fois le filtre « Toutes » sélectionné.
const toHandle = useProReservations(fetchProReservationsToHandle)
const all = useProReservations(fetchProReservations, () => activeFilter.value === 'toutes')

const loading = computed<boolean>(() =>
  activeFilter.value === 'toutes' ? all.loading.value : toHandle.loading.value,
)
const error = computed<unknown>(() =>
  activeFilter.value === 'toutes' ? all.error.value : toHandle.error.value,
)

function toHandleWith(id: ProReservationFilter): ProReservationSummary[] {
  const statut = PRO_RESERVATION_FILTERS.find((f) => f.id === id)?.statut
  return toHandle.reservations.value.filter((r) => r.statut === statut)
}

const filteredReservations = computed<ProReservationSummary[]>(() =>
  activeFilter.value === 'toutes' ? all.reservations.value : toHandleWith(activeFilter.value),
)

// Pas de nombre tant que les réservations à traiter ne sont pas là (chargement ou échec).
const counts = computed<Partial<Record<ProReservationFilter, number>>>(() => {
  if (toHandle.loading.value || toHandle.error.value) return {}
  return {
    'a-contacter': toHandleWith('a-contacter').length,
    'a-conclure': toHandleWith('a-conclure').length,
  }
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;

.pro-board {
  min-height: 100%;
  background-color: $brand-background-alt;
  display: flex;
  flex-direction: column;

  // ── Filtres sticky ─────────────────────────────────────────────────────────────
  .filters {
    position: sticky;
    top: $app-header-height;
    z-index: $z-header;
    background: $surface-white;
    padding: $spacing-s $spacing-m;
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
    margin-bottom: $spacing-s;

    @media (min-width: $desktop) {
      padding: $spacing-s max($spacing-xl, calc((100% - 1200px) / 2));
    }
  }

  // ── Liste des cards ──────────────────────────────────────────────────────────
  .bookings-list {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
    padding: 0 $spacing-xs;

    @media (min-width: $desktop) {
      gap: $spacing-m;
      padding: $spacing-l max($spacing-xl, calc((100% - 1200px) / 2)) $spacing-xl;
    }

    .empty {
      display: flex;
      flex-direction: column;
      gap: $spacing-xs;
      text-align: center;
      padding: $spacing-xl $spacing-m;
    }

    .empty-title {
      font-size: 1rem;
      font-weight: $font-weight-medium;
      color: $text-primary;
      margin: 0;
    }

    .empty-text {
      font-size: 0.875rem;
      color: $text-secondary;
      margin: 0;

      a {
        color: $color-primary;
        text-decoration: underline;
      }
    }
  }
}
</style>
