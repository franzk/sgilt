<template>
  <div class="mini-dashboard">
    <p class="dashboard-header">
      <p>Votre événement est en préparation !</p>
      <p><strong>3 sur 6 prestataires</strong> sont déjà confirmés.</p>
    </p>

    <StatCard icon="Check" label="Confirmées" :value="3" class="success" />
    <StatCard icon="Hourglass" label="En attente" :value="2" class="pending" />
    <StatCard icon="CreditCard" label="Paiement à faire" :value="1" class="danger" />

    <div class="cta-container" v-if="hasPaymentsPending">
      <SgiltButton  class="pay-btn">⚠️ Payer maintenant</SgiltButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import StatCard from '@/components/event_board/mobile/StatCard.vue'
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'

defineProps<{ hasPaymentsPending: boolean }>()
</script>

<style scoped lang="scss">
.mini-dashboard {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.dashboard-header {
  display: flex;
  flex-direction: column;
  border-radius: 14px;
  background: rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  box-shadow: $box-shadow;
}

.cta-container {
  margin-top: 1rem;
  display: flex;
  justify-content: center;
}

.pay-btn {
  background: linear-gradient(45deg, #ff4d4f, #ff6a6a);
  padding: 1rem;
  box-shadow: 0px 4px 12px rgba(255, 0, 0, 0.2); // Adoucir l'ombre
  transition: all 0.3s ease-in-out;
}
</style>
