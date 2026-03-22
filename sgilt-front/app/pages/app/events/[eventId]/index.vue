<template>
  <div class="event-board">
    <!-- ── Bandeau couverture ──────────────────────────────────────────────────── -->
    <div
      v-if="event"
      ref="bannerRef"
      class="cover-banner"
      :style="{ backgroundImage: `url(${coverImage})` }"
    >
      <div class="cover-banner__overlay" />
      <span class="cover-banner__title">{{ event.title }}</span>
      <button class="cover-banner__edit-img" type="button">Modifier l'image</button>
    </div>

    <template v-if="event">
      <!-- ── Widget ─────────────────────────────────────────────────────────────── -->
      <div class="event-widget">
        <p class="event-widget__phrase">{{ event.phrase }}</p>
        <p class="event-widget__subtitle">{{ event.phraseSubtitle }}</p>
        <div class="event-widget__pills">
          <span
            v-for="pill in statusPills"
            :key="pill.status"
            class="status-pill"
            :style="{
              background: RESERVATION_STATUS_CONFIG[pill.status].bgColor,
              color: RESERVATION_STATUS_CONFIG[pill.status].color,
            }"
          >
            <span class="status-pill__icon" aria-hidden="true">{{ pill.icon }}</span>
            <span class="status-pill__count">{{ pill.count }}</span>
            <span class="status-pill__label">{{ pill.label }}</span>
          </span>
        </div>
      </div>

      <!-- ── Contenu ────────────────────────────────────────────────────────────── -->
      <div class="board-content">
        <!-- Bloc événement (accordéon mobile) -->
        <div class="event-block-wrap">
          <button
            class="event-block-toggle"
            type="button"
            @click="eventBlockOpen = !eventBlockOpen"
          >
            <span>Détails de l'événement</span>
            <span
              class="event-block-toggle__chevron"
              :class="{ 'event-block-toggle__chevron--open': eventBlockOpen }"
            >
              ▼
            </span>
          </button>
          <div class="event-block-body" :class="{ 'event-block-body--open': eventBlockOpen }">
            <div class="event-block-body__inner">
              <EventBlock :event="event" @updated="onEventUpdated" />
            </div>
          </div>
        </div>

        <!-- Réservations -->
        <section class="reservations">
          <div class="reservations__grid">
            <SgiltCard
              v-for="r in sortedReservations"
              :key="r.id"
              :image="r.prestatairePhoto || FALLBACK_PHOTO"
              ratio="4/3"
              @click="navigateTo(`/app/events/${eventId}/reservations/${r.id}`)"
            >
              <template #overlay>
                <span class="res-card__category">{{ r.category }}</span>
                <span class="res-card__name">{{ r.prestataireName }}</span>
              </template>
              <template #footer>
                <div class="res-card__footer">
                  <StatusBadge :status="r.status" />
                  <span v-if="r.unreadNotesCount > 0" class="res-card__unread">
                    {{ r.unreadNotesCount }}
                  </span>
                </div>
              </template>
            </SgiltCard>
          </div>

          <button class="add-prestataire-btn" type="button" @click="goToSearch">
            + Ajouter un prestataire
          </button>
        </section>
      </div>
    </template>

    <!-- Skeleton -->
    <div v-else-if="loading" class="board-skeleton">
      <div class="skeleton-widget skeleton-text" />
      <div class="skeleton-pills">
        <div v-for="i in 3" :key="i" class="skeleton-pill skeleton-text" />
      </div>
      <div class="skeleton-card skeleton-text" />
      <div class="skeleton-card skeleton-text" />
      <div class="skeleton-card skeleton-text" />
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltCard from '~/components/basics/cards/SgiltCard.vue'

definePageMeta({ layout: 'app' })

