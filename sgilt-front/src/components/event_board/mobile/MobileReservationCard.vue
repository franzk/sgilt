<template>
  <div
    class="reservation-card"
    :class="{ expanded: isExpanded }"
    @click="toggleExpand"
    :style="statusStyle"
  >
    <div class="reservation-card-body">
      <div class="reservation-header">
        <img
          :src="reservation.partner.imageUrl"
          class="reservation-avatar"
          alt="Avatar du prestataire"
        />
        <div class="partner-name">{{ reservation.partner.title }}</div>
        <div class="chevron">{{ isExpanded ? 'A' : 'V' }}</div>
      </div>
      <div class="second-line">{{ status?.action }}</div>

      <div v-if="isExpanded" class="reservation-details">
        <!--p>📅 **Historique des statuts**</!--p>
      <p>🕒 Demande envoyée : {{ reservation.sentDate }}</p>
      <p v-if="reservation.approvedDate">✅ Acceptée : {{ reservation.approvedDate }}</p>
      <p v-if="reservation.paymentDeadline">
        💳 Paiement dû avant : {{ reservation.paymentDeadline }}
      </p>

      <p-- v-if="reservation.notes">📝 Notes : {{ reservation.notes }}</p-->

        <p>💸 **Détails du prix**</p>
        <p>{{ reservation.price }}</p>
      </div>
    </div>
    <!--div class="chevron">{{ isExpanded ? 'A' : 'V' }}</!--div-->
  </div>
</template>

<script setup lang="ts">
import type { Reservation } from '@/data/domain/Reservation'
import { useReservationStatusStore } from '@/stores/reservation-status.store'
import { computed, ref } from 'vue'

const props = defineProps<{ reservation: Reservation }>()

const reservationStatusStore = useReservationStatusStore()
const status = computed(() =>
  props.reservation?.status
    ? reservationStatusStore.getStatus(props.reservation?.status)
    : undefined,
)
const statusStyle = computed(() => ({
  ...reservationStatusStore.statusColorStyle({
    cssParameter: 'border-color',
    statusKey: props.reservation?.status,
    startTime: props.reservation?.createdAt,
    opacity: 1,
  }),
  ...reservationStatusStore.statusColorStyle({
    cssParameter: 'background-color',
    statusKey: props.reservation?.status,
    startTime: props.reservation?.createdAt,
    opacity: 0.2,
  }),
}))

const isExpanded = ref(false)
const toggleExpand = () => {
  isExpanded.value = !isExpanded.value
}
</script>

<style scoped lang="scss">
$header-height: 2rem;

.reservation-card {
  display: flex;
  flex-direction: row;
  gap: $spacing-m;
  position: relative;
  // border-radius: 10px;
  border: 1px solid;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  padding: 1rem;
  margin-bottom: 1rem;
  cursor: pointer;
  transition: all 0.3s ease-in-out;

  .reservation-card-body {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
  }
}

.reservation-header {
  display: flex;
  align-items: center;
  justify-content: start;
  gap: $spacing-m;

  height: $header-height;
}

.reservation-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.partner-name {
  font-weight: 700;
  @include multiline-ellipsis(1em, 2);
  margin: 0;
  font-size: 1rem;
}

.chevron {
  display: flex;
  margin-top: calc($header-height - $spacing-s);

  font-size: 1rem;
  transform: rotate(0deg);
  transition: transform 0.3s ease-in-out;
}

.second-line {
  display: block;
  text-align: left;
}

.reservation-status {
  font-weight: 600;
}

.reservation-details {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #ddd;
  font-size: 0.9rem;
}
</style>
