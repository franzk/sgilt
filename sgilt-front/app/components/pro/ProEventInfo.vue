<template>
  <!-- ── Desktop ─────────────────────────────────────────────────────────────────── -->
  <template v-if="isDesktop">
    <ProEventInfoDesktop
      :event="event"
      :client-info="clientInfo"
      :last-update-date="lastUpdateDate"
      @open-journal="journalOpen = true"
    />
  </template>

  <!-- ── Mobile ─────────────────────────────────────────────────────────────────── -->
  <template v-if="isMobile">
    <button class="mobile-trigger" type="button" @click="sheetOpen = true">
      <div class="pills">
        <span v-if="event.date" class="event-pill date">
          <CalendarEventIcon class="icon" />{{ formatDateShort(event.date) }}
        </span>
        <span v-if="event.ville" class="event-pill">
          <MapPin2Icon class="icon" />{{ event.ville }}
        </span>
        <span v-if="event.nbInvites" class="event-pill">
          <GroupIcon class="icon" />{{ nbInvitesLabel(event.nbInvites) }}
        </span>
      </div>
      <span class="voir-plus">Voir plus…</span>
    </button>

    <SgiltBottomSheet v-model:open="sheetOpen" :title="event.title" :overlay="true">
      <ProEventInfoMobile
        :event="event"
        :client-info="clientInfo"
        :last-update-date="lastUpdateDate"
        @open-journal="journalOpen = true"
      />
    </SgiltBottomSheet>
  </template>

  <!-- ── Partagé ────────────────────────────────────────────────────────────────── -->
  <EventJournal
    v-model:open="journalOpen"
    :journal="journalEntries"
    :has-more="journalHasMore"
    :loading="journalLoading"
    @load-more="loadJournalPage"
  />
</template>

<script setup lang="ts">
// Bloc d'informations d'un événement, en lecture seule, sur la fiche réservation côté prestataire.
import SgiltBottomSheet from '~/components/basics/sheets/SgiltBottomSheet.vue'
import EventJournal from '~/components/app/EventJournal.vue'
import ProEventInfoDesktop from '~/components/pro/ProEventInfoDesktop.vue'
import ProEventInfoMobile from '~/components/pro/ProEventInfoMobile.vue'
import { useEventJournal } from '~/data/evenement/useEventJournal'
import type { EventMeta } from '~/data/evenement/domain/EventMeta'
import type { ClientContactInfo } from '~/data/reservation/domain/ClientContactInfo'
import { CalendarEventIcon, MapPin2Icon, GroupIcon } from '@remixicons/vue/line'
import { useNbInvitesLabel } from '~/composables/useNbInvitesLabel'

const { nbInvitesLabel } = useNbInvitesLabel()

const props = defineProps<{
  event: EventMeta
  clientInfo: ClientContactInfo
}>()

const { isMobile, isDesktop } = useDevice()

const sheetOpen = ref(false)
const journalOpen = ref(false)

const {
  entries: journalEntries,
  hasMore: journalHasMore,
  loading: journalLoading,
  loadFirstPage: loadFirstJournalPage,
  loadNextPage: loadJournalPage,
} = useEventJournal(props.event.id)

const lastUpdateDate = computed((): string | null => {
  if (!props.event.lastUpdateDate) return null
  return `Dernière mise à jour : ${props.event.lastUpdateDate.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' })}`
})

watch(journalOpen, (open) => {
  if (open) loadFirstJournalPage()
})
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

  &:hover {
    background: $surface-soft;
  }
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
</style>
