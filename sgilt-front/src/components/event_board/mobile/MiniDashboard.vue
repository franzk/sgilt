<template>
  <div class="mini-dashboard">
    <div class="event-description">
      <p>📍 Ratshamausen, 🗓️ 21/06/2à48 à 20h</p>
    </div>

    <div class="dashboard-header">
      <CircleProgressBar :value="3" :max="6" size="3rem">3</CircleProgressBar>
      <p><strong>3 sur 6 prestataires</strong> sont déjà confirmés.</p>
    </div>

    <div class="stat-cards">
      <StatCard icon="Check" label="Confirmées" :value="3" state="success" />
      <StatCard icon="Hourglass" label="En attente" :value="2" state="pending" />
      <StatCard icon="CreditCard" label="Paiement à faire" :value="1" state="danger" />
    </div>

    <div class="pay-now-container">
      <a href="#" class="pay-now-link">Payer maintenant</a>
    </div>
  </div>
</template>

<script setup lang="ts">
import StatCard from '@/components/event_board/mobile/StatCard.vue'
import { CircleProgressBar } from 'circle-progress.vue'

defineProps<{ hasPaymentsPending: boolean }>()
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

  background: rgba(0, 0, 0, 0.1);
}

.event-description {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
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
  font-weight: 700;
  color: #fff;
  background: linear-gradient(90deg, #ff4d4f, #e74c3c);
  border-radius: 20px;
  padding: 0.8rem 1.5rem;
  font-size: 1rem;
  text-decoration: none;
  box-shadow: 0px 4px 10px rgba(231, 76, 60, 0.4);
  transition:
    transform 0.2s ease-in-out,
    box-shadow 0.2s ease-in-out;
}

.pay-now-link:hover {
  transform: scale(1.05);
  box-shadow: 0px 6px 14px rgba(231, 76, 60, 0.5);
}

.pay-now-link::before {
  content: '⚠️ ';
  font-size: 1.2rem;
  margin-right: 0.5rem;
}
</style>
