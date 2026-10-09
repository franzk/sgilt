<template>
  <div class="event-shell">
    <EventSidebar
      v-if="isDesktop && event"
      class="sidebar"
      :event-id="eventId"
      :event="event"
      :rubriques="rubriques"
      :cover-image="coverImage"
    />
    <div class="center">
      <NuxtPage />
    </div>
  </div>
</template>

<script setup lang="ts">
// Route parente des pages d'un événement : charge l'événement et le partage avec elles
// (useEventContext). Sur desktop, ajoute la sidebar à gauche de la page ; ailleurs, la page seule.
import EventSidebar from '~/components/app/EventSidebar.vue'
import { provideEventContext } from '~/data/evenement/useEventContext'
import { defaultCoverPath } from '~/utils/eventCovers'

definePageMeta({ layout: 'app' })

const route = useRoute()
const { isDesktop } = useDevice()

const { eventId, event, rubriques, refresh } = provideEventContext(route.params.eventId as string)

useHead(computed(() => ({ title: event.value?.title ?? '' })))

// Chaque navigation dans l'événement recharge ses données en arrière-plan : statuts et compteurs
// restent à jour après une action sur une réservation, sans faire disparaître la sidebar.
watch(
  () => route.path,
  () => refresh(),
)

const { toUrl } = useImageUrl()
const coverImage = computed(() =>
  event.value ? toUrl(event.value.coverImage || defaultCoverPath(event.value.eventType)) : '',
)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

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
