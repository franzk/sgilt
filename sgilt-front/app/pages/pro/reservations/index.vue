<template>
  <div class="pro-board">

    <!-- ── Accroche ────────────────────────────────────────────────────────────── -->
    <div class="pro-board__header">
      <p class="pro-board__greeting">Bonjour Thomas !</p>
      <p class="pro-board__context">{{ contextLine }}</p>

      <!-- ── Pills de filtrage ─────────────────────────────────────────────────── -->
      <div class="pills-scroll">
        <button
          v-for="pill in PILLS"
          :key="pill.id"
          class="pill"
          :class="{ 'pill--active': isPillActive(pill.id) }"
          type="button"
          @click="togglePill(pill.id)"
        >
          {{ pill.label }}
        </button>
      </div>
    </div>

    <!-- ── Liste des demandes ──────────────────────────────────────────────────── -->
    <div class="demandes-list">

      <!-- Skeleton -->
      <template v-if="loading">
        <div v-for="i in 4" :key="i" class="demande-card demande-card--skeleton">
          <div class="skeleton-badge skeleton-text" />
          <div class="skeleton-body">
            <div class="skeleton-line skeleton-text" style="width: 70%; height: 1.1rem" />
            <div class="skeleton-line skeleton-text" style="width: 45%; height: 0.85rem; margin-top: 5px" />
            <div class="skeleton-line skeleton-text" style="width: 55%; height: 0.75rem; margin-top: 4px" />
          </div>
        </div>
      </template>

      <!-- Vide -->
      <p v-else-if="filteredDemandes.length === 0" class="demandes-empty">
        Aucune demande pour le moment.
      </p>

      <!-- Cards -->
      <button
        v-for="demande in filteredDemandes"
        v-else
        :key="demande.id"
        class="demande-card"
        type="button"
        :style="{ borderLeftColor: cardBorderColor(demande) }"
        @click="navigateTo(`/pro/reservations/${demande.id}`)"
      >
        <span class="demande-card__badge" :class="`demande-card__badge--${demande.statut}`">
          {{ STATUT_LABELS[demande.statut] }}
        </span>
        <div class="demande-card__body">
          <span class="demande-card__titre">{{ demande.titre }}</span>
          <span class="demande-card__date">{{ demande.date }}</span>
          <span
            class="demande-card__context"
            :class="`demande-card__context--${urgencyTier(demande.urgencyLevel)}`"
          >
            {{ demande.ligneContextuelle }}
          </span>
        </div>
        <span class="demande-card__chevron" aria-hidden="true">›</span>
      </button>

    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'pro' })

import { ProMockService } from '~/services/pro.mock'
import type { ProDemandeSummary, ReservationStatus } from '~/types/event'

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
  const cLabel = c > 0 ? `${c} événement${c > 1 ? 's' : ''} confirmé${c > 1 ? 's' : ''} à venir` : ''
  return [nLabel, cLabel].filter(Boolean).join(' · ')
})

// ── Pills ──────────────────────────────────────────────────────────────────────
const PILLS = [
  { id: 'toutes', label: 'Toutes' },
  { id: 'nouvelle', label: 'Nouvelles' },
  { id: 'recontactee', label: 'Recontactées' },
  { id: 'confirmee', label: 'Confirmées' },
  { id: 'cloturee', label: 'Clôturées' },
  { id: 'annulee', label: 'Annulées' },
]

const ALL_STATUTS: ReservationStatus[] = ['nouvelle', 'recontactee', 'confirmee', 'cloturee', 'annulee']
const DEFAULT_PILLS: ReservationStatus[] = ['nouvelle', 'recontactee', 'confirmee']

const activePills = ref<ReservationStatus[]>([...DEFAULT_PILLS])

function isPillActive(id: string): boolean {
  if (id === 'toutes') return ALL_STATUTS.every((s) => activePills.value.includes(s))
  return activePills.value.includes(id)
}

function togglePill(id: string) {
  if (id === 'toutes') {
    activePills.value = isPillActive('toutes') ? [...DEFAULT_PILLS] : [...ALL_STATUTS]
    return
  }
  const next = activePills.value.includes(id)
    ? activePills.value.filter((p) => p !== id)
    : [...activePills.value, id]
  activePills.value = next.length === 0 ? [...DEFAULT_PILLS] : next
}

