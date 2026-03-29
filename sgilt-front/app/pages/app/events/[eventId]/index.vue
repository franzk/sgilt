<template>
  <div class="event-board">
    <!-- ── Bandeau couverture ──────────────────────────────────────────────────── -->
    <div
      v-if="event"
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
          aria-label="Modifier l'événement"
          @click="openEditDialog"
        >
          <EditIcon class="edit-mobile-icon" />
        </button>
      </div>
      <button class="edit-img" type="button" @click="openEditDialog">Modifier l'image</button>
    </div>

    <template v-if="event">
      <!-- ── Widget ─────────────────────────────────────────────────────────────── -->
      <div class="event-widget">
        <p class="phrase">{{ event.phrase }}</p>
        <p class="subtitle">{{ event.phraseSubtitle }}</p>
        <div class="pills">
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
        </div>
      </div>

      <!-- ── Contenu ────────────────────────────────────────────────────────────── -->
      <div class="board-content">
        <!-- Bloc événement -->
        <div class="event-block-wrap">
          <EventBlock
            variant="client"
            :event="event"
            :client-info="mockClientInfo"
            @updated="onEventUpdated"
            @updated-client-info="onClientInfoUpdated"
          />
        </div>

        <!-- Réservations -->
        <section class="reservations">
          <div class="grid">
            <ReservationCard
              v-for="r in sortedReservations"
              :key="r.id"
              :reservation="r"
              @click="navigateTo(`/app/events/${eventId}/reservations/${r.id}`)"
            />
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

    <!-- ── Dialog modification titre + couverture ────────────────────────────── -->
    <SgiltDialog
      v-if="event"
      v-model:open="editDialogOpen"
      title="Modifier l'événement"
      max-width="560px"
    >
      <div class="edit-event-form">
        <!-- Titre -->
        <div class="form-field">
          <label class="form-label">Titre</label>
          <input
            v-model="editDraft.title"
            class="form-input"
            type="text"
            placeholder="Nom de l'événement"
          />
        </div>

        <!-- Couverture -->
        <div class="form-field">
          <label class="form-label">Image de couverture</label>
          <div class="cover-grid">
            <button
              v-for="[key, url] in Object.entries(DEFAULT_COVERS)"
              :key="key"
              class="cover-tile"
              :class="{ selected: editDraft.coverImage === url }"
              type="button"
              :style="{ backgroundImage: `url(${url})` }"
              @click="editDraft.coverImage = url"
            >
              <span class="cover-label">{{ COVER_LABELS[key] }}</span>
              <span v-if="editDraft.coverImage === url" class="cover-check" aria-hidden="true"
                >✓</span
              >
            </button>
          </div>

          <!-- Upload -->
          <button class="upload-btn" type="button" @click="uploadRef?.click()">
            <span>📎 Importer ma propre photo</span>
          </button>
          <input
            ref="uploadRef"
            type="file"
            accept="image/*"
            style="display: none"
            @change="handleUpload"
          />

          <!-- Aperçu custom -->
          <div
            v-if="editDraft.coverImage && !isPresetCover(editDraft.coverImage)"
            class="custom-preview"
            :style="{ backgroundImage: `url(${editDraft.coverImage})` }"
          />
        </div>

        <!-- Actions -->
        <div class="form-actions">
          <SgiltButton variant="secondary" @click="editDialogOpen = false">Annuler</SgiltButton>
          <SgiltButton :disabled="editSaving" @click="saveEdit">
            {{ editSaving ? 'Enregistrement…' : 'Enregistrer' }}
          </SgiltButton>
        </div>
      </div>
    </SgiltDialog>
  </div>
</template>

<script setup lang="ts">
import ReservationCard from '~/components/app/ReservationCard.vue'
import EventBlock from '~/components/app/EventBlock.vue'
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import { EventMockService } from '~/services/event.mock'
import type { EventDetail, EventPatch, ReservationStatus, ClientContactInfo } from '~/types/event'
import { CLIENT_STATUS_CONFIG, RESERVATION_STATUS_ORDER } from '~/constants/reservation-status'
import { EditIcon } from '@remixicons/vue/line'

definePageMeta({ layout: 'app' })

const { t } = useI18n()

const route = useRoute()
const eventId = route.params.eventId as string

// ── Cover images ───────────────────────────────────────────────────────────────
const DEFAULT_COVERS: Record<string, string> = {
  mariage:
    'https://images.unsplash.com/photo-1519741497674-611481863552?w=1400&auto=format&fit=crop',
  anniversaire:
    'https://images.unsplash.com/photo-1530103862676-de8c9debad1d?w=1400&auto=format&fit=crop',
  soiree_privee:
    'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=1400&auto=format&fit=crop',
  fete_entreprise:
    'https://images.unsplash.com/photo-1511578314322-379afb476865?w=1400&auto=format&fit=crop',
  autre: 'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=1400&auto=format&fit=crop',
}

const COVER_LABELS: Record<string, string> = {
  mariage: 'Mariage',
  anniversaire: 'Anniversaire',
  soiree_privee: 'Soirée privée',
  fete_entreprise: "Fête d'entreprise",
  autre: 'Autre',
}

const coverImage = computed(
  () =>
    event.value?.coverImage ?? DEFAULT_COVERS[event.value?.eventType ?? ''] ?? DEFAULT_COVERS.autre,
)

function isPresetCover(url: string) {
  return Object.values(DEFAULT_COVERS).includes(url)
}

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

