<template>
  <div class="evenement">
    <!-- ── Header événement ───────────────────────────────────────────────────── -->
    <header class="event-header">
      <h1 class="title">{{ localEvent.title }}</h1>
      <div class="pills">
        <span v-if="localEvent.date" class="event-pill">
          <CalendarEventIcon class="icon" />{{ formatDate(localEvent.date) }}
        </span>
        <span v-if="localEvent.ville" class="event-pill">
          <MapPin2Icon class="icon" />{{ localEvent.ville }}
        </span>
        <span v-if="localEvent.nbInvites" class="event-pill">
          <GroupIcon class="icon" />{{ localEvent.nbInvites }}
        </span>
      </div>
    </header>

    <!-- ── Rubriques ──────────────────────────────────────────────────────────── -->
    <section class="rubriques">
      <h2 class="section-title">{{ $t('evenement.rubriques-title') }}</h2>
      <div class="list">
        <EventRubriqueItem
          v-for="rubrique in localEvent.rubriques"
          :key="rubrique.key"
          :rubrique="rubrique"
          @click="onRubriqueClick"
        />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { CalendarEventIcon, GroupIcon, MapPin2Icon } from '@remixicons/vue/line'
import EventRubriqueItem from '~/components/evenement/EventRubriqueItem.vue'
import { formatDate } from '~/utils/dateUtils'

definePageMeta({ layout: 'evenement' })

const { t } = useI18n()
useHead({ title: t('evenement.page-title') })

const { localEvent, initMariage } = useLocalEvent()

// Première arrivée : le preset Mariage est injecté dans le state local (pas d'appel réseau).
initMariage(t('evenement.default-title'))

function onRubriqueClick() {
  // Fiche détail rubrique : brief séparé, à venir.
  console.log('stay tuned')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.evenement {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: 0 $spacing-m $spacing-l;
  background-color: $brand-background-alt;

  @media (min-width: $breakpoint-desktop) {
    width: 100%;
    max-width: 45rem;
    margin: 0 auto;
    padding-bottom: $spacing-xl;
  }

  // ── Header événement ────────────────────────────────────────────────────────
  .event-header {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
    margin: 0 (-$spacing-m);
    padding: $spacing-m;
    background: $surface-white;
    box-shadow: 0 1px 4px $shadow-s;

    @media (min-width: $breakpoint-desktop) {
      margin: 0;
      padding: $spacing-xl $spacing-l $spacing-l;
      box-shadow: none;
      border-bottom: 1px solid $divider-color;
    }

    .title {
      margin: 0;
      font-family: 'Cormorant Garamond', serif;
      font-size: 2rem;
      font-weight: 600;
      line-height: 1.15;
      color: $brand-primary;
    }

    .pills {
      display: flex;
      flex-wrap: wrap;
      gap: 0.375rem;

      .event-pill {
        display: inline-flex;
        align-items: center;
        gap: 0.375rem;
        padding: 0.25rem 0.5rem;
        border-radius: 2rem;
        background: $surface-soft;
        border: 1px solid $divider-color;
        font-family: 'Inter', sans-serif;
        font-size: 0.75rem;
        font-weight: 500;
        color: $text-secondary;
        white-space: nowrap;

        .icon {
          width: 0.875rem;
          height: 0.875rem;
          flex-shrink: 0;
        }
      }
    }
  }

  // ── Rubriques ───────────────────────────────────────────────────────────────
  .rubriques {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;

    .section-title {
      margin: 0;
      font-family: 'Inter', sans-serif;
      font-size: 0.7rem;
      font-weight: 700;
      letter-spacing: 0.08em;
      text-transform: uppercase;
      color: $text-secondary;
    }

    .list {
      display: flex;
      flex-direction: column;
      gap: $spacing-s;
    }
  }
}
</style>
