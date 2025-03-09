<template>
  <div class="event-summary">
    <div class="event-info">
      <h2>
        <span class="event-name">{{ title }}</span>
      </h2>
      <p class="event-date">
        <SgiltIcon icon="Event" />
        <span class="event-date-date">{{ formattedDate }}</span> - {{ location }}
      </p>
    </div>

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

    <SgiltButton v-if="reservationsWaitingForPayment > 0" class="pay-button"
      ><SgiltIcon icon="Warning" /> Payer maintenant</SgiltButton
    >
  </div>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'
import { computed, defineProps } from 'vue'
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'

const props = defineProps<{
  title?: string
  date?: Date
  location?: string
  reservationsConfirmed: number
  reservationsWaitingForPayment: number
  reservationsWaiting: number
}>()

// Format date helper
const formattedDate = computed(() => {
  if (!props.date) return ''
  return dayjs(props.date).locale('fr').format('dddd DD MMM YYYY')
})
</script>

<style scoped lang="scss">
.event-summary {
  background: $color-white;
  border-radius: $border-radius-s;
  padding: $spacing-m;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: $spacing-s;

  .event-info {
    h2 {
      font-size: 1.6rem;
      font-weight: 800;
      text-transform: uppercase;
      color: #222; // Contraste max
      margin-bottom: $spacing-s;

      .event-name {
        background: linear-gradient(45deg, #ff8c00, #ff2e63);
        -webkit-background-clip: text;
        background-clip: text;
        -webkit-text-fill-color: transparent;
      }
    }

    .event-date {
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
