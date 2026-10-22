<template>
  <div class="special-actions">
    <!-- ── En-tête ──────────────────────────────────────────────────────────── -->
    <div class="header">
      <button class="back-btn" type="button" @click="$emit('back')">
        {{ $t('reservation.special-actions.back') }}
      </button>
      <div>
        <h1 class="title">{{ $t('reservation.special-actions.page-title') }}</h1>
        <p class="subtitle">{{ $t('reservation.special-actions.page-subtitle') }}</p>
      </div>
    </div>

    <!-- ── Liste des actions ────────────────────────────────────────────────── -->
    <div class="actions-list">
      <!-- Annuler la réservation : seulement une réservation confirmée (règle du back) ; sinon, le
           message à la place du bouton. -->
      <button v-if="isCancellable" class="action-item danger" type="button" @click="cancelOpen = true">
        <span class="action-icon">✕</span>
        <span class="action-label">{{ $t('reservation.special-actions.cancel.label') }}</span>
      </button>
      <p v-else-if="status" class="message">
        {{ $t('reservation.special-actions.cancel.inactive') }}
      </p>

      <p class="message">
        {{ $t('reservation.special-actions.contact') }}
        <a href="mailto:contact@sgilt.alsace">contact@sgilt.alsace</a>
      </p>
    </div>

    <!-- ── Confirmation de l'annulation ─────────────────────────────────────── -->
    <SgiltConfirmDialog
      v-model:open="cancelOpen"
      :title="$t('reservation.special-actions.cancel.dialog-title')"
      :message="$t('reservation.special-actions.cancel.dialog-body')"
      :confirm-label="$t('reservation.special-actions.cancel.dialog-confirm')"
      :cancel-label="$t('reservation.special-actions.cancel.dialog-cancel')"
      :confirm-loading="cancelling"
      destructive
      max-width="480px"
      @confirm="onConfirm"
    >
      <textarea
        v-model="cancelReason"
        class="confirm-reason"
        :placeholder="$t('reservation.special-actions.cancel.reason-placeholder')"
        rows="3"
      />
      <label class="confirm-personal-toggle">
        <input v-model="cancelIsPersonal" type="checkbox" />
        <span>{{ $t('feed.add-note-dialog.private-toggle') }}</span>
      </label>
    </SgiltConfirmDialog>
  </div>
</template>

<script setup lang="ts">
// Page « Actions spéciales » d'une réservation, commune au client et au prestataire : l'annulation
// après confirmation, volontairement à l'écart de la fiche (action exceptionnelle). La page qui
// l'affiche fait l'annulation et décide de la navigation.
import SgiltConfirmDialog from '~/components/basics/dialogs/SgiltConfirmDialog.vue'
import type { ReservationStatus } from '~/data/reservation/domain/ReservationStatus'

const props = defineProps<{
  // Statut de la réservation, null tant qu'elle n'est pas chargée.
  status: ReservationStatus | null
  // Annulation en cours (la fenêtre se ferme quand elle se termine).
  cancelling: boolean
}>()

const emit = defineEmits<{
  back: []
  cancel: [reason: string | undefined, isPersonal: boolean]
}>()

const isCancellable = computed(() => props.status === 'confirmee')

const cancelOpen = ref(false)
const cancelReason = ref('')
const cancelIsPersonal = ref(false)

function onConfirm() {
  if (props.cancelling) return
  emit('cancel', cancelReason.value.trim() || undefined, cancelIsPersonal.value)
}

watch(
  () => props.cancelling,
  (cancelling) => {
    if (cancelling) return
    cancelOpen.value = false
    cancelReason.value = ''
    cancelIsPersonal.value = false
  },
)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.special-actions {
  display: flex;
  flex-direction: column;
  min-height: 100%;
  background-color: $brand-background-alt;

  .header {
    display: flex;
    align-items: center;
    gap: $spacing-m;
    padding: $spacing-m;
    border-bottom: 1px solid $divider-color;
    background: $surface-white;

    @media (min-width: $breakpoint-desktop) {
      padding: $spacing-m $spacing-xl;
    }

    .back-btn {
      flex-shrink: 0;
      padding: 0;
      border: none;
      background: none;
      color: $text-secondary;
      font-family: inherit;
      font-size: $font-size-sm;
      font-weight: $font-weight-medium;
      cursor: pointer;

      &:hover {
        color: $text-primary;
      }
    }

    .title {
      margin: 0;
      font-family: 'Cormorant Garamond', serif;
      font-size: 1.4rem;
      font-weight: $font-weight-semibold;
      color: $text-primary;
      line-height: 1.2;
    }

    .subtitle {
      margin: 0.2rem 0 0;
      font-size: $font-size-xs;
      color: $text-secondary;
    }
  }

  .actions-list {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
    padding: $spacing-m;
    padding-bottom: calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $spacing-m);

    @media (min-width: $breakpoint-desktop) {
      width: 100%;
      max-width: 37.5rem;
      margin: 0 auto;
      padding: $spacing-l 0 $spacing-xl;
    }

    .action-item {
      display: flex;
      align-items: center;
      gap: $spacing-m;
      padding: $spacing-m;
      border: 1px solid $divider-color;
      border-radius: $radius-md;
      background: $surface-white;
      font-family: inherit;
      text-align: left;
      cursor: pointer;
      transition: box-shadow 150ms ease;

      &:hover {
        box-shadow: 0 0.25rem 1rem $shadow-m;
      }

      .action-icon {
        display: flex;
        flex-shrink: 0;
        align-items: center;
        justify-content: center;
        width: 2.5rem;
        height: 2.5rem;
        border-radius: $radius-md;
      }

      .action-label {
        font-size: $font-size-sm;
        font-weight: $font-weight-semibold;
      }

      &.danger {
        .action-icon {
          background: rgba(163, 45, 45, 0.1);
          color: #a32d2d;
        }

        .action-label {
          color: #a32d2d;
        }
      }
    }

    .message {
      margin: 0;
      font-size: $font-size-sm;
      color: $text-secondary;

      a {
        color: $text-primary;
        font-weight: $font-weight-semibold;
      }
    }
  }
}
</style>
