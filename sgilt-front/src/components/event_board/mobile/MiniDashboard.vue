<template>
  <div class="mini-dashboard">
    <div class="event-description">
      <div class="event-icon"><SgiltIcon icon="Event" mobile /></div>
      <div class="event-details">
        <p>{{ sgiltEvent?.location }}</p>
        <p>{{ dayjs(sgiltEvent?.dateTime).locale('fr').format('dddd DD MMM YYYY') }}</p>
      </div>
    </div>

    <div class="dashboard-header">
      <CircleProgressBar
        :value="confirmedReservationsCount"
        :max="reservationsCount"
        size="3rem"
        colorFilled="#4CAF50"
        >{{ confirmedReservationsCount }}</CircleProgressBar
      >
      <p>
        {{ $t('event.event-board.mobile.reservations', confirmedReservationsCount) }}
      </p>
    </div>

    <div class="stat-cards">
      <StatCard
        v-for="group in groupedReservations"
        :key="group.id"
        :statusKeyStyle="group.statusKeyStyle"
        :label="group.label"
        :icon="group.icon"
        :value="group.count"
      />
    </div>

    <div class="pay-now-container" v-if="hasPendingReservations">
      <a href="#" class="pay-now-link"
        ><SgiltIcon icon="Launch" mobile /><span>{{ $t('texts.pay-now') }}</span></a
      >
    </div>
  </div>
</template>

<script setup lang="ts">
import StatCard from '@/components/event_board/mobile/StatCard.vue'
import { CircleProgressBar } from 'circle-progress.vue'
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'
import { computed } from 'vue'
import dayjs from 'dayjs'
import type { ReservationStatusKey } from '@/types/ReservationStatus'
import { useEventStore } from '@/stores/event.store'

const eventStore = useEventStore()
const sgiltEvent = computed(() => eventStore.sgiltEvent)

/**
 * Stat cards configuration for the mini dashboard
 * id: unique identifier
 * statusKeyStyle: status key to get the style from the reservation status store
 * label: label to display in the stat card
 * icon: icon to display in the stat card
 * count: number of reservations to display in the stat card
 */
const groupedReservations = computed(() => [
  {
    id: 'confirmed',
    statusKeyStyle: 'paid' as ReservationStatusKey,
    label: 'Confirmées',
    icon: 'Check',
    count: reservationsCountByStatus('paid'),
  },
  {
    id: 'pending',
    statusKeyStyle: 'pending' as ReservationStatusKey,
    label: 'En attente',
    icon: 'Hourglass',
    count: reservationsCountByStatus('pending') + reservationsCountByStatus('viewed'),
  },
  {
    id: 'toBePaid',
    statusKeyStyle: 'approved' as ReservationStatusKey,
    label: 'Paiement à faire',
    icon: 'CreditCard',
    count: reservationsCountByStatus('approved'),
  },
])

// total count of reservations
const reservationsCount = computed(() => eventStore.reservationsCount)

// count of confirmed reservations
const confirmedReservationsCount = computed(
  () => groupedReservations.value.find((g) => g.id === 'confirmed')?.count || 0,
)

// check if there are pending reservations to pay
const hasPendingReservations = computed(
  () => (groupedReservations.value.find((g) => g.id === 'toBePaid')?.count || 0) > 0,
)

// count of reservations by status
const reservationsCountByStatus = (status: ReservationStatusKey): number =>
  eventStore.reservationsCountByStatus(status)
</script>

<style scoped lang="scss">
p {
  margin: 0;
}
.mini-dashboard {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
}

.dashboard-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-s;
  padding: $spacing-s;

  font-weight: 600;

  background: rgba(0, 0, 0, 0.1);
}

.event-description {
  display: flex;
  flex-direction: row;
  gap: $spacing-m;
  .event-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, 0.1);
    border-radius: 50%;
    padding: $spacing-s;
  }
  .event-details {
    display: flex;
    flex-direction: column;
    align-items: start;
    gap: $spacing-s;
  }
}

.stat-cards {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}

.pay-now-container {
  text-align: center;
  margin-top: 1rem;
}

.pay-now-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: #fff;
  background: linear-gradient(90deg, #ff4d4f, #e74c3c);
  border-radius: 50px;
  padding: 12px 24px;
  text-transform: uppercase;
  font-size: 1rem;
  text-decoration: none;
  box-shadow: 0px 4px 10px rgba(231, 76, 60, 0.4);
  transition: box-shadow 0.2s ease-in-out;

  span {
    margin-left: $spacing-xs;
  }
}
</style>
