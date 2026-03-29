<template>
  <div class="pro-board">
    <!-- ── Header mobile (masqué desktop) ───────────────────────────────────────── -->
    <div class="header-mobile">
      <ProBoardGreeting :subtitle="contextLine" :loading="loading" />
    </div>

    <!-- ── Filtres sticky ────────────────────────────────────────────────────────── -->
    <div class="filters">
      <div class="pills-scroll">
        <button
          v-for="pill in RESERVATION_STATUS_PILLS"
          :key="pill.id"
          class="pill"
          :class="{ active: isPillActive(pill.id) }"
          type="button"
          @click="togglePill(pill.id)"
        >
          {{ t(`reservation.statut.${pill.id}`) }}
        </button>
      </div>
    </div>

    <!-- ── Corps ────────────────────────────────────────────────────────────────── -->
    <div class="body">
      <!-- Sidebar desktop (masquée mobile) -->
      <aside class="sidebar">
        <ProBoardGreeting :subtitle="contextLine" :loading="loading" />
      </aside>

      <!-- Liste des bookings -->
      <div class="bookings-list">
        <template v-if="loading">
          <ProBookingCard v-for="i in 4" :key="i" skeleton />
        </template>

        <p v-else-if="filteredDemandes.length === 0" class="empty">
          Aucune demande pour le moment.
        </p>

        <ProBookingCard
          v-for="(demande, index) in filteredDemandes"
          v-else
          :key="demande.id"
          :demande="demande"
          :animation-delay="index * 60"
          @click="navigateTo(`/pro/reservations/${demande.id}`)"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'pro' })

import { ProMockService } from '~/services/pro.mock'
import type { ProDemandeSummary } from '~/types/event'
import { RESERVATION_STATUS_PILLS, RESERVATION_STATUS_ORDER } from '~/constants/reservation-status'
import type { ReservationStatut } from '~/constants/reservation-status'

// ── Données ────────────────────────────────────────────────────────────────────
const loading = ref(true)
const DEMANDES = ref<ProDemandeSummary[]>([])

onMounted(async () => {
  DEMANDES.value = await ProMockService.getAllDemandes()
  loading.value = false
})

// ── En-tête ────────────────────────────────────────────────────────────────────
const contextLine = computed(() => ProMockService.getGreetingSubtitle(DEMANDES.value))

// ── Pills ──────────────────────────────────────────────────────────────────────
const { t } = useI18n()

const activeFilter = ref<ReservationStatut | null>(null)

function isPillActive(id: string): boolean {
  if (id === 'toutes') return activeFilter.value === null
  return activeFilter.value === (id as ReservationStatut)
}

function togglePill(id: string) {
  activeFilter.value = id === 'toutes' ? null : (id as ReservationStatut)
}

// ── Filtrage + tri ─────────────────────────────────────────────────────────────
const filteredDemandes = computed(() =>
  DEMANDES.value
    .filter((d) => activeFilter.value === null || d.statut === activeFilter.value)
    .sort((a, b) => {
      const statusDiff =
        RESERVATION_STATUS_ORDER.indexOf(a.statut as ReservationStatut) -
        RESERVATION_STATUS_ORDER.indexOf(b.statut as ReservationStatut)
      if (statusDiff !== 0) return statusDiff
      return b.urgencyLevel - a.urgencyLevel
    }),
)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;
$filter-h: 50px;

.pro-board {
  min-height: 100%;
  background-color: #e8e6e3;
  display: flex;
  flex-direction: column;

  // ── Header mobile ──────────────────────────────────────────────────────────────
  .header-mobile {
    background: #fff;
    padding: $spacing-m $spacing-m $spacing-s;
    display: flex;
    flex-direction: column;
    gap: 4px;

    @media (min-width: $desktop) {
      display: none;
    }
  }

  // ── Filtres sticky ─────────────────────────────────────────────────────────────
  .filters {
    position: sticky;
    top: $app-header-height;
    z-index: $z-header;
    background: #fff;
    padding: $spacing-s $spacing-m;
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);

    @media (min-width: $desktop) {
      padding: $spacing-s max($spacing-xl, calc((100% - 1200px) / 2));
    }
  }

  // ── Corps ─────────────────────────────────────────────────────────────────────
  .body {
    display: flex;
    flex-direction: column;
    flex: 1;

    @media (min-width: $desktop) {
      display: grid;
      grid-template-columns: 400px 1fr;
      gap: $spacing-xl;
      padding: $spacing-l max($spacing-xl, calc((100% - 1200px) / 2)) $spacing-xl;
    }
  }

  // ── Sidebar desktop ────────────────────────────────────────────────────────────
  .sidebar {
    display: none;

    @media (min-width: $desktop) {
      display: block;
      position: sticky;
      top: calc($app-header-height + $filter-h + $spacing-l);
      min-height: 15rem;
      align-self: start;
      background: #fff;
      border-radius: $radius-md;
      border: 1px solid $divider-color;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      padding: $spacing-m;
    }
  }
}

.pills-scroll {
  display: flex;
  gap: $spacing-xs;
  overflow-x: auto;
  scrollbar-width: none;
  padding-bottom: 2px;

  &::-webkit-scrollbar {
    display: none;
  }
}

.pill {
  flex-shrink: 0;
  padding: 5px 12px;
  border-radius: 2rem;
  border: none;
  font-family: 'Inter', sans-serif;
  font-size: 0.75rem;
  font-weight: 500;
  cursor: pointer;
  background: none;
  color: $text-secondary;
  transition:
    background 120ms ease,
    color 120ms ease;

  &.active {
    background: $brand-accent;
    color: $brand-primary;
    font-weight: 600;
  }
}

// ── Liste des cards ────────────────────────────────────────────────────────────
.bookings-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
  padding: $spacing-s $spacing-m calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $spacing-m);

  @media (min-width: $desktop) {
    gap: $spacing-m;
    padding: 0;
  }

  .empty {
    font-size: 0.875rem;
    color: $text-secondary;
    font-style: italic;
    text-align: center;
    padding: $spacing-xl 0;
    margin: 0;
  }
}
</style>
