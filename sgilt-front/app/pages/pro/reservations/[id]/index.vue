<template>
  <div class="pro-detail">
    <!-- ── Bandeau couverture ─────────────────────────────────────────────────── -->
    <div
      v-if="reservation"
      ref="bannerRef"
      class="cover-banner"
      :style="{ backgroundImage: `url(${coverImage})` }"
    >
      <div class="overlay" />
      <button class="back-btn" type="button" @click="router.back()">
        {{ $t('pro.reservation-detail.back') }}
      </button>
      <div class="bottom">
        <div class="info">
          <span class="category">{{ reservation.category }}</span>
          <span class="name">{{ reservation.event.title }}</span>
        </div>
        <span class="badge" :style="getStatusOverlayStyle(reservation.status)">
          {{ t(`reservation.statut.${reservation.status}`) }}
        </span>
      </div>
    </div>

    <!-- ── Contenu ───────────────────────────────────────────────────────────── -->
    <template v-if="reservation">
      <!-- Bannière statut — pleine largeur -->
      <BookingStatusBanner
        v-if="reservation.phraseInfoState"
        class="booking-status-banner-full"
        :phrase-info-state="reservation.phraseInfoState"
        :status="reservation.status"
        :show-action-recquise="['nouvelle', 'en_discussion'].includes(reservation.status)"
      />

      <div class="booking-layout">
        <!-- Colonne gauche : bloc bento unique -->
        <div class="left">
          <EventBlock
            variant="pro"
            :event="reservation.event"
            :client-info="reservation.clientInfo"
          />
          <hr v-if="!isMobile" class="left-divider" />

          <!-- nouvelle : CTA statut desktop uniquement -->
          <BookingStatusCta
            v-if="reservation.status === 'nouvelle' && !isMobile"
            layout="column"
            status="nouvelle"
            :loading="ctaLoading"
            @confirm="onMarkContacted"
            @refuse="openRefusalModal"
          />

          <BookingContactActions
            v-if="reservation.status !== 'nouvelle' && !isMobile"
            variant="big"
            layout="column"
            :client-info="reservation.clientInfo"
            :mailto-href="mailtoHref"
          />
        </div>

        <!-- Colonne droite : chaque bloc dans sa propre bento card -->
        <div class="right">
          <!-- nouvelle ─────────────────────────────────────────── -->
          <template v-if="reservation.status === 'nouvelle'">
            <!-- Desktop : cartes contact dans leur bento -->
            <div v-if="!isMobile" class="bento-card">
              <BookingContactActions
                variant="big"
                layout="row"
                :desktop-only="true"
                :client-info="reservation.clientInfo"
                :mailto-href="mailtoHref"
              />
            </div>

            <!-- Mobile : contact + CTA dans un seul bloc continu -->
            <div v-else class="mobile-cta-block">
              <BookingContactActions
                variant="big"
                layout="row"
                :client-info="reservation.clientInfo"
                :mailto-href="mailtoHref"
              />
              <BookingStatusCta
                status="nouvelle"
                :loading="ctaLoading"
                @confirm="onMarkContacted"
                @refuse="openRefusalModal"
              />
            </div>
          </template>

          <!-- en_discussion ────────────────────────────────────── -->
          <template v-else-if="reservation.status === 'en_discussion'">
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
              :items="feed"
              :loading="feedPending"
              current-user-role="prestataire"
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
            v-if="reservation.status === 'refusee' || reservation.status === 'annulee'"
            class="bento-card"
          >
            <BookingResumeContactLink :mailto-href="mailtoHref" />
          </div>

          <!-- confirmee ────────────────────────────────────────── -->
          <div v-if="reservation.status === 'confirmee'" class="bento-card">
            <BookingCriticalActions :reservationId="reservationId" />
          </div>
        </div>
      </div>
    </template>

    <!-- Skeleton cover -->
    <Sk v-else-if="loading" class="cover-banner-sk" />

    <!-- Skeleton layout -->
    <div v-if="loading" class="booking-layout">
      <!-- Colonne gauche -->
      <div class="left">
        <div class="sk-brief">
          <div class="sk-row">
            <Sk v-for="i in 3" :key="i" width="72px" height="24px" radius="2rem" />
          </div>
          <div class="sk-lines">
            <Sk width="80%" height="0.8rem" radius="4px" />
            <Sk width="60%" height="0.8rem" radius="4px" />
            <Sk width="70%" height="0.8rem" radius="4px" />
          </div>
        </div>
      </div>

      <!-- Colonne droite -->
      <div class="right">
        <div class="bento-card sk-bento">
          <Sk width="40%" height="0.8rem" radius="4px" />
          <Sk height="40px" radius="10px" />
        </div>
        <div class="bento-card sk-bento">
          <Sk v-for="i in 3" :key="i" height="56px" radius="6px" />
        </div>
      </div>
    </div>

    <!-- ── Contact sticky mobile uniquement (en_discussion + confirmee) ─────── -->
    <BookingContactActions
      v-if="
        isMobile &&
        reservation &&
        (reservation.status === 'en_discussion' || reservation.status === 'confirmee')
      "
      variant="sticky"
      layout="row"
      :client-info="reservation.clientInfo"
      :mailto-href="mailtoHref"
    />

    <!-- ── Modal refus ────────────────────────────────────────────────────────── -->
    <SgiltDialog
      v-if="refusalModalOpen"
      v-model:open="refusalModalOpen"
      :title="t('pro.reservation-detail.refusal-modal.title')"
      max-width="600px"
    >
      <div class="refusal-form">
        <p class="lead">{{ t('pro.reservation-detail.refusal-modal.lead') }}</p>
        <textarea
          v-model="refusalMessage"
          class="message-input"
          :placeholder="t('pro.reservation-detail.refusal-modal.placeholder')"
          rows="4"
        />
        <button class="submit" type="button" :disabled="refusalLoading" @click="submitRefusal">
          {{ t('pro.reservation-detail.refusal-modal.submit') }}
        </button>
      </div>
    </SgiltDialog>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'pro' })

