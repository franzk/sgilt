<template>
  <SgiltCard format="small" tag="button" @click="$emit('click')">
    <template #avatar>
      <img class="cover" :src="coverUrl" :alt="title" />
    </template>

    <p class="title">{{ title }}</p>
    <p class="meta">
      <span v-if="date">{{ formatDate(date) }}</span>
      <span v-if="date && ville" aria-hidden="true"> · </span>
      <span v-if="ville">{{ ville }}</span>
    </p>

    <template #cta>
      <span class="arrow" aria-hidden="true">›</span>
    </template>
  </SgiltCard>
</template>

<script setup lang="ts">
import SgiltCard from '~/components/basics/cards/SgiltCard.vue'
import { BANK_IMAGE_PATHS } from '~/utils/eventCovers'

const props = defineProps<{
  title: string
  date?: Date
  ville?: string
  coverImage?: string | null
  eventType?: string
}>()

defineEmits<{ click: [] }>()

const { toUrl } = useImageUrl()

const coverUrl = computed(() => {
  const path = props.coverImage ?? BANK_IMAGE_PATHS[props.eventType ?? ''] ?? BANK_IMAGE_PATHS.autre!
  return toUrl(path)
})
</script>

<style scoped lang="scss">
// Avatar de couverture : rectangle arrondi, pas cercle
:deep(.avatar) {
  border-radius: $radius-md;
}

.cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.title {
  font-family: 'Inter', sans-serif;
  font-size: 0.875rem;
  font-weight: 600;
  color: $text-primary;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.meta {
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
