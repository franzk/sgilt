<template>
  <div class="home-page">
    <!-- ── En-tête ──────────────────────────────────────────────────────────── -->
    <div class="home-page__header">
      <p v-if="!currentUser.loading" class="home-page__greeting">
        {{ greetingText }}
      </p>
      <Sk v-else class="home-page__greeting-skeleton" aria-hidden="true" />
      <p class="home-page__subtitle">{{ $t('home.subtitle') }}</p>
      <p class="create-btn-mobile-wrapper">
        <button
          class="home-page__create-btn home-page__create-btn--mobile"
          type="button"
          @click="useFlow().start('new-event', $t('home.new-event-flow'))"
        >
          {{ $t('events.create') }}
        </button>
      </p>
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
          <Sk v-for="i in 2" :key="i" class="demande-skeleton" />
        </div>

        <!-- Demandes en suspens -->
        <template v-else-if="pendingItems.length > 0">
          <div class="home-page__items" :class="{ expanded: pendingExpanded }">
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
          <button
            v-if="hiddenCount > 0"
            class="home-page__show-more"
            type="button"
            @click="pendingExpanded = !pendingExpanded"
          >
            {{
              pendingExpanded
                ? t('home.reduce')
                : t('home.show-more', hiddenCount, { n: hiddenCount })
            }}
          </button>
        </template>

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
          format="big"
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

        <!-- Prochain événement — Big Card -->
        <template v-if="nextEvent">
          <h2 class="home-page__events-label">{{ $t('home.section-next') }}</h2>
          <EventItem
            format="big"
            :title="nextEvent.title"
            :date="nextEvent.date"
            :ville="nextEvent.ville"
            :cover-image="nextEvent.coverImage"
            :event-type="nextEvent.eventType"
            :summary="reservationSummary(nextEvent)"
            @click="navigateTo(`/app/events/${nextEvent!.id}`)"
          />
        </template>

        <!-- Autres événements — Little Cards -->
        <template v-if="otherEvents.length > 0">
          <h2 class="home-page__events-label">{{ $t('home.section-events') }}</h2>

          <EventItem
            v-for="event in otherEvents"
            :key="event.id"
            format="small"
            :title="event.title"
            :date="event.date"
            :ville="event.ville"
            :cover-image="event.coverImage"
            :event-type="event.eventType"
            @click="navigateTo(`/app/events/${event.id}`)"
          />
        </template>
      </template>

      <!-- Skeleton -->
      <template v-else>
        <Sk class="event-card-skeleton" />
        <Sk v-for="i in 2" :key="i" class="event-small-skeleton" />
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import DemandeItem from '~/components/app/DemandeItem.vue'
import EventItem from '~/components/app/EventItem.vue'
import { useActiveReservations } from '~/data/reservation/useActiveReservations'
import { useEvenements } from '~/data/evenement/useEvenements'
import type { EventSummary } from '~/data/evenement/domain/EventSummary'
import Sk from '~/components/basics/Sk.vue'

definePageMeta({ layout: 'app' })
useHead({ title: 'Accueil' })

const { t } = useI18n()
const currentUser = useCurrentUser()

// ── Salutation ────────────────────────────────────────────────────────────────
const greetingText = computed(() => {
  const h = new Date().getHours()
  const key = h >= 6 && h < 18 ? 'home.welcome' : 'home.welcome-night'
  return t(key, { name: currentUser.firstName ?? '' })
})

// ── Demandes actives ──────────────────────────────────────────────────────────
const { data: activeData, loading: loadingActive } = useActiveReservations()

const pendingItems = computed(
  () =>
    activeData.value?.items.filter(
      (i) => i.status === 'nouvelle' || i.status === 'en_discussion',
    ) ?? [],
)

const PENDING_LIMIT = 3
const pendingExpanded = ref(false)
const hiddenCount = computed(() => Math.max(0, pendingItems.value.length - PENDING_LIMIT))

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

const otherEvents = computed(() =>
  events.value
    .filter((e) => e.id !== nextEvent.value?.id)
    .sort((a, b) => {
      if (!a.date) return 1
      if (!b.date) return -1
      return a.date.getTime() - b.date.getTime()
    }),
)

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
  margin-top: 4px;
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

.create-btn-mobile-wrapper {
  display: flex;
  justify-content: flex-end;
  margin: $spacing-s 0 0 0;
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
  background-color: #f7f9fb;

  @media (min-width: $desktop) {
    display: flex;
    flex-direction: column;
    gap: $spacing-m;
    grid-area: events;
    padding: $spacing-l;
  }
}

.home-page__events-label {
  display: none;

  @media (min-width: $desktop) {
    display: block;
    font-family: 'Inter', sans-serif;
    font-size: 0.7rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: $text-secondary;
    margin: 0;
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

.home-page__items {
  @media (max-width: calc(#{$desktop} - 1px)) {
    &:not(.expanded) > *:nth-child(n + 4) {
      display: none;
    }
  }
}

.home-page__show-more {
  appearance: none;
  border: none;
  background: none;
  font: inherit;
  padding: 0;
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  font-weight: 600;
  color: $brand-primary;
  cursor: pointer;
  align-self: flex-start;

  @media (min-width: $desktop) {
    display: none;
  }
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

  &--mobile {
    align-self: flex-end;

    @media (min-width: $desktop) {
      display: none;
    }
  }

  &:active {
    background: $surface-soft;
    border-color: $brand-primary;
  }
}

// ── Skeleton ──────────────────────────────────────────────────────────────────
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

  &.format-big {
    width: 50%;
    align-self: center;
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

.event-card-skeleton {
  border-radius: $radius-lg;
  aspect-ratio: 16/9;
}

.event-small-skeleton {
  height: 64px;
  border-radius: $radius-lg;
}
</style>
