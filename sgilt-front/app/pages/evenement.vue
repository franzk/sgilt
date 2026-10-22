<template>
  <div class="event-shell">
    <EventSidebar
      v-if="isDesktop && localEvent.eventType"
      class="sidebar"
      board-path="/evenement"
      :event="localEvent"
      :rubriques="localEvent.rubriques"
      :cover-image="coverImage"
    />
    <div class="center">
      <NuxtPage />
    </div>
  </div>
</template>

<script setup lang="ts">
// Route parente des pages de l'événement local (parcours public) : sur desktop, ajoute la sidebar
// à gauche de la page ; ailleurs, la page seule. Pendant de /app/events/:eventId.
import EventSidebar from '~/components/evenement/EventSidebar.vue'
import { defaultCoverPath } from '~/utils/eventCovers'

definePageMeta({ layout: 'evenement' })

const { isDesktop } = useDevice()
const { localEvent } = useLocalEvent()

// Même banque d'images que l'event board (fallback par type, jusqu'à 'autre').
const { toUrl } = useImageUrl()
const coverImage = computed(() => toUrl(defaultCoverPath(localEvent.eventType)))
</script>

<style scoped lang="scss">
.event-shell {
  flex: 1;
  display: flex;

  .sidebar {
    flex-shrink: 0;
  }

  .center {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-width: 0;
  }
}
</style>
