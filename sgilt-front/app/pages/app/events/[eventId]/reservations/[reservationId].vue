<template>
  <div class="reservation-page">
    <ReservationMeta
      :reservation="reservation"
      :loading="metaPending"
      @back="navigateTo(`/app/events/${eventId}`)"
    />

    <div class="reservation-detail">
      <ReservationFeed
        :items="feed"
        :loading="feedPending"
        current-user-role="client"
        :can-add-note="true"
        :can-upload-document="true"
        :uploading-document="uploading"
        :show-personal-toggle="true"
        @add-note="onAddNote"
        @upload-document="onUploadDocument"
        @download-document="onDownloadDocument"
        @delete-document="onDeleteDocument"
      />

      <button
        v-if="canDeclareContacted"
        class="status-btn"
        type="button"
        @click="contactedOpen = true"
      >
        {{ $t('reservation.mark-contacted-btn') }}
      </button>

      <button
        v-if="canConfirm"
        class="status-btn"
        type="button"
        @click="confirmDialogOpen = true"
      >
        {{ $t('reservation.confirm-btn') }}
      </button>

      <button v-if="canCancel" class="cancel-btn" type="button" @click="cancelDialogOpen = true">
        {{ isConfirmed ? $t('reservation.cancel-btn-confirmed') : $t('reservation.cancel-btn') }}
      </button>
    </div>
  </div>

  <!-- ── Dialog confirmation "contacté" ────────────────────────────────────── -->
  <SgiltConfirmDialog
    v-model:open="contactedOpen"
    :title="t('reservation.mark-contacted-dialog.title')"
    :message="t('reservation.mark-contacted-dialog.body')"
    :confirm-label="t('reservation.mark-contacted-dialog.confirm')"
    :cancel-label="t('reservation.mark-contacted-dialog.cancel')"
    :confirm-loading="declaringContacted"
    max-width="400px"
    @confirm="onDeclareContacted"
  />

  <!-- ── Dialog confirmation "confirm"  ──────────────────────────────────── -->
  <SgiltConfirmDialog
    v-model:open="confirmDialogOpen"
    :title="t('reservation.confirm-dialog.title')"
    :message="t('reservation.confirm-dialog.body')"
    :confirm-label="t('reservation.confirm-dialog.confirm')"
    :cancel-label="t('reservation.confirm-dialog.cancel')"
    :confirm-loading="confirming"
    max-width="400px"
    @confirm="onConfirm"
  />

  <!-- ── Dialog confirmation annulation ─────────────────────────────────────── -->
  <SgiltConfirmDialog
    v-model:open="cancelDialogOpen"
    :title="isConfirmed ? t('reservation.cancel-dialog.title-confirmed') : t('reservation.cancel-dialog.title')"
    :message="isConfirmed ? t('reservation.cancel-dialog.body-confirmed') : t('reservation.cancel-dialog.body')"
    :confirm-label="t('reservation.cancel-dialog.confirm')"
    :cancel-label="t('reservation.cancel-dialog.keep')"
    :confirm-loading="cancelling"
    destructive
    max-width="400px"
    @confirm="cancelReservation"
  >
    <textarea
      v-model="cancelReason"
      class="confirm-reason"
      :placeholder="t('reservation.cancel-dialog.reason-placeholder')"
      rows="3"
    />
    <label class="confirm-personal-toggle">
      <input v-model="cancelIsPersonal" type="checkbox" />
      <span>{{ $t('feed.add-note-dialog.private-toggle') }}</span>
    </label>
  </SgiltConfirmDialog>
</template>

<script setup lang="ts">
import ReservationMeta from '~/components/app/ReservationMeta.vue'
import ReservationFeed from '~/components/shared/ReservationFeed.vue'
import SgiltConfirmDialog from '~/components/basics/dialogs/SgiltConfirmDialog.vue'
import { useReservation } from '~/data/reservation/useReservation'
import { useReservationFeed } from '~/data/reservation/useReservationFeed'
import { useEventDetail } from '~/data/evenement/useEvenement'
import { useCurrentUser } from '~/composables/useCurrentUser'
import type { FeedItem } from '~/data/reservation/domain/FeedItem'

definePageMeta({ layout: 'app' })

const { t } = useI18n()
const route = useRoute()
const reservationId = route.params.reservationId as string
const eventId = route.params.eventId as string

