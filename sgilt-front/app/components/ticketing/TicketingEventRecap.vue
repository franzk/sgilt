<!--
  Carte récapitulative de l'événement (photo, titre, date, lieu) en tête des écrans du
  parcours d'achat de billets : commande, paiement, confirmation, et de l'e-billet. Avec `mapsUrl`
  (l'e-billet seulement), le lieu est un lien Google Maps.
-->
<template>
  <section class="recap-card">
    <SgiltImage class="thumb" :src="event.heroImage" :alt="event.title" />
    <div class="info">
      <p class="event-title">{{ event.title }}</p>
      <p class="event-subtitle">{{ event.subtitle }}</p>
      <div class="meta-row">
        <CalendarEventIcon class="icon" />
        <span>{{ dateLabel }}</span>
      </div>
      <a
        v-if="mapsUrl"
        :href="mapsUrl"
        target="_blank"
        rel="noopener"
        class="meta-row link"
        :aria-label="$t('ticketing.event.maps-link')"
      >
        <MapPin2Icon class="icon" />
        <span>{{ placeLabel(event.venue, event.city) }}</span>
        <ExternalLinkIcon class="external" aria-hidden="true" />
      </a>
      <div v-else class="meta-row">
        <MapPin2Icon class="icon" />
        <span>{{ placeLabel(event.venue, event.city) }}</span>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import SgiltImage from '~/components/basics/media/SgiltImage.vue'
import { CalendarEventIcon, ExternalLinkIcon, MapPin2Icon } from '@remixicons/vue/line'
import type { PublicEvent } from '~/data/ticketing/domain/PublicEvent'
import { useEventHoursLabel } from '~/composables/useEventHoursLabel'
import { formatDateWithWeekday } from '~/utils/dateUtils'
import { placeLabel } from '~/utils/placeUtils'

// Ce que la carte montre de l'événement : la fiche publique (PublicEvent) en est un, l'e-billet
// construit le sien à partir de la commande.
export type EventRecap = Pick<
  PublicEvent,
  'title' | 'subtitle' | 'heroImage' | 'date' | 'startsAt' | 'endsAt' | 'venue' | 'city'
>

const props = defineProps<{ event: EventRecap; mapsUrl?: string }>()

const { eventHoursLabel } = useEventHoursLabel()

// « Jeudi 12 mars 2026 · 19h – 2h », ou la date seule sans horaires.
const dateLabel = computed(() => {
  const date = formatDateWithWeekday(props.event.date)
  const hours = eventHoursLabel(props.event.startsAt, props.event.endsAt)
  return hours ? `${date} · ${hours}` : date
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.recap-card {
  display: flex;
  gap: $spacing-m;
  padding: $spacing-m;
  background: $surface-soft;
  border-radius: $radius-lg;

  .thumb {
    flex-shrink: 0;
    width: 5rem;
    height: 5rem;
    border-radius: $radius-md;
    overflow: hidden;
  }

  .info {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 0.2rem;
  }

  .event-title {
    margin: 0;
    font-size: $font-size-lg;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }

  .event-subtitle {
    margin: 0 0 0.2rem;
    font-size: $font-size-sm;
    color: $text-secondary;
  }

  .meta-row {
    display: flex;
    align-items: center;
    gap: 0.4rem;
    font-size: $font-size-sm;
    color: $text-primary;

    .icon,
    .external {
      flex-shrink: 0;
      width: 1rem;
      height: 1rem;
      color: $text-secondary;
    }

    &.link {
      text-decoration: none;

      &:hover span {
        text-decoration: underline;
      }
    }
  }
}
</style>
