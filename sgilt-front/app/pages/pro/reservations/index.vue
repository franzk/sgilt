<template>
  <div class="pro-board">
    <!-- ── Accroche ────────────────────────────────────────────────────────────── -->
    <div class="pro-board__header">
      <p class="pro-board__greeting">Bonjour DJ Animation !</p>
      <p class="pro-board__context">{{ contextLine }}</p>

      <!-- ── Pills de filtrage ─────────────────────────────────────────────────── -->
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
  ALL_RESERVATION_STATUTS,
  DEFAULT_ACTIVE_PILLS,
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

// ── Accroche ───────────────────────────────────────────────────────────────────
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

const activePills = ref<ReservationStatut[]>([...DEFAULT_ACTIVE_PILLS])

function isPillActive(id: string): boolean {
  if (id === 'toutes') return ALL_RESERVATION_STATUTS.every((s) => activePills.value.includes(s))
  return activePills.value.includes(id as ReservationStatut)
}

function togglePill(id: string) {
  if (id === 'toutes') {
    activePills.value = isPillActive('toutes')
      ? [...DEFAULT_ACTIVE_PILLS]
      : [...ALL_RESERVATION_STATUTS]
    return
  }
  const statut = id as ReservationStatut
  const next = activePills.value.includes(statut)
    ? activePills.value.filter((p) => p !== statut)
    : [...activePills.value, statut]
  activePills.value = next.length === 0 ? [...DEFAULT_ACTIVE_PILLS] : next
}

// ── Filtrage + tri ─────────────────────────────────────────────────────────────
const filteredDemandes = computed(() =>
  DEMANDES.value
    .filter((d) => activePills.value.includes(d.statut as ReservationStatut))
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
$max-w: 780px;

.pro-board {
  min-height: 100%;
  background-color: #f5f5f3;
  display: flex;
  flex-direction: column;

  @media (min-width: $desktop) {
    padding-bottom: $spacing-xl;
  }
}

// ── Accroche ───────────────────────────────────────────────────────────────────
.pro-board__header {
  background: #fff;
  border-radius: 0;
  padding: $spacing-m;
  // margin: 0 (-$spacing-m);
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
  box-shadow: 0 1px 4px $shadow-s;

  @media (min-width: $desktop) {
    margin: 0;
    padding: 24px max(40px, calc((100% - $max-w) / 2 + 24px));
    box-shadow: none;
    border-bottom: 1px solid $divider-color;
  }
}

.pro-board__greeting {
  font-family: 'Cormorant Garamond', serif;
  font-size: 2rem;
  font-weight: 600;
  color: $brand-primary;
  margin: 0;
  line-height: 1.15;
}

.pro-board__context {
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  color: $text-secondary;
  margin: 0;
}

// ── Pills ──────────────────────────────────────────────────────────────────────
.pills-scroll {
  display: flex;
  gap: $spacing-xs;
  overflow-x: auto;
  scrollbar-width: none;
  padding-bottom: 2px;
  margin-top: $spacing-xs;

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
  transition:
    background 120ms ease,
    color 120ms ease;

  background: #ebebea;
  color: $text-secondary;

  &--active {
    background: $brand-accent;
    color: $brand-primary;
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
