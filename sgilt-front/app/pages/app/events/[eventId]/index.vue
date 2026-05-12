<template>
  <div class="event-board">
    <!-- ── Bandeau couverture ──────────────────────────────────────────────────── -->
    <div
      v-if="!metaPending && event"
      ref="bannerRef"
      class="cover-banner"
      :style="{ backgroundImage: `url(${coverImage})` }"
    >
      <div class="overlay" />
      <div class="title-row">
        <span class="title">{{ event.title }}</span>
        <button
          class="edit-mobile"
          type="button"
          :aria-label="$t('event.board.edit-event-aria')"
          @click="openEditDialog"
        >
          <EditIcon class="edit-mobile-icon" />
        </button>
      </div>
      <button class="edit-img" type="button" @click="openEditDialog">
        {{ $t('event.board.edit-title-image') }}
      </button>
    </div>
    <div v-else class="cover-banner-skeleton skeleton-text shimmer-container" />

    <!-- ── Widget ─────────────────────────────────────────────────────────────── -->
    <div class="event-widget">
      <!-- Mood phrase — attend les counts -->
      <p v-if="!countsPending && counts" class="phrase">
        {{ $t(`event.widget.mood.${counts.mood}`) }}
      </p>
      <div v-else class="skeleton-text shimmer-container widget-mood-skeleton" aria-hidden="true" />

      <!-- Countdown — attend les meta -->
      <p v-if="!metaPending && event" class="subtitle">
        <template v-if="daysUntilEvent !== null && daysUntilEvent >= 0"
          >J-{{ daysUntilEvent }} :
        </template>
        {{ $t('event.widget.countdown.' + event.countdown) }}
      </p>
      <div
        v-else
        class="skeleton-text shimmer-container widget-countdown-skeleton"
        aria-hidden="true"
      />

      <!-- Pills + action — attendent les counts -->
      <div class="pills">
        <template v-if="!countsPending && counts">
          <span
            v-for="pill in statusPills"
            :key="pill.status"
            class="status-pill"
            :style="{
              background: CLIENT_STATUS_CONFIG[pill.status].bgColor,
              color: CLIENT_STATUS_CONFIG[pill.status].color,
            }"
          >
            <span class="icon" aria-hidden="true">{{ pill.icon }}</span>
            <span class="count">{{ pill.count }}</span>
            <span class="label">{{ t(`client.reservation.statut.${pill.status}`) }}</span>
          </span>
        </template>
        <template v-else>
          <div v-for="i in 2" :key="i" class="skeleton-pill skeleton-text shimmer-container" />
        </template>
        <button class="add-prestataire-btn" type="button" @click="startAddPrestataireFlow">
          {{ $t('events.add-provider') }}
        </button>
      </div>

      <!-- Sticky CTA mobile -->
      <div class="sticky-cta">
        <SgiltButton @click="startAddPrestataireFlow">
          {{ $t('events.add-provider') }}
        </SgiltButton>
      </div>
    </div>

    <!-- ── Contenu ────────────────────────────────────────────────────────────── -->
    <div class="board-content">
      <!-- Bloc événement — attend les meta -->
      <div class="event-block-wrap">
        <EventBlock
          v-if="!metaPending && event && clientInfo"
          variant="client"
          :event="event"
          :client-info="clientInfo"
          @updated="onEventUpdated"
          @updated-client-info="onClientInfoUpdated"
        />
        <div v-else class="skeleton-card skeleton-text shimmer-container" />
      </div>

      <!-- Réservations — attendent les reservations -->
      <section class="reservations">
        <div class="grid">
          <template v-if="!reservationsPending">
            <ReservationCard
              v-for="r in sortedReservations"
              :key="r.id"
              :reservation="r"
              @click="navigateTo(`/app/events/${eventId}/reservations/${r.id}`)"
            />
          </template>
          <template v-else>
            <div v-for="i in 3" :key="i" class="skeleton-card skeleton-text shimmer-container" />
          </template>
        </div>
      </section>
    </div>

    <!-- ── Dialog modification titre + couverture ────────────────────────────── -->
    <EventEditDialog v-if="event" v-model:open="editDialogOpen" :event="event" @save="onEditSave" />
  </div>
