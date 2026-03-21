<template>
  <!-- Skeleton -->
  <div v-if="skeleton" class="demande-card demande-card--skeleton">
    <div class="demande-card__photo skeleton-text" />
    <div class="demande-card__content">
      <div class="skeleton-text" style="width: 65%; height: 1.1rem; border-radius: 4px" />
      <div
        class="skeleton-text"
        style="width: 40%; height: 0.7rem; border-radius: 4px; margin-top: 5px"
      />
      <div
        class="skeleton-text"
        style="width: 55%; height: 0.7rem; border-radius: 4px; margin-top: 3px"
      />
    </div>
  </div>

  <!-- Card -->
  <button
    v-else-if="demande"
    class="demande-card"
    type="button"
    :style="{
      borderLeftColor: cardBorderColor,
      animationDelay: `${animationDelay}ms`,
    }"
    @click="emit('click')"
  >
    <!-- Col 1 — Photo -->
    <div class="demande-card__photo">
      <img :src="demande.coverImage || FALLBACK_COVER" alt="" />
    </div>

    <!-- Col 2 — Titre + Date (flex-grow) -->
    <div class="demande-card__content">
      <span class="demande-card__titre">{{ demande.titre }}</span>
      <div class="demande-card__meta">
        <span class="demande-card__date">{{ demande.date }}</span>
        <span
          v-if="demande.ligneContextuelle"
          class="demande-card__context"
          :class="`demande-card__context--${urgencyTier}`"
          >· {{ demande.ligneContextuelle }}</span
        >
      </div>
      <!-- Progress bar — mobile only -->
      <div
        v-if="demande.progressType"
        class="demande-card__progress demande-card__progress--mobile"
      >
        <div class="demande-card__progress-bar" :style="progressBarStyle" />
      </div>
    </div>

    <!-- Col 3 — Statut + Barre (~180px, desktop only) -->
    <div class="demande-card__status">
      <span class="demande-card__statut-pill" :style="{ backgroundColor: statusPillColor }">
        {{ statusLabel }}
      </span>
      <div v-if="demande.progressType" class="demande-card__progress">
        <div class="demande-card__progress-bar" :style="progressBarStyle" />
      </div>
    </div>

    <!-- Col 4 — Pill action -->
    <div v-if="actionLabel" class="demande-card__action">
      {{ actionLabel }}
    </div>
  </button>
</template>

<script setup lang="ts">
import type { ProDemandeSummary, ReservationStatus } from '~/types/event'

const props = defineProps<{
  demande?: ProDemandeSummary
  skeleton?: boolean
  animationDelay?: number
}>()

const emit = defineEmits<{ click: [] }>()

const { t } = useI18n()

const FALLBACK_COVER =
  'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=600&auto=format&fit=crop'

const STATUS_BORDER: Partial<Record<ReservationStatus, string>> = {
  nouvelle: '#e6b800',
  recontactee: '#6366f1',
  confirmee: '#3b6d11',
  cloturee: '#6b635c',
  annulee: '#a32d2d',
}

const STATUS_PILL_COLOR: Partial<Record<ReservationStatus, string>> = {
  nouvelle: '#e6b800',
  recontactee: '#6366f1',
  confirmee: '#3b6d11',
  cloturee: '#6b635c',
  annulee: '#a32d2d',
}

const urgencyTier = computed((): 'neutral' | 'warning' | 'urgent' => {
  const level = props.demande?.urgencyLevel ?? 0
  if (level <= 2) return 'neutral'
  if (level <= 4) return 'warning'
  return 'urgent'
})

const cardBorderColor = computed(() => {
  if (!props.demande) return '#6b635c'
  if (props.demande.urgencyLevel >= 5) return '#a32d2d'
  return STATUS_BORDER[props.demande.statut] ?? '#6b635c'
})

const statusPillColor = computed(() => {
  if (!props.demande) return '#6b635c'
  return STATUS_PILL_COLOR[props.demande.statut] ?? '#6b635c'
})

