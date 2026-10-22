<template>
  <div class="event-details">
    <div class="head">
      <h2 class="title">{{ $t('reservation.details.title') }}</h2>
      <!-- Client : ses informations se modifient dans les paramètres de l'événement. -->
      <NuxtLink v-if="editPath" :to="editPath" class="edit">
        <PencilIcon class="icon" aria-hidden="true" />
        {{ $t('reservation.details.edit') }}
      </NuxtLink>
    </div>

    <!-- ── Infos pratiques ─────────────────────────────────────────────────────── -->
    <section class="card">
      <h3 class="card-title">{{ $t('reservation.details.practical') }}</h3>
      <dl class="infos">
        <div v-for="info in practicalInfos" :key="info.key" class="info">
          <component :is="info.icon" class="icon" aria-hidden="true" />
          <dt class="label">{{ $t(`reservation.details.${info.key}`) }}</dt>
          <dd class="value" :class="{ empty: !info.value }">
            {{ info.value || $t('reservation.details.to-define') }}
          </dd>
        </div>
      </dl>
    </section>

    <!-- ── L'événement ─────────────────────────────────────────────────────────── -->
    <section class="card">
      <h3 class="card-title">{{ $t('reservation.details.event') }}</h3>
      <dl class="fields">
        <div v-for="field in eventFields" :key="field.key" class="field">
          <dt class="label">{{ $t(`reservation.details.${field.key}`) }}</dt>
          <dd class="value" :class="{ empty: !field.value }">
            {{ field.value || $t('reservation.details.to-define') }}
          </dd>
        </div>
      </dl>
    </section>

    <!-- ── Note partagée avec les prestataires ─────────────────────────────────── -->
    <section class="card">
      <h3 class="card-title">{{ $t('reservation.details.shared-note') }}</h3>
      <p class="note" :class="{ empty: !event.sharedNote }">
        {{ event.sharedNote || $t('reservation.details.shared-note-empty') }}
      </p>
    </section>

    <!-- ── Coordonnées du client (ce que le prestataire reçoit) ────────────────── -->
    <section class="card">
      <h3 class="card-title">{{ $t('reservation.details.contact') }}</h3>
      <p class="contact-name">{{ clientName }}</p>
      <div v-for="contact in contacts" :key="contact.key" class="contact">
        <component :is="contact.icon" class="icon" aria-hidden="true" />
        <span class="contact-value">{{ contact.value }}</span>
        <button
          class="copy"
          type="button"
          :aria-label="$t('reservation.details.copy')"
          @click="copy(contact.key, contact.value)"
        >
          <CheckIcon v-if="copied === contact.key" class="icon" />
          <FileCopyIcon v-else class="icon" />
        </button>
      </div>
    </section>

    <!-- ── Dernière mise à jour → journal ──────────────────────────────────────── -->
    <button v-if="event.lastUpdateDate" class="journal" type="button" @click="journalOpen = true">
      <HistoryIcon class="icon" aria-hidden="true" />
      {{ $t('reservation.details.last-update', { date: formatDateTime(event.lastUpdateDate) }) }}
    </button>

    <EventJournal
      v-model:open="journalOpen"
      :journal="journalEntries"
      :has-more="journalHasMore"
      :loading="journalLoading"
      @load-more="loadNextJournalPage"
    />
  </div>
</template>

<script setup lang="ts">
// Onglet Détails de la fiche d'une réservation, le même pour le client et le prestataire :
// l'événement (infos pratiques, type, ambiance…, note partagée), les coordonnées du client que le
// prestataire reçoit, et le journal des modifications. Le client y voit donc exactement ce qu'il
// partage ; lui seul a « Modifier » (vers les paramètres de l'événement).
import {
  CalendarEventIcon,
  CheckIcon,
  FileCopyIcon,
  GroupIcon,
  HistoryIcon,
  MailIcon,
  MapPin2Icon,
  PencilIcon,
  PhoneIcon,
  Building2Icon,
} from '@remixicons/vue/line'
import EventJournal from '~/components/app/EventJournal.vue'
import { choiceLabel } from '~/composables/useLocalEvent'
import type { EventMeta } from '~/data/evenement/domain/EventMeta'
import { useEventJournal } from '~/data/evenement/useEventJournal'
import type { ClientContactInfo } from '~/data/reservation/domain/ClientContactInfo'
import { AMBIANCE_OPTIONS, EVENT_TYPE_OPTIONS, MOMENT_CLE_OPTIONS } from '~/types/evenement'
import { formatDate, formatDateTime } from '~/utils/dateUtils'

const props = defineProps<{
  event: EventMeta
  clientInfo: ClientContactInfo
  // Client : chemin des paramètres de l'événement (bouton « Modifier »). Absent côté prestataire.
  editPath?: string
}>()

const { nbInvitesLabel } = useNbInvitesLabel()

// ── Contenu ───────────────────────────────────────────────────────────────────
// Valeur vide : « À définir ».
const practicalInfos = computed(() => [
  { key: 'date', icon: CalendarEventIcon, value: formatDate(props.event.date) },
  { key: 'ville', icon: MapPin2Icon, value: props.event.ville },
  { key: 'lieu', icon: Building2Icon, value: props.event.lieu },
  { key: 'nb-invites', icon: GroupIcon, value: nbInvitesLabel(props.event.nbInvites) },
])