import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import ReservationFeed from '~/components/shared/ReservationFeed.vue'
import Sk from '~/components/basics/Sk.vue'
import BookingStatusBanner from '~/components/pro/BookingStatusBanner.vue'
import BookingContactActions from '~/components/pro/BookingContactActions.vue'
import BookingStatusCta from '~/components/pro/BookingStatusCta.vue'
import EventBlock from '~/components/app/EventBlock.vue'
import BookingResumeContactLink from '~/components/pro/BookingResumeContactLink.vue'
import BookingCriticalActions from '~/components/pro/BookingCriticalActions.vue'
import { resolveEventCover } from '~/utils/eventCovers'
import { buildReservationMailto } from '~/utils/reservationMailto'
import { getStatusOverlayStyle } from '~/constants/reservation-status'

const { isMobile } = useDevice()
const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const reservationId = String(route.params.id)

// ── Data ──────────────────────────────────────────────────────────────────────
const { reservation, loading, markContacted, confirm, refuse } =
  useProReservationDetail(reservationId)
const { feed, pending: feedPending, load: refreshFeed, addNote, uploadDocument } = useReservationFeed(reservationId)

// ── Cover image ────────────────────────────────────────────────────────────────
const { toUrl } = useImageUrl()

const coverImage = computed(() => {
  if (!reservation.value?.event) return ''
  return resolveEventCover(
    {
      coverImage: reservation.value.event.coverImage ?? null,
      eventType: reservation.value.event.eventType,
    },
    toUrl,
  )
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

const isEditable = computed(() => {
  const s = reservation.value?.status
  return s !== 'refusee' && s !== 'annulee' && s !== 'realisee'
})

// ── Feed handlers ──────────────────────────────────────────────────────────────
async function onAddNote(title: string, content: string, isPersonal: boolean) {
  await addNote(title, content, isPersonal)
}

async function onUploadDocument(file: File) {
  await uploadDocument(file, false)
}

function onDeleteDocument(_id: string) {
  // TODO hors scope — sera implémenté dans une prochaine itération
}

// ── mailto ─────────────────────────────────────────────────────────────────────
const mailtoHref = computed(() =>
  reservation.value ? buildReservationMailto(reservation.value) : '#',
)

// ── CTA ───────────────────────────────────────────────────────────────────────
const ctaLoading = ref(false)

async function onMarkContacted() {
  if (!reservation.value || ctaLoading.value) return
  ctaLoading.value = true
  try {
    await markContacted()
    await refreshFeed()
  } finally {
    ctaLoading.value = false
  }
}

async function confirmer() {
  if (!reservation.value || ctaLoading.value) return
  ctaLoading.value = true
  try {
    await confirm()
    await refreshFeed()
  } finally {
    ctaLoading.value = false
  }
}

// ── Refus ─────────────────────────────────────────────────────────────────────
const refusalModalOpen = ref(false)
const refusalMessage = ref('')
const refusalLoading = ref(false)

function openRefusalModal() {
  refusalMessage.value = ''
  refusalModalOpen.value = true
}

async function submitRefusal() {
  if (refusalLoading.value) return
  refusalLoading.value = true
  const msg = refusalMessage.value.trim()
  try {
    await refuse(msg, msg.length > 0)
    refusalModalOpen.value = false
    await refreshFeed()
  } finally {
    refusalLoading.value = false
  }
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

  .message-input {
    width: 100%;
    box-sizing: border-box;
    resize: vertical;
    padding: $spacing-s $spacing-m;
    border: 1px solid $divider-color;
    border-radius: $radius-md;
    font-family: 'Inter', sans-serif;
    font-size: 0.875rem;
    color: $text-primary;
    background: #fff;
    transition: border-color 150ms ease;
    outline: none;

    &::placeholder {
      color: $text-secondary;
    }

    &:focus {
      border-color: rgba(47, 42, 37, 0.4);
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

// ── Skeleton ──────────────────────────────────────────────────────────────────

.cover-banner-sk {
  height: 200px;

  @media (min-width: $desktop) {
    height: 280px;
    max-height: 30vh;
  }
}

.sk-brief {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

.sk-row {
  display: flex;
  gap: $spacing-xs;
}

.sk-lines {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
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
</style>
