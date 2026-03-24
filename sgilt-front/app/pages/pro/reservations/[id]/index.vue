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
          <span class="cover-banner__name">{{ demande.event.title }}</span>
        </div>
        <span class="cover-banner__badge" :style="getStatusOverlayStyle(demande.status)">
          {{ t(`reservation.statut.${demande.status}`) }}
        </span>
      </div>
    </div>

    <!-- ── Contenu ───────────────────────────────────────────────────────────── -->
    <template v-if="demande">
      <div class="notes-layout">
        <!-- Colonne gauche : Event block sticky -->
        <div class="notes-layout__left">
          <EventBlock v-if="proEventDetail" :event="proEventDetail" @updated="() => {}" />
        </div>

        <!-- Colonne droite -->
        <div class="notes-layout__right">
          <!-- Bloc contact — statut nouvelle -->
          <ContactCTABlock
            v-if="demande.status === 'nouvelle'"
            :client-info="demande.clientInfo"
            :mailto-href="mailtoHref"
            :cta-loading="ctaLoading"
            @confirm="recontacter"
            @refuse="openRefusalModal"
          />

          <!-- Flux notes + documents -->
          <ReservationFeed
            :items="feedItems"
            :can-add-note="true"
            :can-upload-document="demande.status === 'confirmee'"
            :show-personal-toggle="true"
            @add-note="onAddNote"
            @upload-document="onUploadDocument"
            @delete-document="onDeleteDocument"
          />

          <!-- Lien actions spéciales -->
          <button
            v-if="demande.status === 'confirmee'"
            class="actions-link"
            type="button"
            @click="navigateTo(`/pro/reservations/${demandeId}/actions`)"
          >
            Actions spéciales
          </button>
        </div>
      </div>
    </template>

    <!-- Skeleton -->
    <div v-else-if="loading" class="board-skeleton">
      <div class="skeleton-block skeleton-text" />
      <div v-for="i in 3" :key="i" class="skeleton-note skeleton-text" />
    </div>

    <!-- ── CTA sticky (en_discussion uniquement) ─────────────────────────────── -->
    <div v-if="demande && showCta" class="cta-bar">
      <button class="cta-bar__btn cta-bar__btn--secondary" type="button" @click="openRefusalModal">
        Refuser
      </button>
      <button
        class="cta-bar__btn cta-bar__btn--primary"
        type="button"
        :disabled="ctaLoading"
        @click="confirmer"
      >
        Confirmer la réservation
      </button>
    </div>

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
            <input v-model="refusalReason" type="radio" :value="r.id" />
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
          :disabled="!refusalReason || refusalLoading"
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
import ReservationFeed from '~/components/shared/ReservationFeed.vue'
import ContactCTABlock from '~/components/pro/ContactCTABlock.vue'
import { ProMockService } from '~/services/pro.mock'
import type { ProDemandeDetail, ReservationDocument, FeedItem } from '~/types/event'
import { getStatusOverlayStyle } from '~/constants/reservation-status'

const route = useRoute()
const demandeId = String(route.params.id)

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
  () =>
    demande.value?.event.coverImage ??
    COVER_IMAGES[demande.value?.event.eventType ?? ''] ??
    COVER_IMAGES.autre,
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

// ── EventDetail façade (lecture seule) ────────────────────────────────────────
const proEventDetail = computed(() => demande.value?.event ?? null)

const { t } = useI18n()

// ── Feed items ─────────────────────────────────────────────────────────────────
const feedItems = computed<FeedItem[]>(() => {
  if (!demande.value) return []
  const notes: FeedItem[] = demande.value.notes.map((n) => ({ ...n, _kind: 'note' as const }))
  const docs: FeedItem[] = demande.value.documents.map((d) => ({
    ...d,
    _kind: 'document' as const,
  }))
  return [...notes, ...docs]
})

async function onAddNote(content: string, isPersonal: boolean) {
  const note = await ProMockService.addNote(demandeId, content, isPersonal)
  demande.value?.notes.push(note)
}