const eventFields = computed(() => [
  { key: 'type', value: choiceLabel(EVENT_TYPE_OPTIONS, props.event.eventType ?? null) },
  { key: 'ambiance', value: choiceLabel(AMBIANCE_OPTIONS, props.event.ambiance ?? null) },
  { key: 'moment-cle', value: choiceLabel(MOMENT_CLE_OPTIONS, props.event.momentCle ?? null) },
  { key: 'description', value: props.event.description },
])

const clientName = computed(() =>
  [props.clientInfo.firstName, props.clientInfo.lastName].filter(Boolean).join(' '),
)

const contacts = computed(() =>
  [
    { key: 'phone', icon: PhoneIcon, value: props.clientInfo.phone },
    { key: 'email', icon: MailIcon, value: props.clientInfo.email },
  ].filter((contact) => contact.value),
)

// ── Copie d'une coordonnée ────────────────────────────────────────────────────
const copied = ref<string | null>(null)

async function copy(key: string, value: string) {
  await navigator.clipboard.writeText(value)
  copied.value = key
  setTimeout(() => (copied.value = null), 2000)
}

// ── Journal ───────────────────────────────────────────────────────────────────
const journalOpen = ref(false)
const {
  entries: journalEntries,
  hasMore: journalHasMore,
  loading: journalLoading,
  loadFirstPage: loadFirstJournalPage,
  loadNextPage: loadNextJournalPage,
} = useEventJournal(props.event.id)

watch(journalOpen, (open) => {
  if (open) loadFirstJournalPage()
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.event-details {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;

  .head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $spacing-s;

    .title {
      margin: 0;
      font-family: 'Cormorant Garamond', serif;
      font-size: $font-size-2xl;
      font-weight: $font-weight-semibold;
      color: $text-primary;
      line-height: 1.1;
    }

    .edit {
      display: inline-flex;
      flex-shrink: 0;
      align-items: center;
      gap: $spacing-xs;
      padding: $spacing-xs $spacing-m;
      border: 1px solid $divider-color;
      border-radius: 9999px;
      background: $surface-white;
      color: $text-primary;
      font-size: $font-size-sm;
      font-weight: $font-weight-semibold;
      text-decoration: none;

      .icon {
        width: 1rem;
        height: 1rem;
      }
    }
  }

  .card {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
    padding: $spacing-m;
    border: 1px solid $divider-color;
    border-radius: $radius-lg;
    background: $surface-white;

    .card-title {
      margin: 0;
      font-size: $font-size-md;
      font-weight: $font-weight-bold;
      color: $text-primary;
    }

    .label {
      font-size: $font-size-xs;
      color: $text-secondary;
    }

    .value {
      margin: 0;
      font-size: $font-size-sm;
      font-weight: $font-weight-semibold;
      color: $text-primary;
      white-space: pre-wrap;

      &.empty {
        font-weight: $font-weight-regular;
        color: $text-secondary;
      }
    }

    // Infos pratiques : icône, libellé et valeur ; deux par ligne.
    .infos {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: $spacing-m;
      margin: 0;

      .info {
        display: grid;
        grid-template-columns: auto minmax(0, 1fr);
        column-gap: $spacing-xs;

        .icon {
          grid-row: span 2;
          width: 1.25rem;
          height: 1.25rem;
          color: $text-secondary;
        }
      }
    }

    .fields {
      display: flex;
      flex-direction: column;
      gap: $spacing-s;
      margin: 0;

      .field {
        display: flex;
        flex-direction: column;
        gap: 0.125rem;
      }
    }

    .note {
      margin: 0;
      font-size: $font-size-sm;
      line-height: 1.55;
      color: $text-primary;
      white-space: pre-wrap;

      &.empty {
        color: $text-secondary;
      }
    }

    .contact-name {
      margin: 0;
      font-size: $font-size-sm;
      font-weight: $font-weight-semibold;
      color: $text-primary;
    }

    .contact {
      display: flex;
      align-items: center;
      gap: $spacing-xs;

      > .icon {
        flex-shrink: 0;
        width: 1.125rem;
        height: 1.125rem;
        color: $text-secondary;
      }

      .contact-value {
        flex: 1;
        min-width: 0;
        overflow: hidden;
        font-size: $font-size-sm;
        color: $text-primary;
        text-overflow: ellipsis;
      }

      .copy {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 2rem;
        height: 2rem;
        padding: 0;
        border: none;
        border-radius: 50%;
        background: none;
        color: $text-secondary;
        cursor: pointer;

        &:hover {
          background: $surface-soft;
        }

        .icon {
          width: 1rem;
          height: 1rem;
        }
      }
    }
  }

  .journal {
    display: inline-flex;
    align-self: flex-start;
    align-items: center;
    gap: $spacing-xs;
    padding: 0;
    border: none;
    background: none;
    color: $text-secondary;
    font-family: inherit;
    font-size: $font-size-sm;
    text-decoration: underline;
    cursor: pointer;

    .icon {
      width: 1rem;
      height: 1rem;
    }
  }
}
</style>