import EventBlock from '~/components/app/EventBlock.vue'
import { EventMockService } from '~/services/event.mock'
import type { EventDetail, EventPatch, ReservationStatus } from '~/types/event'
import { RESERVATION_STATUS_ORDER as STATUS_ORDER } from '~/utils/reservationStatus'
import { RESERVATION_STATUS_CONFIG } from '~/constants/reservation-status'
import StatusBadge from '~/components/basics/StatusBadge.vue'

const FALLBACK_PHOTO =
  'https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=400&auto=format&fit=crop'

const { isDesktop } = useDevice()
const cardRatio = computed(() => (isDesktop.value ? '16/9' : '3'))

const route = useRoute()
const eventId = route.params.eventId as string

// ── Cover image ────────────────────────────────────────────────────────────────
const COVER_IMAGES: Record<string, string> = {
  mariage:
    'https://images.unsplash.com/photo-1519741497674-611481863552?w=1400&auto=format&fit=crop',
  anniversaire:
    'https://images.unsplash.com/photo-1530103862676-de8c9debad1d?w=1400&auto=format&fit=crop',
  soiree:
    'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=1400&auto=format&fit=crop',
  entreprise:
    'https://images.unsplash.com/photo-1511578314322-379afb476865?w=1400&auto=format&fit=crop',
  autre: 'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=1400&auto=format&fit=crop',
}
const coverImage = computed(() => COVER_IMAGES[event.value?.eventType ?? ''] ?? COVER_IMAGES.autre)

// ── Parallax ───────────────────────────────────────────────────────────────────
const bannerRef = ref<HTMLElement | null>(null)
let rafId: number | null = null

function onScroll() {
  if (rafId !== null) return
  rafId = requestAnimationFrame(() => {
    if (bannerRef.value) {
      bannerRef.value.style.backgroundPositionY = `calc(50% + ${window.scrollY * 0.4}px)`
    }
    rafId = null
  })
}

onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
  if (rafId !== null) cancelAnimationFrame(rafId)
})

// ── Data ──────────────────────────────────────────────────────────────────────
const event = ref<EventDetail | null>(null)
const loading = ref(true)

onMounted(async () => {
  event.value = await EventMockService.getById(eventId)
  loading.value = false
})

function onEventUpdated(patch: EventPatch) {
  if (event.value) Object.assign(event.value, patch)
}

// ── Widget — pills statut ──────────────────────────────────────────────────────
const STATUS_PILL_CONFIG: Array<{ status: ReservationStatus; icon: string; label: string }> = [
  { status: 'confirmee', icon: '✓', label: 'Confirmée(s)' },
  { status: 'recontactee', icon: '↩', label: 'Recontactée(s)' },
  { status: 'envoyee', icon: '→', label: 'En attente' },
  { status: 'brouillon', icon: '✎', label: 'Brouillon(s)' },
  { status: 'cloturee', icon: '✕', label: 'Clôturée(s)' },
  { status: 'annulee', icon: '✕', label: 'Annulée(s)' },
]

const statusPills = computed(() => {
  if (!event.value) return []
  return STATUS_PILL_CONFIG.map((config) => ({
    ...config,
    count: event.value!.reservations.filter((r) => r.status === config.status).length,
  })).filter((pill) => pill.count > 0)
})

// ── Réservations groupées ─────────────────────────────────────────────────────
const sortedReservations = computed(() => {
  if (!event.value) return []
  return [...event.value.reservations].sort(
    (a, b) => STATUS_ORDER.indexOf(a.status) - STATUS_ORDER.indexOf(b.status),
  )
})

// ── Accordéon EventBlock (mobile) ─────────────────────────────────────────────
const eventBlockOpen = ref(false)

// ── FAB → recherche avec contexte ─────────────────────────────────────────────
const { state } = useDemande()

function goToSearch() {
  if (event.value?.eventType) state.eventType = event.value.eventType
  navigateTo({
    path: '/search',
    query: { date: event.value?.date ?? undefined },
  })
}
</script>

<style scoped lang="scss">
$desktop: 900px;

.event-board {
  min-height: 100%;
  background-color: #f5f5f3;
}

