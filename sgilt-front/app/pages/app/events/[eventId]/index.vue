<template>
  <div class="event-board">

    <div v-if="event" class="board-content">
      <!-- ── Bloc événement ──────────────────────────────────────────────────── -->
      <section class="event-block">
        <div class="event-block__title-row">
          <h1 class="event-block__title">{{ event.title }}</h1>
          <button class="event-block__edit-btn" type="button" aria-label="Modifier l'événement">
            <IconEditNote />
          </button>
        </div>

        <!-- Pills -->
        <div class="event-pills">
          <span v-for="pill in pills" :key="pill.label" class="event-pill">
            {{ pill.icon }} {{ pill.label }}
          </span>
        </div>

        <!-- Note partagée -->
        <div class="event-note">
          <label class="event-note__label" for="shared-note">
            Note partagée avec les prestataires
          </label>
          <textarea
            id="shared-note"
            v-model="noteValue"
            class="event-note__textarea"
            placeholder="Informations utiles pour tous vos prestataires : adresse du lieu, horaires, consignes particulières…"
            rows="4"
          />
          <div class="event-note__footer">
            <span class="event-note__save-status" :class="`event-note__save-status--${saveStatus}`">
              <template v-if="saveStatus === 'saving'">Sauvegarde…</template>
              <template v-else-if="saveStatus === 'saved'">Sauvegardé ✓</template>
            </span>
            <span v-if="event.sharedNoteUpdatedAt" class="event-note__timestamp">
              Modifié le {{ formatDatetime(event.sharedNoteUpdatedAt) }}
            </span>
          </div>
        </div>
      </section>

      <!-- ── Réservations ────────────────────────────────────────────────────── -->
      <section class="reservations">
        <div
          v-for="group in groupedReservations"
          :key="group.status"
          class="reservation-group"
        >
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

import ReservationCard from '~/components/app/ReservationCard.vue'
import IconEditNote from '~/components/icons/IconEditNote.vue'
import { EventMockService } from '~/services/event.mock'
import type { EventDetail, ReservationStatus } from '~/types/event'
import { EVENT_TYPE_OPTIONS, AMBIANCE_OPTIONS } from '~/types/demande'
import { useDebounceFn } from '@vueuse/core'

const route = useRoute()
const eventId = route.params.eventId as string

// ── Data ──────────────────────────────────────────────────────────────────────
const event = ref<EventDetail | null>(null)
const loading = ref(true)

onMounted(async () => {
  event.value = await EventMockService.getById(eventId)
  loading.value = false
  if (event.value) noteValue.value = event.value.sharedNote
})

// ── Note partagée ─────────────────────────────────────────────────────────────
const noteValue = ref('')
const saveStatus = ref<'idle' | 'saving' | 'saved'>('idle')
let savedTimer: ReturnType<typeof setTimeout>

const debouncedSave = useDebounceFn(async () => {
  if (!event.value) return
  saveStatus.value = 'saving'
  const { updatedAt } = await EventMockService.patchNote(eventId, noteValue.value)
  event.value.sharedNote = noteValue.value
  event.value.sharedNoteUpdatedAt = updatedAt
  saveStatus.value = 'saved'
  clearTimeout(savedTimer)
  savedTimer = setTimeout(() => (saveStatus.value = 'idle'), 2000)
}, 1000)

watch(noteValue, (val) => {
  if (event.value && val !== event.value.sharedNote) debouncedSave()
})

// ── Pills ─────────────────────────────────────────────────────────────────────
const pills = computed(() => {
  if (!event.value) return []
  const ev = event.value
  const items: { icon: string; label: string }[] = []
  if (ev.date) items.push({ icon: '📅', label: formatDate(ev.date) })
  if (ev.eventType) {
    const opt = EVENT_TYPE_OPTIONS.find((o) => o.value === ev.eventType)
    if (opt) items.push({ icon: opt.emoji, label: opt.label })
  }
  if (ev.ambiance) {
    const opt = AMBIANCE_OPTIONS.find((o) => o.value === ev.ambiance)
    if (opt) items.push({ icon: opt.emoji, label: opt.label })
  }
  if (ev.ville) items.push({ icon: '📍', label: ev.ville })
  if (ev.nbInvites) items.push({ icon: '👥', label: `${ev.nbInvites} invités` })
  return items
})

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

// ── Formatage ─────────────────────────────────────────────────────────────────
function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  })
}

function formatDatetime(iso: string): string {
  return new Date(iso).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
    hour: '2-digit',
    minute: '2-digit',
  })
}
</script>

<style scoped lang="scss">
$bottom-nav-h: 56px;
$fab-size: 52px;

.event-board {
  min-height: 100%;
  background: $surface-soft;
}

// ── Contenu ───────────────────────────────────────────────────────────────────
.board-content {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: $spacing-m;
}

// ── Bloc événement ────────────────────────────────────────────────────────────
.event-block {
  background: #fff;
  border-radius: $radius-lg;
  padding: $spacing-m;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  box-shadow: 0 1px 4px $shadow-s;

  &__title-row {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: $spacing-s;
  }

  &__title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.5rem;
    font-weight: 500;
    color: $brand-primary;
    margin: 0;
    line-height: 1.2;
  }

  &__edit-btn {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 2rem;
    height: 2rem;
    border: none;
    background: none;
    color: $brand-muted;
    border-radius: $radius-sm;
    cursor: pointer;
    transition: background 150ms ease, color 150ms ease;

    &:hover {
      background: $surface-soft;
      color: $brand-primary;
    }
  }
}

// ── Pills ─────────────────────────────────────────────────────────────────────
.event-pills {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-xs;
}

.event-pill {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 2rem;
  background: $surface-soft;
  border: 1px solid $divider-color;
  font-size: 0.8rem;
  color: $text-secondary;
  font-weight: 500;
  white-space: nowrap;
}

// ── Note partagée ─────────────────────────────────────────────────────────────
.event-note {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  &__label {
    font-size: 0.8rem;
    font-weight: 600;
    color: $text-secondary;
    letter-spacing: 0.02em;
  }

  &__textarea {
    width: 100%;
    padding: $spacing-s;
    border: 1px solid $divider-color;
    border-radius: $radius-md;
    font-family: inherit;
    font-size: 0.9rem;
    color: $text-primary;
    line-height: 1.5;
    resize: none;
    background: $surface-soft;
    transition: border-color 150ms ease, box-shadow 150ms ease;
    box-sizing: border-box;

    &:focus {
      outline: none;
      border-color: $input-focus-border-color;
      box-shadow: $input-focus-box-shadow;
      background: #fff;
    }

    &::placeholder {
      color: $text-secondary;
      opacity: 0.55;
    }
  }

  &__footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    min-height: 1rem;
  }

  &__save-status {
    font-size: 0.75rem;
    font-weight: 500;
    transition: opacity 300ms ease;

    &--idle {
      opacity: 0;
    }

    &--saving {
      opacity: 1;
      color: $text-secondary;
    }

    &--saved {
      opacity: 1;
      color: $state-available;
    }
  }

  &__timestamp {
    font-size: 0.7rem;
    color: $text-secondary;
    opacity: 0.6;
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
  transition: transform 120ms ease, box-shadow 120ms ease;
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
