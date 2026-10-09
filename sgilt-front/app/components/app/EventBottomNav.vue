<template>
  <nav class="bottom-nav">
    <NuxtLink :to="boardPath" class="item" :class="{ active: !isPrestatairesActive }">
      <Home2Icon class="icon" />
      <span class="label">{{ $t('nav.home') }}</span>
    </NuxtLink>

    <NuxtLink :to="prestatairesPath" class="item" :class="{ active: isPrestatairesActive }">
      <TeamIcon class="icon" />
      <span class="label">{{ $t('nav.prestataires') }}</span>
    </NuxtLink>
  </nav>
</template>

<script setup lang="ts">
// Navigation à l'intérieur d'un événement (/app/events/:id et ses sous-pages) : remplace la
// navigation globale de l'espace connecté. On sort de l'événement par le header.
import { Home2Icon, TeamIcon } from '@remixicons/vue/line'

const props = defineProps<{
  eventId: string
}>()

const route = useRoute()
const boardPath = computed(() => `/app/events/${props.eventId}`)
const prestatairesPath = computed(() => `${boardPath.value}/prestataires`)
// Onglet en surbrillance :
// - [Prestataires] sur la liste et les réservations ouvertes depuis elle (/prestataires/:id),
// - [Accueil] sur toutes les autres pages de l'événement (board, rubrique, paramètres,
//    réservations ouvertes depuis une rubrique).
const isPrestatairesActive = computed(() => route.path.startsWith(prestatairesPath.value))
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$nav-h: $bottom-nav-h;

.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: $z-header;
  height: calc($nav-h + env(safe-area-inset-bottom, 0px));
  padding-bottom: env(safe-area-inset-bottom, 0px);
  display: flex;
  align-items: stretch;
  background: #fff;
  border-top: 1px solid $divider-color;

  @media (min-width: $breakpoint-desktop) {
    display: none;
  }

  .item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 3px;
    color: $brand-muted;
    transition: color 150ms ease;
    text-decoration: none;

    &.active {
      color: $brand-accent;
    }
  }

  .icon {
    width: 22px;
    height: 22px;
  }

  .label {
    font-size: 0.65rem;
    font-weight: 500;
    letter-spacing: 0.02em;
  }
}
</style>
