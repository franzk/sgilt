<template>
  <div class="pro-detail">
    <!-- ── Bandeau couverture ─────────────────────────────────────────────────── -->
    <div
      v-if="demande"
      ref="bannerRef"
      class="cover-banner"
      :style="{ backgroundImage: `url(${coverImage})` }"
    >
      <div class="overlay" />
      <button class="back-btn" type="button" @click="router.back()">‹ Retour</button>
      <div class="bottom">
        <div class="info">
          <span class="category">{{ demande.category }}</span>
          <span class="name">{{ demande.event.title }}</span>
        </div>
        <span class="badge" :style="getStatusOverlayStyle(demande.status)">
          {{ t(`reservation.statut.${demande.status}`) }}
        </span>
      </div>
    </div>

    <!-- ── Contenu ───────────────────────────────────────────────────────────── -->
    <template v-if="demande">
      <!-- Bannière statut — pleine largeur -->
      <BookingStatusBanner
        v-if="demande.phraseInfoState"
        class="booking-status-banner-full"
        :phrase-info-state="demande.phraseInfoState"
        :status="demande.status"
        :show-action-recquise="['nouvelle', 'en_discussion'].includes(demande.status)"
      />

      <div class="booking-layout">
        <!-- Colonne gauche : bloc bento unique -->
        <div class="left">
          <EventBlock variant="pro" :event="demande.event" :client-info="demande.clientInfo" />
          <hr v-if="!isMobile" class="left-divider" />

          <!-- nouvelle : CTA statut desktop uniquement -->
          <BookingStatusCta
            v-if="demande.status === 'nouvelle' && !isMobile"
            layout="column"
            status="nouvelle"
            :loading="ctaLoading"
            @confirm="recontacter"
            @refuse="openRefusalModal"
          />

          <BookingContactActions
            v-if="demande.status !== 'nouvelle' && !isMobile"
            variant="big"
            layout="column"
            :client-info="demande.clientInfo"
            :mailto-href="mailtoHref"
          />
        </div>

        <!-- Colonne droite : chaque bloc dans sa propre bento card -->
        <div class="right">
          <!-- nouvelle ─────────────────────────────────────────── -->
          <template v-if="demande.status === 'nouvelle'">
            <!-- Desktop : cartes contact dans leur bento -->
            <div v-if="!isMobile" class="bento-card">
              <BookingContactActions
                variant="big"
                layout="row"
                :desktop-only="true"
                :client-info="demande.clientInfo"
                :mailto-href="mailtoHref"
              />
            </div>

            <!-- Mobile : contact + CTA dans un seul bloc continu -->
            <div v-else class="mobile-cta-block">
              <BookingContactActions
                variant="big"
                layout="row"
                :client-info="demande.clientInfo"
                :mailto-href="mailtoHref"
              />
              <BookingStatusCta
                status="nouvelle"
                :loading="ctaLoading"
                @confirm="recontacter"
                @refuse="openRefusalModal"
              />
            </div>
          </template>

          <!-- en_discussion ────────────────────────────────────── -->
          <template v-else-if="demande.status === 'en_discussion'">
            <div class="bento-card">
              <BookingStatusCta
                layout="row"
                status="en_discussion"
                :loading="ctaLoading"
                @confirm="confirmer"
                @refuse="openRefusalModal"
              />
            </div>
          </template>

          <!-- Flux notes + documents -->
          <div class="bento-card">
            <ReservationFeed
              :items="feedItems"
              :can-add-note="isEditable"
              :can-upload-document="isEditable"
              :show-personal-toggle="true"
              @add-note="onAddNote"
              @upload-document="onUploadDocument"
              @delete-document="onDeleteDocument"
            />
          </div>

          <!-- refusee / annulee ────────────────────────────────── -->
          <div
            v-if="demande.status === 'refusee' || demande.status === 'annulee'"
            class="bento-card"
          >
            <BookingResumeContactLink :mailto-href="mailtoHref" />
          </div>

          <!-- confirmee ────────────────────────────────────────── -->
          <div v-if="demande.status === 'confirmee'" class="bento-card">
            <BookingCriticalActions :demande-id="demandeId" />
          </div>
        </div>
      </div>
    </template>

    <!-- Skeleton cover -->
    <div v-else-if="loading" class="cover-banner skeleton skeleton-text" />

    <!-- Skeleton layout -->
    <div v-if="loading" class="booking-layout">
      <!-- Colonne gauche -->
      <div class="left">
        <div class="sk-brief">
          <div class="sk-row">
            <div class="skeleton-text sk-pill" />
            <div class="skeleton-text sk-pill" />
            <div class="skeleton-text sk-pill" />
          </div>
          <div class="sk-lines">
            <div class="skeleton-text sk-line sk-line--80" />
            <div class="skeleton-text sk-line sk-line--60" />
            <div class="skeleton-text sk-line sk-line--70" />
          </div>
        </div>
      </div>

      <!-- Colonne droite -->
      <div class="right">
        <div class="bento-card sk-bento">
          <div class="skeleton-text sk-line sk-line--40" />
          <div class="skeleton-text sk-btn" />
        </div>
        <div class="bento-card sk-bento">
          <div v-for="i in 3" :key="i" class="skeleton-text sk-note" />
        </div>
      </div>
    </div>

    <!-- ── Contact sticky mobile uniquement (en_discussion + confirmee) ─────── -->
    <BookingContactActions
      v-if="
        isMobile &&
        demande &&
        (demande.status === 'en_discussion' || demande.status === 'confirmee')
      "
      variant="sticky"
      layout="row"
      :client-info="demande.clientInfo"
      :mailto-href="mailtoHref"
    />

    <!-- ── Modal refus ────────────────────────────────────────────────────────── -->
    <SgiltDialog
      v-if="refusalModalOpen"
      v-model:open="refusalModalOpen"
      title="Refuser la demande"
      max-width="600px"
    >
      <div class="refusal-form">
        <p class="lead">Pourquoi refusez-vous cette demande ?</p>
        <div class="reasons">
          <label v-for="r in REFUSAL_REASONS" :key="r.id" class="refusal-reason">
            <input v-model="refusalReason" type="radio" :value="r.id" />
            <span>{{ r.label }}</span>
          </label>
        </div>
        <label class="communicate">
          <input v-model="communicateReason" type="checkbox" />
          <span>Communiquer le motif au client</span>
        </label>
        <button
          class="submit"
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
import ReservationFeed from '~/components/shared/ReservationFeed.vue'
import BookingStatusBanner from '~/components/pro/BookingStatusBanner.vue'
import BookingContactActions from '~/components/pro/BookingContactActions.vue'
import BookingStatusCta from '~/components/pro/BookingStatusCta.vue'
import EventBlock from '~/components/app/EventBlock.vue'
import BookingResumeContactLink from '~/components/pro/BookingResumeContactLink.vue'
import BookingCriticalActions from '~/components/pro/BookingCriticalActions.vue'
import { ProMockService } from '~/services/pro.mock'
import type { ProDemandeDetail, ReservationDocument, FeedItem } from '~/types/event'
import { getStatusOverlayStyle } from '~/constants/reservation-status'