</template>

<script setup lang="ts">
import ReservationCard from '~/components/app/ReservationCard.vue'
import EventBlock from '~/components/app/EventBlock.vue'
import EventEditDialog from '~/components/app/EventEditDialog.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import { resolveEventCover } from '~/utils/eventCovers'
import type { EventPatch } from '~/data/evenement/domain/EventPatch'
import type { ReservationStatus, ClientContactInfo } from '~/types/event'
import { CLIENT_STATUS_CONFIG, RESERVATION_STATUS_ORDER } from '~/constants/reservation-status'
import { EditIcon } from '@remixicons/vue/line'
import { useEventDetail, useEventCounts, useEventReservations } from '~/data/evenement/useEvenement'

definePageMeta({ layout: 'app' })

const { t } = useI18n()
const route = useRoute()
const eventId = route.params.eventId as string

// ── 3 appels parallèles ───────────────────────────────────────────────────────
const { event, clientInfo, pending: metaPending } = useEventDetail(eventId)
const { counts, pending: countsPending } = useEventCounts(eventId)
const { reservations, pending: reservationsPending } = useEventReservations(eventId)

// ── Cover image ────────────────────────────────────────────────────────────────
const coverImage = computed(() => (event.value ? resolveEventCover(event.value) : ''))

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

// ── Countdown ─────────────────────────────────────────────────────────────────
const daysUntilEvent = computed(() => {
  if (!event.value?.date) return null
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const eventDate = new Date(event.value.date)
  eventDate.setHours(0, 0, 0, 0)
  return Math.ceil((eventDate.getTime() - today.getTime()) / 86_400_000)
})

// ── Édition ───────────────────────────────────────────────────────────────────
function onEventUpdated(patch: EventPatch) {
  if (event.value) Object.assign(event.value, patch)
}

function onClientInfoUpdated(_patch: Partial<ClientContactInfo>) {
  // TODO: persist via API
}

const editDialogOpen = ref(false)
function openEditDialog() {
  editDialogOpen.value = true
}

async function onEditSave(patch: EventPatch) {
  if (!event.value) return
  // TODO: call PATCH /events/:id
  Object.assign(event.value, patch)
}

// ── Pills statuts depuis les counts ───────────────────────────────────────────
const STATUS_PILL_ICONS: Record<ReservationStatus, string> = {
  nouvelle: '!',
  en_discussion: '↩',
  confirmee: '✓',
  refusee: '✕',
  annulee: '✕',
  realisee: '✓',
}

const statusPills = computed(() => {
  if (!counts.value) return []
  const c = counts.value
  return (
    [
      {
        status: 'nouvelle' as ReservationStatus,
        icon: STATUS_PILL_ICONS.nouvelle,
        count: c.nouvelleCount,
      },
      {
        status: 'en_discussion' as ReservationStatus,
        icon: STATUS_PILL_ICONS.en_discussion,
        count: c.inDiscussionCount,
      },
      {
        status: 'confirmee' as ReservationStatus,
        icon: STATUS_PILL_ICONS.confirmee,
        count: c.confirmedCount,
      },
      {
        status: 'refusee' as ReservationStatus,
        icon: STATUS_PILL_ICONS.refusee,
        count: c.refuseeCount,
      },
      {
        status: 'annulee' as ReservationStatus,
        icon: STATUS_PILL_ICONS.annulee,
        count: c.annuleeCount,
      },
      {
        status: 'realisee' as ReservationStatus,
        icon: STATUS_PILL_ICONS.realisee,
        count: c.realiseeCount,
      },
    ] as { status: ReservationStatus; icon: string; count: number }[]
  ).filter((p) => p.count > 0)
})

