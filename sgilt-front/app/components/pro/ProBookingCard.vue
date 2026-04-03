<template>
  <!-- Skeleton -->
  <div v-if="skeleton" class="booking-card skeleton">
    <div class="left">
      <div class="photo skeleton-text" />
      <div
        class="skeleton-text"
        style="width: 80%; height: 0.85rem; border-radius: 4px; margin-top: 8px"
      />
    </div>
    <div class="right">
      <div class="right-row">
        <div class="skeleton-text" style="width: 90px; height: 0.8rem; border-radius: 3px" />
      </div>
      <div class="right-row">
        <div class="skeleton-text" style="width: 75%; height: 0.8rem; border-radius: 4px" />
      </div>
      <div class="right-row last-row">
        <div class="skeleton-text" style="width: 55%; height: 0.65rem; border-radius: 3px" />
        <div
          class="skeleton-text"
          style="width: 70%; height: 0.85rem; border-radius: 4px; margin-top: 4px"
        />
      </div>
    </div>
  </div>

  <!-- Card -->
  <button
    v-else-if="demande"
    class="booking-card"
    :class="demande.statut"
    type="button"
    :style="{ animationDelay: `${animationDelay ?? 0}ms` }"
    @click="emit('click')"
  >
    <!-- Colonne gauche : photo circulaire + badge + titre -->
    <div class="left">
      <BadgeableComponent :count="demande.unreadNotesCount" :size="18">
        <img class="photo" :src="demande.coverImage || FALLBACK_COVER" alt="" />
      </BadgeableComponent>
      <p class="title">{{ demande.titre }}</p>
    </div>

    <!-- Colonne droite : infos -->
    <div class="right">
      <div v-if="needsAction" class="right-row">
        <span class="action-required" :class="`${demande.statut}`">
          <template v-if="demande.statut === 'nouvelle'">Contactez le client</template>
          <template v-else-if="demande.statut === 'en_discussion'">Validez la demande</template>
        </span>
      </div>

      <div v-if="demande.phraseInfoState" class="right-row">
        <span class="info-value" v-html="demande.phraseInfoState" />
      </div>

      <div class="right-row last-row date-row">
        <span class="calendar-icon icon">
          <CalendarEventIcon />
        </span>
        <div class="date-info">
          <span class="info-label">Date de l'événement</span>
          <span class="info-value">{{ demande.date ? formatDate(demande.date) : '—' }}</span>
        </div>
      </div>
    </div>
  </button>
</template>

<script setup lang="ts">
import BadgeableComponent from '~/components/basics/BadgeableComponent.vue'
import type { ProDemandeSummary } from '~/types/event'
import { STATUTS_AVEC_ACTION } from '~/constants/reservation-status'
import { CalendarEventIcon } from '@remixicons/vue/line'

const props = defineProps<{
  demande?: ProDemandeSummary
  skeleton?: boolean
  animationDelay?: number
}>()

const emit = defineEmits<{ click: [] }>()

const FALLBACK_COVER =
  'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=600&auto=format&fit=crop'

const needsAction = computed(() =>
  props.demande ? STATUTS_AVEC_ACTION.includes(props.demande.statut) : false,
)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

@keyframes card-in {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes card-in-dimmed {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 0.5;
    transform: translateY(0);
  }
}

// ── Card container ─────────────────────────────────────────────────────────────

.booking-card {
  display: flex;
  flex-direction: row;
  align-items: stretch;
  width: 100%;
  background: #fff;
  border: 1px solid rgba(0, 0, 0, 0.07);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
  border-radius: $radius-md;
  text-align: left;
  font-family: inherit;
  cursor: pointer;
  overflow: hidden;
  transition: box-shadow 150ms ease;
  animation: card-in 280ms ease-out both;

  &.refusee,
  &.annulee,
  &.realisee {
    animation-name: card-in-dimmed;
  }

  &:hover {
    box-shadow: 0 4px 18px rgba(0, 0, 0, 0.1);
  }

  &.skeleton {
    pointer-events: none;
    box-shadow: none;
  }

  // ── Colonne gauche ───────────────────────────────────────────────────────────

  .left {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: $spacing-xs;
    padding: $spacing-m 0;
    margin: auto;
    flex: 0 0 130px;
  }

  // ── Photo circulaire ─────────────────────────────────────────────────────────

  .photo {
    width: 56px;
    height: 56px;
    object-fit: cover;
    border-radius: 50%;
    display: block;
    flex-shrink: 0;
    box-shadow:
      0 0 0 3px rgba($brand-accent, 0.6),
      0 0 12px 6px rgba($brand-accent, 0.4);
  }

  // ── Titre événement ──────────────────────────────────────────────────────────

  .title {
    width: 100%;
    margin: 0;
    font-family: 'Cormorant Garamond', serif;
    font-size: 0.85rem;
    font-weight: 600;
    color: $text-primary;
    line-height: 1.2;
    text-align: center;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
  }

  // ── Colonne droite ───────────────────────────────────────────────────────────

  .right {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 0;
  }

  // ── Ligne droite (séparateur entre chaque) ───────────────────────────────────

  .right-row {
    display: flex;
    flex-direction: column;
    gap: 1px;
    padding: $spacing-s $spacing-m $spacing-s $spacing-s;
  }

  .last-row {
    border-top: 1px solid $divider-color;
  }

  .date-row {
    flex-direction: row;
    align-items: center;
    gap: $spacing-xs;

    .icon {
      width: 24px;
      height: 24px;
      flex-shrink: 0;
      color: $text-secondary;
    }

    .date-info {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }
  }

  // ── "Action requise" ────────────────────────────────────────────────────────

  .action-required {
    font-family: 'Inter', sans-serif;
    font-size: 0.72rem;
    text-transform: uppercase;
    font-weight: 600;
    color: #fff;
    padding: 4px 8px;
    border-radius: $radius-xl;
    letter-spacing: 0.01em;

    &.nouvelle {
      background: #d93025;
    }

    &.en_discussion {
      background: #e67e22;
    }
  }

  .info-label {
    font-family: 'Inter', sans-serif;
    font-size: 0.6rem;
    font-weight: 600;
    // letter-spacing: 0.07em;
    text-transform: uppercase;
    color: $text-secondary;
  }

  .info-value {
    font-family: 'Inter', sans-serif;
    font-size: 0.82rem;
    font-weight: 400;
    color: $text-primary;
    line-height: 1.3;

    :deep(strong) {
      font-weight: 700;
    }
  }
}
</style>
