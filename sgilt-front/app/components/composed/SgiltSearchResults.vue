<script setup lang="ts">
import type { PrestataireCardDetail } from '~/types/prestataire'
import PrestataireCard from '~/components/cards/PrestataireCard.vue'

defineProps<{
  results: PrestataireCardDetail[]
  loading: boolean
  error?: string | null
}>()
</script>

<template>
  <div class="results-section">
    <div v-if="loading" class="grid-container">
      <div v-for="i in 4" :key="i" class="skeleton-card" />
    </div>

    <div v-else-if="error" class="status-message error">
      {{ error }}
    </div>

    <div v-else-if="results.length === 0" class="status-message">
      Aucun prestataire trouvé pour ces critères.
    </div>

    <div v-else class="grid-container">
      <PrestataireCard v-for="provider in results" :key="provider.id" :provider="provider" />
    </div>
  </div>
</template>

<style scoped lang="scss">
.results-section {
  padding: 1rem;
  background-color: #f7f9fb;
}

.grid-container {
  display: grid;
  // Le secret : auto-fill avec un min-width
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 1.5rem 1rem;
  // padding: 1rem;

  // Sur très petits écrans ( < 350px ), on passe en 1 colonne pour laisser respirer la typo
  @media (max-width: 350px) {
    grid-template-columns: 1fr;
  }
}

.status-message {
  text-align: center;
  padding: 3rem 1rem;
  color: #64748b;
  &.error {
    color: #ef4444;
  }
}

.skeleton-card {
  aspect-ratio: 4 / 3;
  background: #f1f5f9;
  border-radius: 12px;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% {
    opacity: 0.6;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.6;
  }
}
</style>
