<template>
  <div class="parametres">
    <div v-if="fields && clientInfo" class="column">
      <button class="back" type="button" @click="backToBoard">
        <ArrowLeftIcon class="icon" aria-hidden="true" />
        {{ $t('evenement.settings.back') }}
      </button>

      <!-- Chaque panneau validé est enregistré aussitôt. Événement enregistré : ses informations
           essentielles ne peuvent pas être effacées. -->
      <EventInfoCards
        :event="fields"
        with-title
        date-locked
        essentials-required
        @update="saveFields"
      />

      <!-- ── Note partagée ───────────────────────────────────────────────────── -->
      <section class="section">
        <h2 class="section-title">{{ $t('event.block.shared-note-label') }}</h2>
        <textarea
          v-model="sharedNote"
          class="note"
          @change="saveSharedNote"
          rows="4"
          :placeholder="$t('event.block.shared-note-placeholder')"
        />
      </section>

      <!-- ── Coordonnées ─────────────────────────────────────────────────────── -->
      <section class="section">
        <h2 class="section-title">{{ $t('event.block.coordinates-label') }}</h2>
        <p class="contact-name">{{ clientName }}</p>
        <p v-if="clientInfo.phone" class="contact">{{ clientInfo.phone }}</p>
        <p class="contact">{{ clientInfo.email }}</p>
      </section>

      <!-- ── Couverture et journal ───────────────────────────────────────────── -->
      <div class="links">
        <button class="link" type="button" @click="coverDialogOpen = true">
          <ImageIcon class="icon" aria-hidden="true" />
          {{ $t('evenement.settings.cover') }}
        </button>
        <button class="link" type="button" @click="journalOpen = true">
          <HistoryIcon class="icon" aria-hidden="true" />
          {{ $t('evenement.settings.journal') }}
        </button>
      </div>
    </div>
    <Sk v-else class="skeleton" />

    <EventEditDialog
      v-if="event"
      v-model:open="coverDialogOpen"
      :event="event"
      :event-id="eventId"
      @save="onCoverDialogSave"
      @cover-updated="onCoverUpdated"
    />
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
import { ArrowLeftIcon, HistoryIcon, ImageIcon } from '@remixicons/vue/line'
import EventEditDialog from '~/components/app/EventEditDialog.vue'
import EventJournal from '~/components/app/EventJournal.vue'
import Sk from '~/components/basics/Sk.vue'
import EventInfoCards from '~/components/evenement/EventInfoCards.vue'
import type { EventInfoFields } from '~/composables/useLocalEvent'
import type { EventMetaPatch } from '~/data/evenement/domain/EventMetaPatch'
import { patchEvent } from '~/data/evenement/service/evenementService'
import { useEventContext } from '~/data/evenement/useEventContext'
import { useEventJournal } from '~/data/evenement/useEventJournal'

definePageMeta({ layout: 'app' })

const { t } = useI18n()
useHead({ title: t('evenement.settings.page-title') })

const { eventId, event, clientInfo } = useEventContext()

// ── Informations ──────────────────────────────────────────────────────────────
// Celles de l'événement en base : chaque modification est enregistrée aussitôt.
const fields = computed<EventInfoFields | null>(() => {
  const loaded = event.value
  if (!loaded) return null
  return {
    title: loaded.title,
    eventType: loaded.eventType ?? null,
    date: loaded.date,
    ville: loaded.ville ?? '',
    lieu: loaded.lieu ?? '',
    nbInvites: loaded.nbInvites ?? '',
    ambiance: loaded.ambiance ?? null,
    momentCle: loaded.momentCle ?? null,
    description: loaded.description ?? '',
  }
})

// La date n'est pas envoyée : elle ne se modifie pas sur un événement en base. « Autre » sans
// précision n'est pas enregistré, comme à la création de l'événement (toEvenementRequest).
async function saveFields(update: Partial<EventInfoFields>) {
  if (!fields.value) return
  const updated = { ...fields.value, ...update }
  const withoutBareAutre = (value: string | null) => (!value || value === 'autre' ? '' : value)
  event.value = await patchEvent(eventId, {
    title: updated.title,
    eventType: withoutBareAutre(updated.eventType),
    ville: updated.ville,
    lieu: updated.lieu,
    nbInvites: updated.nbInvites,
    ambiance: withoutBareAutre(updated.ambiance),
    momentCle: withoutBareAutre(updated.momentCle),
    description: updated.description,
  })
}

