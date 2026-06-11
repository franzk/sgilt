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

      <button v-if="canCancel" class="cancel-btn" type="button" @click="confirmOpen = true">
        {{ $t('reservation.cancel-btn') }}
      </button>
    </div>
  </div>

  <!-- ── Dialog confirmation annulation ─────────────────────────────────────── -->
  <SgiltConfirmDialog
    v-model:open="confirmOpen"
    :title="t('reservation.cancel-dialog.title')"
    :message="t('reservation.cancel-dialog.body')"
    :confirm-label="t('reservation.cancel-dialog.confirm')"
    :cancel-label="t('reservation.cancel-dialog.keep')"
    :confirm-loading="cancelling"
    destructive
    max-width="400px"
    @confirm="cancelReservation"
  />
</template>

<script setup lang="ts">
import ReservationMeta from '~/components/app/ReservationMeta.vue'
import ReservationFeed from '~/components/shared/ReservationFeed.vue'
import SgiltConfirmDialog from '~/components/basics/dialogs/SgiltConfirmDialog.vue'
import { useReservation } from '~/data/reservation/useReservation'
import { useReservationFeed } from '~/data/reservation/useReservationFeed'
import { useEventDetail } from '~/data/evenement/useEvenement'

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
  removeItem,
} = useReservationFeed(reservationId)

// ── Annulation ─────────────────────────────────────────────────────────────────
const confirmOpen = ref(false)

async function cancelReservation() {
  await cancel()
  confirmOpen.value = false
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

function onDeleteDocument(id: string) {
  removeItem(id)
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
.cancel-btn {
  align-self: center;
  background: none;
  border: 1px solid rgba($state-error, 0.35);
  border-radius: $radius-md;
  padding: $spacing-xs $spacing-m;
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  font-weight: 500;
  color: $state-error;
  cursor: pointer;
  transition:
    background 150ms ease,
    border-color 150ms ease;

  &:hover {
    background: rgba($state-error, 0.06);
    border-color: $state-error;
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
