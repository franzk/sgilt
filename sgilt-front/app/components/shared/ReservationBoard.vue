<!--
  Fiche d'une réservation, la même pour le client et le prestataire. Affiche l'en-tête, la barre
  d'onglets et l'onglet ouvert — Suivi (actions de statut, frise, lien vers les actions spéciales),
  Détails, Documents, Prestataire — ou, sur le segment /actions, la page des actions spéciales.
-->
<template>
  <!-- ── Actions spéciales : page à part (annulation après confirmation) ─────── -->
  <ReservationSpecialActions
    v-if="segment === SPECIAL_ACTIONS_SEGMENT"
    :status="reservation?.status ?? null"
    :cancelling="specialCancelling"
    @back="navigateTo(reservationPath)"
    @cancel="onSpecialCancel"
  />

  <div
    v-else
    class="reservation-board"
    :class="{ 'with-contact-bar': isPro && isMobile, 'with-status-bar': hasFixedStatusBar }"
  >
    <!-- ── En-tête : le prestataire côté client, l'événement côté prestataire ─── -->
    <ReservationMeta
      v-if="isClient"
      :reservation="reservation"
      :loading="metaPending"
      @back="$emit('back')"
    />
    <ProReservationCover v-else :reservation="proReservation" @back="$emit('back')" />

    <ReservationTabs v-if="tab" :reservation-path="reservationPath" :active="tab" />

    <!-- ── Suivi ─────────────────────────────────────────────────────────────── -->
    <div v-if="tab === 'suivi'" class="suivi" :class="{ 'with-contact': hasContactColumn }">
      <!-- Prestataire, desktop : les options de contact du client à gauche. -->
      <aside v-if="hasContactColumn && proReservation" class="contact">
        <BookingContactActions
          variant="big"
          layout="column"
          :client-info="proReservation.clientInfo"
          :mailto-href="mailtoHref"
        />
      </aside>

      <div class="main">
        <!-- Desktop : au-dessus de la frise. Mobile : fixés en bas d'écran (essai, voir plus bas). -->
        <ReservationStatusActions
          v-if="isDesktop"
          :actions="statusActions"
          @action="onStatusAction"
        />

        <ReservationFeed
          :items="feed"
          :loading="feedPending"
          :current-user-role="role"
          :can-add-note="canEditFeed"
          :can-upload-document="canEditFeed"
          :uploading-document="uploading"
          :adding-note="addingNote"
          :add-note-failed="addNoteFailed"
          :show-personal-toggle="true"
          @add-note="addNote"
          @upload-document="(file) => uploadDocument(file, false)"
          @download-document="download"
          @delete-document="deleteDocument"
        />

        <ReservationSpecialActionsLink
          v-if="reservation?.status === 'confirmee'"
          :to="`${reservationPath}/${SPECIAL_ACTIONS_SEGMENT}`"
        />
      </div>
    </div>
    <!-- ── Détails : l'événement et les coordonnées partagées ─────────────────── -->
    <div v-else-if="tab === 'details'" class="tab-content">
      <ReservationEventDetails
        v-if="details"
        :event="details.event"
        :client-info="details.clientInfo"
        :edit-path="details.editPath"
      />
    </div>

    <!-- ── Documents : tous les documents de la réservation ──────────────────── -->
    <div v-else-if="tab === 'documents'" class="tab-content">
      <ReservationDocuments
        :items="feed"
        :current-user-role="role"
        :loading="feedPending"
        :can-upload="canEditFeed"
        :uploading="uploading"
        @upload="(file) => uploadDocument(file, false)"
        @download="download"
        @delete="deleteDocument"
      />
    </div>

    <!-- ── Prestataire : sa fiche publique, en lecture seule ─────────────────── -->
    <div v-else-if="tab === 'prestataire'" class="tab-content">
      <ReservationPrestataire v-if="prestataireSlug" :slug="prestataireSlug" />
    </div>

    <!-- ── Mobile, onglet Suivi : actions de statut fixées en bas (essai) ──────────
         Client : au-dessus de la barre de navigation de l'événement. Prestataire : au-dessus de la
         barre de contact compacte, ou dans le bloc des grands boutons de contact (nouvelle). -->
    <div
      v-if="hasFixedStatusBar && !(isPro && proReservation?.status === 'nouvelle')"
      class="status-bar"
      :class="{ 'above-contact': isPro }"
    >
      <ReservationStatusActions :actions="statusActions" @action="onStatusAction" />
    </div>

    <!-- ── Prestataire, mobile : contact du client fixé en bas, sur tous les onglets ── -->
    <template v-if="isPro && isMobile && proReservation">
      <div v-if="proReservation.status === 'nouvelle'" class="contact-bar">
        <ReservationStatusActions
          v-if="hasFixedStatusBar"
          class="contact-bar-status"
          :actions="statusActions"
          @action="onStatusAction"
        />
        <BookingContactActions
          variant="big"
          layout="row"
          :client-info="proReservation.clientInfo"
          :mailto-href="mailtoHref"
        />
      </div>
      <BookingContactActions
        v-else
        variant="sticky"
        layout="row"
        :client-info="proReservation.clientInfo"
        :mailto-href="mailtoHref"
      />
    </template>
  </div>

  <!-- ── Client : « le prestataire m'a contacté·e » ─────────────────────────── -->
  <SgiltConfirmDialog
    v-model:open="contactedOpen"
    :title="t('reservation.mark-contacted-dialog.title')"
    :message="t('reservation.mark-contacted-dialog.body')"
    :confirm-label="t('reservation.mark-contacted-dialog.confirm')"
    :cancel-label="t('reservation.mark-contacted-dialog.cancel')"
    :confirm-loading="clientData?.declaringContacted.value"
    max-width="400px"
    @confirm="onDeclareContacted"
  />

  <!-- ── Client : « le prestataire a confirmé » ─────────────────────────────── -->
  <SgiltConfirmDialog
    v-model:open="confirmDialogOpen"
    :title="t('reservation.confirm-dialog.title')"
    :message="t('reservation.confirm-dialog.body')"
    :confirm-label="t('reservation.confirm-dialog.confirm')"
    :cancel-label="t('reservation.confirm-dialog.cancel')"
    :confirm-loading="clientData?.confirming.value"
    max-width="400px"
    @confirm="onConfirm"
  />

  <!-- ── Sortie avant confirmation : annulation (client) ou refus (prestataire) ─ -->
  <SgiltConfirmDialog
    v-model:open="exitDialogOpen"
    :title="isClient ? t('reservation.cancel-dialog.title') : t('pro.reservation-detail.refusal-modal.title')"
    :message="isClient ? t('reservation.cancel-dialog.body') : t('pro.reservation-detail.refusal-modal.lead')"
    :confirm-label="
      isClient ? t('reservation.cancel-dialog.confirm') : t('pro.reservation-detail.refusal-modal.submit')
    "
    :cancel-label="
      isClient ? t('reservation.cancel-dialog.keep') : t('pro.reservation-detail.refusal-modal.cancel')
    "
    :confirm-loading="exiting"
    destructive
    max-width="400px"
    @confirm="onExit"
  >
    <textarea
      v-model="exitReason"
      class="confirm-reason"
      :placeholder="
        isClient
          ? t('reservation.cancel-dialog.reason-placeholder')
          : t('pro.reservation-detail.refusal-modal.placeholder')
      "
      rows="3"
    />
    <label v-if="isClient" class="confirm-personal-toggle">
      <input v-model="exitIsPersonal" type="checkbox" />
      <span>{{ $t('feed.add-note-dialog.private-toggle') }}</span>
    </label>
  </SgiltConfirmDialog>
