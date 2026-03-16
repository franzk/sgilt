<template>
  <div class="reservation-detail">
    <!-- ── Bouton back ────────────────────────────────────────────────────── -->
    <button class="back-btn" type="button" @click="navigateTo(`/app/events/${eventId}`)">
      ‹ Retour
    </button>

    <template v-if="reservation">
      <!-- ── Card titre ─────────────────────────────────────────────────────── -->
      <div class="title-card">
        <div class="title-card__media">
          <img
            v-if="reservation.prestatairePhoto"
            :src="reservation.prestatairePhoto"
            :alt="reservation.prestataireName"
            class="title-card__img"
          />
          <span v-else class="title-card__fallback">{{ initials }}</span>
        </div>
        <div class="title-card__info">
          <span class="title-card__category">{{ reservation.category }}</span>
          <span class="title-card__name">{{ reservation.prestataireName }}</span>
        </div>
        <span class="title-card__badge" :class="`title-card__badge--${reservation.status}`">
          {{ STATUS_LABELS[reservation.status] }}
        </span>
      </div>

      <!-- ── Card notes ─────────────────────────────────────────────────────── -->
      <div class="notes-card">
        <p v-if="sortedNotes.length === 0" class="notes-card__empty">Aucune note pour le moment.</p>
        <ul v-else class="notes-list">
          <li
            v-for="note in sortedNotes"
            :key="note.id"
            class="note-item"
            :class="`note-item--${note.author.role}`"
          >
            <div class="note-body">
              <div class="note-meta">
                <div class="note-avatar">
                  <img
                    v-if="note.author.photo"
                    :src="note.author.photo"
                    :alt="note.author.name"
                    class="note-avatar__img"
                  />
                  <span v-else class="note-avatar__initials">{{
                    authorInitials(note.author.name)
                  }}</span>
                </div>
                <span class="note-meta__author">{{ note.author.name }}</span>
                <span class="note-meta__dot" aria-hidden="true" />
                <time class="note-meta__date">{{ formatDate(note.createdAt) }}</time>
              </div>
              <p class="note-content">{{ note.content }}</p>
            </div>
          </li>
        </ul>
      </div>
    </template>

    <!-- Skeleton -->
    <template v-else-if="loading">
      <div class="skeleton-header skeleton-text" />
      <div class="skeleton-notes">
        <div v-for="i in 3" :key="i" class="skeleton-note skeleton-text" />
      </div>
    </template>

    <!-- ── FAB ────────────────────────────────────────────────────────────── -->
    <button
      v-if="reservation"
      class="note-fab"
      type="button"
      aria-label="Ajouter une note"
      @click="noteSheetOpen = true"
    >
      +
    </button>

    <!-- ── Bottom sheet / dialog ajout note ──────────────────────────────── -->
    <SgiltDialog v-if="noteSheetOpen" v-model:open="noteSheetOpen" title="Ajouter une note">
      <div class="note-form">
        <textarea
          ref="noteTextareaRef"
          v-model="newNote"
          class="note-form__textarea"
          placeholder="Ajouter une note..."
          rows="5"
          @input="autoResize"
        />
        <button
          class="note-form__send"
          type="button"
          :disabled="!newNote.trim() || sending"
          @click="sendNote"
        >
          Ajouter la note
        </button>
      </div>
    </SgiltDialog>
  </div>
</template>

<script setup lang="ts">
import { ReservationMockService } from '~/services/reservation.mock'
import type { ReservationDetail, ReservationStatus } from '~/types/event'
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'

definePageMeta({ layout: 'app' })

const route = useRoute()
const reservationId = route.params.reservationId as string
const eventId = route.params.eventId as string

// ── Data ──────────────────────────────────────────────────────────────────────
const reservation = ref<ReservationDetail | null>(null)
const loading = ref(true)

onMounted(async () => {
  reservation.value = await ReservationMockService.getById(reservationId)
  loading.value = false
})

