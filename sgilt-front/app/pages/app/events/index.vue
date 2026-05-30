<template>
  <div class="events-page">
    <!-- ── En-tête ──────────────────────────────────────────────────────────── -->
    <div class="events-page__header">
      <p class="events-page__count">
        {{ t('events.count', events.length, { n: events.length }) }}
      </p>
      <button
        class="events-page__create-btn"
        type="button"
        @click="useFlow().start('new-event', t('events.create'))"
      >
        {{ t('events.create') }}
      </button>
    </div>

    <!-- ── Liste ────────────────────────────────────────────────────────────── -->
    <div v-if="!loading" class="events-list">
      <p v-if="events.length === 0" class="events-list__empty">
        {{ t('events.empty') }}
      </p>
      <SgiltCard
        v-for="event in events"
        :key="event.id"
        :image="coverImage(event)"
        :ratio="cardRatio"
        @click="navigateTo(`/app/events/${event.id}`)"
      >
        <template #overlay>
          <h2 class="event-card__title">{{ event.title }}</h2>
          <p class="event-card__meta">
            <span v-if="event.date">{{ formatDate(event.date) }}</span>
            <span v-if="event.date && event.ville" aria-hidden="true"> · </span>
            <span v-if="event.ville">{{ event.ville }}</span>
          </p>
          <p class="event-card__summary">{{ reservationSummary(event) }}</p>
          <span class="event-card__cta" aria-hidden="true">{{ t('events.see-event') }}</span>
        </template>
      </SgiltCard>
    </div>

    <!-- Skeleton -->
    <div v-else class="events-list">
      <div v-for="i in 2" :key="i" class="event-card-skeleton">
        <Sk class="event-card-skeleton__cover" />
        <div class="event-card-skeleton__footer">
          <Sk width="60%" height="1.2rem" radius="4px" />
          <Sk width="40%" height="0.85rem" radius="4px" style="margin-top: 6px" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { EventSummary } from '~/data/evenement/domain/EventSummary'
import { useEvenements } from '~/data/evenement/useEvenements'
import { BANK_IMAGE_PATHS } from '~/utils/eventCovers'
import SgiltCard from '~/components/basics/cards/SgiltCard.vue'
import Sk from '~/components/basics/Sk.vue'

definePageMeta({ layout: 'app' })

useHead({ title: 'Mes événements' })

const { t } = useI18n()
const { isDesktop } = useDevice()
const cardRatio = computed(() => (isDesktop.value ? '16/9' : '3/2'))

// Sur desktop, /app intègre la liste — on redirige
onMounted(() => {
  if (isDesktop.value) navigateTo('/app', { replace: true })
})

// ── Data ──────────────────────────────────────────────────────────────────────
const { events, loading } = useEvenements()

// ── Cover images ──────────────────────────────────────────────────────────────
const { toUrl } = useImageUrl()

function coverImage(event: EventSummary): string {
  const imagePath =
    event.coverImage ?? BANK_IMAGE_PATHS[event.eventType ?? ''] ?? BANK_IMAGE_PATHS.autre!
  return toUrl(imagePath)
}

// ── Résumé réservations ───────────────────────────────────────────────────────
function reservationSummary(event: EventSummary): string {
  const parts: string[] = []
  if (event.confirmedCount)
    parts.push(`✓ ${t('events.confirmed', event.confirmedCount, { n: event.confirmedCount })}`)
  if (event.inDiscussionCount)
    parts.push(t('events.in-progress', event.inDiscussionCount, { n: event.inDiscussionCount }))
  return parts.join(' · ') || '—'
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;

.events-page {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: 0 $spacing-m calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $spacing-m);
  background-color: #f5f5f3;

  @media (min-width: $desktop) {
    padding: 0 0 $spacing-xl;
    gap: 0;
  }
}

// ── En-tête ───────────────────────────────────────────────────────────────────
.events-page__header {
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
    padding: 24px max(40px, calc((100% - 1200px) / 2 + 40px));
    box-shadow: none;
    border-bottom: 1px solid $divider-color;
  }
}

.events-page__count {
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  color: $text-secondary;
  margin: 0;
}

.events-page__create-btn {
  align-self: flex-end;
  padding: 7px 14px;
  border: 1px solid $brand-border;
  border-radius: $radius-md;
  background: transparent;
  font-family: inherit;
  font-size: 0.8rem;
  font-weight: 600;
  color: $brand-primary;
  cursor: pointer;
  white-space: nowrap;
  transition:
    background 150ms ease,
    border-color 150ms ease;

  &:active {
    background: $surface-soft;
    border-color: $brand-primary;
  }
}

// ── Liste ─────────────────────────────────────────────────────────────────────
.events-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;

  @media (min-width: $desktop) {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 24px;
    max-width: 1200px;
    width: 100%;
    margin: 0 auto;
    padding: 32px 40px;

    &:has(> :only-child) {
      grid-template-columns: 1fr;
      max-width: 600px;
    }
  }
}

.events-list__empty {
  font-size: 0.875rem;
  color: $text-secondary;
  font-style: italic;
  text-align: center;
  padding: $spacing-xl 0;
}

// ── Cards ──────────────────────────────────────────────────────────────────────
.events-list :deep(.sgilt-card) {
  box-shadow: 0 4px 20px rgba(47, 42, 37, 0.12);
  transition: box-shadow 200ms ease;

  &:hover {
    box-shadow: 0 8px 32px rgba(47, 42, 37, 0.18);
  }
}

// ── Contenu overlay (slot rendu dans le scope parent) ─────────────────────────
.event-card__title {
  font-family: 'Cormorant Garamond', serif;
  font-size: 1.375rem;
  font-weight: 600;
  color: #fff;
  margin: 0;
  line-height: 1.2;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.25);
}

.event-card__meta {
  font-family: 'Inter', sans-serif;
  font-size: 0.78rem;
  color: rgba(255, 255, 255, 0.82);
  margin: 3px 0 0;
}

.event-card__summary {
  font-family: 'Inter', sans-serif;
  font-size: 0.72rem;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.75);
  margin: 5px 0 0;
}

.event-card__cta {
  display: inline-block;
  margin-top: $spacing-s;
  padding: 6px 14px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: $radius-xl;
  font-family: 'Inter', sans-serif;
  font-size: 0.75rem;
  font-weight: 600;
  color: #fff;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(4px);
  pointer-events: none;
  align-self: flex-start;
}

// ── Skeleton ──────────────────────────────────────────────────────────────────
.event-card-skeleton {
  border-radius: $radius-lg;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 2px 12px rgba(47, 42, 37, 0.06);

  &__cover {
    width: 100%;
    aspect-ratio: 3/2;

    @media (min-width: $desktop) {
      aspect-ratio: 16/9;
    }
  }

  &__footer {
    padding: $spacing-s;
  }
}
</style>