</template>

<script setup lang="ts">
import { CloseCircleIcon, ShakeHandsIcon, UserVoiceIcon } from '@remixicons/vue/line'
import ReservationMeta from '~/components/app/ReservationMeta.vue'
import SgiltConfirmDialog from '~/components/basics/dialogs/SgiltConfirmDialog.vue'
import BookingContactActions from '~/components/pro/BookingContactActions.vue'
import ProReservationCover from '~/components/pro/ProReservationCover.vue'
import ReservationDocuments from '~/components/shared/ReservationDocuments.vue'
import ReservationEventDetails from '~/components/shared/ReservationEventDetails.vue'
import ReservationPrestataire from '~/components/shared/ReservationPrestataire.vue'
import ReservationFeed from '~/components/shared/ReservationFeed.vue'
import ReservationSpecialActions from '~/components/shared/ReservationSpecialActions.vue'
import ReservationSpecialActionsLink from '~/components/shared/ReservationSpecialActionsLink.vue'
import ReservationStatusActions, {
  type StatusAction,
} from '~/components/shared/ReservationStatusActions.vue'
import ReservationTabs from '~/components/shared/ReservationTabs.vue'
import { useCurrentUser } from '~/composables/useCurrentUser'
import { SPECIAL_ACTIONS_SEGMENT, reservationTabOf } from '~/constants/reservation-tabs'
import type { FeedItem } from '~/data/reservation/domain/FeedItem'
import type { ReservationMeta as ReservationMetaData } from '~/data/reservation/domain/ReservationMeta'
import { useEventContext } from '~/data/evenement/useEventContext'
import { useProReservationDetail } from '~/data/reservation/useProReservationDetail'
import { useReservation } from '~/data/reservation/useReservation'
import { useReservationFeed } from '~/data/reservation/useReservationFeed'
import { buildReservationMailto } from '~/utils/reservationMailto'

