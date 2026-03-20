<template>
  <div class="pro-detail">

    <!-- ── Bandeau couverture ─────────────────────────────────────────────────── -->
    <div
      v-if="demande"
      ref="bannerRef"
      class="cover-banner"
      :style="{ backgroundImage: `url(${coverImage})` }"
    >
      <div class="cover-banner__overlay" />
      <button class="back-btn" type="button" @click="navigateTo('/pro/reservations')">
        ‹ Retour
      </button>
      <div class="cover-banner__bottom">
        <div class="cover-banner__info">
          <span class="cover-banner__category">{{ demande.category }}</span>
          <span class="cover-banner__name">{{ demande.clientInfo.eventName }}</span>
        </div>
        <span class="cover-banner__badge" :class="`cover-banner__badge--${demande.statut}`">
          {{ STATUT_LABELS[demande.statut] }}
        </span>
      </div>
    </div>

    <!-- ── Layout principal ──────────────────────────────────────────────────── -->
    <div v-if="demande" class="pro-layout">

      <!-- Colonne gauche : Event block (sticky desktop) ── -->
      <div class="pro-layout__left">
        <EventBlock v-if="proEventDetail" :event="proEventDetail" @updated="() => {}" />
      </div>

      <!-- Colonne droite : Onglets + Contenu ───────────── -->
      <div class="pro-layout__right">

        <!-- Tab bar -->
        <div class="tab-bar">
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

        <!-- Tab content -->
        <div class="detail-body">

          <!-- ══ Onglet Notes ══════════════════════════════════════════════════ -->
          <template v-if="activeTab === 'notes'">

            <!-- Desktop: bouton ajout note -->
            <div class="tab-section-header">
              <button class="tab-section-btn" type="button" @click="openNoteModal">
                + Ajouter une note
              </button>
            </div>

            <p v-if="sortedNotes.length === 0" class="tab-empty">Aucune note pour le moment.</p>
            <ul v-else class="notes-list">
              <li v-for="note in sortedNotes" :key="note.id">

                <!-- Message initial -->
                <div v-if="note.isMessageInitial" class="note-initial">
                  <span class="note-initial__label">Message initial</span>
                  <p class="note-initial__content">{{ note.content }}</p>
                  <span class="note-initial__meta">{{ note.authorName }} · {{ formatNoteDate(note.createdAt) }}</span>
                </div>

                <!-- Note personnelle (pro only) -->
                <div v-else-if="note.isPersonal" class="note-card note-card--personal">
                  <div class="note-card__header">
                    <span class="note-card__lock" aria-label="Note privée">🔒</span>
                    <span class="note-card__author">{{ note.authorName }}</span>
                    <span class="note-card__date">{{ formatNoteDate(note.createdAt) }}</span>
                  </div>
                  <p class="note-card__content">{{ note.content }}</p>
                </div>

                <!-- Note partagée -->
                <div
                  v-else
                  class="note-card"
                  :class="note.authorRole === 'client' ? 'note-card--client' : 'note-card--pro'"
                >
                  <div class="note-card__header">
                    <span class="note-card__author">{{ note.authorName }}</span>
                    <span class="note-card__date">{{ formatNoteDate(note.createdAt) }}</span>
                  </div>
                  <p class="note-card__content">{{ note.content }}</p>
                </div>

              </li>
            </ul>

            <!-- Lien actions spéciales (confirmée uniquement) -->
            <button
              v-if="demande.statut === 'confirmee'"
              class="actions-link"
              type="button"
              @click="navigateTo(`/pro/reservations/${demandeId}/actions`)"
            >
              Actions spéciales →
            </button>

          </template>

          <!-- ══ Onglet Documents ══════════════════════════════════════════════ -->
          <template v-else-if="activeTab === 'documents'">

            <div class="tab-section-header">
              <button
                class="tab-section-btn"
                type="button"
                :disabled="demande.statut !== 'confirmee'"
                @click="triggerUpload"
              >
                ↑ Déposer un document
              </button>
            </div>

            <div v-if="demande.statut !== 'confirmee'" class="docs-info">
              Le dépôt de documents est disponible une fois la réservation confirmée.
            </div>

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
                  <span class="doc-meta">{{ doc.uploadedByName }} · {{ formatDocDate(doc.uploadedAt) }}</span>
                </div>
                <div class="doc-actions">
                  <a :href="doc.url" download class="doc-btn" title="Télécharger">↓</a>
                  <button
                    v-if="doc.uploadedByRole === 'pro'"
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

            <button
              v-if="demande.statut === 'confirmee'"
              class="upload-btn-mobile"
              type="button"
              @click="triggerUpload"
            >
              ↑ Déposer un document
            </button>

          </template>

        </div>
      </div>
    </div>

    <!-- Skeleton -->
    <div v-else-if="loading" class="board-skeleton">
      <div class="skeleton-block skeleton-text" />
      <div class="skeleton-notes">
        <div v-for="i in 3" :key="i" class="skeleton-note skeleton-text" />
      </div>
    </div>

    <!-- ── FAB (mobile, onglet notes) ─────────────────────────────────────────── -->
    <button
      v-if="demande && activeTab === 'notes'"
      class="note-fab"
      type="button"
      aria-label="Ajouter une note"
      @click="openNoteModal"
    >
      +
    </button>

    <!-- ── CTA sticky ─────────────────────────────────────────────────────────── -->
    <div v-if="demande && showCta" class="cta-bar">
      <button
        v-if="demande.statut === 'nouvelle' || demande.statut === 'recontactee'"
        class="cta-bar__btn cta-bar__btn--secondary"
        type="button"
        @click="openRefusalModal"
      >
        Refuser
      </button>
      <button
        v-if="demande.statut === 'nouvelle'"
        class="cta-bar__btn cta-bar__btn--primary"
        type="button"
        :disabled="ctaLoading"
        @click="recontacter"
      >
        Recontacter le client
      </button>
      <button
        v-else-if="demande.statut === 'recontactee'"
        class="cta-bar__btn cta-bar__btn--primary"
        type="button"
        :disabled="ctaLoading"
        @click="confirmer"
      >
        Confirmer la réservation
      </button>
      <div v-else-if="demande.statut === 'confirmee'" class="cta-bar__confirmed">
        Réservation confirmée ✓
      </div>
    </div>

    <!-- ── Modal ajout note ───────────────────────────────────────────────────── -->
    <SgiltDialog
      v-if="noteModalOpen"
      v-model:open="noteModalOpen"
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
        <label class="note-form__personal">
          <input v-model="noteIsPersonal" type="checkbox" />
          <span>Note privée 🔒 (visible uniquement par moi)</span>
        </label>
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

    <!-- ── Modal refus ────────────────────────────────────────────────────────── -->
    <SgiltDialog
      v-if="refusalModalOpen"
      v-model:open="refusalModalOpen"
      title="Refuser la demande"
      max-width="600px"
    >
      <div class="refusal-form">
        <p class="refusal-form__lead">Pourquoi refusez-vous cette demande ?</p>
        <div class="refusal-form__reasons">
          <label v-for="r in REFUSAL_REASONS" :key="r.id" class="refusal-reason">
            <input v-model="refusalReasons" type="checkbox" :value="r.id" />
            <span>{{ r.label }}</span>
          </label>
        </div>
        <label class="refusal-form__communicate">
          <input v-model="communicateReason" type="checkbox" />
          <span>Communiquer le motif au client</span>
        </label>
        <button
          class="refusal-form__submit"
          type="button"
          :disabled="refusalReasons.length === 0 || refusalLoading"
          @click="submitRefusal"
        >
          Confirmer le refus
        </button>
      </div>
    </SgiltDialog>

  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'pro' })

