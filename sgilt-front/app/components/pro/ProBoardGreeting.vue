<!--
  En-tête de l'accueil prestataire (/pro) : « Bonjour {prénom} ! », sous-titre et trois tuiles de
  compteurs, chacune menant à la liste correspondante — À contacter et À conclure vers l'onglet
  Réservations filtré, Ce mois-ci (prestations confirmées du mois) vers la vue mensuelle du
  Calendrier. L'image de la fiche, si fournie, s'affiche en filigrane en haut à droite.
-->
<template>
  <div class="pro-greeting">
    <div
      v-if="backgroundImage"
      class="watermark"
      :style="{ backgroundImage: `url(${backgroundImage})` }"
      aria-hidden="true"
    />

    <div class="hello">
      <p class="title">
        <template v-if="user.loading || !user.firstName">{{ $t('pro.board.greeting.title') }}</template>
        <template v-else>{{ $t('pro.board.greeting.titleNamed', { name: user.firstName }) }}</template>
      </p>
      <p class="subtitle">{{ $t('pro.board.greeting.subtitle') }}</p>
    </div>

    <div class="counts">
      <NuxtLink
        v-for="tile in tiles"
        :key="tile.key"
        :to="tile.to"
        class="count-card"
        :style="tile.color ? { '--card-color': tile.color } : {}"
      >
        <component :is="tile.icon" class="card-icon" />
        <Sk v-if="loading" width="1.5rem" height="1.6rem" radius="0.25rem" />
        <span v-else class="number">{{ error ? '–' : tile.value }}</span>
        <span class="label">{{ $t(`pro.board.greeting.tiles.${tile.key}`) }}</span>
      </NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import { PhoneIcon, ChatSmile2Icon, CalendarCheckIcon } from '@remixicons/vue/line'
import Sk from '~/components/basics/Sk.vue'
import { RESERVATION_STATUS_CONFIG } from '~/constants/reservation-status'

const props = defineProps<{
  loading?: boolean
  /** Chargement en échec : les nombres sont inconnus, un tiret les remplace. */
  error?: boolean
  /** URL de l'image affichée en filigrane derrière la salutation. */
  backgroundImage?: string
  counts: { aContacter: number; aConclure: number; ceMois: number }
}>()

const user = useCurrentUser()

interface Tile {
  key: 'a-contacter' | 'a-conclure' | 'ce-mois'
  icon: Component
  value: number
  to: string
  /** Couleur de la tuile ; absente = neutre (couleur de texte), pour une info qui n'est pas une action. */
  color?: string
}

const tiles = computed<Tile[]>(() => [
  {
    key: 'a-contacter',
    icon: PhoneIcon,
    value: props.counts.aContacter,
    to: '/pro/reservations',
    color: RESERVATION_STATUS_CONFIG.nouvelle.color,
  },
  {
    key: 'a-conclure',
    icon: ChatSmile2Icon,
    value: props.counts.aConclure,
    to: '/pro/reservations?filtre=a-conclure',
    color: RESERVATION_STATUS_CONFIG.en_discussion.color,
  },
  {
    key: 'ce-mois',
    icon: CalendarCheckIcon,
    value: props.counts.ceMois,
    to: '/pro/calendrier?vue=mois',
  },
])
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// Bandeau blanc pleine largeur : salutation et tuiles.
.pro-greeting {
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding: $spacing-l $spacing-m;
  background: $surface-white;

  // Image en filigrane, en haut à droite, fondue dans le blanc vers la gauche et vers le bas
  // (deux dégradés combinés).
  .watermark {
    position: absolute;
    top: 0;
    right: 0;
    width: 70%;
    height: 7.5rem;
    background-size: cover;
    background-position: center;
    opacity: 0.55;
    pointer-events: none;
    mask-image:
      linear-gradient(to right, transparent, #000 65%),
      linear-gradient(to bottom, #000 40%, transparent);
    mask-composite: intersect;
    -webkit-mask-image:
      linear-gradient(to right, transparent, #000 65%),
      linear-gradient(to bottom, #000 40%, transparent);
    -webkit-mask-composite: source-in;
  }

  .hello {
    position: relative;

    .title {
      font-family: 'Cormorant Garamond', serif;
      font-size: 2.25rem;
      font-weight: 700;
      color: $brand-primary;
      margin: 0;
      line-height: 1.1;
    }

    .subtitle {
      margin: 0.375rem 0 0;
      font-size: 0.9375rem;
      color: $text-secondary;
    }
  }

  .counts {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    position: relative;
    gap: $spacing-s;
    margin-top: $spacing-l;

    .count-card {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 0.375rem;
      min-height: 7.5rem;
      padding: $spacing-m $spacing-xs;
      border-radius: $radius-lg;
      background: color-mix(in srgb, var(--card-color, #{$text-primary}) 8%, $surface-white);
      border: 1px solid color-mix(in srgb, var(--card-color, #{$text-primary}) 12%, $surface-white);
      box-shadow: 0 0.25rem 0.75rem $shadow-s;
      text-decoration: none;
      transition: transform 0.2s ease;

      &:active {
        transform: scale(0.98);
      }

      .card-icon {
        width: 1.75rem;
        height: 1.75rem;
        color: var(--card-color, #{$text-primary});
      }

      .number {
        font-size: 1.75rem;
        font-weight: 600;
        line-height: 1;
        color: var(--card-color, #{$text-primary});
      }

      .label {
        font-size: 0.875rem;
        font-weight: 500;
        color: var(--card-color, #{$text-primary});
        text-align: center;
      }
    }
  }
}
</style>