const props = defineProps<{
  role: 'client' | 'prestataire'
  reservationId: string
  // Chemin de la réservation (onglet Suivi), base des onglets et des actions spéciales.
  reservationPath: string
  // Segment d'URL après la réservation : un onglet, 'actions', ou rien (Suivi).
  segment?: string
  // Titre de l'événement, pour le titre de l'onglet du navigateur (client).
  eventTitle?: string
}>()

defineEmits<{
  back: []
}>()

const { t } = useI18n()
const { isMobile, isDesktop } = useDevice()

const isClient = props.role === 'client'
const isPro = props.role === 'prestataire'

// ── Données : appels au back propres à chaque rôle ──────────────────────────────
const clientData = isClient ? useReservation(props.reservationId) : null
const proData = isPro ? useProReservationDetail(props.reservationId) : null

const reservation = computed<ReservationMetaData | null>(
  () => clientData?.reservation.value ?? proData?.reservation.value ?? null,
)
const proReservation = computed(() => proData?.reservation.value ?? null)

// L'événement et les coordonnées du client : dans la réservation côté prestataire ; côté client,
// dans l'événement en cours (la fiche est toujours ouverte sous /app/events/:eventId).
const eventContext = isClient ? useEventContext() : null
const details = computed(() => {
  if (eventContext) {
    const event = eventContext.event.value
    const clientInfo = eventContext.clientInfo.value
    if (!event || !clientInfo) return null
    return { event, clientInfo, editPath: `/app/events/${eventContext.eventId}/parametres?from=reservation` }
  }
  if (!proReservation.value) return null
  return {
    event: proReservation.value.event,
    clientInfo: proReservation.value.clientInfo,
    editPath: undefined,
  }
})
const metaPending = computed(() => clientData?.pending.value ?? proData?.loading.value ?? false)
const status = computed(() => reservation.value?.status ?? '')

useHead(
  computed(() => ({
    title:
      reservation.value && props.eventTitle
        ? `${reservation.value.prestataireName} / ${props.eventTitle}`
        : '',
  })),
)

const {
  feed,
  pending: feedPending,
  load: reloadFeed,
  uploading,
  addingNote,
  addNoteFailed,
  addNote,
  uploadDocument,
  download,
  deleteDocument,
} = useReservationFeed(props.reservationId)

// Le client écrit toujours dans le fil ; le prestataire plus une fois la réservation terminée.
const canEditFeed = computed(
  () => isClient || !['refusee', 'annulee', 'realisee'].includes(status.value),
)

// ── Onglet ────────────────────────────────────────────────────────────────────
// Segment inconnu (URL tapée à la main) : retour sur Suivi.
const tab = computed(() => reservationTabOf(props.segment))
watch(
  () => props.segment,
  () => {
    if (props.segment !== SPECIAL_ACTIONS_SEGMENT && !tab.value) {
      navigateTo(props.reservationPath, { replace: true })
    }
  },
  { immediate: true },
)