// ── Réservations triées ───────────────────────────────────────────────────────
const sortedReservations = computed(() => {
  return [...reservations.value].sort(
    (a, b) =>
      RESERVATION_STATUS_ORDER.indexOf(a.status) - RESERVATION_STATUS_ORDER.indexOf(b.status),
  )
})

// ── Flow ajout prestataire ────────────────────────────────────────────────────
const { start } = useFlow()
const startAddPrestataireFlow = () => {
  start('add-prestataire', `Ajouter à ${event.value?.title ?? "l'événement"}`, {
    id: event.value?.id,
    nom: event.value?.title,
    date: event.value?.date ?? null,
    ville: event.value?.ville,
    ambiance: event.value?.ambiance,
    invites: event.value?.nbInvites,
  })
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;

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

  .overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(to bottom, rgba(47, 42, 37, 0.1), rgba(47, 42, 37, 0.65));
    pointer-events: none;
  }

  .title-row {
    position: relative;
    display: flex;
    align-items: flex-end;
    gap: $spacing-xs;
  }

  .title {
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

  .edit-mobile {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 28px;
    height: 28px;
    border: none;
    background: rgba(0, 0, 0, 0.25);
    border-radius: $radius-sm;
    color: rgba(255, 255, 255, 0.85);
    cursor: pointer;
    backdrop-filter: blur(4px);
    flex-shrink: 0;
    margin-bottom: 3px;

    @media (min-width: $desktop) {
      display: none;
    }
  }

  .edit-mobile-icon {
    width: 14px;
    height: 14px;
  }

  .edit-img {
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

.cover-banner-skeleton {
  height: 200px;

  @media (min-width: $desktop) {
    height: 33vh;
  }
}

// ── Widget ─────────────────────────────────────────────────────────────────────
.event-widget {
  padding: $spacing-m;
  background: #fff;
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  @media (min-width: $desktop) {
    padding: $spacing-m max(40px, calc((100% - 1200px) / 2 + 40px));
  }
}

.phrase {
  font-family: 'Cormorant Garamond', serif;
  font-size: 1.1rem;
  font-weight: 600;
  color: $brand-primary;
  margin: 0;
}

.subtitle {
  font-size: 0.82rem;
  color: $text-secondary;
  margin: 0;
}

.widget-mood-skeleton {
  height: 1.2rem;
  width: 12rem;
  border-radius: 4px;
}

.widget-countdown-skeleton {
  height: 0.9rem;
  width: 10rem;
  border-radius: 4px;
}

.pills {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: $spacing-xs;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 500;
  white-space: nowrap;

  .count {
    font-weight: 700;
  }
}

.skeleton-pill {
  height: 26px;
  width: 80px;
  border-radius: 999px;
}

// ── Contenu principal ─────────────────────────────────────────────────────────
.board-content {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: $spacing-m;

  @media (min-width: $desktop) {
    flex-direction: row;
    align-items: flex-start;
    gap: 32px;
    max-width: 1200px;
    margin: 0 auto;
    padding: 32px 40px;
  }
}

.event-block-wrap {
  @media (min-width: $desktop) {
    width: 340px;
    flex-shrink: 0;
  }
}

.skeleton-card {
  border-radius: $radius-lg;
  height: 140px;
  width: 100%;
}

// ── Réservations ──────────────────────────────────────────────────────────────
.reservations {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}

.grid {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;

  @media (min-width: $desktop) {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
}

.add-prestataire-btn {
  margin-left: auto;
  padding: 7px 16px;
  border: 1px dashed $brand-border;
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

  &:hover {
    border-color: $brand-primary;
    color: $brand-primary;
  }

  @media (max-width: calc($desktop - 1px)) {
    display: none;
  }
}

.sticky-cta {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 20;
  padding: $spacing-s $spacing-m calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $spacing-s);
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  display: flex;
  justify-content: center;

  @media (min-width: $desktop) {
    display: none;
  }
}
</style>