// ── Note partagée ─────────────────────────────────────────────────────────────
// Saisie libre : enregistrée quand on quitte le champ. Remplie une seule fois, dès que l'événement
// est disponible : ses rechargements en arrière-plan n'écrasent pas la saisie en cours.
const sharedNote = ref('')
let sharedNoteFilled = false
watch(
  event,
  (loaded) => {
    if (!loaded || sharedNoteFilled) return
    sharedNoteFilled = true
    sharedNote.value = loaded.sharedNote
  },
  { immediate: true },
)

async function saveSharedNote() {
  event.value = await patchEvent(eventId, { sharedNote: sharedNote.value })
}

const clientName = computed(() =>
  [clientInfo.value?.firstName, clientInfo.value?.lastName].filter(Boolean).join(' '),
)

// ── Couverture ────────────────────────────────────────────────────────────────
// Le dialogue enregistre lui-même la couverture et le nom.
const coverDialogOpen = ref(false)

async function onCoverDialogSave(patch: EventMetaPatch) {
  event.value = await patchEvent(eventId, patch)
}

function onCoverUpdated(imagePath: string) {
  if (event.value) event.value.coverImage = imagePath
}

// ── Journal ───────────────────────────────────────────────────────────────────
const journalOpen = ref(false)
const {
  entries: journalEntries,
  hasMore: journalHasMore,
  loading: journalLoading,
  loadFirstPage: loadFirstJournalPage,
  loadNextPage: loadNextJournalPage,
} = useEventJournal(eventId)

watch(journalOpen, (open) => {
  if (open) loadFirstJournalPage()
})

// ── Navigation ────────────────────────────────────────────────────────────────
function backToBoard() {
  navigateTo(`/app/events/${eventId}`)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.parametres {
  display: flex;
  justify-content: center;
  flex: 1;
  background: $surface-white;

  .column {
    display: flex;
    flex-direction: column;
    gap: $spacing-l;
    width: 100%;
    max-width: 30rem;
    padding: $spacing-m $section-padding-x $spacing-xl;

    .back {
      display: inline-flex;
      align-items: center;
      align-self: flex-start;
      gap: $spacing-xxs;
      padding: 0;
      border: none;
      background: none;
      color: $text-primary;
      font-family: inherit;
      font-size: $font-size-sm;
      cursor: pointer;

      .icon {
        width: 1.125rem;
        height: 1.125rem;
      }
    }

    .section {
      display: flex;
      flex-direction: column;
      gap: $spacing-xs;

      .section-title {
        margin: 0;
        font-size: 0.7rem;
        font-weight: 700;
        letter-spacing: 0.08em;
        text-transform: uppercase;
        color: $text-secondary;
      }

      .note {
        width: 100%;
        padding: $spacing-s;
        border: 1px solid $divider-color;
        border-radius: $radius-lg;
        font-family: inherit;
        font-size: $font-size-sm;
        resize: vertical;
      }

      .contact-name {
        margin: 0;
        font-weight: 600;
      }

      .contact {
        margin: 0;
        font-size: $font-size-sm;
        color: $text-secondary;
      }
    }

    .links {
      display: flex;
      flex-direction: column;
      gap: $spacing-xs;

      .link {
        display: inline-flex;
        align-items: center;
        gap: $spacing-xs;
        padding: 0;
        border: none;
        background: none;
        color: $brand-primary;
        font-family: inherit;
        font-size: $font-size-sm;
        font-weight: 500;
        cursor: pointer;

        .icon {
          width: 1.125rem;
          height: 1.125rem;
        }
      }
    }
  }

  .skeleton {
    width: 100%;
    max-width: 30rem;
    height: 20rem;
    margin: $spacing-m;
  }
}
</style>
