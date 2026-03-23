<template>
  <!-- Skeleton -->
  <div v-if="skeleton" class="booking-card booking-card--skeleton">
    <div class="booking-card__img skeleton-text" />
    <div class="booking-card__title">
      <div class="skeleton-text" style="width: 65%; height: 1.1rem; border-radius: 4px" />
    </div>
    <div class="booking-card__action">
      <div class="skeleton-text" style="width: 110px; height: 1.5rem; border-radius: 2rem" />
    </div>
    <div class="booking-card__date">
      <div class="skeleton-text" style="width: 50%; height: 0.65rem; border-radius: 3px" />
      <div class="skeleton-text" style="width: 80%; height: 1rem; border-radius: 4px; margin-top: 3px" />
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
    <!-- Col 1 — Image -->
    <img
      class="booking-card__img"
      :src="demande.coverImage || FALLBACK_COVER"
      alt=""
    />

    <!-- Col 2 — Titre -->
    <div class="booking-card__title">
      <span>{{ demande.titre }}</span>
    </div>

    <!-- Col 3 — Pill statut + Phrase urgence -->
    <div class="booking-card__action">
      <span
        class="booking-card__pill"
        :style="{ background: statusConfig.pillBg, color: statusConfig.pillText }"
      >{{ pillLabel }}</span>
      <p
        v-if="demande.phraseUrgence"
        class="booking-card__phrase"
        v-html="demande.phraseUrgence"
      />
    </div>

    <!-- Col 4 — Date de l'événement -->
    <div class="booking-card__date">
      <span class="booking-card__date-label">Date de l'événement</span>
      <span class="booking-card__date-value">{{ demande.date || '—' }}</span>
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
  flex-wrap: wrap;
  align-items: flex-start;
  column-gap: $spacing-s;
  row-gap: $spacing-xs;
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
    flex-wrap: nowrap;
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

// ── Col 1 — Image ──────────────────────────────────────────────────────────────

.booking-card__img {
  flex: 0 0 72px;
  width: 72px;
  height: 72px;
  object-fit: cover;
  border-radius: $radius-md;
  display: block;
  align-self: flex-start;

  @media (min-width: $breakpoint-desktop) {
    flex: 0 0 80px;
    width: 80px;
    height: 80px;
    align-self: center;
  }
}

// ── Col 2 — Titre ──────────────────────────────────────────────────────────────

.booking-card__title {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  align-self: center;

  span {
    display: block;
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.05rem;
    font-weight: 600;
    color: $text-primary;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    line-height: 1.2;
  }
}

// ── Col 3 — Pill + Phrase urgence ─────────────────────────────────────────────

.booking-card__action {
  flex: 0 0 100%;   // pleine largeur sur mobile (nouvelle ligne)
  display: flex;
  flex-direction: column;
  gap: 4px;

  @media (min-width: $breakpoint-desktop) {
    flex: 0 0 200px;
  }
}

// ── Col 4 — Date de l'événement ───────────────────────────────────────────────

.booking-card__date {
  flex: 0 0 100%;   // pleine largeur sur mobile (nouvelle ligne)
  display: flex;
  flex-direction: column;
  gap: 2px;

  @media (min-width: $breakpoint-desktop) {
    flex: 0 0 140px;
  }
}

// ── Éléments internes ─────────────────────────────────────────────────────────

.booking-card__pill {
  display: inline-flex;
  align-items: center;
  align-self: flex-start;
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
  font-family: 'Cormorant Garamond', serif;
  font-size: 1rem;
  font-weight: 600;
  color: $text-primary;
  line-height: 1.2;
}
</style>