// Slug du prestataire, pour sa fiche publique : dans la réservation côté client ; côté prestataire,
// c'est le sien.
const currentUser = useCurrentUser()
const prestataireSlug = computed(() =>
  isClient ? reservation.value?.prestataireSlug : (currentUser.slug ?? undefined),
)

// ── Contact du client (prestataire) ───────────────────────────────────────────
const hasContactColumn = computed(() => isPro && isDesktop.value)
const mailtoHref = computed(() =>
  proReservation.value ? buildReservationMailto(proReservation.value) : '#',
)

// ── Note système optimiste ────────────────────────────────────────────────────
// Le contenu d'une note système (contacté/confirmé/annulé) est entièrement déductible côté front :
// pas besoin d'aller-retour serveur pour l'afficher immédiatement.

function pushSystemNote(generatedKey: string, content?: string, isPersonal = false) {
  const item: FeedItem = {
    type: 'note',
    id: crypto.randomUUID(),
    title: '',
    author: {
      id: '',
      name: `${currentUser.firstName} ${currentUser.lastName}`.trim(),
      photo: currentUser.photo ?? undefined,
      role: props.role,
    },
    createdAt: new Date(),
    content: content ?? null,
    isPersonal,
    generatedKey,
  }
  feed.value.unshift(item)
}

// ── Actions de statut (au-dessus de la frise) ──────────────────────────────────
// Client : l'étape suivante, puis annuler (avant confirmation). Prestataire : refuser (avant
// confirmation). Après confirmation, la sortie passe par les actions spéciales.
const contactedOpen = ref(false)
const confirmDialogOpen = ref(false)
const exitDialogOpen = ref(false)

const statusActions = computed<StatusAction[]>(() => {
  const beforeConfirmation = ['nouvelle', 'en_discussion'].includes(status.value)
  if (isPro) {
    return beforeConfirmation
      ? [
          {
            key: 'exit',
            label: t('pro.reservation-detail.refuse-btn'),
            icon: CloseCircleIcon,
            kind: 'exit',
          },
        ]
      : []
  }
  return [
    ...(status.value === 'nouvelle'
      ? [
          {
            key: 'contacted',
            label: t('reservation.mark-contacted-btn'),
            icon: UserVoiceIcon,
            kind: 'main' as const,
          },
        ]
      : []),
    ...(status.value === 'en_discussion'
      ? [
          {
            key: 'confirm',
            label: t('reservation.confirm-btn'),
            icon: ShakeHandsIcon,
            kind: 'main' as const,
          },
        ]
      : []),
    ...(beforeConfirmation
      ? [
          {
            key: 'exit',
            label: t('reservation.cancel-btn'),
            icon: CloseCircleIcon,
            kind: 'exit' as const,
          },
        ]
      : []),
  ]
})

// Essai : sur mobile, les actions de statut sont fixées en bas de l'onglet Suivi.
const hasFixedStatusBar = computed(
  () => isMobile.value && tab.value === 'suivi' && statusActions.value.length > 0,
)

function onStatusAction(key: string) {
  if (key === 'contacted') contactedOpen.value = true
  else if (key === 'confirm') confirmDialogOpen.value = true
  else if (key === 'exit') {
    exitReason.value = ''
    exitIsPersonal.value = false
    exitDialogOpen.value = true
  }
}

async function onDeclareContacted() {
  await clientData?.declareContacted()
  pushSystemNote('feed.system.contacted')
  contactedOpen.value = false
}

async function onConfirm() {
  await clientData?.confirm()
  pushSystemNote('feed.system.confirmed')
  confirmDialogOpen.value = false
}

// ── Sortie avant confirmation : annulation (client) ou refus (prestataire) ────────
const exitReason = ref('')
const exitIsPersonal = ref(false)
const exiting = ref(false)

