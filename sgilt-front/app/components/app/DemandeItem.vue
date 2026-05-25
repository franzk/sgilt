<template>
  <SgiltCard format="small" tag="button" @click="$emit('click')">
    <template #avatar>
      <img v-if="avatar" :src="avatarUrl" :alt="prestataireName" />
      <span v-else class="fallback">{{ initials }}</span>
    </template>

    <p class="name">{{ prestataireName }}</p>
    <p class="event">{{ evenementTitle }}</p>

    <template #cta>
      <StatusBadge :status="status" context="client" />
      <span class="arrow" aria-hidden="true">›</span>
    </template>
  </SgiltCard>
</template>

<script setup lang="ts">
import SgiltCard from '~/components/basics/cards/SgiltCard.vue'
import StatusBadge from '~/components/basics/StatusBadge.vue'
import type { ReservationStatus } from '~/data/reservation/domain/ReservationStatus'

const props = defineProps<{
  prestataireName: string
  prestataireAvatar: string | null
  evenementTitle: string
  status: ReservationStatus
}>()

defineEmits<{ click: [] }>()

const { toUrl } = useImageUrl()

const avatar = computed(() => props.prestataireAvatar)
const avatarUrl = computed(() => (props.prestataireAvatar ? toUrl(props.prestataireAvatar) : ''))
const initials = computed(() => props.prestataireName.slice(0, 2).toUpperCase())
</script>

<style scoped lang="scss">
.fallback {
  font-family: 'Inter', sans-serif;
  font-size: 0.75rem;
  font-weight: 700;
  color: $text-secondary;
}

.name {
  font-family: 'Inter', sans-serif;
  font-size: 0.875rem;
  font-weight: 600;
  color: $text-primary;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.event {
  font-family: 'Inter', sans-serif;
  font-size: 0.75rem;
  color: $text-secondary;
  margin: 2px 0 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.arrow {
  font-size: 1.2rem;
  color: $text-secondary;
  line-height: 1;
}
</style>
