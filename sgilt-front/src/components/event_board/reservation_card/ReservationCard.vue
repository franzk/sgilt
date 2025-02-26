<template>
  <div class="reservation-card">
    <ReservationCardTitle :partner="reservation?.partner" />

    <ReservationCardStatusBanner :statusKey="props.reservation?.status" />

    <div class="bottom-section">
      <ReservationCardPriceZone
        :price="reservation?.price"
        :quantity="reservation?.quantity"
        :totalPrice="reservation?.totalPrice"
        class="reservation-price"
      />

      <ReservationCardActions
        :showPayButton="showPayButton"
        :canCancel="canCancel"
        @pay="pay"
        @message="message"
        @cancel="cancel"
      />

      <!--div class="reservation-actions">
        <button v-if="showPayButton" @click="pay" class="pay-btn">💳 Payer</button>
        <button @click="message" class="message-btn">💬 Message</button>
        <button v-if="canCancel" @click="cancel" class="cancel-btn">🛑 Annuler</button>
      </!--div-->
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { Reservation } from '@/data/domain/Reservation'
import { useReservationStatusStore } from '@/stores/reservation-status.store'
import ReservationCardTitle from '@/components/event_board/reservation_card/ReservationCardTitle.vue'
import ReservationCardStatusBanner from '@/components/event_board/reservation_card/ReservationCardStatusBanner.vue'
import ReservationCardPriceZone from '@/components/event_board/reservation_card/ReservationCardPriceZone.vue'
import ReservationCardActions from '@/components/event_board/reservation_card/ReservationCardActions.vue'

const props = defineProps<{
  reservation?: Reservation
}>()

const reservationStatusStore = useReservationStatusStore()
const statusColor = computed(() => reservationStatusStore.getStatusColor(props.reservation?.status))

const showPayButton = computed(() => props.reservation?.status === 'approved')
const canCancel = computed(() =>
  ['pending', 'viewed', 'approved'].includes(props.reservation?.status || ''),
)

const pay = () => {
  console.log('Paiement en cours...')
}

const message = () => {
  console.log('Ouvrir la conversation...')
}

const cancel = () => {
  console.log('Annulation en cours...')
}
</script>

<style scoped lang="scss">
$status-color: v-bind(statusColor);

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
  border-radius: $border-radius-m;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  text-align: center;
  transition: 0.3s ease-in-out;
  border-left: 5px solid transparent;
  border-left-color: $status-color;
}

.bottom-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
  padding: 0 $spacing-s $spacing-s $spacing-s;
}

.reservation-price {
  flex: 1;
}

.reservation-actions {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
}

button {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: $border-radius-s;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  font-weight: bold;
}

.pay-btn {
  // background: $color-accent;
  // color: $color-white;
  background-color: #f4b400; /* Jaune */
  color: #fff;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
  transition:
    background-color 2s ease-in-out,
    transform 2s ease-in-out;

  &:hover {
    background-color: #e59400; /* Jaune plus foncé */
    transform: translateY(-2px); /* Léger effet de survol */
  }
}

.message-btn {
  background: $color-primary;
  color: $color-white;
}

.cancel-btn {
  background: $color-error;
  color: $color-white;
}
</style>
