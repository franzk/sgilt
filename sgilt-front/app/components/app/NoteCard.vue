<template>
  <div class="note-card" :class="note.author.role">
    <div class="meta">
      <div class="avatar">
        <img
          v-if="note.author.photo"
          :src="note.author.photo"
          :alt="note.author.name"
          class="avatar-img"
        />
        <span v-else class="avatar-initials">{{ initials }}</span>
      </div>
      <span class="author">{{ note.author.name }}</span>
      <span class="dot" aria-hidden="true" />
      <time class="date">{{ formattedDate }}</time>
    </div>
    <p class="content">{{ note.content }}</p>
  </div>
</template>

<script setup lang="ts">
import type { ReservationNote } from '~/types/event'

const props = defineProps<{ note: ReservationNote }>()

const initials = computed(() =>
  props.note.author.name
    .split(' ')
    .slice(0, 2)
    .map((w) => w[0]?.toUpperCase() ?? '')
    .join(''),
)

const formattedDate = computed(() =>
  new Date(props.note.createdAt).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }),
)
</script>

<style scoped lang="scss">
.note-card {
  padding: $spacing-s;
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  border-radius: $border-radius-xs;
  box-shadow: 0 1px 4px rgba(47, 42, 37, 0.07);

  &.client {
    background: #ffffff;
  }

  &.prestataire {
    background: #f5f0e8;
  }

  .meta {
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .avatar {
    flex-shrink: 0;
    width: 24px;
    height: 24px;
    border-radius: 50%;
    background: rgba($brand-muted, 0.15);
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;

    .avatar-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .avatar-initials {
      font-family: 'Inter', sans-serif;
      font-size: 9px;
      font-weight: 600;
      color: $brand-muted;
      line-height: 1;
    }
  }

  .author {
    font-family: 'Inter', sans-serif;
    font-size: 10px;
    font-weight: 600;
    letter-spacing: 0.06em;
    text-transform: uppercase;
    color: $brand-muted;
  }

  .dot {
    width: 2px;
    height: 2px;
    border-radius: 50%;
    background: $text-secondary;
    opacity: 0.4;
  }

  .date {
    font-family: 'Inter', sans-serif;
    font-size: 10px;
    color: $text-secondary;
    opacity: 0.6;
  }

  .content {
    font-family: 'Inter', sans-serif;
    font-size: 0.875rem;
    font-weight: 400;
    color: $text-primary;
    line-height: 1.55;
    margin: 0;
    white-space: pre-wrap;
  }
}
</style>
