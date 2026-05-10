<template>
  <div class="eb-desktop">
    <button class="toggle" type="button" @click="open = !open">
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
      <ArrowUpSIcon v-if="open" class="chevron" />
      <ArrowDownSIcon v-else class="chevron" />
    </button>

    <div v-if="open" class="body">
      <!-- 1. Logistique -->
      <div class="section">
        <dl class="brief-fields">
          <template v-if="event.ville">
            <dt>{{ $t('event.block.party-edit-city') }}</dt>
            <dd>{{ event.ville }}</dd>
          </template>
          <dt>{{ $t('event.block.logistics-label') }}</dt>
          <dd :class="{ empty: !event.lieu }">{{ event.lieu || $t('event.block.logistics-empty') }}</dd>
          <template v-if="event.nbInvites">
            <dt>{{ $t('event.block.party-edit-guests') }}</dt>
            <dd>{{ event.nbInvites }}</dd>
          </template>
        </dl>
      </div>
      <hr class="divider" />

      <!-- 2. Note partagée -->
      <div class="section">
        <span class="section-label">{{ $t('event.block.shared-note-label') }}</span>
        <p class="note-text" :class="{ empty: !event.sharedNote }">
          {{ event.sharedNote || $t('event.block.shared-note-empty') }}
        </p>
      </div>
      <hr class="divider" />

      <!-- 3. L'événement -->
      <div class="section">
        <span class="section-label">{{ $t('event.block.party-label') }}</span>
        <dl v-if="eventTypeLabel || ambianceLabel || event.momentCle || event.description" class="brief-fields">
          <template v-if="eventTypeLabel">
            <dt>{{ $t('event.block.party-type-label') }}</dt>
            <dd>{{ eventTypeEmoji }} {{ eventTypeLabel }}</dd>
          </template>
          <template v-if="ambianceLabel">
            <dt>{{ $t('event.block.party-ambiance-label') }}</dt>
            <dd>{{ ambianceEmoji }} {{ ambianceLabel }}</dd>
          </template>
          <dt>{{ $t('event.block.party-moment-label') }}</dt>
          <dd :class="{ empty: !event.momentCle }">{{ momentCleLabel || $t('event.block.party-moment-empty') }}</dd>
          <template v-if="event.description">
            <dt>{{ $t('event.block.party-edit-description') }}</dt>
            <dd>{{ event.description }}</dd>
          </template>
        </dl>
        <p v-else class="field-value empty">{{ $t('event.block.party-empty') }}</p>
      </div>
      <hr class="divider" />

      <!-- 4. Contact -->
      <div class="section">
        <template v-if="variant === 'pro'">
          <span class="contact-name">{{ clientInfo.firstName }}</span>
          <div class="contact-row">
            <span class="contact-value">{{ clientInfo.phone }}</span>
            <button class="copy-btn" type="button" :class="{ copied: phoneCopied }" @click="copyPhone">
              <CheckIcon v-if="phoneCopied" class="copy-icon" />
              <FileCopyIcon v-else class="copy-icon" />
            </button>
          </div>
          <div class="contact-row">
            <span class="contact-value email">{{ clientInfo.email }}</span>
            <button class="copy-btn" type="button" :class="{ copied: emailCopied }" @click="copyEmail">
              <CheckIcon v-if="emailCopied" class="copy-icon" />
              <FileCopyIcon v-else class="copy-icon" />
            </button>
          </div>
        </template>
        <template v-else>
          <span class="contact-name">{{ clientInfo.firstName }}{{ clientInfo.lastName ? ' ' + clientInfo.lastName : '' }}</span>
          <span class="contact-value">{{ clientInfo.phone }}</span>
          <span class="contact-value email">{{ clientInfo.email }}</span>
        </template>
      </div>
      <hr class="divider" />

      <!-- 5. Mise à jour -->
      <div class="section">
        <div class="update-row">
          <span class="section-label">{{ $t('event.block.update-label') }}</span>
          <button v-if="variant === 'client'" class="edit-btn" type="button" @click="$emit('openEdit')">
            <EditIcon />
          </button>
        </div>
        <div class="update-date-row">
          <button v-if="lastUpdateDate" class="journal-btn" type="button" @click="$emit('openJournal')">
            {{ lastUpdateDate }}
          </button>
          <p v-else class="field-value empty">{{ $t('event.block.no-update') }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { EventDetail } from '~/data/evenement/domain/EventDetail'
import type { ClientContactInfo } from '~/data/reservation/domain/ClientContactInfo'
import { EVENT_TYPE_OPTIONS, AMBIANCE_OPTIONS, MOMENT_CLE_OPTIONS } from '~/types/demande'
import {
  ArrowDownSIcon, ArrowUpSIcon, CalendarEventIcon, MapPin2Icon, GroupIcon,
  FileCopyIcon, CheckIcon, EditIcon,
} from '@remixicons/vue/line'

const props = defineProps<{
  event: EventDetail
  clientInfo: ClientContactInfo
  variant: 'client' | 'pro'
  lastUpdateDate: string | null
}>()

defineEmits<{ openEdit: []; openJournal: [] }>()

const open = ref(true)
const phoneCopied = ref(false)
const emailCopied = ref(false)

const eventTypeOpt = computed(() => EVENT_TYPE_OPTIONS.find((o) => o.value === props.event.eventType))
const eventTypeLabel = computed(() => eventTypeOpt.value?.label ?? null)
const eventTypeEmoji = computed(() => eventTypeOpt.value?.emoji ?? '')

const ambianceOpt = computed(() => AMBIANCE_OPTIONS.find((o) => o.value === props.event.ambiance))
const ambianceLabel = computed(() => ambianceOpt.value?.label ?? null)
const ambianceEmoji = computed(() => ambianceOpt.value?.emoji ?? '')

const momentCleOpt = computed(() => MOMENT_CLE_OPTIONS.find((o) => o.value === props.event.momentCle))
const momentCleLabel = computed(() =>
  momentCleOpt.value ? `${momentCleOpt.value.emoji} ${momentCleOpt.value.label}` : null,
)

async function copyPhone() {
  await navigator.clipboard.writeText(props.clientInfo.phone)
  phoneCopied.value = true
  setTimeout(() => (phoneCopied.value = false), 2000)
}

async function copyEmail() {
  await navigator.clipboard.writeText(props.clientInfo.email)
  emailCopied.value = true
  setTimeout(() => (emailCopied.value = false), 2000)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.eb-desktop {
  background: #fff;
  border-radius: $radius-md;
  overflow: hidden;

  .toggle {
    width: 100%;
    display: flex;
    align-items: center;
    gap: $spacing-s;
    padding: $spacing-s $spacing-m;
    background: none;
    border: none;
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

  .chevron {
    flex-shrink: 0;
    width: 16px;
    height: 16px;
    color: $text-secondary;
  }

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

  &.empty {
    color: $text-secondary;
    opacity: 0.55;
    font-style: italic;
  }
}

.note-text {
  font-family: 'Inter', sans-serif;
  font-size: 0.85rem;
  color: $text-primary;
  line-height: 1.55;
  margin: 0;
  white-space: pre-wrap;

  &.empty {
    color: $text-secondary;
    opacity: 0.45;
    font-style: italic;
  }
}

.brief-fields {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 4px $spacing-s;
  margin: 0;

  dt {
    font-family: 'Inter', sans-serif;
    font-size: 0.7rem;
    font-weight: 600;
    letter-spacing: 0.06em;
    text-transform: uppercase;
    color: $text-secondary;
    align-self: center;
  }

  dd {
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    color: $text-primary;
    margin: 0;
    align-self: center;

    &.empty {
      color: $text-secondary;
      opacity: 0.55;
      font-style: italic;
    }
  }
}

.contact-name {
  font-family: 'Inter', sans-serif;
  font-size: 0.875rem;
  font-weight: 600;
  color: $text-primary;
}

.contact-row {
  display: flex;
  align-items: center;
  gap: $spacing-xs;
  min-width: 0;
}

.contact-value {
  flex: 1;
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  color: $text-primary;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;

  &.email {
    font-size: 0.75rem;
    color: $text-secondary;
  }
}

.copy-btn {
  flex-shrink: 0;
  width: 22px;
  height: 22px;
  border-radius: $radius-sm;
  border: 1px solid $divider-color;
  background: none;
  color: $text-secondary;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 120ms ease, color 120ms ease;

  &.copied {
    background: $brand-accent;
    color: $brand-primary;
    border-color: $brand-accent;
  }
}

.copy-icon {
  width: 13px;
  height: 13px;
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

.edit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  border: none;
  background: none;
  color: $brand-muted;
  border-radius: $radius-sm;
  cursor: pointer;
  transition: background 150ms ease, color 150ms ease;

  &:hover {
    background: $surface-soft;
    color: $brand-primary;
  }
}

.journal-btn {
  align-self: flex-start;
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
    color: color-mix(in srgb, #e6b800 82%, #000);
  }
}
</style>
