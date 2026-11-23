<!--
  Accueil de l'espace client (/app) — onglet « Mes événements ».
  Salutation, bouton de création, puis les événements : le prochain en grande carte, les autres
  en petites cartes.
  Mobile : les réservations à suivre sont sur l'onglet /app/a-suivre.
  Desktop : elles occupent la colonne droite (ToFollowList).
-->
<template>
  <div class="home-page">
    <div class="main">
      <!-- ── En-tête ──────────────────────────────────────────────────────────── -->
      <div class="header">
        <div class="text">
          <p v-if="!currentUser.loading" class="greeting">{{ greetingText }}</p>
          <Sk v-else class="greeting-skeleton" aria-hidden="true" />
          <p class="subtitle">{{ t('home.subtitle') }}</p>
        </div>
        <SgiltButton
          variant="primary"
          class="create-btn"
          @click="useFlow().start('new-event', t('home.new-event-flow'))"
        >
          <AddIcon class="create-icon" aria-hidden="true" />
          {{ t('events.create') }}
        </SgiltButton>
      </div>

      <!-- ── Mes événements ───────────────────────────────────────────────────── -->
      <section class="events">
        <SectionHeading :title="t('home.section-my-events')" />

        <template v-if="!loading">
          <p v-if="events.length === 0" class="empty">{{ t('events.empty') }}</p>

          <EventItem
            v-if="nextEvent"
            format="big"
            :ratio="isDesktop ? '3/1' : '3/2'"
            :title="nextEvent.title"
            :date="nextEvent.date"
            :ville="nextEvent.ville"
            :cover-image="nextEvent.coverImage"
            :event-type="nextEvent.eventType"
            :badge="t('home.next-event-badge')"
            :summary="reservationSummary(nextEvent)"
            @click="navigateTo(`/app/events/${nextEvent!.id}`)"
          />

          <EventItem
            v-for="event in otherEvents"
            :key="event.id"
            format="small"
            :title="event.title"
            :date="event.date"
            :ville="event.ville"
            :cover-image="event.coverImage"
            :event-type="event.eventType"
            :badge="inProgressBadge(event)"
            @click="navigateTo(`/app/events/${event.id}`)"
          />
        </template>

        <!-- Skeleton -->
        <template v-else>
          <Sk class="big-skeleton" />
          <Sk v-for="i in 2" :key="i" class="small-skeleton" />
        </template>
      </section>
    </div>

    <!-- ── À suivre (desktop ; sur mobile, c'est l'onglet /app/a-suivre) ──────── -->
    <aside v-if="isDesktop" class="sidebar">
      <ToFollowList />
    </aside>
  </div>
</template>

<script setup lang="ts">
import { AddIcon } from '@remixicons/vue/line'
import EventItem from '~/components/app/EventItem.vue'
import SectionHeading from '~/components/app/SectionHeading.vue'
import ToFollowList from '~/components/app/ToFollowList.vue'
import Sk from '~/components/basics/Sk.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import type { EventSummary } from '~/data/evenement/domain/EventSummary'
import { useEvenements } from '~/data/evenement/useEvenements'

definePageMeta({ layout: 'app' })
useHead({ title: 'Accueil' })

const { t } = useI18n()
const currentUser = useCurrentUser()
const { isDesktop } = useDevice()

// ── Salutation ────────────────────────────────────────────────────────────────
const greetingText = computed(() => {
  const h = new Date().getHours()
  const key = h >= 6 && h < 18 ? 'home.welcome' : 'home.welcome-night'
  return t(key, { name: currentUser.firstName ?? '' })
})

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

// Pastille de la grande carte : rien tant qu'aucune réservation n'est confirmée
function reservationSummary(event: EventSummary): string | undefined {
  if (!event.confirmedCount) return undefined
  return t('home.confirmed-summary', { n: event.confirmedCount }, event.confirmedCount)
}

// Badge des petites cartes : les échanges en cours sur l'événement
function inProgressBadge(event: EventSummary): string | undefined {
  if (!event.inDiscussionCount) return undefined
  return t('home.in-progress-badge', { n: event.inDiscussionCount }, event.inDiscussionCount)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;

.home-page {
  min-height: 100%;
  background-color: $brand-background-alt;

  @media (min-width: $desktop) {
    display: grid;
    grid-template-columns: 5fr 3fr;
    min-height: 0;
    height: calc(100vh - $app-header-height);
  }

  .main {
    display: flex;
    flex-direction: column;
    gap: $spacing-l;
    padding: $spacing-m $spacing-m calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $spacing-m);

    @media (min-width: $desktop) {
      overflow-y: auto;
      padding: $spacing-xl;
    }

    .header {
      display: flex;
      flex-direction: column;
      gap: $spacing-m;

      @media (min-width: $desktop) {
        flex-direction: row;
        align-items: center;
        justify-content: space-between;
      }

      .text {
        display: flex;
        flex-direction: column;
        gap: $spacing-xs;

        .greeting {
          font-family: 'Cormorant Garamond', serif;
          font-size: 2rem;
          font-weight: 600;
          color: $brand-primary;
          margin: 0;
          line-height: 1.15;

          @media (min-width: $desktop) {
            font-size: 2.5rem;
          }
        }

        .greeting-skeleton {
          height: 2.3rem;
          width: 15rem;
          border-radius: 0.25rem;
        }

        .subtitle {
          font-family: 'Inter', sans-serif;
          font-size: 0.95rem;
          color: $text-secondary;
          margin: 0;
        }
      }

      // Variante maquette : pilule jaune unie, texte foncé
      // (.primary pour passer devant le style de SgiltButton)
      .create-btn.primary {
        align-self: flex-end;
        border-radius: 999px;
        padding: 0 1.25rem;
        background: $brand-accent;
        border: none;
        box-shadow: none;
        color: $brand-primary;
        font-weight: 600;
        text-shadow: none;

        @media (min-width: $desktop) {
          align-self: center;
          height: 3rem;
          padding: 0 1.75rem;
          font-size: 1rem;
        }

        .create-icon {
          width: 1.1rem;
          height: 1.1rem;
        }
      }
    }

    .events {
      display: flex;
      flex-direction: column;
      gap: $spacing-s;

      .empty {
        font-size: 0.875rem;
        color: $text-secondary;
        font-style: italic;
        text-align: center;
        padding: $spacing-xl 0;
      }

      .big-skeleton {
        border-radius: $radius-xl;
        aspect-ratio: 3/2;

        @media (min-width: $desktop) {
          aspect-ratio: 3/1;
        }
      }

      .small-skeleton {
        height: 5.5rem;
        border-radius: $radius-md;
      }
    }
  }

  .sidebar {
    overflow-y: auto;
    border-left: 1px solid $divider-color;
    padding: $spacing-xl;
  }
}
</style>
