<template>
  <div class="reservation-card">
    <h3 class="partner-title">
      <img :src="reservation?.partner?.imageUrl" class="partner-image" />
      <span class="partner-name">{{ reservation?.partner?.title }}</span>
    </h3>

    <div class="reservation-status">
      <div class="status-wrapper">
        <p class="status-state">✔️ {{ status?.state }}</p>
        <p class="status-action">
          <span><SgiltIcon v-if="status" :icon="status?.icon" /></span>
          <span class="status-action-label">{{ status?.action }}</span>
        </p>
      </div>
      <p class="status-subtext">{{ status?.subtext }}</p>
    </div>

    <div class="bottom-section">
      <div class="reservation-price">
        <PriceItem v-if="reservation?.price.title" :label="reservation.price.title" icon="Star" />
        <PriceItem
          v-if="reservation?.quantity"
          :label="`${reservation.quantity} ${reservation.price.unity}`"
          icon="Profile"
        />
        <PriceItem
          v-if="reservation?.quantity && reservation?.price.price"
          :label="`${reservation.price.price.toString()} / ${reservation.price.unity}`"
          icon="More"
        />
        <PriceItem
          v-if="reservation?.totalPrice"
          :label="reservation.totalPrice.toString()"
          icon="Price"
        />
      </div>

      <div class="reservation-actions">
        <button v-if="showPayButton" @click="pay" class="pay-btn">💳 Payer</button>
        <button @click="message" class="message-btn">💬 Message</button>
        <button v-if="canCancel" @click="cancel" class="cancel-btn">🛑 Annuler</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'
import type { Reservation } from '@/data/domain/Reservation'
import { useReservationStatusStore } from '@/stores/reservation-status.store'
import PriceItem from './PriceItem.vue'

const props = defineProps<{
  reservation?: Reservation
}>()

const reservationStatusStore = useReservationStatusStore()

const status = computed(() => reservationStatusStore.getStatus(props.reservation?.status))
const statusColor = computed(() => status.value?.color || 'transparent')

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
  //width: 100%;
  gap: $spacing-m;
  background: $color-white;
  // padding: $spacing-m;
  border-radius: $border-radius-m;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  // width: 250px;
  text-align: center;
  transition: 0.3s ease-in-out;
  border-left: 5px solid transparent;
  border-left-color: $status-color;
  // > * {
  //  width: 100%;
  // }
}

.reservation-status {
  display: flex;
  flex-direction: column;
  position: relative;
  gap: $spacing-s;

  .status-wrapper {
    display: flex;
    flex-direction: column;
    position: relative;
    align-items: start;
    text-align: left;
    width: 100%;
    box-shadow: $box-shadow;

    .status-state {
      font-weight: 500;
      color: $color-primary;
      line-height: 1.5;
      padding: $spacing-xs $spacing-s 0 $spacing-s;
    }

    .status-action {
      display: flex;
      flex-direction: row;
      align-items: center;
      padding: 0 $spacing-s;
      gap: 0.5rem;
      font-weight: 700;
      svg {
        height: 2rem;
        aspect-ratio: 1;
      }
      .status-action-label {
        line-height: 1.1;
      }
    }

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: $status-color;
      opacity: 0.5;
    }
    * > * {
      z-index: 0;
    }
  }
}

.status-subtext {
  // text-align: left;
  font-style: italic;
  padding: 0 $spacing-s;
}

.partner-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: $spacing-s $spacing-s 0 $spacing-s;
  margin: 0;
  .partner-image {
    height: 3rem;
    aspect-ratio: 1;
    border-radius: 100%;
    object-fit: cover;
  }
}

.bottom-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
  padding: 0 $spacing-s $spacing-s $spacing-s;
}

.reservation-price {
  // display: grid;
  // grid-template-columns: repeat(auto-fit, minmax(100%, 1fr));
  /*flex: 1;
  display: flex;
  flex-direction: column;

  gap: 0.5rem;
  padding: 1rem;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1); */
  // background: rgba(0, 0, 0, 0.05); // Fond léger, ajustable
  // border-radius: 8px;
  // padding: 10px 15px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
  font-size: $font-size-base;

  background: rgba(255, 255, 255, 0.6); // Un fond très léger, plus doux que le gris
  border: 1px solid rgba(0, 0, 0, 0.08); // Une bordure subtile pour structurer
  border-radius: 8px;
  padding: 10px 15px;
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
