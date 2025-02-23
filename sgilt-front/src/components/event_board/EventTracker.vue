<template>
  <div class="event-tracker">
    <StepBox
      v-for="(step, index) in steps"
      :key="index"
      :value="step.value"
      :label="step.label"
      :index="index"
      :pawns="pawns[index]"
      class="event-step-box"
    />
    <div class="final-step">
      <p class="event-title">{{ event?.title }}</p>
      <p class="date-location">
        {{ dayjs(event?.dateTime).locale('fr').format('dddd DD MMM YYYY') }}
      </p>
      <p class="date-location">{{ event?.location }}</p>
      <IconStar class="icon-star" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import StepBox from '@/components/event_board/StepBox.vue'
import type { SgiltEvent } from '@/data/domain/SgiltEvent'
import IconStar from '@/components/icons/IconStar.vue'
import dayjs from 'dayjs'

const props = defineProps<{
  event?: SgiltEvent
}>()

const steps = ref([
  { value: 'pending', label: 'Demande envoyée !' },
  { value: 'viewed', label: 'Votre partenaire a vu votre demande' },
  { value: 'approved', label: 'Il est prêt, à vous de payer !' },
  { value: 'paied', label: 'C’est réservé, préparez la fête !' },
])

const pawns = []
/* computed(() =>
  steps.value.map((step) =>
    props.event?.reservations
      .filter((reservation: Reservation) => reservation.status === step.value)
      .map((reservation: Reservation) => reservation.partner.imageUrl),
  ),
) */
</script>

<style scoped lang="scss">
$step-height: 12rem;
$arrow-width: 3rem;
$step-spacing: 0.8rem;

.event-tracker {
  display: flex;
  flex-direction: row;

  padding: $spacing-l 0;
  position: relative;

  height: $step-height;
  padding-left: calc(($arrow-width - $step-spacing) * 2.4);

  background: linear-gradient(135deg, #f6f8fc 0%, #ebeff5 100%);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);

  .event-step-box {
    flex: 1;
  }

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
      color: #555;
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
      color: rgba(0, 0, 0, 0.1);
      opacity: 0.8;
      text-shadow:
        -2px -2px 3px rgba(255, 255, 255, 0.8),
        2px 2px 3px rgba(0, 0, 0, 0.15);
      filter: drop-shadow(1px 1px 2px rgba(0, 0, 0, 0.1));
      mix-blend-mode: multiply;
      transition: all 0.3s ease-in-out;
      backdrop-filter: blur(2px);
    }

    // Option pour une icône en filigrane
    &::before {
      content: url('/icons/event.svg'); // Remplace par ton icône SVG
      position: absolute;
      top: 10px;
      left: 10px;
      width: 40px;
      height: 40px;
      opacity: 0.2;
    }
  }
}
</style>
