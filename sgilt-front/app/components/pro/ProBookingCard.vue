<template>
  <!-- Skeleton -->
  <div v-if="skeleton" class="demande-card demande-card--skeleton">
    <div class="skeleton-body">
      <div class="skeleton-text" style="width: 52px; height: 16px; border-radius: 2rem" />
      <div
        class="skeleton-text"
        style="width: 70%; height: 1.05rem; border-radius: 4px; margin-top: 6px"
      />
      <div
        class="skeleton-text"
        style="width: 45%; height: 0.75rem; border-radius: 4px; margin-top: 4px"
      />
      <div
        class="skeleton-text"
        style="width: 60%; height: 0.75rem; border-radius: 4px; margin-top: 3px"
      />
    </div>
    <div class="demande-card__vignette skeleton-text" />
  </div>

  <!-- Card -->
  <button
    v-else-if="demande"
    class="demande-card"
    type="button"
    :style="{
      borderLeftColor: cardBorderColor,
      backgroundColor: cardBgColor,
      animationDelay: `${animationDelay}ms`,
    }"
    @click="emit('click')"
  >
    <!-- Zone texte -->
    <div class="demande-card__body">
      <span class="demande-card__badge" :class="`demande-card__badge--${demande.statut}`">
        {{ STATUT_LABELS[demande.statut] }}
      </span>
      <span class="demande-card__titre">{{ demande.titre }}</span>
      <span class="demande-card__date">{{ demande.date }}</span>
      <span class="demande-card__context" :class="`demande-card__context--${urgencyTier}`">
        {{ demande.ligneContextuelle }}
      </span>
      <div v-if="demande.progressType" class="demande-card__progress">
        <div class="demande-card__progress-bar" :style="progressBarStyle" />
      </div>
      <span v-if="actionLabel" class="demande-card__action">{{ actionLabel }}</span>
    </div>

    <!-- Vignette photo -->
    <div class="demande-card__vignette">
      <img :src="demande.coverImage || FALLBACK_COVER" alt="" />
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

const STATUT_LABELS: Partial<Record<ReservationStatus, string>> = {
  nouvelle: 'Nouvelle',
  recontactee: 'Recontactée',
  confirmee: 'Confirmée',
  cloturee: 'Clôturée',
  annulee: 'Annulée',
}

const STATUS_BORDER: Partial<Record<ReservationStatus, string>> = {
  nouvelle: '#e6b800',
  recontactee: '#6366f1',
  confirmee: '#3b6d11',
  cloturee: '#6b635c',
  annulee: '#a32d2d',
}

const STATUS_BG: Partial<Record<ReservationStatus, string>> = {
  nouvelle: 'rgba(245, 197, 24, 0.07)',
  recontactee: 'rgba(99, 102, 241, 0.06)',
  confirmee: 'rgba(34, 197, 94, 0.06)',
  cloturee: 'rgba(0, 0, 0, 0.03)',
  annulee: 'rgba(239, 68, 68, 0.05)',
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

const cardBgColor = computed(() => {
  if (!props.demande) return '#fff'
  return STATUS_BG[props.demande.statut] ?? '#fff'
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
  align-items: stretch;
  gap: 0;
  padding: 0;
  border-radius: $radius-md;
  border: 0.5px solid rgba(47, 42, 37, 0.1);
  border-left-width: 3px;
  box-shadow: 0 1px 4px rgba(47, 42, 37, 0.08);
  text-align: left;
  font-family: inherit;
  cursor: pointer;
  overflow: hidden;
  transition: box-shadow 150ms ease;
  animation: card-in 300ms ease-out both;

  &:hover {
    box-shadow: 0 4px 16px rgba(47, 42, 37, 0.12);
  }

  &--skeleton {
    min-height: 88px;
    border-left-color: transparent;
    box-shadow: none;
    pointer-events: none;
  }

  &__body {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 3px;
    padding: 12px $spacing-s;
  }

  &__badge {
    align-self: flex-start;
    margin-bottom: 2px;
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
      background: rgba(99, 102, 241, 0.1);
      color: #4f52c9;
    }

    &--confirmee {
      background: rgba(34, 197, 94, 0.12);
      color: #166534;
    }

    &--cloturee {
      background: #f0efee;
      color: #888;
    }

    &--annulee {
      background: rgba(239, 68, 68, 0.1);
      color: #a32d2d;
    }
  }

  &__titre {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.1rem;
    font-weight: 600;
    color: #1a1a1a;
    line-height: 1.2;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
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

  &__progress {
    margin-top: 6px;
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

  &__action {
    font-family: 'Inter', sans-serif;
    font-size: 0.875rem;
    font-weight: 700;
    color: $brand-accent;
    margin-top: 4px;
  }

  &__vignette {
    flex-shrink: 0;
    width: 72px;
    align-self: stretch;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
    }
  }
}

.skeleton-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 12px $spacing-s;
}
</style>
