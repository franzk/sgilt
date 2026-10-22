<template>
  <article class="feed-entry-card">
    <!-- Point sur la ligne de la frise (ReservationFeed), de la couleur de la pastille. -->
    <span class="dot" :class="kind" :style="pastilleStyle" aria-hidden="true" />
    <span class="pastille" :class="kind" :style="pastilleStyle" aria-hidden="true">
      <component :is="icon" class="icon" />
    </span>

    <div class="body">
      <div class="head">
        <strong class="title">{{ title }}</strong>
        <time class="date">{{ formatRelativeDateTime(createdAt) }}</time>
      </div>

      <!-- Contenu propre au type d'élément. -->
      <slot />

      <footer class="foot">
        <span class="avatar">
          <img v-if="author.photo" :src="author.photo" alt="" class="avatar-img" />
          <span v-else class="initials">{{ initials }}</span>
        </span>
        <span class="author">{{ isOwn ? $t('feed.you') : author.name }}</span>
        <!-- Complément du pied (ex. visibilité d'une note). -->
        <slot name="foot" />
      </footer>
    </div>
  </article>
</template>

<script setup lang="ts">
// Coquille d'une carte de la frise « Notes & suivi » : point sur la frise, pastille, titre, date,
// contenu (slot), auteur. FeedEntry la remplit selon le type d'élément.
import type { Component } from 'vue'
import type { NoteAuthor } from '~/data/reservation/domain/NoteAuthor'
import { formatRelativeDateTime } from '~/utils/dateUtils'

const props = defineProps<{
  // Type de carte : couleur de la pastille et du point (voir les styles).
  kind: 'initial' | 'status' | 'note-client' | 'note-prestataire' | 'document'
  icon: Component
  // Couleur imposée (un changement de statut prend celle de son statut).
  pastilleStyle?: Record<string, string>
  title: string
  createdAt: Date
  author: NoteAuthor
  // L'auteur est l'utilisateur courant : « Vous » à la place de son nom.
  isOwn: boolean
}>()

const initials = computed(() =>
  props.author.name
    .split(' ')
    .slice(0, 2)
    .map((word) => word[0]?.toUpperCase() ?? '')
    .join(''),
)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// Couleurs des pastilles par type (un changement de statut prend celle de son statut).
$pastille-initial: $brand-subtle;
$pastille-note-client: $brand-accent;
$pastille-note-prestataire: #5a8f6b;
$pastille-document: #4a6fa5;

.feed-entry-card {
  position: relative;
  display: flex;
  gap: $spacing-s;
  padding: $spacing-m;
  border: 1px solid $divider-color;
  border-radius: $radius-lg;
  background: $surface-white;

  // Dans la marge gauche de la frise ($spacing-l, posée par ReservationFeed), en face de la pastille.
  .dot {
    position: absolute;
    top: calc(#{$spacing-m} + 0.9375rem);
    left: calc(-#{$spacing-l} + 0.05rem);
    width: 0.625rem;
    height: 0.625rem;
    border: 2px solid $surface-white;
    border-radius: 50%;
  }

  .dot,
  .pastille {
    &.initial {
      background: $text-secondary;
    }

    &.note-client {
      background: $pastille-note-client;
    }

    &.note-prestataire {
      background: $pastille-note-prestataire;
    }

    &.document {
      background: $pastille-document;
    }
  }

  .pastille {
    display: flex;
    flex-shrink: 0;
    align-items: center;
    justify-content: center;
    width: 2.5rem;
    height: 2.5rem;
    border-radius: 50%;
    color: $text-inverted;

    // La demande initiale, en gris clair : son icône passe en foncé.
    &.initial {
      background: $pastille-initial;
      color: $text-primary;
    }

    .icon {
      width: 1.25rem;
      height: 1.25rem;
    }
  }

  .body {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
    min-width: 0;

    .head {
      display: flex;
      flex-direction: column;
      gap: 0.125rem;

      .title {
        font-size: $font-size-md;
        font-weight: $font-weight-bold;
        color: $text-primary;
        line-height: 1.3;
      }

      .date {
        font-size: $font-size-xs;
        color: $text-secondary;
      }
    }

    .foot {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      gap: $spacing-xs;
      margin-top: $spacing-xxs;

      .avatar {
        display: flex;
        flex-shrink: 0;
        align-items: center;
        justify-content: center;
        width: 1.75rem;
        height: 1.75rem;
        overflow: hidden;
        border-radius: 50%;
        background: $brand-subtle;

        .avatar-img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .initials {
          font-size: 0.65rem;
          font-weight: $font-weight-semibold;
          color: $text-primary;
        }
      }

      .author {
        font-size: $font-size-sm;
        color: $text-secondary;
      }
    }
  }
}
</style>
