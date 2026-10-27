<!--
  Carte récapitulative de l'événement (photo, titre, date, lieu) en tête des écrans du
  parcours d'achat de billets : commande, paiement, confirmation.
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
      <div class="meta-row">
        <MapPin2Icon class="icon" />
        <span>{{ event.venue }} · {{ event.city }}</span>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import SgiltImage from '~/components/basics/media/SgiltImage.vue'
import { CalendarEventIcon, MapPin2Icon } from '@remixicons/vue/line'
import type { PublicEvent } from '~/data/ticketing/domain/PublicEvent'
import { useEventHoursLabel } from '~/composables/useEventHoursLabel'
import { formatDateWithWeekday } from '~/utils/dateUtils'

const props = defineProps<{ event: PublicEvent }>()

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

    .icon {
      flex-shrink: 0;
      width: 1rem;
      height: 1rem;
      color: $text-secondary;
    }
  }
}
</style>
