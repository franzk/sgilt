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

    <!-- ── Onglets ────────────────────────────────────────────────────────────── -->
    <div v-if="reservation" class="tab-bar">
      <button
        class="tab-btn"
        :class="{ 'tab-btn--active': activeTab === 'notes' }"
        type="button"
        @click="activeTab = 'notes'"
      >
        Notes
      </button>
      <button
        class="tab-btn"
        :class="{ 'tab-btn--active': activeTab === 'documents' }"
        type="button"
        @click="activeTab = 'documents'"
      >
        Documents
      </button>
    </div>

    <!-- ── Contenu ───────────────────────────────────────────────────────────── -->
    <div class="reservation-detail">
      <template v-if="reservation">

        <!-- ══ Onglet Notes ══════════════════════════════════════════════════ -->
        <template v-if="activeTab === 'notes'">
          <!-- Bouton ajout note (desktop) -->
          <div class="tab-section-header">
            <button class="tab-section-btn" type="button" @click="noteSheetOpen = true">
              + Ajouter une note
            </button>
          </div>

          <p v-if="sortedNotes.length === 0" class="tab-empty">Aucune note pour le moment.</p>
          <ul v-else class="notes-list">
            <li v-for="note in sortedNotes" :key="note.id">
              <NoteCard :note="note" />
            </li>
          </ul>
        </template>

        <!-- ══ Onglet Documents ══════════════════════════════════════════════ -->
        <template v-else-if="activeTab === 'documents'">
          <!-- Bouton déposer (desktop) -->
          <div class="tab-section-header">
            <button class="tab-section-btn" type="button" @click="triggerUpload">
              ↑ Déposer un document
            </button>
          </div>

          <!-- File input caché -->
          <input ref="fileInputRef" type="file" style="display: none" @change="handleUpload" />

          <p v-if="sortedDocuments.length === 0" class="tab-empty">
            Aucun document partagé pour le moment.
          </p>
          <ul v-else class="doc-list">
            <li v-for="doc in sortedDocuments" :key="doc.id" class="doc-item">
              <div class="doc-icon" :class="`doc-icon--${doc.fileType}`">
                {{ doc.fileType === 'pdf' ? 'PDF' : doc.fileType === 'image' ? 'IMG' : 'DOC' }}
              </div>
              <div class="doc-info">
                <span class="doc-name">{{ doc.name }}</span>
                <span class="doc-meta">
                  {{ doc.uploadedBy.name }} · {{ formatDocDate(doc.uploadedAt) }}
                </span>
              </div>
              <div class="doc-actions">
                <a :href="doc.url" download class="doc-btn" title="Télécharger">↓</a>
                <button
                  v-if="doc.uploadedBy.role === 'client'"
                  class="doc-btn doc-btn--delete"
                  type="button"
                  title="Supprimer"
                  @click="deleteDocument(doc.id)"
                >
                  ✕
                </button>
              </div>
            </li>
          </ul>

          <!-- Bouton déposer (mobile, pleine largeur, en bas) -->
          <button v-if="isMobile" class="upload-btn-mobile" type="button" @click="triggerUpload">
            ↑ Déposer un document
          </button>
        </template>

      </template>

      <!-- Skeleton -->
      <template v-else-if="loading">
        <div class="skeleton-header skeleton-text" />
        <div class="skeleton-notes">
          <div v-for="i in 3" :key="i" class="skeleton-note skeleton-text" />
        </div>
      </template>

      <!-- ── FAB notes (mobile uniquement) ─────────────────────────────────── -->
      <button
        v-if="reservation && isMobile && activeTab === 'notes'"
        class="note-fab"
        type="button"
        aria-label="Ajouter une note"
        @click="noteSheetOpen = true"
      >
        +
      </button>

      <!-- ── Dialog ajout note ─────────────────────────────────────────────── -->
      <SgiltDialog
        v-if="noteSheetOpen"
        v-model:open="noteSheetOpen"
        title="Ajouter une note"
        max-width="800px"
      >
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
  </div>
</template>

<script setup lang="ts">
import { ReservationMockService } from '~/services/reservation.mock'
import type { ReservationDetail, ReservationDocument, NoteAuthor } from '~/types/event'
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import NoteCard from '~/components/app/NoteCard.vue'
import { getStatusOverlayStyle } from '~/constants/reservation-status'

definePageMeta({ layout: 'app' })

const { t } = useI18n()
const route = useRoute()
const reservationId = route.params.reservationId as string
const eventId = route.params.eventId as string
const { isMobile } = useDevice()

// ── Utilisateur connecté (mock) ────────────────────────────────────────────────
const CURRENT_USER: NoteAuthor = {
  id: 'client-1',
  name: 'Julie M.',
  role: 'client',
  photo: 'https://picsum.photos/seed/julie-m/64/64',
}

// ── Onglets ────────────────────────────────────────────────────────────────────
const activeTab = ref<'notes' | 'documents'>('notes')

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

// ── Notes ─────────────────────────────────────────────────────────────────────
const sortedNotes = computed(() =>
  (reservation.value?.notes ?? [])
    .filter((n) => !n.isPersonal)
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()),
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

// ── Documents ─────────────────────────────────────────────────────────────────
const sortedDocuments = computed(() =>
  [...(reservation.value?.documents ?? [])].sort(
    (a, b) => new Date(b.uploadedAt).getTime() - new Date(a.uploadedAt).getTime(),
  ),
)

const fileInputRef = ref<HTMLInputElement | null>(null)

function triggerUpload() {
  fileInputRef.value?.click()
}