import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import EventBlock from '~/components/app/EventBlock.vue'
import { ProMockService } from '~/services/pro.mock'
import type { ProDemandeDetail, ProDocument } from '~/services/pro.mock'
import type { EventDetail } from '~/types/event'

const route = useRoute()
const demandeId = Number(route.params.id)

// ── Cover images ───────────────────────────────────────────────────────────────
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

const coverImage = computed(
  () => COVER_IMAGES[demande.value?.clientInfo.eventType ?? ''] ?? COVER_IMAGES.autre,
)

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
const demande = ref<ProDemandeDetail | null>(null)
const loading = ref(true)

onMounted(async () => {
  demande.value = await ProMockService.getDemandeById(demandeId)
  loading.value = false
})

// ── EventDetail façade (lecture seule pour EventBlock) ────────────────────────
const proEventDetail = computed<EventDetail | null>(() => {
  const d = demande.value
  if (!d) return null
  return {
    id: String(d.id),
    title: d.clientInfo.eventName,
    date: d.dateIso,
    eventType: d.clientInfo.eventType,
    ville: d.clientInfo.ville,
    nbInvites: d.clientInfo.nbInvites,
    sharedNote: '',
    reservations: [],
    journal: [],
  }
})

// ── Statut labels ─────────────────────────────────────────────────────────────
const STATUT_LABELS: Record<string, string> = {
  nouvelle: 'Nouvelle',
  recontactee: 'Recontactée',
  confirmee: 'Confirmée',
  cloturee: 'Clôturée',
  annulee: 'Annulée',
}

