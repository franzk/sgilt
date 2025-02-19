<template>
  <div class="event-board">
    <div class="col-left">
      <div
        class="reservation-cards"
        v-for="reservation in sgiltEvent?.reservations"
        :key="reservation.id"
      >
        <ReservationCard :reservation="sgiltEvent?.reservations[0]" />
      </div>
    </div>
    <div class="col-middle">
      <EventMainCard
        :eventTitle="sgiltEvent?.title"
        :location="sgiltEvent?.location"
        :date="sgiltEvent?.dateTime"
      />
    </div>
    <div class="col-right">&nbsp;</div>
  </div>
</template>

<script setup lang="ts">
import EventMainCard from '@/components/event_board/EventMainCard.vue'
import ReservationCard from '@/components/event_board/ReservationCard.vue'
import type { SgiltEvent } from '@/data/domain/SgiltEvent'
import { getTestEvent } from '@/data/repository/EventRepository'
import { onMounted, ref } from 'vue'

const sgiltEvent = ref<SgiltEvent>()

onMounted(async () => {
  sgiltEvent.value = await getTestEvent()
})
</script>

<style scoped lang="scss">
.event-board {
  height: 100%;
  display: flex;
  flex-direction: row;
  align-items: center;
  background: #f9f9f9;
  background-image: repeating-linear-gradient(45deg, rgba(0, 0, 0, 0.02) 2px, transparent 4px),
    repeating-linear-gradient(-45deg, rgba(0, 0, 0, 0.02) 2px, transparent 4px);

  .col-left {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  .col-middle {
    flex: 1;
    display: flex;
    justify-content: center;
  }

  .col-right {
    flex: 1;
  }
}

.event-widgets {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}
</style>
