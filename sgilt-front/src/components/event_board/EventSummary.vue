<template>
  <div class="event-summary">
    <!-- Event details -->
    <div v-if="showEventDetails" class="event-info">
      <h2>
        <span class="event-name">{{ sgiltEvent?.title || '' }}</span>
      </h2>
      <p class="event-date">
        <SgiltIcon icon="Event" />
        <span class="event-date-date">{{ formattedDate }}</span>
      </p>
      <p class="event-location">
        <SgiltIcon icon="Place" />
        <span>{{ sgiltEvent?.location || '' }} </span>
      </p>
    </div>

    <!-- Event title if details are not shown -->
    <div v-else>
      <h3>{{ $t('texts.your-event') }}</h3>
    </div>

    <!-- Event status -->
    <div class="event-status">
      <p class="status-item confirmed">
        <SgiltIcon icon="Check" /> {{ reservationsConfirmed }} réservation(s) confirmée(s)
      </p>
      <p class="status-item waiting" v-if="reservationsWaiting > 0">
        <SgiltIcon icon="Progress" /> {{ reservationsWaiting }} en attente d’acceptation
      </p>
      <p class="status-item pending" v-if="reservationsWaitingForPayment > 0">
        <SgiltIcon icon="Price" /> {{ reservationsWaitingForPayment }} en attente de paiement
      </p>
    </div>

    <!-- Pay button -->
    <SgiltButton v-if="reservationsWaitingForPayment > 0" class="pay-button"
      ><SgiltIcon icon="Warning" />{{ $t('texts.pay-now') }}</SgiltButton
    >
  </div>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'
import { computed, defineProps } from 'vue'
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'
import type { SgiltEvent } from '@/data/domain/SgiltEvent'

const props = defineProps<{
  sgiltEvent?: SgiltEvent
  showEventDetails?: boolean
}>()

const formattedDate = computed(() =>
  props.sgiltEvent?.dateTime
    ? dayjs(props.sgiltEvent.dateTime).locale('fr').format('dddd DD MMM YYYY')
    : '',
)

const reservationsConfirmed = computed(
  () => props.sgiltEvent?.reservations.filter((r) => r.status === 'paid').length || 0,
)

const reservationsWaitingForPayment = computed(
  () => props.sgiltEvent?.reservations.filter((r) => r.status === 'approved').length || 0,
)

const reservationsWaiting = computed(
  () =>
    props.sgiltEvent?.reservations.filter((r) => ['pending', 'viewed'].includes(r.status)).length ||
    0,
)
</script>

<style scoped lang="scss">
.event-summary {
  background: $color-white;
  padding: $spacing-m;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  box-shadow: $box-shadow;

  p {
    margin: 0;
  }

  .event-info {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;

    h2 {
      font-size: 1.6rem;
      font-weight: 800;
      text-transform: uppercase;
      color: #222; // Contraste max

      .event-name {
        background: linear-gradient(45deg, #ff8c00, #ff2e63);
        -webkit-background-clip: text;
        background-clip: text;
        -webkit-text-fill-color: transparent;
      }
    }

    .event-date,
    .event-location {
      font-size: 1rem;
      line-height: 1.5;
      color: $color-subtext;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: $spacing-s;

      svg {
        width: 1.2rem;
        aspect-ratio: 1;
      }

      .event-date-date {
        font-weight: bold;
        color: $color-primary;
      }
    }
  }

  .event-status {
    .status-item {
      font-size: 1rem;
      padding: $spacing-xs 0;
      font-weight: bold;
      display: flex;
      gap: $spacing-s;
      align-items: center;

      svg {
        width: 1rem;
        aspect-ratio: 1;
      }
    }
    .confirmed {
      color: #28a745;
    }
    .pending {
      color: #ff9800;
    }
    .waiting {
      color: #007bff;
    }
  }
}
.pay-button {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: $spacing-s;
  svg {
    width: 1.2rem;
    aspect-ratio: 1;
  }
}
</style>
