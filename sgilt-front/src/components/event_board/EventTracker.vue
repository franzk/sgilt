<template>
  <div class="event-tracker">
    <!-- step boxes -->
    <StepBox
      v-for="(step, index) in steps"
      :key="index"
      :value="step.value"
      :label="step.label"
      :index="index"
      :pawns="pawns[index]"
      class="event-step-box"
    />

    <!-- final step -->
    <div v-if="showFinalStep" class="final-step">
      <p class="event-title">{{ sgiltEvent?.title }}</p>
      <p class="date-location">
        {{ dayjs(sgiltEvent?.dateTime).locale('fr').format('dddd DD MMM YYYY') }}
      </p>
      <p class="date-location">{{ sgiltEvent?.location }}</p>
      <IconStar class="icon-star" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import StepBox from '@/components/event_board/StepBox.vue'
import IconStar from '@/components/icons/IconStar.vue'
import dayjs from 'dayjs'
import { useReservationStepsStore } from '@/stores/reservation-steps.store'
import type { Reservation } from '@/data/domain/Reservation'
import { useEventStore } from '@/stores/event.store'

const eventStore = useEventStore()
const sgiltEvent = computed(() => eventStore.sgiltEvent)

defineProps<{
  showFinalStep: boolean
}>()

const reservationStepsStore = useReservationStepsStore()

const steps = ref(reservationStepsStore.steps)

const pawns = computed(() =>
  steps.value.map((step) =>
    sgiltEvent.value?.reservations
      .filter((reservation: Reservation) => reservation.status === step.value)
      .map((reservation: Reservation) => reservation.partner.imageUrl),
  ),
)
</script>

<style scoped lang="scss">
$step-height: 12rem;
$arrow-width: 3rem;
$step-spacing: 0.8rem;

// EventTracker container
.event-tracker {
  display: flex;
  flex-direction: row;

  padding: $spacing-l 0;
  position: relative;

  height: $step-height;
  padding-left: calc(($arrow-width - $step-spacing) * 2.4);

  background: linear-gradient(135deg, #f6f8fc 0%, #ebeff5 100%);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);

  // steps
  .event-step-box {
    flex: 1;
  }

  // final step
  .final-step {
    flex: 1;

    transform: translateX(calc(($arrow-width - $step-spacing) * -2));

    height: 75%;

    background: linear-gradient(135deg, #fff6e0 0%, #ffd700 100%);
    border-radius: 10px;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
    padding: 1.5rem;

    text-align: center;
    position: relative;

    .event-title {
      font-size: 1.2rem;
      line-height: 1.2;
      font-weight: 700;
      text-transform: uppercase;
      color: #333;
    }

    .date-location {
      font-size: 0.9rem;
      font-style: italic;
      color: $color-primary;
      font-weight: 400;
    }

    .icon-star {
      position: absolute;
      top: 0;
      bottom: 0;
      left: 0;
      right: 0;
      align-self: center;
      margin: auto;
      z-index: -1;
      border: 0;
      color: rgba(0, 0, 0, 0.09);
      opacity: 0.8;
      text-shadow:
        -2px -2px 3px rgba(255, 255, 255, 0.8),
        2px 2px 3px rgba(0, 0, 0, 0.15);
      filter: drop-shadow(1px 1px 2px rgba(0, 0, 0, 0.1));
      mix-blend-mode: multiply;
      transition: all 0.3s ease-in-out;
      backdrop-filter: blur(2px);
    }
  }
}
</style>