// ── Filtrage + tri ─────────────────────────────────────────────────────────────
const filteredDemandes = computed(() =>
  DEMANDES.value.filter((d) => activePills.value.includes(d.statut as ReservationStatus)).sort((a, b) => {
    if (b.urgencyLevel !== a.urgencyLevel) return b.urgencyLevel - a.urgencyLevel
    return new Date(a.dateIso).getTime() - new Date(b.dateIso).getTime()
  }),
)

// ── Urgence ───────────────────────────────────────────────────────────────────
function urgencyTier(level: number): 'neutral' | 'warning' | 'urgent' {
  if (level <= 2) return 'neutral'
  if (level <= 4) return 'warning'
  return 'urgent'
}

// ── Couleur border-left des cards ─────────────────────────────────────────────
const STATUS_BORDER: Partial<Record<ReservationStatus, string>> = {
  nouvelle: '#e6b800',
  recontactee: '#2c5cc5',
  confirmee: '#3b6d11',
  cloturee: '#6b635c',
  annulee: '#a32d2d',
}

function cardBorderColor(demande: ProDemandeSummary): string {
  if (demande.urgencyLevel >= 5) return '#a32d2d'
  return STATUS_BORDER[demande.statut] ?? '#6b635c'
}

// ── Labels statut ─────────────────────────────────────────────────────────────
const STATUT_LABELS: Partial<Record<ReservationStatus, string>> = {
  nouvelle: 'Nouvelle',
  recontactee: 'Recontactée',
  confirmee: 'Confirmée',
  cloturee: 'Clôturée',
  annulee: 'Annulée',
}
</script>

<style scoped lang="scss">
$bottom-nav-h: 56px;
$desktop: 900px;
$max-w: 680px;

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
  margin: 0 (-$spacing-m);
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
.demandes-list {
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

// ── Card demande ───────────────────────────────────────────────────────────────
.demande-card {
  display: flex;
  align-items: center;
  gap: $spacing-s;
  padding: 12px $spacing-s;
  background: #fff;
  border-radius: $radius-md;
  border: 0.5px solid rgba(47, 42, 37, 0.10);
  border-left-width: 3px;
  box-shadow: 0 1px 4px rgba(47, 42, 37, 0.08);
  text-align: left;
  font-family: inherit;
  cursor: pointer;
  transition: box-shadow 150ms ease;

  &:hover {
    box-shadow: 0 4px 16px rgba(47, 42, 37, 0.12);
  }

  &--skeleton {
    display: flex;
    align-items: center;
    gap: $spacing-s;
    min-height: 76px;
    border-left-color: transparent;
    box-shadow: none;
    pointer-events: none;
  }

  &__badge {
    flex-shrink: 0;
    align-self: flex-start;
    margin-top: 2px;
    font-size: 0.625rem;
    font-weight: 600;
    padding: 2px 7px;
    border-radius: 2rem;
    white-space: nowrap;

    &--nouvelle {
      background: rgba($brand-accent, 0.15);
      color: darken(#e6b800, 20%);
    }

    &--recontactee {
      background: #e8f0fe;
      color: #2c5cc5;
    }

    &--confirmee {
      background: rgba(59, 109, 17, 0.1);
      color: #3b6d11;
    }

    &--cloturee {
      background: #f0efee;
      color: #888;
    }

    &--annulee {
      background: rgba(163, 45, 45, 0.1);
      color: #a32d2d;
    }
  }

  &__body {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  &__titre {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.05rem;
    font-weight: 600;
    color: $brand-primary;
    line-height: 1.2;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  &__date {
    font-family: 'Inter', sans-serif;
    font-size: 0.75rem;
    color: $text-secondary;
  }

  &__context {
    font-family: 'Inter', sans-serif;
    font-size: 0.75rem;
    font-weight: 500;
    margin-top: 1px;

    &--neutral {
      color: $text-secondary;
    }

    &--warning {
      color: #b06b00;
    }

    &--urgent {
      color: #a32d2d;
    }
  }

  &__chevron {
    flex-shrink: 0;
    font-size: 1.2rem;
    color: $brand-muted;
    line-height: 1;
    margin-left: 2px;
  }
}

// ── Skeleton ───────────────────────────────────────────────────────────────────
.skeleton-badge {
  flex-shrink: 0;
  width: 70px;
  height: 20px;
  border-radius: 2rem;
  align-self: flex-start;
  margin-top: 2px;
}

.skeleton-body {
  flex: 1;
}

.skeleton-line {
  border-radius: $radius-sm;
}
</style>