// ── Notes ─────────────────────────────────────────────────────────────────────
const sortedNotes = computed(() =>
  [...(reservation.value?.notes ?? [])].sort(
    (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
  ),
)

const noteSheetOpen = ref(false)
const newNote = ref('')
const sending = ref(false)
const noteTextareaRef = ref<HTMLTextAreaElement | null>(null)

watch(noteSheetOpen, (val) => {
  if (val) nextTick(() => noteTextareaRef.value?.focus())
})

function autoResize(e: Event) {
  const el = e.target as HTMLTextAreaElement
  el.style.height = 'auto'
  el.style.height = `${el.scrollHeight}px`
}

async function sendNote() {
  const content = newNote.value.trim()
  if (!content || sending.value) return
  sending.value = true
  const note = await ReservationMockService.addNote(reservationId, content)
  reservation.value?.notes.unshift(note)
  newNote.value = ''
  sending.value = false
  noteSheetOpen.value = false
}

// ── Labels ────────────────────────────────────────────────────────────────────
const STATUS_LABELS: Record<ReservationStatus, string> = {
  brouillon: 'Brouillon',
  envoyee: 'Envoyée',
  recontactee: 'Recontactée',
  confirmee: 'Confirmée',
  annulee: 'Annulée',
  cloturee: 'Clôturée',
  terminee: 'Terminée',
}

const initials = computed(() =>
  (reservation.value?.prestataireName ?? '')
    .split(' ')
    .slice(0, 2)
    .map((w) => w[0]?.toUpperCase() ?? '')
    .join(''),
)

function authorInitials(name: string) {
  return name
    .split(' ')
    .slice(0, 2)
    .map((w) => w[0]?.toUpperCase() ?? '')
    .join('')
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}
</script>

<style scoped lang="scss">
$bottom-nav-h: 56px;

.reservation-detail {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: $spacing-m $spacing-m calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $spacing-m);
  background: rgba(230, 184, 0, 0.5);
}

// ── Bouton back ────────────────────────────────────────────────────────────────
.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 0;
  border: none;
  background: none;
  font-family: 'Inter', sans-serif;
  font-size: 0.875rem;
  font-weight: 500;
  color: $text-secondary;
  cursor: pointer;
  transition: color 150ms ease;

  &:active {
    color: $brand-primary;
  }
}