// ── Bandeau couverture ─────────────────────────────────────────────────────────
.cover-banner {
  display: flex;
  position: relative;
  height: 200px;
  background-size: cover;
  background-position: center;
  align-items: flex-end;
  justify-content: space-between;
  padding: $spacing-m;

  @media (min-width: $desktop) {
    height: 33vh;
    padding: $spacing-l $spacing-xl;
  }

  &__overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(to bottom, rgba(47, 42, 37, 0.1), rgba(47, 42, 37, 0.65));
    pointer-events: none;
  }

  &__title {
    position: relative;
    font-family: 'Cormorant Garamond', serif;
    font-size: 30px;
    font-weight: 600;
    color: #fff;
    line-height: 1.1;
    text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
    max-width: 85%;

    @media (min-width: $desktop) {
      font-size: 42px;
      max-width: 70%;
    }
  }

  &__edit-img {
    display: none;

    @media (min-width: $desktop) {
      display: block;
      position: relative;
      flex-shrink: 0;
      padding: 6px 14px;
      border: 1px solid rgba(255, 255, 255, 0.5);
      border-radius: $radius-md;
      background: rgba(0, 0, 0, 0.3);
      color: rgba(255, 255, 255, 0.85);
      font-family: inherit;
      font-size: 0.8rem;
      font-weight: 500;
      cursor: pointer;
      backdrop-filter: blur(4px);
      transition: background 150ms ease;

      &:hover {
        background: rgba(0, 0, 0, 0.45);
      }
    }
  }
}

// ── Widget ─────────────────────────────────────────────────────────────────────
.event-widget {
  background: #fdfaf0;
  padding: $spacing-l $spacing-m $spacing-m;
  display: flex;
  flex-direction: column;
  gap: $spacing-s;

  @media (min-width: $desktop) {
    padding: $spacing-xl 40px $spacing-l;
  }

  &__phrase {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.75rem;
    font-weight: 600;
    color: $brand-primary;
    margin: 0;
    line-height: 1.15;

    @media (min-width: $desktop) {
      font-size: 2.2rem;
    }
  }

  &__subtitle {
    font-family: 'Inter', sans-serif;
    font-size: 0.82rem;
    color: $text-secondary;
    margin: 0;
    line-height: 1.5;
  }

  &__pills {
    display: flex;
    flex-wrap: nowrap;
    gap: $spacing-xs;
    overflow-x: auto;
    scrollbar-width: none;
    padding-bottom: 2px;
    margin-top: 4px;

    &::-webkit-scrollbar {
      display: none;
    }

    @media (min-width: $desktop) {
      flex-wrap: wrap;
      overflow: visible;
    }
  }
}

// ── Pills statut ───────────────────────────────────────────────────────────────
.status-pill {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 5px 12px;
  border-radius: 2rem;
  font-family: 'Inter', sans-serif;
  font-size: 0.78rem;
  font-weight: 600;
  white-space: nowrap;

  &__icon {
    font-size: 0.7rem;
    line-height: 1;
  }

  &__count {
    font-weight: 700;
  }

  &__label {
    font-weight: 500;
  }

}

// ── Accordéon EventBlock (mobile) ─────────────────────────────────────────────
.event-block-wrap {
  display: flex;
  flex-direction: column;
}

.event-block-toggle {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 14px $spacing-s;
  background: #fff;
  border: none;
  border-radius: $radius-md;
  font-family: 'Inter', sans-serif;
  font-size: 0.875rem;
  font-weight: 600;
  color: $brand-primary;
  cursor: pointer;
  text-align: left;
  box-shadow: 0 1px 4px rgba(47, 42, 37, 0.08);
  transition: box-shadow 150ms ease;

  &:hover {
    box-shadow: 0 2px 8px rgba(47, 42, 37, 0.12);
  }

  @media (min-width: $desktop) {
    display: none;
  }

  &__chevron {
    flex-shrink: 0;
    font-size: 0.75rem;
    color: $text-secondary;
    transition: transform 250ms ease;
    line-height: 1;

    &--open {
      transform: rotate(180deg);
    }
  }
}

