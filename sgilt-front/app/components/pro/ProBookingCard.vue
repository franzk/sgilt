<template>
  <!-- Skeleton -->
  <div v-if="skeleton" class="booking-card booking-card--skeleton">
    <div class="booking-card__row1">
      <div class="booking-card__img skeleton-text" />
      <div class="booking-card__title">
        <div class="skeleton-text" style="width: 65%; height: 1.1rem; border-radius: 4px" />
      </div>
    </div>
    <div class="booking-card__row2">
      <div class="booking-card__action">
        <div class="skeleton-text" style="width: 110px; height: 1.5rem; border-radius: 2rem" />
      </div>
      <div class="booking-card__date">
        <div class="skeleton-text" style="width: 50%; height: 0.65rem; border-radius: 3px" />
        <div
          class="skeleton-text"
          style="width: 80%; height: 1rem; border-radius: 4px; margin-top: 3px"
        />
      </div>
    </div>
  </div>

  <!-- Card -->
  <button
    v-else-if="demande"
    class="booking-card"
    type="button"
    :style="{ animationDelay: `${animationDelay ?? 0}ms` }"
    @click="emit('click')"
  >
    <!-- Ligne 1 — Photo + Titre -->
    <div class="booking-card__row1">
      <img class="booking-card__img" :src="demande.coverImage || FALLBACK_COVER" alt="" />
      <div class="booking-card__title">
        <span>{{ demande.titre }}</span>
      </div>
    </div>

    <!-- Ligne 2 — Pill/Phrase + Date -->
    <div class="booking-card__row2">
      <div class="booking-card__action">
        <span
          class="booking-card__pill"
          :style="{ background: statusConfig.pillBg, color: statusConfig.pillText }"
          >{{ pillLabel }}</span
        >
        <p
          v-if="demande.phraseUrgence"
          class="booking-card__phrase"
          v-html="demande.phraseUrgence"
        />
      </div>
      <div class="booking-card__date">
        <span class="booking-card__date-label">Date de l'événement</span>
        <span class="booking-card__date-value">{{ demande.date || '—' }}</span>
      </div>
    </div>
  </button>
</template>

<script setup lang="ts">
import type { ProDemandeSummary } from '~/types/event'
import { RESERVATION_STATUS_CONFIG, STATUTS_AVEC_ACTION } from '~/constants/reservation-status'

const props = defineProps<{
  demande?: ProDemandeSummary
  skeleton?: boolean
  animationDelay?: number
}>()

const emit = defineEmits<{ click: [] }>()

const { t } = useI18n()

const FALLBACK_COVER =
  'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=600&auto=format&fit=crop'

const statusConfig = computed(() =>
  props.demande
    ? RESERVATION_STATUS_CONFIG[props.demande.statut]
    : RESERVATION_STATUS_CONFIG.refusee,
)

const pillLabel = computed(() => {
  if (!props.demande) return ''
  return STATUTS_AVEC_ACTION.includes(props.demande.statut)
    ? t('pro.board.card.pill.action_requise')
    : t(`reservation.statut.${props.demande.statut}`)
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

// ── Card container ─────────────────────────────────────────────────────────────

.booking-card {
  display: flex;
  flex-direction: column;
  padding: $spacing-s;
  background: #fff;
  border-radius: $radius-md;
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07);
  text-align: left;
  font-family: inherit;
  cursor: pointer;
  overflow: hidden;
  transition: box-shadow 150ms ease;
  animation: card-in 300ms ease-out both;

  @media (min-width: $breakpoint-desktop) {
    flex-direction: row;
    align-items: center;
    gap: $spacing-m;
    padding: $spacing-s $spacing-m;
  }

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  }

  &--skeleton {
    pointer-events: none;
    box-shadow: none;
  }
}

// ── Ligne 1 — Photo + Titre ────────────────────────────────────────────────────

.booking-card__row1 {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: $spacing-s;

  @media (min-width: $breakpoint-desktop) {
    display: contents;
  }
}

// ── Ligne 2 — Action + Date ────────────────────────────────────────────────────

.booking-card__row2 {
  display: flex;
  flex-direction: row;
  align-items: center;
  border-top: 1px solid $divider-color;
  margin-top: $spacing-s;
  padding-top: $spacing-s;

  @media (min-width: $breakpoint-desktop) {
    display: contents;
  }
}

// ── Photo ──────────────────────────────────────────────────────────────────────

.booking-card__img {
  flex: 0 0 72px;
  width: 72px;
  height: 72px;
  object-fit: cover;
  border-radius: $radius-md;
  display: block;

  @media (min-width: $breakpoint-desktop) {
    flex: 0 0 80px;
    width: 80px;
    height: 80px;
  }
}

// ── Titre ──────────────────────────────────────────────────────────────────────

.booking-card__title {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-align: center;

  @media (min-width: $breakpoint-desktop) {
    text-align: left;
  }

  span {
    display: block;
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.05rem;
    font-weight: 600;
    color: $text-primary;
    overflow: hidden;
    line-height: 1.2;

    @media (min-width: $breakpoint-desktop) {
      white-space: normal;
      overflow: visible;
      text-overflow: unset;
    }
  }
}

// ── Action (pill + phrase) ─────────────────────────────────────────────────────

.booking-card__action {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding-right: $spacing-s;

  @media (min-width: $breakpoint-desktop) {
    flex: 0 0 12rem;
    padding-right: 0;
  }
}

// ── Date ───────────────────────────────────────────────────────────────────────

.booking-card__date {
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  border-left: 1px solid $divider-color;
  padding-left: $spacing-s;

  @media (min-width: $breakpoint-desktop) {
    flex: 0 0 140px;
    border-left: none;
    padding-left: 0;
  }
}

// ── Éléments internes ─────────────────────────────────────────────────────────

.booking-card__pill {
  padding: 4px 10px;
  border-radius: 2rem;
  font-family: 'Inter', sans-serif;
  font-size: 0.75rem;
  font-weight: 700;
  white-space: nowrap;
  line-height: 1.3;
  letter-spacing: 0.01em;
}

.booking-card__phrase {
  margin: 0;
  font-family: 'Inter', sans-serif;
  font-size: 0.75rem;
  color: $text-primary;
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;

  :deep(strong) {
    font-weight: 700;
  }
}

.booking-card__date-label {
  font-family: 'Inter', sans-serif;
  font-size: 0.65rem;
  color: $text-secondary;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.booking-card__date-value {
  font-size: 0.9rem;
  font-weight: 400;
  color: $text-primary;
  line-height: 1.2;
}
</style>
