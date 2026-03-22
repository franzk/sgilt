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

    <!-- Col 2 — Contenu -->
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

      <!-- Statut + Barre — mobile uniquement -->
      <div class="demande-card__status-row">
        <StatusBadge :status="demande.statut" />
        <div v-if="demande.progressType" class="demande-card__progress">
          <div class="demande-card__progress-bar" :style="progressBarStyle" />
        </div>
      </div>

      <!-- Pill action — mobile uniquement, pleine largeur -->
      <div v-if="actionLabel" class="demande-card__action demande-card__action--mobile">
        {{ actionLabel }}
      </div>
    </div>

    <!-- Col 3 — Statut + Barre (desktop uniquement) -->
    <div class="demande-card__status">
      <StatusBadge :status="demande.statut" />
      <div v-if="demande.progressType" class="demande-card__progress">
        <div class="demande-card__progress-bar" :style="progressBarStyle" />
      </div>
    </div>

    <!-- Col 4 — Pill action (desktop uniquement) -->
    <div v-if="actionLabel" class="demande-card__action demande-card__action--desktop">
      {{ actionLabel }}
    </div>
  </button>
</template>

<script setup lang="ts">
import type { ProDemandeSummary } from '~/types/event'
import { RESERVATION_STATUS_CONFIG } from '~/constants/reservation-status'
import StatusBadge from '~/components/basics/StatusBadge.vue'

const props = defineProps<{
  demande?: ProDemandeSummary
  skeleton?: boolean
  animationDelay?: number
}>()

const emit = defineEmits<{ click: [] }>()

const { t } = useI18n()

const FALLBACK_COVER =
  'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=600&auto=format&fit=crop'

const urgencyTier = computed((): 'neutral' | 'warning' | 'urgent' => {
  const level = props.demande?.urgencyLevel ?? 0
  if (level <= 2) return 'neutral'
  if (level <= 4) return 'warning'
  return 'urgent'
})

const cardBorderColor = computed(() => {
  if (!props.demande) return RESERVATION_STATUS_CONFIG.cloturee.color
  if (props.demande.urgencyLevel >= 5) return RESERVATION_STATUS_CONFIG.nouvelle.color
  return RESERVATION_STATUS_CONFIG[props.demande.statut]?.color ?? RESERVATION_STATUS_CONFIG.cloturee.color
})

const progressBarStyle = computed((): Record<string, string> => {
  if (!props.demande) return {}
  const { progressType, progressValue } = props.demande
  if (!progressType || progressValue === null) return {}
  let color: string
  if (progressType === 'deadline') {
    color =
      progressValue > 0.5
        ? RESERVATION_STATUS_CONFIG.confirmee.color
        : progressValue > 0.25
          ? RESERVATION_STATUS_CONFIG.recontactee.color
          : RESERVATION_STATUS_CONFIG.nouvelle.color
  } else if (progressType === 'duration') {
    color = RESERVATION_STATUS_CONFIG.recontactee.color
  } else {
    color = RESERVATION_STATUS_CONFIG.confirmee.color
  }
  return { width: `${progressValue * 100}%`, backgroundColor: color }
})

const actionLabel = computed(() => {
  if (!props.demande) return ''
  return t(`pro.board.card.action.${props.demande.statut}`)
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

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
  align-items: flex-start;
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

  @media (min-width: $breakpoint-desktop) {
    align-items: center;
  }

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
    flex: 0 0 72px;
    height: 72px;
    border-radius: $radius-md;
    overflow: hidden;

    @media (min-width: $breakpoint-desktop) {
      flex: 0 0 80px;
      height: 80px;
    }

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
    gap: 5px;

    @media (min-width: $breakpoint-desktop) {
      flex: 0 0 280px;
      gap: 4px;
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

  // ── Ligne statut (mobile) ────────────────────────────────────────────────────
  &__status-row {
    display: flex;
    align-items: center;
    gap: $spacing-xs;

    @media (min-width: $breakpoint-desktop) {
      display: none;
    }

    .demande-card__progress {
      flex: 1;
    }
  }

  // ── Col 3 — Statut + Barre (desktop) ────────────────────────────────────────
  &__status {
    display: none;

    @media (min-width: $breakpoint-desktop) {
      display: flex;
      flex-direction: column;
      justify-content: center;
      gap: 6px;
      flex: 1;
    }
  }

  // ── Progress bar ─────────────────────────────────────────────────────────────
  &__progress {
    height: 4px;
    border-radius: 2px;
    background: rgba(47, 42, 37, 0.08);
    overflow: hidden;
  }

  &__progress-bar {
    height: 100%;
    border-radius: 2px;
    transition: width 400ms ease;
  }

  // ── Pill action ──────────────────────────────────────────────────────────────
  &__action {
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

    // Mobile : pleine largeur colonne contenu
    &--mobile {
      display: block;
      width: 100%;

      @media (min-width: $breakpoint-desktop) {
        display: none;
      }
    }

    // Desktop : pill compacte à droite
    &--desktop {
      display: none;
      flex-shrink: 0;

      @media (min-width: $breakpoint-desktop) {
        display: block;
      }
    }
  }
}
</style>