// ── Data ──────────────────────────────────────────────────────────────────────
const {
  reservation,
  pending: metaPending,
  cancelling,
  canCancel,
  cancel,
  declaringContacted,
  canDeclareContacted,
  declareContacted,
  confirming,
  canConfirm,
  confirm,
} = useReservation(reservationId)
const { event } = useEventDetail(eventId)

useHead(
  computed(() => ({
    title:
      reservation.value && event.value
        ? `${reservation.value.prestataireName} / ${event.value.title}`
        : '',
  })),
)
const {
  feed,
  pending: feedPending,
  uploading,
  addNote,
  uploadDocument,
  download,
  deleteDocument,
} = useReservationFeed(reservationId)

// ── Note système optimiste ────────────────────────────────────────────────────
// Le contenu d'une note système (contacté/confirmé/annulé) est entièrement déductible
// côté front — pas besoin d'aller-retour serveur pour l'afficher immédiatement.
const currentUser = useCurrentUser()

function pushSystemNote(generatedKey: string, content?: string, isPersonal = false) {
  const item: FeedItem = {
    type: 'note',
    id: crypto.randomUUID(),
    title: '',
    author: {
      id: '',
      name: `${currentUser.firstName} ${currentUser.lastName}`.trim(),
      photo: currentUser.photo ?? undefined,
      role: 'client',
    },
    createdAt: new Date(),
    content: content ?? null,
    isPersonal,
    generatedKey,
  }
  feed.value.unshift(item)
}

// ── Annulation ─────────────────────────────────────────────────────────────────
const isConfirmed = computed(() => reservation.value?.status === 'confirmee')
const cancelDialogOpen = ref(false)
const cancelReason = ref('')
const cancelIsPersonal = ref(false)

async function cancelReservation() {
  const reason = cancelReason.value.trim() || undefined
  const isPersonal = cancelIsPersonal.value
  await cancel(reason, isPersonal)
  pushSystemNote('feed.system.cancelled', reason, isPersonal)
  cancelDialogOpen.value = false
  cancelReason.value = ''
  cancelIsPersonal.value = false
}

// ── Contacté / confirm ────────────────────────────────────────────────────────
const contactedOpen = ref(false)
const confirmDialogOpen = ref(false)

async function onDeclareContacted() {
  await declareContacted()
  pushSystemNote('feed.system.contacted')
  contactedOpen.value = false
}

async function onConfirm() {
  await confirm()
  pushSystemNote('feed.system.confirmed')
  confirmDialogOpen.value = false
}

// ── Handlers ──────────────────────────────────────────────────────────────────
async function onAddNote(title: string, content: string, isPersonal: boolean) {
  await addNote(title, content, isPersonal)
}

async function onUploadDocument(file: File) {
  await uploadDocument(file, false)
}

async function onDownloadDocument(url: string, fileName: string) {
  await download(url, fileName)
}

async function onDeleteDocument(id: string) {
  await deleteDocument(id)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;

.reservation-page {
  min-height: 100%;
  background-color: $brand-background-alt;
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

// ── Bouton annulation ──────────────────────────────────────────────────────────
.cancel-btn,
.status-btn {
  align-self: center;
  background: none;
  border-radius: $radius-md;
  padding: $spacing-xs $spacing-m;
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  font-weight: 500;
  cursor: pointer;
  transition:
    background 150ms ease,
    border-color 150ms ease;
}

.cancel-btn {
  border: 1px solid rgba($state-error, 0.35);
  color: $state-error;

  &:hover {
    background: rgba($state-error, 0.06);
    border-color: $state-error;
  }
}

.status-btn {
  border: 1px solid rgba($brand-primary, 0.35);
  color: $brand-primary;

  &:hover {
    background: rgba($brand-primary, 0.06);
    border-color: $brand-primary;
  }
}

// ── Dialog confirmation ────────────────────────────────────────────────────────
.confirm-cancel {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: $spacing-m $spacing-l $spacing-l;

  &__body {
    font-family: 'Inter', sans-serif;
    font-size: 0.95rem;
    color: $text-primary;
    margin: 0;
    line-height: 1.5;
  }

  &__actions {
    display: flex;
    gap: $spacing-s;
    justify-content: flex-end;

    :deep(.btn.destructive) {
      background: rgba($state-error, 0.1);
      color: $state-error;
      border: 1px solid rgba($state-error, 0.25);
      box-shadow: none;
      text-shadow: none;

      &:hover:not(:disabled) {
        background: rgba($state-error, 0.18);
        filter: none;
      }
    }
  }
}
</style>
