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
      <CircleProgressBar :value="confirmedReservationsCount" :max="reservationsCount" size="3rem">{{
        confirmedReservationsCount
      }}</CircleProgressBar>
      <p>
        {{ $t('event.event-board.mobile.reservations', confirmedReservationsCount) }}
      </p>
    </div>

    <div class="stat-cards">
      <StatCard
        icon="Check"
        label="Confirmées"
        :value="confirmedReservationsCount"
        state="success"
      />
      <StatCard
        icon="Hourglass"
        label="En attente"
        :value="pendingReservationsCount"
        state="pending"
      />
      <StatCard
        icon="CreditCard"
        label="Paiement à faire"
        :value="toBePaidReservationsCount"
        state="danger"
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

const reservationsCount = computed(() => sgiltEvent.value?.reservations.length)
const confirmedReservationsCount = computed(() => reservationsCountByStatus('paid'))
const pendingReservationsCount = computed(
  () => reservationsCountByStatus('pending') + reservationsCountByStatus('viewed'),
)
const toBePaidReservationsCount = computed(() => reservationsCountByStatus('approved'))

const hasPendingReservations = computed(() => toBePaidReservationsCount.value > 0)

const reservationsCountByStatus = (status: ReservationStatusKey): number =>
  sgiltEvent.value?.reservations.filter((r) => r.status === status).length || 0
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
