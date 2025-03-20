<template>
  <div class="reservation-card" :style="statusStyle">
    <!-- Title -->
    <ReservationCardTitle :partner="reservation?.partner" :compactMode="compactMode" />

    <!-- Status banner -->
    <ReservationCardStatusBanner
      :statusKey="reservation?.status"
      :createdAt="reservation?.createdAt"
    />

    <div class="bottom-section">
      <!-- Price zone -->
      <ReservationCardPriceZone
        :price="reservation?.price"
        :quantity="reservation?.quantity"
        :totalPrice="reservation?.totalPrice"
        :compactMode="compactMode"
        class="reservation-price"
      />

      <!-- Buttons -->
      <ReservationCardActions
        v-if="!compactMode"
        :showPayButton="showPayButton"
        :canCancel="canCancel"
        @pay="pay"
        @message="message"
        @cancel="cancel"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { Reservation } from '@/data/domain/Reservation'
import { useReservationStatusColor } from '@/composable/useReservationStatusColor.ts'
import ReservationCardTitle from '@/components/event_board/reservation_card/ReservationCardTitle.vue'
import ReservationCardStatusBanner from '@/components/event_board/reservation_card/ReservationCardStatusBanner.vue'
import ReservationCardPriceZone from '@/components/event_board/reservation_card/ReservationCardPriceZone.vue'
import ReservationCardActions from '@/components/event_board/reservation_card/ReservationCardActions.vue'

const props = defineProps<{
  reservation?: Reservation
  compactMode?: boolean
}>()

// Reservation status color
/*const reservationStatusStore = useReservationStatusStore()
const statusColor = computed(() =>
  props.reservation?.status ? reservationStatusStore.getStatusColor(props.reservation?.status) : '',
)*/

const reservationStatusColor = useReservationStatusColor()
const statusStyle = computed(() =>
  reservationStatusColor.statusColorStyle(
    'border-left-color',
    props.reservation?.status,
    props.reservation?.createdAt,
  ),
)

// Action buttons
const showPayButton = computed(() => props.reservation?.status === 'approved')
const canCancel = computed(() =>
  ['pending', 'viewed', 'approved'].includes(props.reservation?.status || ''),
)

const pay = () => console.log('Paiement en cours...')

const message = () => console.log('Ouvrir la conversation...')

const cancel = () => console.log('Annulation en cours...')
</script>

<style scoped lang="scss">
// $status-color: v-bind(statusColor);

p {
  margin: 0;
  padding: 0;
}

.reservation-card {
  display: flex;
  flex-direction: column;
  position: relative;
  gap: $spacing-m;
  background: $color-white;
  border-radius: $border-radius-s;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  text-align: center;
  transition: 0.3s ease-in-out;
  border-left: 5px solid transparent;
  // border-left-color: $status-color;
  padding-bottom: $spacing-m;

  .bottom-section {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
    padding: $spacing-m $spacing-m 0 $spacing-m;

    .reservation-price {
      flex: 1;
    }

    .reservation-actions {
      display: flex;
      justify-content: center;
      gap: 0.5rem;
    }
  }
}
</style>
