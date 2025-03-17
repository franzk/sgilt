<template>
  <div class="mini-dashboard">
    <div class="event-header">
      <h2 class="event-name">🎉 Mariage de Jean-Philippe et Marie-Catherine</h2>
      <p class="event-meta">📍 Dimanche 23 mars 2025 - Ratshamhausen</p>
    </div>

    <div class="dashboard-badges">
      <div class="badge success">✅ Confirmées <strong>3</strong></div>
      <div class="badge pending">⏳ En attente <strong>2</strong></div>
      <div class="badge danger">💳 Paiements <strong>1</strong></div>
    </div>

    <div class="cta-container" v-if="hasPaymentsPending">
      <SgiltButton class="pay-btn">⚠️ Payer Maintenant</SgiltButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'

defineProps<{ hasPaymentsPending: boolean }>()
</script>

<style scoped lang="scss">
.mini-dashboard {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 1.2rem;
  gap: 1rem;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  text-align: center;
  background:
    linear-gradient(to bottom, rgba(0, 0, 0, 0.05), transparent),
    url('@/assets/images/mobile/background.svg') center/cover no-repeat;
}

.event-header {
  text-align: center;
  .event-name {
    font-size: clamp(1.5rem, 4vw, 1.9rem);
    font-weight: bold;
    color: #fff;
    text-shadow: 0px 0px 5px rgba(255, 255, 255, 0.8);
    max-height: 3.6rem; /* Limite de 2 lignes */
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
  }
  .event-meta {
    font-size: 0.95rem;
    color: rgba(255, 255, 255, 0.8);
  }
}

.dashboard-badges {
  display: flex;
  justify-content: space-around;
  gap: 0.5rem;
}

.badge {
  flex: 1;
  padding: 0.8rem;
  border-radius: 30px;
  font-weight: bold;
  text-align: center;
  font-size: 0.9rem;
  background: rgba(255, 255, 255, 0.2);
  box-shadow: 0px 3px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  color: #fff;
}

.success {
  background: rgba(46, 204, 113, 0.25);
}

.pending {
  background: rgba(255, 193, 7, 0.25);
}

.danger {
  background: rgba(231, 76, 60, 0.25);
}

.cta-container {
  margin-top: 1rem;
  .pay-btn {
    background: #ffbf00;
    color: #fff;
    font-weight: bold;
    border-radius: 1rem;
    padding: 0.8rem 1.5rem;
    transition: all 0.3s ease-in-out;
  }

  .pay-btn:hover {
    background: #e0a800;
  }
}
</style>