function handleUpload(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file || !reservation.value) return
  const fileType: ReservationDocument['fileType'] =
    file.type === 'application/pdf'
      ? 'pdf'
      : file.type.startsWith('image/')
        ? 'image'
        : 'other'
  const doc: ReservationDocument = {
    id: `doc-${Date.now()}`,
    name: file.name,
    fileType,
    url: URL.createObjectURL(file),
    uploadedBy: CURRENT_USER,
    uploadedAt: new Date().toISOString(),
  }
  reservation.value.documents.unshift(doc)
  ;(e.target as HTMLInputElement).value = ''
}

function deleteDocument(id: string) {
  if (!reservation.value) return
  reservation.value.documents = reservation.value.documents.filter((d) => d.id !== id)
}

function formatDocDate(iso: string) {
  return new Date(iso).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  })
}
</script>

<style scoped lang="scss">
$bottom-nav-h: 56px;
$desktop: 900px;

// ── Page wrapper ───────────────────────────────────────────────────────────────
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

    @media (min-width: $desktop) {
      font-size: 38px;
    }
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

// ── Bouton back ────────────────────────────────────────────────────────────────
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

  &:active {
    color: #fff;
  }
}

// ── Barre d'onglets ────────────────────────────────────────────────────────────
.tab-bar {
  display: flex;
  background: #fff;
  border-bottom: 1px solid $divider-color;
  padding: 0 $spacing-m;

  @media (min-width: $desktop) {
    padding: 0 40px;
  }
}

.tab-btn {
  flex: 1;
  padding: $spacing-s 0;
  border: none;
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
  background: none;
  font-family: 'Inter', sans-serif;
  font-size: 0.875rem;
  font-weight: 500;
  color: $text-secondary;
  cursor: pointer;
  transition:
    color 150ms ease,
    border-color 150ms ease;

  &--active {
    color: $brand-primary;
    border-bottom-color: $brand-accent;
    font-weight: 600;
  }

  @media (min-width: $desktop) {
    flex: 0 1 auto;
    padding: $spacing-s $spacing-m;
  }
}

// ── Contenu centré ─────────────────────────────────────────────────────────────
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

// ── Header de section (desktop uniquement) ────────────────────────────────────
.tab-section-header {
  display: none;

  @media (min-width: $desktop) {
    display: flex;
    justify-content: flex-end;
    margin-bottom: $spacing-xs;
  }
}

.tab-section-btn {
  padding: 5px 14px;
  border: 1px solid $brand-border;
  border-radius: $radius-md;
  background: transparent;
  font-family: inherit;
  font-size: 0.8rem;
  font-weight: 600;
  color: $text-secondary;
  cursor: pointer;
  transition:
    border-color 150ms ease,
    color 150ms ease;

  &:hover {
    border-color: $brand-primary;
    color: $brand-primary;
  }
}

// ── État vide ─────────────────────────────────────────────────────────────────
.tab-empty {
  font-size: 0.875rem;
  color: $text-secondary;
  opacity: 0.55;
  font-style: italic;
  margin: 0;
  text-align: center;
  padding: $spacing-m 0;
}

// ── Liste notes ───────────────────────────────────────────────────────────────
.notes-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

// ── Liste documents ───────────────────────────────────────────────────────────
.doc-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

.doc-item {
  display: flex;
  align-items: center;
  gap: $spacing-s;
  padding: 10px $spacing-s;
  background: #fff;
  border-radius: $radius-md;
  border: 0.5px solid rgba(47, 42, 37, 0.10);
  box-shadow: 0 1px 4px rgba(47, 42, 37, 0.07);
}

.doc-icon {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  border-radius: $radius-sm;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Inter', sans-serif;
  font-size: 9px;
  font-weight: 700;
  letter-spacing: 0.04em;

  &--pdf {
    background: rgba(163, 45, 45, 0.1);
    color: #a32d2d;
  }

  &--image {
    background: rgba(24, 95, 165, 0.1);
    color: #185fa5;
  }

  &--other {
    background: rgba(107, 99, 92, 0.1);
    color: #6b635c;
  }
}

.doc-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.doc-name {
  font-family: 'Inter', sans-serif;
  font-size: 0.875rem;
  font-weight: 500;
  color: $text-primary;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.doc-meta {
  font-family: 'Inter', sans-serif;
  font-size: 0.75rem;
  color: $text-secondary;
  opacity: 0.7;
}

.doc-actions {
  display: flex;
  align-items: center;
  gap: $spacing-xs;
  flex-shrink: 0;
}

.doc-btn {
  width: 28px;
  height: 28px;
  border-radius: $radius-sm;
  border: none;
  background: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.875rem;
  cursor: pointer;
  text-decoration: none;
  color: $text-secondary;
  transition:
    background 150ms ease,
    color 150ms ease;

  &:hover {
    background: $surface-soft;
    color: $text-primary;
  }

  &--delete {
    font-size: 0.75rem;

    &:hover {
      background: rgba(163, 45, 45, 0.1);
      color: #a32d2d;
    }
  }
}

// ── Bouton déposer (mobile) ────────────────────────────────────────────────────
.upload-btn-mobile {
  width: 100%;
  padding: $spacing-m;
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
}

// ── FAB (mobile, notes uniquement) ────────────────────────────────────────────
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
  flex: 1;
  padding: 32px;
  gap: $spacing-l;

  &__textarea {
    flex: 1;
    width: 100%;
    min-height: 300px;
    padding: 0;
    border: none;
    border-radius: 0;
    resize: none;
    font-family: inherit;
    font-size: 1rem;
    line-height: 1.8;
    color: $text-primary;
    background: transparent;
    outline: none;
    overflow: hidden;

    &::placeholder {
      color: $text-secondary;
      opacity: 0.4;
    }
  }

  &__send {
    flex-shrink: 0;
    width: 100%;
    height: 48px;
    border: none;
    border-radius: $radius-md;
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
