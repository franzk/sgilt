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
        <span>{{ dateLabel }} · {{ timeLabel }}</span>
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
import type { TicketingMockEvent } from '~/data/ticketing/mockEvent'
import { formatDateWithWeekday, formatTimeOfDay } from '~/utils/dateUtils'

const props = defineProps<{ event: TicketingMockEvent }>()

const { t } = useI18n()

const dateLabel = computed(() => formatDateWithWeekday(props.event.startsAt))
const timeLabel = computed(() =>
  t('ticketing.event.time-label', {
    start: formatTimeOfDay(props.event.startsAt),
    end: formatTimeOfDay(props.event.endsAt),
  }),
)
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
