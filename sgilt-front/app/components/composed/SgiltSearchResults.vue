<script setup lang="ts">
import type { PrestataireCardDetail } from '~/data/prestataire/domain/prestataire'
import PrestataireCard from '~/components/cards/PrestataireCard.vue'

defineProps<{
  results: PrestataireCardDetail[]
  loading: boolean
  error?: string | null
  selectable?: boolean
}>()
defineEmits<{ select: [provider: PrestataireCardDetail] }>()
</script>

<template>
  <div class="results-section">
    <div v-if="loading" class="grid-container">
      <PrestataireCard v-for="i in 6" :key="i" loading />
    </div>

    <div v-else-if="error" class="status-message error">
      {{ error }}
    </div>

    <div v-else-if="results.length === 0" class="status-message">
      Aucun prestataire trouvé pour ces critères.
    </div>

    <div v-else class="grid-container">
      <PrestataireCard
        v-for="provider in results"
        :key="provider.id"
        :provider="provider"
        :selectable="selectable"
        @select="$emit('select', $event)"
      />
    </div>
  </div>
</template>

<style scoped lang="scss">
.results-section {
  flex: 1;
  padding: 10px;
  background-color: #f7f9fb;

  @media (min-width: 768px) {
    padding: 2rem;
  }
}

.grid-container {
  display: grid;

  grid-template-columns: repeat(auto-fill, minmax(170px, 1fr));
  gap: 10px;
  // Par défaut (mobile) : on force 2 colonnes
  grid-template-columns: repeat(2, 1fr);
  padding: 0;

  // DESKTOP : On repasse sur un comportement flexible
  @media (min-width: 600px) {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
    gap: 16px;
  }

  @media (min-width: 1024px) {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 20px;
  }

  //  Très petits écrans (ex: iPhone SE ou vieux modèles)
  @media (max-width: 340px) {
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