const { isMobile } = useDevice()

const route = useRoute()
const router = useRouter()
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

const { t } = useI18n()

const isEditable = computed(() => {
  const s = demande.value?.status
  return s !== 'refusee' && s !== 'annulee' && s !== 'realisee'
})

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
    uploadedBy: {
      id: 'presta-3',
      name: 'DJ Animation',
      role: 'prestataire',
      photo: '/images/prestataires/dj-animation.jpg',
    },
    uploadedAt: new Date(),
  }
  demande.value.documents.unshift(doc)
}

function onDeleteDocument(id: string) {
  if (!demande.value) return
  demande.value.documents = demande.value.documents.filter((d) => d.id !== id)
}

// ── mailto ─────────────────────────────────────────────────────────────────────
const mailtoHref = computed(() => {
  if (!demande.value) return '#'
  const { email, firstName } = demande.value.clientInfo
  const subject = encodeURIComponent(`Votre demande — ${demande.value.event.title}`)
  const body = encodeURIComponent(
    `Bonjour ${firstName},\n\nJe reviens vers vous suite à votre demande pour ${demande.value.event.title}.\n\nCordialement,\nDJ Animation`,
  )
  return `mailto:${email}?subject=${subject}&body=${body}`
})

// ── CTA ───────────────────────────────────────────────────────────────────────
const ctaLoading = ref(false)

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
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;
$sticky-h: 56px;

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
    max-height: 30vh;
    padding: $spacing-m $spacing-xl $spacing-l;
  }

  &.skeleton {
    background-image: none !important;
  }

  .overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(to bottom, rgba(47, 42, 37, 0.1), rgba(47, 42, 37, 0.65));
    pointer-events: none;
  }

  .bottom {
    position: relative;
    display: flex;
    align-items: flex-end;
    justify-content: space-between;
    gap: $spacing-s;
  }

  .info {
    display: flex;
    flex-direction: column;
    gap: 3px;
  }

  .category {
    font-family: 'Inter', sans-serif;
    font-size: 11px;
    font-weight: 600;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: rgba(255, 255, 255, 0.75);
  }

  .name {
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

  .badge {
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

// ── Bannière pleine largeur ────────────────────────────────────────────────────
.booking-status-banner-full {
  margin: $spacing-m $spacing-m 0;

  @media (min-width: $desktop) {
    max-width: 1200px;
    width: calc(100% - 80px);
    margin: 32px auto 0;
    border-radius: $radius-md;
  }
}

$bento-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
$bento-radius: $radius-sm;

// ── Layout 2 colonnes desktop ──────────────────────────────────────────────────
.booking-layout {
  display: flex;
  flex-direction: column;
  flex: 1;

  @media (min-width: $desktop) {
    display: grid;
    grid-template-columns: 340px 1fr;
    gap: 28px;
    align-items: start;
    max-width: 1200px;
    width: 100%;
    margin: 0 auto;
    padding: 32px 40px 60px;
  }

  // ── Colonne gauche — bento unique sur desktop ──────────────────────────────────
  .left {
    display: flex;
    flex-direction: column;
    gap: $spacing-m;
    padding: $spacing-m $spacing-m 0;

    @media (min-width: $desktop) {
      padding: $spacing-m;
      gap: $spacing-m;
      position: sticky;
      top: calc(3.3rem + 16px);
      background: #fff;
      border-radius: $bento-radius;
      box-shadow: $bento-shadow;
      border: 1px solid $divider-color;

      .left-divider {
        border: none;
        border-top: 1px solid $divider-color;
        margin: 0;
      }

      // EventBlock perd sa carte individuelle — le bento est la carte
      :deep(.event-block) {
        background: transparent;
        box-shadow: none;
        border-radius: 0;
      }

      // ContactActionCards dans la colonne — supprimer ombre redondante
      :deep(.contact-card) {
        box-shadow: none;
      }
    }
  }

  // ── Colonne droite ─────────────────────────────────────────────────────────────
  .right {
    display: flex;
    flex-direction: column;
    gap: $spacing-m;
    padding: $spacing-m $spacing-m
      calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $sticky-h + $spacing-m);
    min-width: 0;

    @media (min-width: $desktop) {
      padding: 0;
      gap: $spacing-m;
    }
  }
}

// ── Bloc CTA mobile (contact + confirm + refuse) ──────────────────────────────
.mobile-cta-block {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

// ── Bento card — colonne droite ────────────────────────────────────────────────
.bento-card {
  @media (min-width: $desktop) {
    background: #fff;
    border-radius: $bento-radius;
    box-shadow: $bento-shadow;
    overflow: hidden;
    padding: $spacing-m;

    // ContactActionCards dans une bento — supprimer ombre redondante, gérer le débordement
    :deep(.contact-card) {
      box-shadow: none;
    }

    // BookingStatusCta — les boutons s'étalent sur toute la largeur
    :deep(.booking-cta) {
      width: 100%;
    }

    // Feed — supprimer le padding interne superflu
    :deep(.feed) {
      gap: $spacing-s;
    }
  }
}

// ── Formulaire refus ──────────────────────────────────────────────────────────
.refusal-form {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: 32px;

  .lead {
    font-family: 'Inter', sans-serif;
    font-size: 0.9rem;
    color: $text-primary;
    margin: 0;
  }

  .reasons {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
  }

  .communicate {
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

  .submit {
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

.sk-brief {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

.sk-row {
  display: flex;
  gap: $spacing-xs;
}

.sk-pill {
  height: 24px;
  width: 72px;
  border-radius: 2rem;
}

.sk-lines {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
}

.sk-line {
  height: 0.8rem;
  border-radius: 4px;

  &--40 {
    width: 40%;
  }
  &--60 {
    width: 60%;
  }
  &--70 {
    width: 70%;
  }
  &--80 {
    width: 80%;
  }
}

.sk-bento {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
  padding: $spacing-m;
  background: #fff;
  border-radius: $bento-radius;
  box-shadow: $bento-shadow;
}

.sk-btn {
  height: 40px;
  border-radius: $radius-md;
}

.sk-note {
  height: 56px;
  border-radius: $radius-sm;
}
</style>
