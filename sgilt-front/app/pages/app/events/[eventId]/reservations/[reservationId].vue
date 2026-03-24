<template>
  <div class="reservation-page">
    <!-- ── Bandeau couverture ────────────────────────────────────────────────── -->
    <div
      v-if="reservation"
      ref="bannerRef"
      class="cover-banner"
      :style="
        reservation.prestatairePhoto
          ? { backgroundImage: `url(${reservation.prestatairePhoto})` }
          : {}
      "
    >
      <div class="cover-banner__overlay" />
      <button class="back-btn" type="button" @click="navigateTo(`/app/events/${eventId}`)">
        ‹ Retour
      </button>
      <div class="cover-banner__bottom">
        <div class="cover-banner__info">
          <span class="cover-banner__category">{{ reservation.category }}</span>
          <span class="cover-banner__name">{{ reservation.prestataireName }}</span>
        </div>
        <span class="cover-banner__badge" :style="getStatusOverlayStyle(reservation.status)">
          {{ t(`reservation.statut.${reservation.status}`) }}
        </span>
      </div>
    </div>

    <!-- ── Contenu ───────────────────────────────────────────────────────────── -->
    <div class="reservation-detail">
      <template v-if="reservation">
        <ReservationFeed
          :items="feedItems"
          :can-add-note="true"
          :can-upload-document="false"
          :show-personal-toggle="false"
          @add-note="onAddNote"
          @delete-document="onDeleteDocument"
        />
      </template>

      <!-- Skeleton -->
      <template v-else-if="loading">
        <div class="skeleton-header skeleton-text" />
        <div class="skeleton-notes">
          <div v-for="i in 3" :key="i" class="skeleton-note skeleton-text" />
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ReservationMockService } from '~/services/reservation.mock'
import type { ReservationDetail, ReservationDocument, FeedItem, NoteAuthor } from '~/types/event'
import ReservationFeed from '~/components/shared/ReservationFeed.vue'
import { getStatusOverlayStyle } from '~/constants/reservation-status'

definePageMeta({ layout: 'app' })

const { t } = useI18n()
const route = useRoute()
const reservationId = route.params.reservationId as string
const eventId = route.params.eventId as string

const CURRENT_USER: NoteAuthor = {
  id: 'client-1',
  name: 'Julie M.',
  role: 'client',
  photo: 'https://picsum.photos/seed/julie-m/64/64',
}

// ── Data ──────────────────────────────────────────────────────────────────────
const reservation = ref<ReservationDetail | null>(null)
const loading = ref(true)

onMounted(async () => {
  reservation.value = await ReservationMockService.getById(reservationId)
  loading.value = false
})

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

// ── Feed items ─────────────────────────────────────────────────────────────────
const feedItems = computed<FeedItem[]>(() => {
  if (!reservation.value) return []
  const notes: FeedItem[] = reservation.value.notes
    .filter((n) => !n.isPersonal)
    .map((n) => ({ ...n, _kind: 'note' as const }))
  const docs: FeedItem[] = reservation.value.documents.map((d) => ({
    ...d,
    _kind: 'document' as const,
  }))
  return [...notes, ...docs]
})

// ── Handlers ──────────────────────────────────────────────────────────────────
async function onAddNote(content: string, _isPersonal: boolean) {
  const note = await ReservationMockService.addNote(reservationId, content)
  reservation.value?.notes.unshift(note)
}

function onDeleteDocument(id: string) {
  if (!reservation.value) return
  reservation.value.documents = reservation.value.documents.filter((d) => d.id !== id)
}
</script>

<style scoped lang="scss">
$bottom-nav-h: 56px;
$desktop: 900px;

.reservation-page {
  min-height: 100%;
  background-color: #f5f5f3;
}

// ── Bandeau couverture ─────────────────────────────────────────────────────────
.cover-banner {
  position: relative;
  height: 200px;
  background-size: cover;
  background-position: center 50%;
  background-color: $brand-subtle;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: $spacing-s $spacing-m $spacing-m;

  @media (min-width: $desktop) {
    height: 280px;
    padding: $spacing-m $spacing-xl $spacing-l;
  }

  &__overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(to bottom, rgba(47, 42, 37, 0.1), rgba(47, 42, 37, 0.65));
    pointer-events: none;
  }

  &__bottom {
    position: relative;
    display: flex;
    align-items: flex-end;
    justify-content: space-between;
    gap: $spacing-s;
  }

  &__info {
    display: flex;
    flex-direction: column;
    gap: 3px;
  }

  &__category {
    font-family: 'Inter', sans-serif;
    font-size: 11px;
    font-weight: 600;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: rgba(255, 255, 255, 0.75);
  }

  &__name {
    font-family: 'Cormorant Garamond', serif;
    font-size: 28px;
    font-weight: 600;
    color: #fff;
    line-height: 1.1;
    text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);

    @media (min-width: $desktop) { font-size: 38px; }
  }

  &__badge {
    flex-shrink: 0;
    position: relative;
    font-size: 0.625rem;
    font-weight: 600;
    padding: 3px 9px;
    border-radius: 2rem;
    white-space: nowrap;
    backdrop-filter: blur(4px);
  }
}

.back-btn {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 0;
  border: none;
  background: none;
  font-family: 'Inter', sans-serif;
  font-size: 0.875rem;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.85);
  cursor: pointer;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
  transition: color 150ms ease;

  &:active { color: #fff; }
}

// ── Contenu ───────────────────────────────────────────────────────────────────
.reservation-detail {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: $spacing-m $spacing-m calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $spacing-m);

  @media (min-width: $desktop) {
    position: relative;
    padding: $spacing-l 0;
    align-items: center;

    > * {
      width: 100%;
      max-width: 760px;
    }
  }
}

// ── Skeleton ───────────────────────────────────────────────────────────────────
.skeleton-header { height: 84px; border-radius: $radius-lg; }
.skeleton-notes { display: flex; flex-direction: column; gap: $spacing-s; }
.skeleton-note { height: 80px; border-radius: $radius-md; }
</style>
