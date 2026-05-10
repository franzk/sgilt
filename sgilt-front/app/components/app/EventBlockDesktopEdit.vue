<template>
  <div class="eb-desktop-edit">
    <div class="body">
      <!-- 1. Logistique -->
      <div class="section">
        <div class="edit-fields">
          <div class="edit-field">
            <label class="label">{{ $t('event.block.party-edit-city') }}</label>
            <input v-model="draft.ville" class="input" type="text" :placeholder="$t('event.block.party-city-placeholder')" />
          </div>
          <div class="edit-field">
            <label class="label">{{ $t('event.block.logistics-label') }}</label>
            <input v-model="draft.lieu" class="input" type="text" :placeholder="$t('event.block.logistics-placeholder')" />
          </div>
          <div class="edit-field">
            <label class="label">{{ $t('event.block.party-edit-guests') }}</label>
            <input v-model="draft.nbInvites" class="input" type="text" placeholder="ex. 80" />
          </div>
        </div>
      </div>
      <hr class="divider" />

      <!-- 2. Note partagée -->
      <div class="section">
        <span class="section-label">{{ $t('event.block.shared-note-label') }}</span>
        <textarea
          ref="noteRef"
          v-model="draft.sharedNote"
          class="textarea"
          :placeholder="$t('event.block.shared-note-placeholder')"
          rows="3"
          @input="autoResize"
        />
      </div>
      <hr class="divider" />

      <!-- 3. L'événement -->
      <div class="section">
        <span class="section-label">{{ $t('event.block.party-label') }}</span>
        <div v-if="variant === 'client'" class="edit-fields">
          <div class="edit-field">
            <label class="label">{{ $t('event.block.party-edit-event-type') }}</label>
            <select v-model="draft.eventType" class="select">
              <option value="">{{ $t('event.block.party-select-empty') }}</option>
              <option v-for="o in EVENT_TYPE_OPTIONS" :key="o.value" :value="o.value">{{ o.emoji }} {{ o.label }}</option>
            </select>
          </div>
          <div class="edit-field">
            <label class="label">{{ $t('event.block.party-edit-ambiance') }}</label>
            <select v-model="draft.ambiance" class="select">
              <option value="">{{ $t('event.block.party-select-empty') }}</option>
              <option v-for="o in AMBIANCE_OPTIONS" :key="o.value" :value="o.value">{{ o.emoji }} {{ o.label }}</option>
            </select>
          </div>
          <div class="edit-field">
            <label class="label">{{ $t('event.block.party-moment-label') }}</label>
            <select v-model="draft.momentCle" class="select">
              <option value="">{{ $t('event.block.party-select-empty') }}</option>
              <option v-for="o in MOMENT_CLE_OPTIONS" :key="o.value" :value="o.value">{{ o.emoji }} {{ o.label }}</option>
            </select>
          </div>
          <div class="edit-field">
            <label class="label">{{ $t('event.block.party-edit-description') }}</label>
            <textarea v-model="draft.description" class="textarea" rows="3" :placeholder="$t('event.block.party-description-placeholder')" />
          </div>
        </div>
      </div>
      <hr class="divider" />

      <!-- 4. Contact -->
      <div class="section">
        <template v-if="variant === 'client'">
          <div class="edit-field">
            <label class="label">{{ $t('event.block.edit-client-firstname') }}</label>
            <input v-model="draft.firstName" class="input" type="text" />
          </div>
          <div class="edit-field">
            <label class="label">{{ $t('event.block.edit-client-lastname') }}</label>
            <input v-model="draft.lastName" class="input" type="text" />
          </div>
          <div class="edit-field">
            <label class="label">{{ $t('event.block.edit-client-phone') }}</label>
            <input v-model="draft.phone" class="input" type="tel" />
          </div>
          <div class="edit-field">
            <label class="label">{{ $t('event.block.edit-client-email') }}</label>
            <input v-model="draft.email" class="input" type="email" />
          </div>
        </template>
        <template v-else>
          <span class="contact-name">{{ clientInfo.firstName }}</span>
          <span class="contact-value">{{ clientInfo.phone }}</span>
          <span class="contact-value email">{{ clientInfo.email }}</span>
        </template>
      </div>
      <hr class="divider" />

      <!-- 5. Mise à jour -->
      <div class="section">
        <div class="update-row">
          <span class="section-label">{{ $t('event.block.update-label') }}</span>
          <button class="cancel-btn" type="button" @click="cancel">{{ $t('event.block.cancel') }}</button>
        </div>
        <div class="update-date-row">
          <button v-if="lastUpdateDate" class="journal-btn" type="button" @click="$emit('openJournal')">{{ lastUpdateDate }}</button>
          <p v-else class="field-value empty">{{ $t('event.block.no-update') }}</p>
          <button class="save-btn" type="button" :disabled="saving" @click="save">
            {{ saving ? $t('event.block.saving') : $t('event.block.save') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { EventDetail } from '~/data/evenement/domain/EventDetail'
import type { ClientContactInfo } from '~/data/reservation/domain/ClientContactInfo'
import type { EventPatchRequestDto } from '~/data/evenement/dto/EventDetailDto'
import { EVENT_TYPE_OPTIONS, AMBIANCE_OPTIONS, MOMENT_CLE_OPTIONS } from '~/types/demande'

const props = defineProps<{
  event: EventDetail
  clientInfo: ClientContactInfo
  variant: 'client' | 'pro'
  saving: boolean
  lastUpdateDate: string | null
}>()

const emit = defineEmits<{
  save: [payload: { eventPatch: EventPatchRequestDto; clientPatch: Partial<ClientContactInfo> }]
  cancel: [isDirty: boolean]
  openJournal: []
}>()

const draft = reactive({
  eventType: '', ambiance: '', ville: '', lieu: '', nbInvites: '',
  sharedNote: '', description: '', momentCle: '',
  firstName: '', lastName: '', phone: '', email: '',
})

onMounted(() => {
  draft.eventType = props.event.eventType ?? ''
  draft.ambiance = props.event.ambiance ?? ''
  draft.ville = props.event.ville ?? ''
  draft.lieu = props.event.lieu ?? ''
  draft.nbInvites = props.event.nbInvites ?? ''
  draft.sharedNote = props.event.sharedNote
  draft.description = props.event.description ?? ''
  draft.momentCle = props.event.momentCle ?? ''
  draft.firstName = props.clientInfo.firstName
  draft.lastName = props.clientInfo.lastName ?? ''
  draft.phone = props.clientInfo.phone
  draft.email = props.clientInfo.email
  nextTick(autoResize)
})

const isDirty = computed(
  () =>
    draft.eventType !== (props.event.eventType ?? '') ||
    draft.ambiance !== (props.event.ambiance ?? '') ||
    draft.ville !== (props.event.ville ?? '') ||
    draft.lieu !== (props.event.lieu ?? '') ||
    draft.nbInvites !== (props.event.nbInvites ?? '') ||
    draft.sharedNote !== props.event.sharedNote ||
    draft.description !== (props.event.description ?? '') ||
    draft.momentCle !== (props.event.momentCle ?? '') ||
    draft.firstName !== props.clientInfo.firstName ||
    draft.lastName !== (props.clientInfo.lastName ?? '') ||
    draft.phone !== props.clientInfo.phone ||
    draft.email !== props.clientInfo.email,
)

function save() {
  emit('save', {
    eventPatch: {
      eventType: draft.eventType || undefined,
      ambiance: draft.ambiance || undefined,
      ville: draft.ville || undefined,
      lieu: draft.lieu || undefined,
      nbInvites: draft.nbInvites || undefined,
      sharedNote: draft.sharedNote,
      description: draft.description || undefined,
      momentCle: draft.momentCle || undefined,
    },
    clientPatch: {
      firstName: draft.firstName,
      lastName: draft.lastName || undefined,
      phone: draft.phone,
      email: draft.email,
    },
  })
}

function cancel() {
  emit('cancel', isDirty.value)
}

const noteRef = ref<HTMLTextAreaElement | null>(null)

function autoResize() {
  const el = noteRef.value
  if (!el) return
  el.style.height = 'auto'
  el.style.height = `${el.scrollHeight}px`
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.eb-desktop-edit {
  background: #fff;
  border-radius: $radius-md;
  overflow: hidden;

  .body {
    display: flex;
    flex-direction: column;
    border-top: 1px solid $divider-color;
  }
}

.section {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
  padding: $spacing-s $spacing-m;
}

.section-label {
  font-family: 'Inter', sans-serif;
  font-size: 0.65rem;
  font-weight: 600;
  letter-spacing: 0.09em;
  text-transform: uppercase;
  color: $text-secondary;
}

.divider {
  border: none;
  border-top: 1px solid $divider-color;
  margin: 0;
}

.field-value {
  font-family: 'Inter', sans-serif;
  font-size: 0.85rem;
  color: $text-primary;
  margin: 0;
  &.empty { color: $text-secondary; opacity: 0.55; font-style: italic; }
}

.contact-name {
  font-family: 'Inter', sans-serif;
  font-size: 0.875rem;
  font-weight: 600;
  color: $text-primary;
}

.contact-value {
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  color: $text-primary;
  &.email { font-size: 0.75rem; color: $text-secondary; }
}

.update-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.update-date-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-s;
}

.cancel-btn {
  background: none;
  border: none;
  font-family: inherit;
  font-size: 0.85rem;
  color: $brand-muted;
  cursor: pointer;
  padding: 4px 0;
  transition: color 150ms ease;
  &:hover { color: $text-primary; }
}

.save-btn {
  flex-shrink: 0;
  background: none;
  border: 1px solid $divider-color;
  border-radius: $radius-sm;
  font-family: inherit;
  font-size: 0.85rem;
  color: $text-primary;
  cursor: pointer;
  padding: 3px 10px;
  transition: border-color 150ms ease, color 150ms ease;
  &:hover { border-color: $brand-primary; color: $brand-primary; }
  &:disabled { opacity: 0.45; cursor: default; }
}

.journal-btn {
  background: none;
  border: none;
  padding: 0;
  font-family: inherit;
  font-size: 0.75rem;
  font-style: italic;
  color: $text-secondary;
  opacity: 0.6;
  cursor: pointer;
  text-decoration: underline;
  text-underline-offset: 2px;
  transition: opacity 150ms ease;
  &:hover { opacity: 1; }
}

.edit-fields {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

.edit-field {
  display: flex;
  flex-direction: column;
  gap: 4px;

  .label {
    font-size: 0.75rem;
    font-weight: 600;
    color: $text-secondary;
    letter-spacing: 0.02em;
  }

  .input,
  .select,
  .textarea {
    width: 100%;
    padding: $spacing-xs $spacing-s;
    border: 1px solid $divider-color;
    border-radius: $radius-sm;
    font-family: inherit;
    font-size: 0.9rem;
    color: $text-primary;
    background: $surface-soft;
    outline: none;
    box-sizing: border-box;
    transition: border-color 150ms ease, box-shadow 150ms ease;
    appearance: none;

    &:focus {
      border-color: $input-focus-border-color;
      box-shadow: $input-focus-box-shadow;
      background: #fff;
    }

    &::placeholder { color: $text-secondary; opacity: 0.45; }
  }

  .select {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='8' viewBox='0 0 12 8'%3E%3Cpath d='M1 1l5 5 5-5' stroke='%236b635c' stroke-width='1.5' fill='none' stroke-linecap='round'/%3E%3C/svg%3E");
    background-repeat: no-repeat;
    background-position: right $spacing-s center;
    padding-right: $spacing-xl;
  }

  .textarea {
    resize: none;
    line-height: 1.6;
    overflow: hidden;
  }
}
</style>