// ── Card titre ─────────────────────────────────────────────────────────────────
.title-card {
  background: #fff;
  border-radius: $radius-md;
  padding: $spacing-m;
  display: flex;
  align-items: center;
  gap: $spacing-s;
  box-shadow: 0 1px 4px $shadow-s;

  &__media {
    flex-shrink: 0;
    width: 52px;
    height: 52px;
    border-radius: 50%;
    overflow: hidden;
    background: $brand-subtle;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &__img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  &__fallback {
    font-family: 'Cormorant Garamond', serif;
    font-size: 18px;
    font-weight: 500;
    color: $text-secondary;
    line-height: 1;
  }

  &__info {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  &__category {
    font-family: 'Inter', sans-serif;
    font-size: 10px;
    font-weight: 600;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: $brand-muted;
  }

  &__name {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.25rem;
    font-weight: 600;
    color: $brand-primary;
    line-height: 1.2;
  }

  &__badge {
    flex-shrink: 0;
    font-size: 0.625rem;
    font-weight: 600;
    padding: 2px 7px;
    border-radius: 2rem;
    white-space: nowrap;

    &--brouillon,
    &--terminee {
      background: #f0efee;
      color: #888;
    }
    &--envoyee {
      background: #e8f0fe;
      color: #2c5cc5;
    }
    &--recontactee {
      background: rgba($brand-accent, 0.15);
      color: darken(#e6b800, 20%);
    }
    &--confirmee {
      background: rgba($state-available, 0.12);
      color: $state-available;
    }
    &--annulee,
    &--cloturee {
      background: rgba($state-error, 0.1);
      color: $state-error;
    }
  }
}

// ── Card notes ─────────────────────────────────────────────────────────────────
.notes-card {
  background: #fff;
  padding: $spacing-m;
  box-shadow: 0 1px 4px $shadow-s;
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

.notes-card__empty {
  font-size: 0.875rem;
  color: $text-secondary;
  opacity: 0.55;
  font-style: italic;
  margin: 0;
  text-align: center;
  padding: $spacing-m 0;
}

.notes-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

// ── Note item ──────────────────────────────────────────────────────────────────
.note-item {
  display: block;
}

.note-body {
  padding: $spacing-s;
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
  box-shadow:
    0 1px 3px rgba(0, 0, 0, 0.07),
    0 1px 2px rgba(0, 0, 0, 0.04);

  .note-item--client & {
    background: rgba($brand-accent, 0.08);
  }

  .note-item--prestataire & {
    background: $brand-subtle;
  }
}

.note-avatar {
  flex-shrink: 0;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: rgba($brand-muted, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  &__img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  &__initials {
    font-family: 'Inter', sans-serif;
    font-size: 9px;
    font-weight: 600;
    color: $brand-muted;
    line-height: 1;
  }
}

.note-meta {
  display: flex;
  align-items: center;
  gap: 6px;

  &__author {
    font-family: 'Inter', sans-serif;
    font-size: 10px;
    font-weight: 600;
    letter-spacing: 0.06em;
    text-transform: uppercase;
    color: $brand-muted;
  }

  &__dot {
    width: 2px;
    height: 2px;
    border-radius: 50%;
    background: $text-secondary;
    opacity: 0.4;
  }

  &__date {
    font-family: 'Inter', sans-serif;
    font-size: 10px;
    color: $text-secondary;
    opacity: 0.6;
  }
}

.note-content {
  font-family: 'Inter', sans-serif;
  font-size: 0.875rem;
  font-weight: 400;
  color: $text-primary;
  line-height: 1.55;
  margin: 0;
  white-space: pre-wrap;
}

// ── FAB ────────────────────────────────────────────────────────────────────────
.note-fab {
  position: fixed;
  right: $spacing-m;
  bottom: calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $spacing-m);
  z-index: $z-dropdown;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  border: none;
  background: $brand-accent;
  color: $brand-primary;
  font-size: 1.75rem;
  line-height: 1;
  cursor: pointer;
  box-shadow:
    0 4px 12px rgba(0, 0, 0, 0.15),
    0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  transition:
    transform 120ms ease,
    box-shadow 120ms ease;

  &:active {
    transform: scale(0.94);
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);
  }
}

// ── Formulaire dans la sheet ───────────────────────────────────────────────────
.note-form {
  display: flex;
  flex-direction: column;

  &__textarea {
    width: 100%;
    min-height: calc(3 * 1.5em + 2 * #{$spacing-s});
    padding: $spacing-s $spacing-m;
    border: none;
    border-bottom: 0.5px solid $brand-border;
    border-radius: 0;
    resize: none;
    font-family: inherit;
    font-size: 0.875rem;
    line-height: 1.5;
    color: $text-primary;
    background: transparent;
    outline: none;
    overflow: hidden;

    &::placeholder {
      color: $text-secondary;
      opacity: 0.5;
    }
  }

  &__send {
    width: 100%;
    height: 48px;
    border: none;
    border-radius: 0;
    background: $brand-accent;
    color: $brand-primary;
    font-family: inherit;
    font-size: 0.875rem;
    font-weight: 700;
    cursor: pointer;
    transition: opacity 150ms ease;

    &:disabled {
      opacity: 0.4;
      cursor: default;
    }
  }
}

// ── Skeleton ───────────────────────────────────────────────────────────────────
.skeleton-header {
  height: 84px;
  border-radius: $radius-lg;
}

.skeleton-notes {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

.skeleton-note {
  height: 80px;
  border-radius: $radius-md;
}
</style>