.event-block-body {
  display: grid;
  grid-template-rows: 0fr;
  transition: grid-template-rows 280ms ease;

  &__inner {
    overflow: hidden;
  }

  &--open {
    grid-template-rows: 1fr;
  }

  @media (min-width: $desktop) {
    display: contents;
  }
}

// ── Contenu ───────────────────────────────────────────────────────────────────
.board-content {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: $spacing-m;

  @media (min-width: $desktop) {
    display: grid;
    grid-template-columns: 380px 1fr;
    gap: 28px;
    align-items: start;
    max-width: 1200px;
    margin: 0 auto;
    padding: 32px 40px;
  }
}

// ── Titre masqué + icône crayon repositionnée en haut à droite ────────────────
:deep(.event-block__title),
:deep(.event-block__title-input) {
  display: none;
}

:deep(.event-block) {
  position: relative;
}

:deep(.event-block__header) {
  position: absolute;
  top: $spacing-s;
  right: $spacing-s;
  width: auto;
}

:deep(.event-pills) {
  padding-right: 2.5rem;
}

// ── Event block sticky (desktop) ──────────────────────────────────────────────
@media (min-width: $desktop) {
  .event-block-wrap {
    position: sticky;
    top: 60px;
    align-self: start;
  }
}

// ── Shadow event block ─────────────────────────────────────────────────────────
:deep(.event-block) {
  box-shadow: 0 4px 20px rgba(47, 42, 37, 0.12);
}

// ── Grille réservations ───────────────────────────────────────────────────────
.reservations {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;

  @media (min-width: $desktop) {
    gap: $spacing-l;
  }
}

.reservations__grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-s;

  @media (min-width: $desktop) {
    grid-template-columns: repeat(3, 1fr);
  }
}

// ── Cards prestataires — contenu slots ────────────────────────────────────────
.res-card__category {
  font-family: 'Inter', sans-serif;
  font-size: 0.625rem;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 3px;
}

.res-card__name {
  font-family: 'Cormorant Garamond', serif;
  font-size: 1.05rem;
  font-weight: 600;
  color: #fff;
  line-height: 1.2;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

.res-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-xs;
  padding: 8px $spacing-s;
}


.res-card__unread {
  flex-shrink: 0;
  padding: 2px 7px;
  border-radius: 999px;
  background: $brand-accent;
  color: $brand-primary;
  font-family: 'Inter', sans-serif;
  font-size: 0.625rem;
  font-weight: 700;
  line-height: 1.4;
}

// ── Bouton ajout prestataire ──────────────────────────────────────────────────
.add-prestataire-btn {
  width: 100%;
  padding: $spacing-s;
  border: 1.5px dashed $color-primary;
  border-radius: $radius-md;
  background: transparent;
  font-family: inherit;
  font-size: 0.875rem;
  font-weight: 500;
  color: $text-primary;
  cursor: pointer;
  transition:
    border-color 150ms ease,
    color 150ms ease;

  &:active {
    border-color: $color-primary;
    color: $color-primary;
  }

  @media (min-width: $desktop) {
    transition: background 150ms ease;

    &:hover {
      background: rgba(255, 255, 255, 0.1);
    }

    &:active {
      border-color: rgba(255, 255, 255, 0.6);
      color: $color-white;
    }
  }
}

// ── Skeleton ──────────────────────────────────────────────────────────────────
.board-skeleton {
  padding: $spacing-m;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}

.skeleton-widget {
  height: 100px;
  border-radius: $radius-md;
}

.skeleton-pills {
  display: flex;
  gap: $spacing-xs;
}

.skeleton-pill {
  height: 1.6rem;
  width: 5rem;
  border-radius: 2rem;
}

.skeleton-card {
  height: 72px;
  border-radius: 16px;
}
</style>
