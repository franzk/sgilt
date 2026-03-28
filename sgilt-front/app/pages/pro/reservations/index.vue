<template>
  <div class="pro-board">
    <!-- ── En-tête ──────────────────────────────────────────────────────────────── -->
    <div class="pro-board__header">
      <p class="pro-board__greeting">Bonjour DJ Animation !</p>
      <p class="pro-board__subtitle">{{ contextLine }}</p>
    </div>

    <!-- ── Zone filtres ─────────────────────────────────────────────────────────── -->
    <div class="pro-board__filters">
      <div class="pills-scroll">
        <button
          v-for="pill in RESERVATION_STATUS_PILLS"
          :key="pill.id"
          class="pill"
          :class="{ 'pill--active': isPillActive(pill.id) }"
          type="button"
          @click="togglePill(pill.id)"
        >
          {{ t(`reservation.statut.${pill.id}`) }}
        </button>
      </div>
    </div>

    <!-- ── Liste des bookings ──────────────────────────────────────────────────── -->
    <div class="bookings-list">
      <!-- Skeleton -->
      <template v-if="loading">
        <ProBookingCard v-for="i in 4" :key="i" skeleton />
      </template>

      <!-- Vide -->
      <p v-else-if="filteredDemandes.length === 0" class="demandes-empty">
        Aucune demande pour le moment.
      </p>

      <!-- Cards -->
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
</template>

<script setup lang="ts">
definePageMeta({ layout: 'pro' })

import { ProMockService } from '~/services/pro.mock'
import type { ProDemandeSummary } from '~/types/event'
import {
  RESERVATION_STATUS_PILLS,
  RESERVATION_STATUS_ORDER,
} from '~/constants/reservation-status'
import type { ReservationStatut } from '~/constants/reservation-status'

// ── Données ────────────────────────────────────────────────────────────────────
const loading = ref(true)
const DEMANDES = ref<ProDemandeSummary[]>([])

onMounted(async () => {
  DEMANDES.value = await ProMockService.getAllDemandes()
  loading.value = false
})

// ── En-tête ────────────────────────────────────────────────────────────────────
const newCount = computed(() => DEMANDES.value.filter((d) => d.statut === 'nouvelle').length)
const confirmedCount = computed(() => DEMANDES.value.filter((d) => d.statut === 'confirmee').length)

const contextLine = computed(() => {
  const n = newCount.value
  const c = confirmedCount.value
  if (n === 0 && c === 0) return 'Tout est à jour.'
  const nLabel = n > 0 ? `${n} nouvelle${n > 1 ? 's' : ''} demande${n > 1 ? 's' : ''}` : ''
  const cLabel =
    c > 0 ? `${c} événement${c > 1 ? 's' : ''} confirmé${c > 1 ? 's' : ''} à venir` : ''
  return [nLabel, cLabel].filter(Boolean).join(' · ')
})

// ── Pills ──────────────────────────────────────────────────────────────────────
const { t } = useI18n()

// 'toutes' = null, sinon le statut sélectionné
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
$max-w: 680px;

.pro-board {
  min-height: 100%;
  background-color: #e8e6e3;
  display: flex;
  flex-direction: column;

  @media (min-width: $desktop) {
    padding-bottom: $spacing-xl;
  }
}

// ── En-tête ────────────────────────────────────────────────────────────────────

.pro-board__header {
  background: #fff;
  padding: $spacing-m $spacing-m $spacing-s;
  display: flex;
  flex-direction: column;
  gap: 4px;

  @media (min-width: $desktop) {
    padding: 28px max(40px, calc((100% - $max-w) / 2 + 24px)) $spacing-s;
  }
}

.pro-board__greeting {
  font-family: 'Cormorant Garamond', serif;
  font-size: 2.1rem;
  font-weight: 700;
  color: $brand-primary;
  margin: 0;
  line-height: 1.1;
}

.pro-board__subtitle {
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  color: $text-secondary;
  margin: 0;
}

// ── Zone filtres ───────────────────────────────────────────────────────────────

.pro-board__filters {
  background: #f0eeeb;
  padding: $spacing-s $spacing-m;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);

  @media (min-width: $desktop) {
    padding: $spacing-s max(40px, calc((100% - $max-w) / 2 + 24px));
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

  &--active {
    background: #2d2d2b;
    color: #fff;
    font-weight: 600;
  }
}

// ── Liste demandes ─────────────────────────────────────────────────────────────

.bookings-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
  padding: $spacing-m $spacing-m calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $spacing-m);

  @media (min-width: $desktop) {
    max-width: $max-w;
    width: 100%;
    margin: 0 auto;
    padding: $spacing-l 0 $spacing-xl;
  }
}

.demandes-empty {
  font-size: 0.875rem;
  color: $text-secondary;
  font-style: italic;
  text-align: center;
  padding: $spacing-xl 0;
  margin: 0;
}
</style>
