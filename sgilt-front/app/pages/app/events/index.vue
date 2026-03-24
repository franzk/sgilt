<template>
  <div class="events-page">
    <!-- ── En-tête ──────────────────────────────────────────────────────────── -->
    <div class="events-page__header">
      <p class="events-page__greeting">{{ t('events-list.welcome', { name: 'Julie' }) }}</p>
      <div class="events-page__subheader">
        <p class="events-page__count">
          {{ t('events-list.count', events.length, { n: events.length }) }}
        </p>
        <button class="events-page__create-btn" type="button">
          {{ t('events-list.create') }}
        </button>
      </div>
    </div>

    <!-- ── Liste ────────────────────────────────────────────────────────────── -->
    <div v-if="!loading" class="events-list">
      <p v-if="events.length === 0" class="events-list__empty">
        {{ t('events-list.empty') }}
      </p>
      <SgiltCard
        v-for="event in events"
        :key="event.id"
        :image="coverImage(event.eventType)"
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
        </template>
      </SgiltCard>
    </div>

    <!-- Skeleton -->
    <div v-else class="events-list">
      <div v-for="i in 2" :key="i" class="event-card-skeleton">
        <div class="event-card-skeleton__cover skeleton-text" />
        <div class="event-card-skeleton__footer">
          <div class="skeleton-text" style="width: 60%; height: 1.2rem; border-radius: 4px" />
          <div
            class="skeleton-text"
            style="width: 40%; height: 0.85rem; border-radius: 4px; margin-top: 6px"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { EventMockService } from '~/services/event.mock'
import type { EventDetail } from '~/types/event'
import SgiltCard from '~/components/basics/cards/SgiltCard.vue'

definePageMeta({ layout: 'app' })

const { t } = useI18n()
const { isDesktop } = useDevice()
const cardRatio = computed(() => (isDesktop.value ? '16/9' : '3/2'))

// ── Data ──────────────────────────────────────────────────────────────────────
const events = ref<EventDetail[]>([])
const loading = ref(true)

onMounted(async () => {
  events.value = await EventMockService.getAll()
  loading.value = false
})

// ── Cover images ──────────────────────────────────────────────────────────────
const COVER_IMAGES: Record<string, string> = {
  mariage:
    'https://images.unsplash.com/photo-1519741497674-611481863552?w=800&auto=format&fit=crop',
  anniversaire:
    'https://images.unsplash.com/photo-1530103862676-de8c9debad1d?w=800&auto=format&fit=crop',
  soiree: 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=800&auto=format&fit=crop',
  entreprise:
    'https://images.unsplash.com/photo-1511578314322-379afb476865?w=800&auto=format&fit=crop',
  autre: 'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800&auto=format&fit=crop',
}

function coverImage(eventType?: string) {
  return COVER_IMAGES[eventType ?? ''] ?? COVER_IMAGES.autre
}

// ── Résumé réservations ───────────────────────────────────────────────────────
function reservationSummary(event: EventDetail): string {
  const r = event.reservations
  const confirmed = r.filter((x) => x.status === 'confirmee').length
  const inProgress = r.filter((x) => x.status === 'en_discussion').length

  const parts: string[] = []
  if (confirmed) parts.push(`✓ ${t('events-list.confirmed', confirmed, { n: confirmed })}`)
  if (inProgress) parts.push(t('events-list.in-progress', inProgress, { n: inProgress }))

  return parts.join(' · ') || '—'
}

// ── Formatage ─────────────────────────────────────────────────────────────────
function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  })
}
</script>

<style scoped lang="scss">
$desktop: 900px;

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

.events-page__greeting {
  font-family: 'Cormorant Garamond', serif;
  font-size: 2rem;
  font-weight: 600;
  color: $brand-primary;
  margin: 0;
  line-height: 1.15;
}

.events-page__subheader {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-s;
}

.events-page__count {
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  color: $text-secondary;
  margin: 0;
  flex: 1;
}

.events-page__create-btn {
  flex-shrink: 0;
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
  }
}

.events-list__empty {
  font-size: 0.875rem;
  color: $text-secondary;
  font-style: italic;
  text-align: center;
  padding: $spacing-xl 0;
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
