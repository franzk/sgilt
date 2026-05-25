<template>
  <div class="home-page">
    <!-- ── En-tête ──────────────────────────────────────────────────────────── -->
    <div class="home-page__header">
      <p v-if="!currentUser.loading" class="home-page__greeting">
        {{ greetingText }}
      </p>
      <div
        v-else
        class="skeleton-text shimmer-container home-page__greeting-skeleton"
        aria-hidden="true"
      />
      <p class="home-page__subtitle">{{ $t('home.subtitle') }}</p>
      <!-- Desktop uniquement : compte + bouton créer -->
      <div class="home-page__header-actions">
        <p class="home-page__events-count">
          {{ t('home.events-count', events.length, { n: events.length }) }}
        </p>
        <button
          class="home-page__create-btn"
          type="button"
          @click="useFlow().start('new-event', $t('home.new-event-flow'))"
        >
          {{ $t('events.create') }}
        </button>
      </div>
    </div>

    <!-- ── Préparatifs (mobile : inline, desktop : colonne droite sticky) ─── -->
    <aside class="home-page__sidebar">
      <section class="home-page__section">
        <h2 class="home-page__section-title">{{ $t('home.section-active') }}</h2>

        <!-- Chargement -->
        <div v-if="loadingActive" class="home-page__skeletons">
          <div v-for="i in 2" :key="i" class="demande-skeleton shimmer-container" />
        </div>

        <!-- Demandes en suspens -->
        <div v-else-if="pendingItems.length > 0" class="home-page__items">
          <DemandeItem
            v-for="item in pendingItems"
            :key="item.reservationId"
            :prestataire-name="item.prestataireName"
            :prestataire-avatar="item.prestataireAvatar"
            :evenement-title="item.evenementTitle"
            :status="item.status"
            @click="
              navigateTo(`/app/events/${item.evenementId}/reservations/${item.reservationId}`)
            "
          />
        </div>

        <!-- Tout confirmé -->
        <p
          v-else-if="activeData?.hasConfirmed"
          class="home-page__message home-page__message--success"
        >
          {{ $t('home.all-confirmed') }}
        </p>

        <!-- Rien en cours -->
        <div v-else class="home-page__empty">
          <p class="home-page__message">{{ $t('home.empty-message') }}</p>
          <button
            class="home-page__create-btn"
            type="button"
            @click="useFlow().start('new-event', $t('home.new-event-flow'))"
          >
            {{ $t('events.create') }}
          </button>
        </div>
      </section>
    </aside>

    <!-- ── Sections mobiles uniquement ──────────────────────────────────────── -->
    <div class="home-page__mobile-sections">
      <!-- Prochain événement -->
      <section v-if="nextEvent" class="home-page__section">
        <h2 class="home-page__section-title">{{ $t('home.section-next') }}</h2>
        <EventItem
          :title="nextEvent.title"
          :date="nextEvent.date"
          :ville="nextEvent.ville"
          :cover-image="nextEvent.coverImage"
          :event-type="nextEvent.eventType"
          @click="navigateTo(`/app/events/${nextEvent!.id}`)"
        />
      </section>

      <!-- Lien événements -->
      <section class="home-page__section">
        <NuxtLink class="home-page__events-link" to="/app/events">
          <span>{{ $t('home.section-events') }}</span>
          <span aria-hidden="true">›</span>
        </NuxtLink>
      </section>
    </div>

    <!-- ── Liste événements (desktop uniquement, colonne gauche) ────────────── -->
    <div class="home-page__events-list">
      <template v-if="!loading">
        <p v-if="events.length === 0" class="events-empty">
          {{ $t('events.empty') }}
        </p>
        <SgiltCard
          v-for="event in events"
          :key="event.id"
          :image="coverImage(event)"
          ratio="3/2"
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
            <span class="event-card__cta" aria-hidden="true">{{ $t('events.see-event') }}</span>
          </template>
        </SgiltCard>
      </template>

      <!-- Skeleton -->
      <template v-else>
        <div v-for="i in 2" :key="i" class="event-card-skeleton">
          <div class="event-card-skeleton__cover skeleton-text" />
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import DemandeItem from '~/components/app/DemandeItem.vue'
import EventItem from '~/components/app/EventItem.vue'
import SgiltCard from '~/components/basics/cards/SgiltCard.vue'
import { BANK_IMAGE_PATHS } from '~/utils/eventCovers'
import { useActiveReservations } from '~/data/reservation/useActiveReservations'
import { useEvenements } from '~/data/evenement/useEvenements'
import type { EventSummary } from '~/data/evenement/domain/EventSummary'

definePageMeta({ layout: 'app' })
useHead({ title: 'Accueil' })

const { t } = useI18n()
const currentUser = useCurrentUser()
const { toUrl } = useImageUrl()

// ── Salutation ────────────────────────────────────────────────────────────────
const greetingText = computed(() => {
  const h = new Date().getHours()
  const salut = h >= 6 && h < 18 ? 'Bonjour' : 'Bonsoir'
  return currentUser.firstName ? `${salut} ${currentUser.firstName}.` : `${salut}.`
})

// ── Demandes actives ──────────────────────────────────────────────────────────
const { data: activeData, loading: loadingActive } = useActiveReservations()

const pendingItems = computed(
  () =>
    activeData.value?.items.filter(
      (i) => i.status === 'nouvelle' || i.status === 'en_discussion',
    ) ?? [],
)