// ── Onglets ────────────────────────────────────────────────────────────────────
const activeTab = ref<'notes' | 'documents'>('notes')

// ── Notes ─────────────────────────────────────────────────────────────────────
const sortedNotes = computed(() =>
  [...(demande.value?.notes ?? [])].sort(
    (a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime(),
  ),
)

const noteModalOpen = ref(false)
const newNote = ref('')
const noteIsPersonal = ref(false)
const sending = ref(false)
const noteTextareaRef = ref<HTMLTextAreaElement | null>(null)

function openNoteModal() {
  noteIsPersonal.value = false
  noteModalOpen.value = true
}

watch(noteModalOpen, (val) => {
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
  const note = await ProMockService.addNote(demandeId, content, noteIsPersonal.value)
  demande.value?.notes.push(note)
  newNote.value = ''
  noteIsPersonal.value = false
  sending.value = false
  noteModalOpen.value = false
}

// ── Documents ─────────────────────────────────────────────────────────────────
const sortedDocuments = computed(() =>
  [...(demande.value?.documents ?? [])].sort(
    (a, b) => new Date(b.uploadedAt).getTime() - new Date(a.uploadedAt).getTime(),
  ),
)

const fileInputRef = ref<HTMLInputElement | null>(null)

function triggerUpload() {
  if (demande.value?.statut !== 'confirmee') return
  fileInputRef.value?.click()
}

function handleUpload(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file || !demande.value) return
  const fileType: ProDocument['fileType'] =
    file.type === 'application/pdf' ? 'pdf' : file.type.startsWith('image/') ? 'image' : 'other'
  const doc: ProDocument = {
    id: `pd-${Date.now()}`,
    name: file.name,
    fileType,
    url: URL.createObjectURL(file),
    uploadedByRole: 'pro',
    uploadedByName: 'DJ Animation',
    uploadedAt: new Date().toISOString(),
  }
  demande.value.documents.unshift(doc)
  ;(e.target as HTMLInputElement).value = ''
}

function deleteDocument(id: string) {
  if (!demande.value) return
  demande.value.documents = demande.value.documents.filter((d) => d.id !== id)
}

// ── CTA bar ───────────────────────────────────────────────────────────────────
const ctaLoading = ref(false)
const showCta = computed(() =>
  demande.value ? ['nouvelle', 'recontactee', 'confirmee'].includes(demande.value.statut) : false,
)

async function recontacter() {
  if (!demande.value || ctaLoading.value) return
  ctaLoading.value = true
  await ProMockService.updateStatut(demandeId, 'recontactee')
  demande.value.statut = 'recontactee'
  ctaLoading.value = false
}

async function confirmer() {
  if (!demande.value || ctaLoading.value) return
  ctaLoading.value = true
  await ProMockService.updateStatut(demandeId, 'confirmee')
  demande.value.statut = 'confirmee'
  ctaLoading.value = false
}

// ── Refus ─────────────────────────────────────────────────────────────────────
const REFUSAL_REASONS = [
  { id: 'indisponible', label: 'Je ne suis pas disponible à cette date' },
  { id: 'hors-zone', label: "La localisation est hors de ma zone d'intervention" },
  { id: 'hors-budget', label: 'Le budget indiqué est insuffisant' },
  { id: 'incompatible', label: 'La prestation ne correspond pas à mon offre' },
  { id: 'autre', label: 'Autre raison' },
]

const refusalModalOpen = ref(false)
const refusalReasons = ref<string[]>([])
const communicateReason = ref(false)
const refusalLoading = ref(false)

function openRefusalModal() {
  refusalReasons.value = []
  communicateReason.value = false
  refusalModalOpen.value = true
}

async function submitRefusal() {
  if (refusalReasons.value.length === 0 || refusalLoading.value) return
  refusalLoading.value = true
  await ProMockService.updateStatut(demandeId, 'annulee')
  if (demande.value) demande.value.statut = 'annulee'
  refusalLoading.value = false
  refusalModalOpen.value = false
}

// ── Format dates ──────────────────────────────────────────────────────────────
function formatNoteDate(iso: string) {
  return new Date(iso).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  })
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
$cta-h: 72px;