const statusLabel = computed(() => {
  if (!props.demande) return ''
  return t(`pro.board.card.statut.${props.demande.statut}`, props.demande.statut)
})

const progressBarStyle = computed((): Record<string, string> => {
  if (!props.demande) return {}
  const { progressType, progressValue } = props.demande
  if (!progressType || progressValue === null) return {}
  let color: string
  if (progressType === 'deadline') {
    color = progressValue > 0.5 ? '#3b6d11' : progressValue > 0.25 ? '#b06b00' : '#a32d2d'
  } else if (progressType === 'duration') {
    color = '#e6b800'
  } else {
    color = '#3b6d11'
  }
  return { width: `${progressValue * 100}%`, backgroundColor: color }
})

const actionLabel = computed(() => {
  if (!props.demande) return ''
  return t(`pro.board.card.action.${props.demande.statut}`)
})
</script>

<style scoped lang="scss">
$desktop: 900px;

@keyframes card-in {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.demande-card {
  display: flex;
  align-items: center;
  gap: $spacing-s;
  padding: $spacing-s $spacing-m $spacing-s $spacing-s;
  border-radius: $radius-md;
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-left-width: 3px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07);
  text-align: left;
  font-family: inherit;
  cursor: pointer;
  overflow: hidden;
  transition: box-shadow 150ms ease;
  animation: card-in 300ms ease-out both;

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  }

  &--skeleton {
    pointer-events: none;
    box-shadow: none;
    border-left-color: transparent;
  }

  // ── Col 1 — Photo ───────────────────────────────────────────────────────────
  &__photo {
    flex: 0 0 80px;
    height: 80px;
    border-radius: $radius-md;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
    }
  }

  // ── Col 2 — Contenu ─────────────────────────────────────────────────────────
  &__content {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 4px;

    @media (min-width: $desktop) {
      flex: 0 0 280px;
    }
  }

  &__titre {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.1rem;
    font-weight: 600;
    color: $text-primary;
    line-height: 1.2;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  &__meta {
    display: flex;
    flex-wrap: wrap;
    gap: 3px;
    align-items: baseline;
  }

  &__date {
    font-family: 'Inter', sans-serif;
    font-size: 0.72rem;
    color: $text-secondary;
  }

  &__context {
    font-family: 'Inter', sans-serif;
    font-size: 0.72rem;
    font-weight: 500;

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

  // ── Col 3 — Statut + Barre ──────────────────────────────────────────────────
  &__status {
    // Mobile: hidden — statut pill not shown, progress is in __content
    display: none;

    @media (min-width: $desktop) {
      display: flex;
      flex-direction: column;
      justify-content: center;
      gap: 6px;
      flex: 1;
    }
  }

  &__statut-pill {
    display: inline-block;
    align-self: flex-start;
    padding: 2px $spacing-xs;
    border-radius: $radius-sm;
    font-family: 'Inter', sans-serif;
    font-size: 0.7rem;
    font-weight: 600;
    color: #fff;
    white-space: nowrap;
    // nouvelle (yellow) needs dark text
    &[style*='#e6b800'] {
      color: #5a4500;
    }
  }

  // ── Progress bar ────────────────────────────────────────────────────────────
  &__progress {
    height: 4px;
    border-radius: 2px;
    background: rgba(47, 42, 37, 0.08);
    overflow: hidden;

    // Mobile version (inside __content): shown on mobile, hidden on desktop
    &--mobile {
      margin-top: 2px;

      @media (min-width: $desktop) {
        display: none;
      }
    }
  }

  &__progress-bar {
    height: 100%;
    border-radius: 2px;
    transition: width 400ms ease;
  }

  // ── Col 4 — Pill action ─────────────────────────────────────────────────────
  &__action {
    flex-shrink: 0;
    white-space: nowrap;
    padding: $spacing-xs $spacing-s;
    border-radius: $border-radius-xs;
    background: $color-accent;
    color: #fff;
    font-family: 'Inter', sans-serif;
    font-size: 0.75rem;
    font-weight: 700;
    letter-spacing: 0.02em;
    text-align: center;
    line-height: 1.35;
  }
}
</style>
