<template>
  <div class="event-board">
    <!-- ── Bandeau couverture (desktop) ───────────────────────────────────────── -->
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

    <div v-if="event" class="board-content">
      <!-- ── Bloc événement ──────────────────────────────────────────────────── -->
      <EventBlock :event="event" @updated="onEventUpdated" />

      <!-- ── Réservations ────────────────────────────────────────────────────── -->
      <section class="reservations">
        <div v-for="group in groupedReservations" :key="group.status" class="reservation-group">
          <h2 class="reservation-group__title">{{ group.label }}</h2>
          <div class="reservation-group__list">
            <ReservationCard
              v-for="r in group.reservations"
              :key="r.id"
              :reservation="r"
              @click="navigateTo(`/app/events/${eventId}/reservations/${r.id}`)"
            />
          </div>
        </div>

        <button class="add-prestataire-btn" type="button" @click="goToSearch">
          + Ajouter un prestataire
        </button>
      </section>
    </div>

    <!-- Skeleton -->
    <div v-else-if="loading" class="board-skeleton">
      <div class="skeleton-title skeleton-text" />
      <div class="skeleton-pills">
        <div v-for="i in 4" :key="i" class="skeleton-pill skeleton-text" />
      </div>
      <div class="skeleton-card skeleton-text" />
      <div class="skeleton-card skeleton-text" />
      <div class="skeleton-card skeleton-text" />
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'app' })

import EventBlock from '~/components/app/EventBlock.vue'
import ReservationCard from '~/components/app/ReservationCard.vue'
import { EventMockService } from '~/services/event.mock'
import type { EventDetail, EventPatch, ReservationStatus } from '~/types/event'
import {
  RESERVATION_STATUS_ORDER as STATUS_ORDER,
  RESERVATION_STATUS_SECTION_LABELS as STATUS_SECTION_LABELS,
} from '~/utils/reservationStatus'

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

// ── Réservations groupées ─────────────────────────────────────────────────────

const groupedReservations = computed(() => {
  if (!event.value) return []
  const map = new Map<ReservationStatus, typeof event.value.reservations>()
  for (const r of event.value.reservations) {
    if (!map.has(r.status)) map.set(r.status, [])
    map.get(r.status)!.push(r)
  }
  return STATUS_ORDER.filter((s) => map.has(s)).map((s) => ({
    status: s,
    label: STATUS_SECTION_LABELS[s],
    reservations: map.get(s)!,
  }))
})

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
  :deep(.event-block) {
    position: sticky;
    top: 60px;
  }
}

// ── Shadows + border cards réservation ────────────────────────────────────────
:deep(.reservation-card) {
  border-color: rgba(47, 42, 37, 0.1);
  border-left-width: 3px;
  box-shadow: 0 1px 4px rgba(47, 42, 37, 0.08);
}

// ── Shadow event block ─────────────────────────────────────────────────────────
:deep(.event-block) {
  box-shadow: 0 4px 20px rgba(47, 42, 37, 0.12);
}

@media (min-width: $desktop) {
  :deep(.reservation-card:hover) {
    box-shadow: 0 4px 16px rgba(47, 42, 37, 0.12);
  }
}

// ── Sections réservations ─────────────────────────────────────────────────────
.reservations {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}

.reservation-group {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  &__title {
    font-size: 0.7rem;
    font-weight: 700;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    color: $brand-accent;
    margin: 0;
    padding: 0 2px;
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
  }
}

// ── Bouton ajout prestataire ──────────────────────────────────────────────────
.add-prestataire-btn {
  width: 100%;
  padding: $spacing-s;
  border: 1.5px dashed $brand-border;
  border-radius: $radius-md;
  background: transparent;
  font-family: inherit;
  font-size: 0.875rem;
  font-weight: 500;
  color: $text-secondary;
  cursor: pointer;
  transition:
    border-color 150ms ease,
    color 150ms ease;

  &:active {
    border-color: $brand-primary;
    color: $brand-primary;
  }

  @media (min-width: $desktop) {
    border-color: rgba(255, 255, 255, 0.6);
    color: $color-white;
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

.skeleton-title {
  height: 1.8rem;
  width: 70%;
  border-radius: $radius-sm;
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