// ── Page wrapper ───────────────────────────────────────────────────────────────
.pro-detail {
  min-height: 100%;
  background-color: #f5f5f3;
  display: flex;
  flex-direction: column;
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
    font-size: 26px;
    font-weight: 600;
    color: #fff;
    line-height: 1.1;
    text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
    max-width: 85%;

    @media (min-width: $desktop) {
      font-size: 36px;
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

    &--nouvelle {
      background: rgba(230, 184, 0, 0.45);
      color: #fff;
    }
    &--recontactee {
      background: rgba(44, 92, 197, 0.45);
      color: #fff;
    }
    &--confirmee {
      background: rgba(59, 109, 17, 0.45);
      color: #fff;
    }
    &--annulee,
    &--cloturee {
      background: rgba(163, 45, 45, 0.45);
      color: #fff;
    }
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

// ── Layout principal ───────────────────────────────────────────────────────────
.pro-layout {
  display: flex;
  flex-direction: column;
  flex: 1;

  @media (min-width: $desktop) {
    display: grid;
    grid-template-columns: 380px 1fr;
    gap: 28px;
    align-items: start;
    max-width: 1200px;
    width: 100%;
    margin: 0 auto;
    padding: 32px 40px calc($cta-h + 40px);
  }
}

// ── Colonne gauche : Event block ───────────────────────────────────────────────
.pro-layout__left {
  padding: $spacing-m $spacing-m 0;

  @media (min-width: $desktop) {
    padding: 0;
    position: sticky;
    top: calc(3.3rem + 16px);
  }
}

// ── Colonne droite : Onglets + Contenu ─────────────────────────────────────────
.pro-layout__right {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

// ── EventBlock overrides (read-only, header masqué) ────────────────────────────
:deep(.event-block__header) {
  display: none;
}

:deep(.event-note) {
  display: none;
}

:deep(.event-block) {
  box-shadow: 0 1px 4px rgba(47, 42, 37, 0.07);
}

// ── Barre d'onglets ────────────────────────────────────────────────────────────
.tab-bar {
  position: sticky;
  top: 3.3rem;
  z-index: $z-header;
  display: flex;
  background: #fff;
  border-bottom: 1px solid $divider-color;
  padding: 0 $spacing-m;

  @media (min-width: $desktop) {
    padding: 0;
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

    &:first-child {
      padding-left: 0;
    }
  }
}

// ── Corps ─────────────────────────────────────────────────────────────────────
.detail-body {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: $spacing-m $spacing-m calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $cta-h + $spacing-m);

  @media (min-width: $desktop) {
    padding: $spacing-m 0 0;
  }
}

// ── Header de section (desktop) ────────────────────────────────────────────────
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

  &:hover:not(:disabled) {
    border-color: $brand-primary;
    color: $brand-primary;
  }

  &:disabled {
    opacity: 0.4;
    cursor: default;
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

// ── Message initial ───────────────────────────────────────────────────────────
.note-initial {
  background: #fff;
  border-radius: $radius-md;
  border: 0.5px solid rgba(47, 42, 37, 0.10);
  border-left: 3px solid $brand-accent;
  box-shadow: 0 1px 4px rgba(47, 42, 37, 0.07);
  padding: $spacing-s $spacing-m;
  display: flex;
  flex-direction: column;
  gap: 6px;

  &__label {
    font-family: 'Inter', sans-serif;
    font-size: 0.65rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: $brand-accent;
  }

  &__content {
    font-family: 'Inter', sans-serif;
    font-size: 0.9rem;
    color: $text-primary;
    line-height: 1.6;
    margin: 0;
    white-space: pre-wrap;
  }

  &__meta {
    font-family: 'Inter', sans-serif;
    font-size: 0.72rem;
    color: $text-secondary;
    opacity: 0.65;
  }
}

// ── Note card ─────────────────────────────────────────────────────────────────
.note-card {
  background: #fff;
  border-radius: $radius-md;
  border: 0.5px solid rgba(47, 42, 37, 0.10);
  box-shadow: 0 1px 4px rgba(47, 42, 37, 0.07);
  padding: $spacing-s $spacing-m;
  display: flex;
  flex-direction: column;
  gap: 6px;

  &--client {
    background: #fff;
  }

  &--pro {
    background: #f5f0e8;
  }

  &--personal {
    background: #f5f5f3;
    border-style: dashed;
    border-color: rgba(47, 42, 37, 0.15);
  }

  &__header {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
  }

  &__lock {
    font-size: 0.75rem;
  }

  &__author {
    font-family: 'Inter', sans-serif;
    font-size: 0.75rem;
    font-weight: 600;
    color: $text-primary;
  }

  &__date {
    font-family: 'Inter', sans-serif;
    font-size: 0.72rem;
    color: $text-secondary;
    opacity: 0.65;
    margin-left: auto;
  }

  &__content {
    font-family: 'Inter', sans-serif;
    font-size: 0.875rem;
    color: $text-primary;
    line-height: 1.6;
    margin: 0;
    white-space: pre-wrap;
  }
}

// ── Lien actions spéciales ────────────────────────────────────────────────────
.actions-link {
  display: block;
  width: 100%;
  padding: $spacing-s 0;
  border: none;
  background: none;
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  font-weight: 500;
  color: $text-secondary;
  text-align: left;
  cursor: pointer;
  transition: color 150ms ease;

  &:hover {
    color: $text-primary;
  }
}

// ── Documents ─────────────────────────────────────────────────────────────────
.docs-info {
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  color: $text-secondary;
  background: rgba(47, 42, 37, 0.04);
  border-radius: $radius-md;
  padding: $spacing-s $spacing-m;
  text-align: center;
}

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

// ── FAB ───────────────────────────────────────────────────────────────────────
.note-fab {
  position: fixed;
  right: $spacing-m;
  bottom: calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $cta-h + $spacing-m);
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

  @media (min-width: $desktop) {
    display: none;
  }
}

// ── CTA sticky bar ────────────────────────────────────────────────────────────
.cta-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: calc($bottom-nav-h + env(safe-area-inset-bottom, 0px));
  z-index: $z-header;
  height: $cta-h;
  background: #fff;
  border-top: 1px solid $divider-color;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-s;
  padding: 0 $spacing-m;

  @media (min-width: $desktop) {
    bottom: 0;
    padding: 0 40px;
    justify-content: flex-end;
  }

  &__btn {
    height: 44px;
    border-radius: $radius-md;
    border: none;
    font-family: 'Inter', sans-serif;
    font-size: 0.875rem;
    font-weight: 600;
    cursor: pointer;
    transition: opacity 150ms ease;

    &:disabled {
      opacity: 0.5;
      cursor: default;
    }

    &--primary {
      flex: 1;
      max-width: 280px;
      background: $brand-accent;
      color: $brand-primary;

      &:hover:not(:disabled) {
        opacity: 0.85;
      }
    }

    &--secondary {
      flex-shrink: 0;
      padding: 0 $spacing-m;
      background: transparent;
      border: 1px solid rgba(163, 45, 45, 0.4);
      color: #a32d2d;

      &:hover:not(:disabled) {
        background: rgba(163, 45, 45, 0.05);
      }
    }
  }

  &__confirmed {
    font-family: 'Inter', sans-serif;
    font-size: 0.875rem;
    font-weight: 600;
    color: #3b6d11;
  }
}

// ── Formulaire note ───────────────────────────────────────────────────────────
.note-form {
  display: flex;
  flex-direction: column;
  flex: 1;
  padding: 32px;
  gap: $spacing-m;

  &__textarea {
    flex: 1;
    width: 100%;
    min-height: 200px;
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

  &__personal {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    color: $text-secondary;
    cursor: pointer;
    user-select: none;

    input {
      cursor: pointer;
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

// ── Formulaire refus ──────────────────────────────────────────────────────────
.refusal-form {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: 32px;

  &__lead {
    font-family: 'Inter', sans-serif;
    font-size: 0.9rem;
    color: $text-primary;
    margin: 0;
  }

  &__reasons {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
  }

  &__communicate {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    color: $text-secondary;
    cursor: pointer;
    padding-top: $spacing-xs;
    border-top: 1px solid $divider-color;
    user-select: none;

    input {
      cursor: pointer;
    }
  }

  &__submit {
    width: 100%;
    height: 48px;
    border: none;
    border-radius: $radius-md;
    background: #a32d2d;
    color: #fff;
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

.refusal-reason {
  display: flex;
  align-items: center;
  gap: $spacing-xs;
  font-family: 'Inter', sans-serif;
  font-size: 0.875rem;
  color: $text-primary;
  cursor: pointer;
  user-select: none;

  input {
    cursor: pointer;
  }
}

// ── Skeleton ──────────────────────────────────────────────────────────────────
.board-skeleton {
  padding: $spacing-m;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}

.skeleton-block {
  height: 120px;
  border-radius: $radius-md;
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
