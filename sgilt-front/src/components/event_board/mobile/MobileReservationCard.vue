<template>
  <div
    class="reservation-card"
    :class="{ expanded: isExpanded }"
    @click="toggleExpand"
    :style="statusStyle"
  >
    <div class="reservation-header">
      <img
        :src="reservation.partner.imageUrl"
        class="reservation-avatar"
        alt="Avatar du prestataire"
      />
      <div class="reservation-info">
        <h3 class="provider-name">{{ reservation.partner.title }}</h3>
        <span class="reservation-price">{{ reservation.price.price }}€</span>
      </div>
      <span class="reservation-status">{{ reservation.status }}</span>
      <span class="chevron">{{ isExpanded ? '▲' : '▼' }}</span>
    </div>

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
</template>

<script setup lang="ts">
import { useReservationStatusColor } from '@/composable/useReservationStatusColor'
import type { Reservation } from '@/data/domain/Reservation'
import { computed, ref } from 'vue'

const props = defineProps<{ reservation: Reservation }>()

const reservationStatusColor = useReservationStatusColor()
const statusStyle = computed(() =>
  reservationStatusColor.statusColorStyle({
    cssParameter: 'background-color',
    statusKey: props.reservation?.status,
    startTime: props.reservation?.createdAt,
    opacity: 0.5,
  }),
)

const isExpanded = ref(false)
const toggleExpand = () => {
  isExpanded.value = !isExpanded.value
}
</script>

<style scoped lang="scss">
.reservation-card {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  padding: 1rem;
  margin-bottom: 1rem;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
}

.reservation-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.reservation-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.provider-name {
  font-weight: bold;
}

.reservation-status {
  padding: 5px 10px;
  border-radius: 5px;
  font-size: 0.9rem;
  font-weight: bold;
}

.status-sent {
  background: #ffb347;
  color: #fff;
}
.status-pending {
  background: #ff9800;
  color: #fff;
}
.status-approved {
  background: #d9534f;
  color: #fff;
}
.status-paid {
  background: #28a745;
  color: #fff;
}
.status-cancelled {
  background: #343a40;
  color: #fff;
}

.chevron {
  font-size: 1.2rem;
  font-weight: bold;
}

.reservation-details {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #ddd;
  font-size: 0.9rem;
}
</style>
