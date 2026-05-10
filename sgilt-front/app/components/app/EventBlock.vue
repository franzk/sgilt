<template>
  <!-- ── Desktop ─────────────────────────────────────────────────────────────────── -->
  <template v-if="!isMobile">
    <EventBlockDesktopDisplay
      v-if="!editMode"
      :event="event"
      :client-info="clientInfo"
      :variant="variant"
      :last-update-date="lastUpdateDate"
      @open-edit="enterEditMode"
      @open-journal="journalOpen = true"
    />
    <EventBlockDesktopEdit
      v-else
      :event="event"
      :client-info="clientInfo"
      :variant="variant"
      :saving="saving"
      :last-update-date="lastUpdateDate"
      @save="onSave"
      @cancel="onCancel"
      @open-journal="journalOpen = true"
    />
  </template>

  <!-- ── Mobile ─────────────────────────────────────────────────────────────────── -->
  <template v-else>
    <button class="mobile-trigger" type="button" @click="sheetOpen = true">
      <div class="pills">
        <span v-if="event.date" class="event-pill date">
          <CalendarEventIcon class="icon" />{{ formatDateShort(event.date) }}
        </span>
        <span v-if="event.ville" class="event-pill">
          <MapPin2Icon class="icon" />{{ event.ville }}
        </span>
        <span v-if="event.nbInvites" class="event-pill">
          <GroupIcon class="icon" />{{ event.nbInvites }}
        </span>
      </div>
      <span class="voir-plus">Voir plus…</span>
    </button>

    <SgiltBottomSheet v-model:open="sheetOpen" :title="event.title" :overlay="true">
      <EventBlockMobileDisplay
        v-if="!editMode"
        :event="event"
        :client-info="clientInfo"
        :variant="variant"
        :last-update-date="lastUpdateDate"
        @open-edit="enterEditMode"
        @open-journal="journalOpen = true"
      />
      <EventBlockMobileEdit
        v-else
        :event="event"
        :client-info="clientInfo"
        :variant="variant"
        :saving="saving"
        :last-update-date="lastUpdateDate"
        @save="onSave"
        @cancel="onCancel"
        @open-journal="journalOpen = true"
      />
    </SgiltBottomSheet>
  </template>

  <!-- ── Partagé ────────────────────────────────────────────────────────────────── -->
  <EventJournal v-model:open="journalOpen" :journal="event.journal ?? []" />

  <Teleport to="body">
    <Transition name="modal">
      <div
        v-if="showAbandonModal"
        class="abandon-overlay"
        role="dialog"
        aria-modal="true"
        @click.self="showAbandonModal = false"
      >
        <div class="abandon-modal">
          <h3 class="title">{{ $t('event.block.abandon-title') }}</h3>
          <p class="body">{{ $t('event.block.abandon-body') }}</p>
          <div class="actions">
            <SgiltButton variant="secondary" @click="showAbandonModal = false">
              {{ $t('event.block.abandon-continue') }}
            </SgiltButton>
            <button class="destructive" type="button" @click="confirmAbandon">
              {{ $t('event.block.abandon-confirm') }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import SgiltBottomSheet from '~/components/basics/sheets/SgiltBottomSheet.vue'
import EventJournal from '~/components/app/EventJournal.vue'
import EventBlockDesktopDisplay from '~/components/app/EventBlockDesktopDisplay.vue'
import EventBlockDesktopEdit from '~/components/app/EventBlockDesktopEdit.vue'
import EventBlockMobileDisplay from '~/components/app/EventBlockMobileDisplay.vue'
import EventBlockMobileEdit from '~/components/app/EventBlockMobileEdit.vue'
import { patchEventApi } from '~/data/evenement/api/evenementApi'
import type { EventDetail } from '~/data/evenement/domain/EventDetail'
import type { ClientContactInfo } from '~/data/reservation/domain/ClientContactInfo'
import type { EventPatchRequestDto } from '~/data/evenement/dto/EventDetailDto'
import { CalendarEventIcon, MapPin2Icon, GroupIcon } from '@remixicons/vue/line'

const props = defineProps<{
  event: EventDetail
  clientInfo: ClientContactInfo
  variant?: 'client' | 'pro'
}>()

const emit = defineEmits<{
  updated: [patch: Partial<EventDetail>]
  updatedClientInfo: [patch: Partial<ClientContactInfo>]
}>()

const variant = computed(() => props.variant ?? 'client')

const { isMobile } = useDevice()

const editMode = ref(false)
const saving = ref(false)
const sheetOpen = ref(false)
const journalOpen = ref(false)
const showAbandonModal = ref(false)

const lastUpdateDate = computed(() => {
  const journal = props.event.journal
  if (!journal?.length) return null
  const last = journal[journal.length - 1]
  return last?.date
    ? `Dernière mise à jour : ${new Date(last.date).toLocaleDateString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' })}`
    : null
})

function enterEditMode() {
  if (variant.value === 'pro') return
  editMode.value = true
  if (isMobile.value) sheetOpen.value = true
}

function onCancel(isDirty: boolean) {
  if (isDirty) {
    showAbandonModal.value = true
  } else {
    editMode.value = false
  }
}

function confirmAbandon() {
  showAbandonModal.value = false
  editMode.value = false
}

async function onSave(payload: { eventPatch: EventPatchRequestDto; clientPatch: Partial<ClientContactInfo> }) {
  saving.value = true
  try {
    const updated = await patchEventApi(props.event.id, payload.eventPatch)
    emit('updated', {
      eventType: updated.eventType,
      ambiance: updated.ambiance,
      ville: updated.ville,
      lieu: updated.lieu,
      nbInvites: updated.nbInvites,
      sharedNote: updated.sharedNote,
      description: updated.description,
      momentCle: updated.momentCle,
    })
    emit('updatedClientInfo', payload.clientPatch)
    editMode.value = false
  } finally {
    saving.value = false
  }
}
</script>

<style scoped lang="scss">
@use 'sass:color';
@use '@/assets/styles/base' as *;

// ── Trigger mobile ─────────────────────────────────────────────────────────────

.mobile-trigger {
  width: 100%;
  display: flex;
  align-items: center;
  gap: $spacing-s;
  padding: $spacing-s $spacing-m;
  background: #fff;
  border: none;
  border-radius: $radius-md;
  cursor: pointer;
  text-align: left;
  transition: background 120ms ease;

  &:hover { background: $surface-soft; }
}

.pills {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  min-width: 0;
}

.voir-plus {
  flex-shrink: 0;
  font-family: 'Inter', sans-serif;
  font-size: 0.75rem;
  font-weight: 500;
  color: $brand-muted;
  text-decoration: underline;
  text-underline-offset: 2px;
}

.event-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 8px;
  border-radius: 2rem;
  background: $surface-soft;
  border: 1px solid $divider-color;
  font-family: 'Inter', sans-serif;
  font-size: 0.688rem;
  color: $text-secondary;
  font-weight: 500;
  white-space: nowrap;

  .icon {
    width: 11px;
    height: 11px;
    flex-shrink: 0;
  }

  &.date {
    background: rgba($brand-accent, 0.08);
    border-color: rgba($brand-accent, 0.3);
    color: color.adjust(#e6b800, $lightness: -18%);
  }
}

// ── Modale abandon ────────────────────────────────────────────────────────────

.abandon-overlay {
  position: fixed;
  inset: 0;
  z-index: $z-modal;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-m;
}

.abandon-modal {
  background: #fff;
  border-radius: $radius-lg;
  padding: $spacing-l;
  width: 100%;
  max-width: 340px;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);

  .title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.2rem;
    font-weight: 600;
    color: $brand-primary;
    margin: 0;
  }

  .body {
    font-size: 0.9rem;
    color: $text-secondary;
    margin: 0;
    line-height: 1.5;
  }

  .actions {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
  }

  .destructive {
    background: none;
    border: none;
    font-family: inherit;
    font-size: 0.9rem;
    color: #c0392b;
    cursor: pointer;
    padding: $spacing-xs 0;
    text-align: center;
    transition: opacity 150ms ease;

    &:hover { opacity: 0.75; }
  }
}

// ── Transition modale ─────────────────────────────────────────────────────────

.modal-enter-active,
.modal-leave-active {
  transition: opacity 200ms ease;
  .abandon-modal { transition: transform 200ms ease; }
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  .abandon-modal { transform: scale(0.96); }
}
</style>
