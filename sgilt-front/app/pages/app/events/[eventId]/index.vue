<template>
  <div class="event-board">
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

    <!-- FAB -->
    <button class="event-fab" type="button" aria-label="Ajouter un prestataire" @click="goToSearch">
      <span class="event-fab__plus">+</span>
    </button>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'app' })

import EventBlock from '~/components/app/EventBlock.vue'
import ReservationCard from '~/components/app/ReservationCard.vue'
import { EventMockService } from '~/services/event.mock'
import type { EventDetail, EventPatch, ReservationStatus } from '~/types/event'

const route = useRoute()
const eventId = route.params.eventId as string

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
const STATUS_ORDER: ReservationStatus[] = [
  'confirmee',
  'recontactee',
  'envoyee',
  'brouillon',
  'cloturee',
  'annulee',
  'terminee',
]

const STATUS_SECTION_LABELS: Record<ReservationStatus, string> = {
  confirmee: 'Confirmées',
  recontactee: 'Recontactées',
  envoyee: 'Envoyées',
  brouillon: 'Brouillons',
  cloturee: 'Clôturées',
  annulee: 'Annulées',
  terminee: 'Terminées',
}

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
$bottom-nav-h: 56px;
$fab-size: 52px;

.event-board {
  min-height: 100%;
  background: rgba($color-accent, 0.5); // $surface-soft;
}

// ── Contenu ───────────────────────────────────────────────────────────────────
.board-content {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: $spacing-m;
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
    color: $text-secondary;
    opacity: 0.6;
    margin: 0;
    padding: 0 2px;
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
  }
}

// ── FAB ───────────────────────────────────────────────────────────────────────
.event-fab {
  position: fixed;
  bottom: calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $spacing-m);
  right: $spacing-m;
  width: $fab-size;
  height: $fab-size;
  border-radius: 50%;
  border: none;
  background: $brand-accent;
  color: #fff;
  box-shadow:
    0 4px 16px rgba($brand-accent, 0.45),
    0 2px 6px rgba(0, 0, 0, 0.12);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition:
    transform 120ms ease,
    box-shadow 120ms ease;
  z-index: $z-dropdown;

  &:active {
    transform: scale(0.94);
    box-shadow: 0 2px 8px rgba($brand-accent, 0.35);
  }

  &__plus {
    font-size: 1.6rem;
    line-height: 1;
    font-weight: 300;
    margin-top: -1px;
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
