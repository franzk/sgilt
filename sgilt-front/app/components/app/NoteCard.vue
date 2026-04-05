<template>
  <div class="note-card" :class="note.author.role">
    <div class="body">
      <strong class="titre">{{ note.titre }}</strong>
      <p class="content">{{ note.content }}</p>
    </div>
    <div class="divider" />
    <div class="footer">
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
      <time class="date">{{ relativeDate }}</time>
    </div>
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

const relativeDate = computed(() => {
  const diff = Date.now() - new Date(props.note.createdAt).getTime()
  const minutes = Math.floor(diff / 60_000)
  const hours = Math.floor(diff / 3_600_000)
  const days = Math.floor(diff / 86_400_000)

  if (minutes < 1) return "à l'instant"
  if (hours < 1) return `il y a ${minutes} minute${minutes > 1 ? 's' : ''}`
  if (days < 1) return `il y a ${hours} heure${hours > 1 ? 's' : ''}`
  return `il y a ${days} jour${days > 1 ? 's' : ''}`
})
</script>

<style scoped lang="scss">
.note-card {
  padding: $spacing-s;
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
  border-radius: $radius-sm;
  box-shadow: 0 1px 4px rgba(47, 42, 37, 0.07);

  box-shadow: 0 2px 8px rgba(47, 42, 37, 0.08);

  &.client {
    background: #ffffff;
  }

  &.prestataire {
    background: #f8f9fa;
    border-left: 3px solid $brand-accent;
  }

  .body {
    display: flex;
    flex-direction: column;
    gap: $spacing-xxs;
  }

  .titre {
    font-family: 'Inter', sans-serif;
    font-size: 0.875rem;
    font-weight: 700;
    color: $text-primary;
    line-height: 1.3;
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

  .divider {
    height: 1px;
    background: $divider-color;
    opacity: 0.6;
  }

  .footer {
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .avatar {
    flex-shrink: 0;
    width: 20px;
    height: 20px;
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
      font-size: 8px;
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
}
</style>
