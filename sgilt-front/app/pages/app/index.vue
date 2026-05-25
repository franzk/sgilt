<template>
  <div class="home-page">

    <!-- ── Accueil ──────────────────────────────────────────────────────────── -->
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
    </div>

    <!-- ── Préparatifs en cours ─────────────────────────────────────────────── -->
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
          @click="navigateTo(`/app/events/${item.evenementId}/reservations/${item.reservationId}`)"
        />
      </div>

      <!-- Tout confirmé -->
      <p v-else-if="activeData?.hasConfirmed" class="home-page__message home-page__message--success">
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

    <!-- ── Prochain événement ────────────────────────────────────────────────── -->
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

    <!-- ── Vos événements ────────────────────────────────────────────────────── -->
    <section class="home-page__section">
      <NuxtLink class="home-page__events-link" to="/app/events">
        <span>{{ $t('home.section-events') }}</span>
        <span aria-hidden="true">›</span>
      </NuxtLink>
    </section>

  </div>
</template>

<script setup lang="ts">
import DemandeItem from '~/components/app/DemandeItem.vue'
import EventItem from '~/components/app/EventItem.vue'
import { useActiveReservations } from '~/data/reservation/useActiveReservations'
import { useEvenements } from '~/data/evenement/useEvenements'

definePageMeta({ layout: 'app' })
useHead({ title: 'Accueil' })

const currentUser = useCurrentUser()

// ── Salutation ────────────────────────────────────────────────────────────────
const greetingText = computed(() => {
  const h = new Date().getHours()
  const salut = h >= 6 && h < 18 ? 'Bonjour' : 'Bonsoir'
  return currentUser.firstName ? `${salut} ${currentUser.firstName}.` : `${salut}.`
})

// ── Demandes actives ──────────────────────────────────────────────────────────
const { data: activeData, loading: loadingActive } = useActiveReservations()

const pendingItems = computed(() =>
  activeData.value?.items.filter((i) => i.status === 'nouvelle' || i.status === 'en_discussion') ?? [],
)

// ── Prochain événement ────────────────────────────────────────────────────────
const { events } = useEvenements()

const nextEvent = computed(() => {
  const now = new Date()
  return (
    events.value
      .filter((e) => e.date && e.date >= now)
      .sort((a, b) => a.date!.getTime() - b.date!.getTime())[0] ?? null
  )
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;

.home-page {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: 0 $spacing-m calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $spacing-m);
  background-color: #f5f5f3;

  @media (min-width: $desktop) {
    padding: 0 0 $spacing-xl;
    gap: $spacing-xl;
    max-width: 680px;
    margin: 0 auto;
    width: 100%;
  }
}

// ── Header ────────────────────────────────────────────────────────────────────
.home-page__header {
  background: $surface-white;
  padding: $spacing-m;
  margin: 0 (-$spacing-m);
  box-shadow: 0 1px 4px $shadow-s;

  @media (min-width: $desktop) {
    margin: 0;
    padding: $spacing-xl $spacing-m $spacing-m;
    box-shadow: none;
    border-bottom: 1px solid $divider-color;
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
  padding: 8px 16px;
  border: 1px solid $brand-border;
  border-radius: $radius-md;
  background: transparent;
  font-family: inherit;
  font-size: 0.875rem;
  font-weight: 600;
  color: $brand-primary;
  cursor: pointer;
  transition: background 150ms ease;

  &:active {
    background: $surface-soft;
  }
}

// ── Skeleton demandes ─────────────────────────────────────────────────────────
.demande-skeleton {
  height: 72px;
  border-radius: $radius-lg;
}

// ── Lien événements ───────────────────────────────────────────────────────────
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
</style>