function onUploadDocument(file: File) {
  if (!demande.value) return
  const fileType: ReservationDocument['fileType'] =
    file.type === 'application/pdf' ? 'pdf' : file.type.startsWith('image/') ? 'image' : 'other'
  const doc: ReservationDocument = {
    id: `pd-${Date.now()}`,
    name: file.name,
    fileType,
    url: URL.createObjectURL(file),
    uploadedBy: { id: 'presta-3', name: 'DJ Animation', role: 'prestataire', photo: '/images/prestataires/dj-animation.jpg' },
    uploadedAt: new Date().toISOString(),
  }
  demande.value.documents.unshift(doc)
}

function onDeleteDocument(id: string) {
  if (!demande.value) return
  demande.value.documents = demande.value.documents.filter((d) => d.id !== id)
}

// ── CTA ───────────────────────────────────────────────────────────────────────
const ctaLoading = ref(false)
const showCta = computed(() => demande.value?.status === 'en_discussion')

const mailtoHref = computed(() => {
  if (!demande.value) return '#'
  const { email, firstName } = demande.value.clientInfo
  const subject = encodeURIComponent(`Votre demande — ${demande.value.event.title}`)
  const body = encodeURIComponent(
    `Bonjour ${firstName},\n\nJe reviens vers vous suite à votre demande pour ${demande.value.event.title}.\n\nCordialement,\nDJ Animation`,
  )
  return `mailto:${email}?subject=${subject}&body=${body}`
})

async function recontacter() {
  if (!demande.value || ctaLoading.value) return
  ctaLoading.value = true
  await ProMockService.updateStatut(demandeId, 'en_discussion')
  demande.value.status = 'en_discussion'
  ctaLoading.value = false
}

async function confirmer() {
  if (!demande.value || ctaLoading.value) return
  ctaLoading.value = true
  await ProMockService.updateStatut(demandeId, 'confirmee')
  demande.value.status = 'confirmee'
  ctaLoading.value = false
}

// ── Refus ─────────────────────────────────────────────────────────────────────
const REFUSAL_REASONS = [
  { id: 'date', label: '📅 Date non disponible' },
  { id: 'zone', label: '📍 Zone géographique trop éloignée' },
  { id: 'jauge', label: "👥 Jauge d'invités incompatible" },
  { id: 'budget', label: '💰 Budget incompatible' },
  { id: 'type', label: "🎯 Type d'événement hors de mes prestations" },
  { id: 'autre', label: '••• Autre' },
]

const refusalModalOpen = ref(false)
const refusalReason = ref('')
const communicateReason = ref(true)
const refusalLoading = ref(false)

function openRefusalModal() {
  refusalReason.value = ''
  communicateReason.value = true
  refusalModalOpen.value = true
}

async function submitRefusal() {
  if (!refusalReason.value || refusalLoading.value) return
  refusalLoading.value = true
  await ProMockService.updateStatut(demandeId, 'annulee')
  if (demande.value) demande.value.status = 'annulee'
  refusalLoading.value = false
  refusalModalOpen.value = false
}

// ── Format dates ──────────────────────────────────────────────────────────────
function formatDate(iso: string) {
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
$tab-bar-h: 45px;

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

// ── Onglet Notes : layout 2 colonnes desktop ───────────────────────────────────
.notes-layout {
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

.notes-layout__left {
  padding: $spacing-m $spacing-m 0;

  @media (min-width: $desktop) {
    padding: 0;
    position: sticky;
    top: calc(3.3rem + $tab-bar-h + 16px);
  }
}

.notes-layout__right {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: $spacing-m $spacing-m
    calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $cta-h + $spacing-m);
  min-width: 0;

  @media (min-width: $desktop) {
    padding: 0;
  }
}

// ── EventBlock overrides (read-only) ──────────────────────────────────────────
:deep(.event-block__header) {
  display: none;
}

:deep(.event-note) {
  display: none;
}

:deep(.event-block) {
  box-shadow: 0 1px 4px rgba(47, 42, 37, 0.07);
}

// ── Lien actions spéciales ────────────────────────────────────────────────────
.actions-link {
  display: block;
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

.skeleton-note {
  height: 80px;
  border-radius: $radius-md;
}
</style>