async function onExit() {
  if (exiting.value) return
  exiting.value = true
  const reason = exitReason.value.trim() || undefined
  try {
    if (clientData) {
      await clientData.cancel(reason, exitIsPersonal.value)
      pushSystemNote('feed.system.cancelled', reason, exitIsPersonal.value)
    } else if (proData) {
      await proData.refuse(reason ?? '')
      await reloadFeed()
    }
    exitDialogOpen.value = false
  } finally {
    exiting.value = false
  }
}

// ── Après confirmation, depuis les actions spéciales ───────────────────────────
// Client : retour sur Suivi ; prestataire : retour à ses réservations.
const specialCancelling = ref(false)

async function onSpecialCancel(reason: string | undefined, isPersonal: boolean) {
  specialCancelling.value = true
  try {
    if (clientData) {
      await clientData.cancel(reason, isPersonal)
      pushSystemNote('feed.system.cancelled', reason, isPersonal)
      navigateTo(props.reservationPath)
    } else if (proData) {
      await proData.cancelByPro(reason, isPersonal)
      navigateTo('/pro/reservations')
    }
  } finally {
    specialCancelling.value = false
  }
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;
// Hauteur de la barre de contact fixée en bas (prestataire, mobile) : le contenu ne passe pas dessous.
$contact-bar-h: 12.5rem;

.reservation-board {
  min-height: 100%;
  background-color: $brand-background-alt;

  &.with-contact-bar {
    padding-bottom: $contact-bar-h;
  }

  // ── Suivi : une colonne ; prestataire desktop : le contact à gauche ────────────
  .suivi {
    padding: $spacing-m $spacing-m calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $spacing-m);

    @media (min-width: $desktop) {
      width: 100%;
      max-width: 47.5rem;
      margin: 0 auto;
      padding: $spacing-l 0;
    }

    &.with-contact {
      @media (min-width: $desktop) {
        display: grid;
        grid-template-columns: 21.25rem minmax(0, 1fr);
        gap: $spacing-l;
        align-items: start;
        max-width: 75rem;
        padding: $spacing-xl 2.5rem;
      }
    }

    .contact {
      position: sticky;
      top: calc(#{$app-header-height} + #{$spacing-m});
      padding: $spacing-m;
      border: 1px solid $divider-color;
      border-radius: $radius-sm;
      background: $surface-white;

      :deep(.contact-card) {
        box-shadow: none;
      }
    }

    .main {
      display: flex;
      flex-direction: column;
      gap: $spacing-m;
      min-width: 0;
    }
  }

  // ── Onglets sans colonnes (Détails…) ───────────────────────────────────────────
  .tab-content {
    padding: $spacing-m $spacing-m calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $spacing-m);

    @media (min-width: $desktop) {
      width: 100%;
      max-width: 47.5rem;
      margin: 0 auto;
      padding: $spacing-l 0;
    }
  }

  // ── Mobile : actions de statut fixées en bas (essai) ───────────────────────────
  // Client : au-dessus de la barre de navigation de l'événement ; prestataire : au-dessus de la
  // barre de contact compacte (56px).
  &.with-status-bar .suivi {
    // Jusqu'à deux boutons empilés (pleine largeur) dans la barre.
    padding-bottom: calc(#{$bottom-nav-h} + env(safe-area-inset-bottom, 0px) + 7rem);
  }

  .status-bar {
    position: fixed;
    right: 0;
    bottom: calc(#{$bottom-nav-h} + env(safe-area-inset-bottom, 0px));
    left: 0;
    z-index: 100;
    padding: $spacing-s $spacing-m;
    border-top: 1px solid rgba(0, 0, 0, 0.06);
    background: rgba(245, 245, 243, 0.85);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);

    &.above-contact {
      bottom: calc(3.5rem + env(safe-area-inset-bottom, 0px));
    }
  }

  // ── Prestataire, mobile, réservation nouvelle : grands boutons de contact fixés en bas ──
  .contact-bar {
    position: fixed;
    right: 0;
    bottom: env(safe-area-inset-bottom, 0px);
    left: 0;
    z-index: 100;
    padding: $spacing-s $spacing-m;
    border-top: 1px solid rgba(0, 0, 0, 0.06);
    background: rgba(245, 245, 243, 0.85);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);

    .contact-bar-status {
      margin-bottom: $spacing-s;
    }
  }
}
</style>
