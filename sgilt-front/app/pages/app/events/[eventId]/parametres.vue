<template>
  <div class="parametres">
    <div v-if="event && clientInfo" class="column">
      <button class="back" type="button" @click="backToBoard">
        <ArrowLeftIcon class="icon" aria-hidden="true" />
        {{ $t('evenement.settings.back') }}
      </button>

      <!-- Les panneaux alimentent le brouillon ; l'événement n'est modifié qu'à l'enregistrement. -->
      <EventInfoCards
        :event="draft"
        with-title
        date-locked
        @update="Object.assign(draft, $event)"
      />

      <!-- ── Note partagée ───────────────────────────────────────────────────── -->
      <section class="section">
        <h2 class="section-title">{{ $t('event.block.shared-note-label') }}</h2>
        <textarea
          v-model="sharedNote"
          class="note"
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

      <!-- ── Actions ──────────────────────────────────────────────────────────── -->
      <div class="actions">
        <SgiltButton variant="secondary" @click="backToBoard">
          {{ $t('common.cancel') }}
        </SgiltButton>
        <SgiltButton @click="save">{{ $t('common.save') }}</SgiltButton>
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
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
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

// ── Brouillon ─────────────────────────────────────────────────────────────────
// Rempli au chargement de l'événement ; l'événement n'est modifié qu'à l'enregistrement.
const draft = reactive<EventInfoFields>({
  title: '',
  eventType: null,
  date: undefined,
  ville: '',
  lieu: '',
  nbInvites: '',
  ambiance: null,
  momentCle: null,
  description: '',
})
const sharedNote = ref('')

// Rempli une seule fois, dès que l'événement est disponible (il l'est déjà si l'on vient d'une
// autre page de l'événement) : ses rechargements en arrière-plan n'écrasent pas la saisie.
let draftFilled = false
watch(
  event,
  (loaded) => {
    if (!loaded || draftFilled) return
    draftFilled = true
    Object.assign(draft, {
      title: loaded.title,
      eventType: loaded.eventType ?? null,
      date: loaded.date,
      ville: loaded.ville ?? '',
      lieu: loaded.lieu ?? '',
      nbInvites: loaded.nbInvites ?? '',
      ambiance: loaded.ambiance ?? null,
      momentCle: loaded.momentCle ?? null,
      description: loaded.description ?? '',
    })
    sharedNote.value = loaded.sharedNote
  },
  { immediate: true },
)

const clientName = computed(() =>
  [clientInfo.value?.firstName, clientInfo.value?.lastName].filter(Boolean).join(' '),
)

// ── Couverture ────────────────────────────────────────────────────────────────
// Le dialogue enregistre lui-même la couverture ; le nom qu'il modifie est reporté dans le brouillon.
const coverDialogOpen = ref(false)

async function onCoverDialogSave(patch: EventMetaPatch) {
  event.value = await patchEvent(eventId, patch)
  draft.title = event.value.title
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

// ── Actions ───────────────────────────────────────────────────────────────────
function backToBoard() {
  navigateTo(`/app/events/${eventId}`)
}

// La date n'est pas envoyée : elle ne se modifie pas sur un événement en base. « Autre » sans
// précision n'est pas enregistré, comme à la création de l'événement (toEvenementRequest).
async function save() {
  const withoutBareAutre = (value: string | null) => (!value || value === 'autre' ? '' : value)
  await patchEvent(eventId, {
    title: draft.title,
    eventType: withoutBareAutre(draft.eventType),
    ville: draft.ville,
    lieu: draft.lieu,
    nbInvites: draft.nbInvites,
    ambiance: withoutBareAutre(draft.ambiance),
    momentCle: withoutBareAutre(draft.momentCle),
    description: draft.description,
    sharedNote: sharedNote.value,
  })
  backToBoard()
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

    .actions {
      display: flex;
      justify-content: flex-end;
      gap: $spacing-s;
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
