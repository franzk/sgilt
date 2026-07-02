<template>
  <!-- ── Big ─────────────────────────────────────────────────────────────────── -->
  <SgiltCard
    v-if="format === 'big'"
    format="big"
    tag="button"
    :image="coverUrl"
    :ratio="ratio"
    @click="$emit('click')"
  >
    <template #overlay>
      <div class="info">
        <h2 class="title">{{ title }}</h2>
        <p class="meta">
          <span v-if="date">{{ formatDate(date) }}</span>
          <span v-if="date && ville" aria-hidden="true"> · </span>
          <span v-if="ville">{{ ville }}</span>
        </p>
        <p v-if="summary" class="summary">{{ summary }}</p>
        <span class="cta" aria-hidden="true">{{ $t('events.see-event') }}</span>
      </div>
    </template>
  </SgiltCard>

  <!-- ── Small ────────────────────────────────────────────────────────────────── -->
  <SgiltCard v-else format="small" tag="button" @click="$emit('click')">
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

const props = withDefaults(
  defineProps<{
    title: string
    date?: Date
    ville?: string
    coverImage?: string | null
    eventType?: string
    format?: 'big' | 'small'
    summary?: string
    ratio?: string
  }>(),
  {
    format: 'small',
    ratio: '3/2',
  },
)

defineEmits<{ click: [] }>()

const { toUrl } = useImageUrl()

const coverUrl = computed(() => {
  const path =
    props.coverImage ?? BANK_IMAGE_PATHS[props.eventType ?? ''] ?? BANK_IMAGE_PATHS.autre!
  return toUrl(path)
})
</script>

<style scoped lang="scss">
// ── Big ──────────────────────────────────────────────────────────────────────────
.info {
  display: contents;

  .title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.375rem;
    font-weight: 600;
    color: #fff;
    margin: 0;
    line-height: 1.2;
    text-shadow: 0 1px 4px rgba(0, 0, 0, 0.25);
  }

  .meta {
    font-family: 'Inter', sans-serif;
    font-size: 0.78rem;
    color: rgba(255, 255, 255, 0.82);
    margin: 3px 0 0;
  }

  .summary {
    font-family: 'Inter', sans-serif;
    font-size: 0.72rem;
    font-weight: 500;
    color: rgba(255, 255, 255, 0.75);
    margin: 5px 0 0;
  }

  .cta {
    display: inline-block;
    margin-top: $spacing-s;
    padding: 6px 14px;
    border: 1px solid rgba(255, 255, 255, 0.5);
    border-radius: $radius-xl;
    font-family: 'Inter', sans-serif;
    font-size: 0.75rem;
    font-weight: 600;
    color: #fff;
    background: rgba(255, 255, 255, 0.12);
    backdrop-filter: blur(4px);
    pointer-events: none;
    align-self: flex-start;
  }
}

// ── Small ─────────────────────────────────────────────────────────────────────────
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
