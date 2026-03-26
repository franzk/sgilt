<template>
  <div class="booking-brief" :class="{ 'booking-brief--open': open }">
    <!-- ── Ligne résumé (toujours visible) ──────────────────────────────────── -->
    <button class="booking-brief__toggle" type="button" @click="open = !open">
      <div class="booking-brief__pills">
        <span v-if="event.date" class="brief-pill brief-pill--date">
          📅 {{ formatDate(event.date) }}
        </span>
        <span v-if="event.ville" class="brief-pill">📍 {{ event.ville }}</span>
        <span v-if="event.nbInvites" class="brief-pill">👥 {{ event.nbInvites }}</span>
        <span v-if="eventTypeLabel" class="brief-pill">{{ eventTypeEmoji }} {{ eventTypeLabel }}</span>
      </div>
      <PhCaretUp v-if="open" class="booking-brief__chevron" weight="light" :size="16" />
      <PhCaretDown v-else class="booking-brief__chevron" weight="light" :size="16" />
    </button>

    <!-- ── Contenu déplié ────────────────────────────────────────────────────── -->
    <div v-if="open" class="booking-brief__body">
      <dl class="brief-fields">
        <template v-if="event.eventType">
          <dt>Type</dt>
          <dd>{{ eventTypeEmoji }} {{ eventTypeLabel }}</dd>
        </template>
        <template v-if="event.ambiance">
          <dt>Ambiance</dt>
          <dd>{{ ambianceEmoji }} {{ ambianceLabel }}</dd>
        </template>
        <template v-if="event.phraseSubtitle">
          <dt>Moment clé</dt>
          <dd>{{ event.phraseSubtitle }}</dd>
        </template>
      </dl>

      <template v-if="messageInitial">
        <hr class="brief-divider" />
        <div class="brief-message">
          <span class="brief-message__label">Message initial</span>
          <p class="brief-message__text">{{ messageInitial.content }}</p>
        </div>
      </template>

      <hr class="brief-divider" />
      <div class="brief-contact">
        <span class="brief-contact__label">COORDONNÉES CLIENT</span>
        <span class="brief-contact__name">{{ clientInfo.firstName }}</span>
        <div class="brief-contact__row">
          <span class="brief-contact__value">{{ clientInfo.phone }}</span>
          <button
            class="brief-copy"
            type="button"
            :class="{ 'brief-copy--copied': phoneCopied }"
            :aria-label="phoneCopied ? 'Copié' : 'Copier le numéro'"
            @click="copyPhone"
          >
            <PhCheck v-if="phoneCopied" weight="bold" :size="12" />
            <PhCopy v-else weight="light" :size="12" />
          </button>
        </div>
        <div class="brief-contact__row">
          <span class="brief-contact__value brief-contact__value--email">{{ clientInfo.email }}</span>
          <button
            class="brief-copy"
            type="button"
            :class="{ 'brief-copy--copied': emailCopied }"
            :aria-label="emailCopied ? 'Copié' : 'Copier l\'email'"
            @click="copyEmail"
          >
            <PhCheck v-if="emailCopied" weight="bold" :size="12" />
            <PhCopy v-else weight="light" :size="12" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { EventDetail, ClientContactInfo, ReservationNote } from '~/types/event'
import { EVENT_TYPE_OPTIONS, AMBIANCE_OPTIONS } from '~/types/demande'
import { PhCaretDown, PhCaretUp, PhCopy, PhCheck } from '@phosphor-icons/vue'

const props = defineProps<{
  event: EventDetail
  clientInfo: ClientContactInfo
  messageInitial?: ReservationNote | null
}>()

const open = ref(false)

const eventTypeOpt = computed(() => EVENT_TYPE_OPTIONS.find((o) => o.value === props.event.eventType))
const eventTypeLabel = computed(() => eventTypeOpt.value?.label ?? null)
const eventTypeEmoji = computed(() => eventTypeOpt.value?.emoji ?? '')

const ambianceOpt = computed(() => AMBIANCE_OPTIONS.find((o) => o.value === props.event.ambiance))
const ambianceLabel = computed(() => ambianceOpt.value?.label ?? null)
const ambianceEmoji = computed(() => ambianceOpt.value?.emoji ?? '')

const phoneCopied = ref(false)
const emailCopied = ref(false)

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

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  })
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.booking-brief {
  background: #fff;
  border-radius: $radius-md;
  box-shadow: 0 2px 10px rgba($brand-accent, 0.18);
  overflow: hidden;
}

.booking-brief__toggle {
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

  &:hover {
    background: $surface-soft;
  }
}

.booking-brief__pills {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  min-width: 0;
}

.booking-brief__chevron {
  flex-shrink: 0;
  color: $text-secondary;
}

.brief-pill {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  padding: 3px 8px;
  border-radius: 2rem;
  background: $surface-soft;
  border: 1px solid $divider-color;
  font-family: 'Inter', sans-serif;
  font-size: 0.688rem;
  color: $text-secondary;
  font-weight: 500;
  white-space: nowrap;

  &--date {
    background: rgba($brand-accent, 0.08);
    border-color: rgba($brand-accent, 0.3);
    color: darken(#e6b800, 18%);
  }
}

.booking-brief__body {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
  padding: 0 $spacing-m $spacing-m;
  border-top: 1px solid $divider-color;
}

.brief-divider {
  border: none;
  border-top: 1px solid $divider-color;
  margin: 0;
}

// Champs
.brief-fields {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 4px $spacing-s;
  margin: 0;
  padding-top: $spacing-s;

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
  }
}

// Message initial
.brief-message {
  display: flex;
  flex-direction: column;
  gap: 4px;

  &__label {
    font-family: 'Inter', sans-serif;
    font-size: 0.65rem;
    font-weight: 600;
    letter-spacing: 0.09em;
    text-transform: uppercase;
    color: $text-secondary;
  }

  &__text {
    font-family: 'Inter', sans-serif;
    font-size: 0.85rem;
    font-style: italic;
    color: $text-secondary;
    line-height: 1.55;
    margin: 0;
    white-space: pre-wrap;
  }
}

// Coordonnées client
.brief-contact {
  display: flex;
  flex-direction: column;
  gap: 5px;

  &__label {
    font-family: 'Inter', sans-serif;
    font-size: 0.65rem;
    font-weight: 600;
    letter-spacing: 0.09em;
    text-transform: uppercase;
    color: $text-secondary;
  }

  &__name {
    font-family: 'Inter', sans-serif;
    font-size: 0.875rem;
    font-weight: 600;
    color: $text-primary;
  }

  &__row {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    min-width: 0;
  }

  &__value {
    flex: 1;
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    color: $text-primary;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;

    &--email {
      font-size: 0.75rem;
      color: $text-secondary;
    }
  }
}

.brief-copy {
  flex-shrink: 0;
  width: 22px;
  height: 22px;
  border-radius: $radius-sm;
  border: 1px solid $divider-color;
  background: none;
  font-size: 0.75rem;
  color: $text-secondary;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 120ms ease, color 120ms ease;

  &--copied {
    background: $brand-accent;
    color: $brand-primary;
    border-color: $brand-accent;
  }
}
</style>