// ── Événements ────────────────────────────────────────────────────────────────
const { events, loading } = useEvenements()

const nextEvent = computed(() => {
  const now = new Date()
  return (
    events.value
      .filter((e) => e.date && e.date >= now)
      .sort((a, b) => a.date!.getTime() - b.date!.getTime())[0] ?? null
  )
})

function coverImage(event: EventSummary): string {
  const path =
    event.coverImage ?? BANK_IMAGE_PATHS[event.eventType ?? ''] ?? BANK_IMAGE_PATHS.autre!
  return toUrl(path)
}

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

// ── Layout ────────────────────────────────────────────────────────────────────
.home-page {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: 0 $spacing-m calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $spacing-m);
  background-color: #f5f5f3;

  @media (min-width: $desktop) {
    display: grid;
    grid-template-columns: 2fr 1fr;
    grid-template-areas:
      'header  sidebar'
      'events  sidebar';
    grid-template-rows: auto 1fr;
    gap: 0;
    padding: 0;
    min-height: 0;
    height: calc(100vh - $app-header-height);
  }
}

// ── En-tête ───────────────────────────────────────────────────────────────────
.home-page__header {
  background: $surface-white;
  padding: $spacing-m;
  margin: 0 (-$spacing-m);
  box-shadow: 0 1px 4px $shadow-s;

  @media (min-width: $desktop) {
    grid-area: header;
    margin: 0;
    padding: $spacing-xl $spacing-l $spacing-l;
    box-shadow: none;
    border-bottom: 1px solid $divider-color;
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
  }
}

.home-page__greeting {
  font-family: 'Cormorant Garamond', serif;
  font-size: 2rem;
  font-weight: 600;
  color: $brand-primary;
  margin: 0;
  line-height: 1.15;
}

.home-page__greeting-skeleton {
  height: 2.3rem;
  width: 15rem;
  border-radius: 4px;
}

.home-page__subtitle {
  font-family: 'Inter', sans-serif;
  font-size: 0.85rem;
  color: $text-secondary;
  margin: $spacing-xs 0 0;

  @media (min-width: $desktop) {
    margin: 0;
  }
}

.home-page__header-actions {
  display: none;

  @media (min-width: $desktop) {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $spacing-s;
    margin-top: $spacing-xs;
  }
}

.home-page__events-count {
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  color: $text-secondary;
  margin: 0;
}

// ── Préparatifs sidebar ───────────────────────────────────────────────────────
.home-page__sidebar {
  @media (min-width: $desktop) {
    grid-area: sidebar;
    overflow-y: auto;
    background: $surface-white;
    border-left: 1px solid $divider-color;
    padding: $spacing-l;
  }
}

// ── Sections mobiles ──────────────────────────────────────────────────────────
.home-page__mobile-sections {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;

  @media (min-width: $desktop) {
    display: none;
  }
}

// ── Liste événements desktop ──────────────────────────────────────────────────
.home-page__events-list {
  display: none;

  @media (min-width: $desktop) {
    display: grid;
    grid-template-columns: 1fr 1fr;
    align-content: start;
    gap: $spacing-m;
    grid-area: events;
    padding: $spacing-l;

    &:has(> :only-child) {
      grid-template-columns: 1fr;
    }
  }
}

// ── Sections ──────────────────────────────────────────────────────────────────
.home-page__section {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

.home-page__section-title {
  font-family: 'Inter', sans-serif;
  font-size: 0.7rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: $text-secondary;
  margin: 0;
}

// ── Items list ────────────────────────────────────────────────────────────────
.home-page__items,
.home-page__skeletons {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

// ── Messages ──────────────────────────────────────────────────────────────────
.home-page__message {
  font-family: 'Inter', sans-serif;
  font-size: 0.875rem;
  color: $text-secondary;
  margin: 0;

  &--success {
    color: $state-success;
    font-weight: 500;
  }
}

// ── Empty state ───────────────────────────────────────────────────────────────
.home-page__empty {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

.home-page__create-btn {
  align-self: flex-start;
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

// ── Skeleton demandes ─────────────────────────────────────────────────────────
.demande-skeleton {
  height: 72px;
  border-radius: $radius-lg;
}

// ── Lien événements (mobile) ──────────────────────────────────────────────────
.home-page__events-link {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-m;
  background: $surface-white;
  border-radius: $radius-lg;
  box-shadow: 0 2px 12px rgba(47, 42, 37, 0.06);
  text-decoration: none;
  font-family: 'Inter', sans-serif;
  font-size: 0.875rem;
  font-weight: 600;
  color: $brand-primary;
  transition: background 120ms ease;

  &:active {
    background: $surface-soft;
  }
}

// ── Event cards (liste desktop) ───────────────────────────────────────────────
.home-page__events-list :deep(.sgilt-card) {
  box-shadow: 0 4px 20px rgba(47, 42, 37, 0.12);
  transition: box-shadow 200ms ease;

  &:hover {
    box-shadow: 0 8px 32px rgba(47, 42, 37, 0.18);
  }
}

.events-empty {
  grid-column: 1 / -1;
  font-size: 0.875rem;
  color: $text-secondary;
  font-style: italic;
  text-align: center;
  padding: $spacing-xl 0;
}

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

.event-card-skeleton {
  border-radius: $radius-lg;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(47, 42, 37, 0.06);

  &__cover {
    width: 100%;
    aspect-ratio: 3/2;
  }
}
</style>
