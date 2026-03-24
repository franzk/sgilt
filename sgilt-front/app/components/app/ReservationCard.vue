<template>
  <BadgeableComponent :count="reservation.unreadNotesCount">
    <SgiltCard
      :image="reservation.prestatairePhoto || FALLBACK_PHOTO"
      ratio="4/3"
      @click="$emit('click')"
    >
      <template #overlay>
        <span class="res-card__category">{{ reservation.category }}</span>
        <span class="res-card__name">{{ reservation.prestataireName }}</span>
      </template>
      <template #footer>
        <div class="res-card__footer">
          <StatusBadge :status="reservation.status" context="client" />
        </div>
      </template>
    </SgiltCard>
  </BadgeableComponent>
</template>

<script setup lang="ts">
import BadgeableComponent from '~/components/basics/BadgeableComponent.vue'
import SgiltCard from '~/components/basics/cards/SgiltCard.vue'
import StatusBadge from '~/components/basics/StatusBadge.vue'
import type { Reservation } from '~/types/event'

defineProps<{ reservation: Reservation }>()
defineEmits<{ click: [] }>()

const FALLBACK_PHOTO =
  'https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=400&auto=format&fit=crop'
</script>

<style scoped lang="scss">
.res-card__category {
  font-family: 'Inter', sans-serif;
  font-size: 0.625rem;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 3px;
}

.res-card__name {
  font-family: 'Cormorant Garamond', serif;
  font-size: 1.05rem;
  font-weight: 600;
  color: #fff;
  line-height: 1.2;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

.res-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-xs;
  padding: 8px $spacing-s;
}
</style>