const mockClientInfo: ClientContactInfo = {
  firstName: 'Sophie',
  lastName: 'Martin',
  phone: '06 12 34 56 78',
  email: 'sophie.martin@example.com',
}

function onEventUpdated(patch: EventPatch) {
  if (event.value) Object.assign(event.value, patch)
}

function onClientInfoUpdated(_patch: Partial<ClientContactInfo>) {
  // TODO: persist client info via API
}

// ── Dialog modification ────────────────────────────────────────────────────────
const editDialogOpen = ref(false)
const editSaving = ref(false)
const uploadRef = ref<HTMLInputElement | null>(null)

const editDraft = reactive({
  title: '',
  coverImage: null as string | undefined | null,
})

function openEditDialog() {
  editDraft.title = event.value?.title ?? ''
  editDraft.coverImage =
    event.value?.coverImage ?? DEFAULT_COVERS[event.value?.eventType ?? ''] ?? DEFAULT_COVERS.autre
  editDialogOpen.value = true
}

function handleUpload(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  editDraft.coverImage = URL.createObjectURL(file)
}

async function saveEdit() {
  if (!event.value) return
  editSaving.value = true
  const patch: EventPatch = { title: editDraft.title, coverImage: editDraft.coverImage }
  await EventMockService.patchEvent(event.value.id, patch)
  Object.assign(event.value, patch)
  editDialogOpen.value = false
  editSaving.value = false
}

// ── Widget — pills statut ──────────────────────────────────────────────────────
const STATUS_PILL_ICONS: Record<ReservationStatus, string> = {
  nouvelle: '!',
  en_discussion: '↩',
  confirmee: '✓',
  refusee: '✕',
  annulee: '✕',
  realisee: '✓',
}

const statusPills = computed(() => {
  if (!event.value) return []
  return (Object.keys(STATUS_PILL_ICONS) as ReservationStatus[])
    .map((status) => ({
      status,
      icon: STATUS_PILL_ICONS[status],
      count: event.value!.reservations.filter((r) => r.status === status).length,
    }))
    .filter((pill) => pill.count > 0)
})

// ── Réservations groupées ─────────────────────────────────────────────────────
const sortedReservations = computed(() => {
  if (!event.value) return []
  return [...event.value.reservations].sort(
    (a, b) =>
      RESERVATION_STATUS_ORDER.indexOf(a.status) - RESERVATION_STATUS_ORDER.indexOf(b.status),
  )
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

  .phrase {
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

  .subtitle {
    font-family: 'Inter', sans-serif;
    font-size: 0.82rem;
    color: $text-secondary;
    margin: 0;
    line-height: 1.5;
  }

  .pills {
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

  .icon {
    font-size: 0.7rem;
    line-height: 1;
  }

  .count {
    font-weight: 700;
  }

  .label {
    font-weight: 500;
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

// ── Event block sticky (desktop) ──────────────────────────────────────────────
.event-block-wrap {
  @media (min-width: $desktop) {
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

  .grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: $spacing-s;

    @media (min-width: $desktop) {
      grid-template-columns: repeat(3, 1fr);
    }
  }
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

// ── Dialog modification ────────────────────────────────────────────────────────

.edit-event-form {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: $spacing-m $spacing-l $spacing-l;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
}

.form-label {
  font-family: 'Inter', sans-serif;
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  color: $text-secondary;
}

.form-input {
  width: 100%;
  padding: $spacing-xs $spacing-s;
  border: 1px solid $divider-color;
  border-radius: $radius-sm;
  font-family: 'Cormorant Garamond', serif;
  font-size: 1.2rem;
  font-weight: 600;
  color: $brand-primary;
  background: $surface-soft;
  outline: none;
  box-sizing: border-box;
  transition:
    border-color 150ms ease,
    box-shadow 150ms ease;

  &:focus {
    border-color: $input-focus-border-color;
    box-shadow: $input-focus-box-shadow;
    background: #fff;
  }
}

.cover-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-xs;

  @media (min-width: $desktop) {
    grid-template-columns: repeat(5, 1fr);
  }
}

.cover-tile {
  position: relative;
  aspect-ratio: 3/2;
  border-radius: $radius-sm;
  background-size: cover;
  background-position: center;
  border: 2.5px solid transparent;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 120ms ease;
  padding: 0;

  &.selected {
    border-color: $brand-accent;
  }

  .cover-label {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 4px 6px;
    background: linear-gradient(to top, rgba(0, 0, 0, 0.55), transparent);
    font-family: 'Inter', sans-serif;
    font-size: 0.6rem;
    font-weight: 600;
    color: #fff;
    text-align: center;
  }

  .cover-check {
    position: absolute;
    top: 4px;
    right: 4px;
    width: 18px;
    height: 18px;
    border-radius: 50%;
    background: $brand-accent;
    color: $brand-primary;
    font-size: 0.65rem;
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.upload-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-xs;
  width: 100%;
  padding: $spacing-s;
  border: 1.5px dashed $divider-color;
  border-radius: $radius-sm;
  background: none;
  font-family: inherit;
  font-size: 0.85rem;
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

.custom-preview {
  width: 100%;
  aspect-ratio: 16/9;
  border-radius: $radius-sm;
  background-size: cover;
  background-position: center;
  border: 2.5px solid $brand-accent;
}

.form-actions {
  display: flex;
  gap: $spacing-s;
  justify-content: flex-end;
}
</style>
