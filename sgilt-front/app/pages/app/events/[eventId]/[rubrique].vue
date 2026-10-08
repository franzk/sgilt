<template>
  <EventRubriqueDetail
    v-if="event && rubrique"
    :rubrique-key="rubrique.key"
    @back="backToBoard"
    @search="startAddPrestataireFlow"
  >
    <template v-if="rubrique.reservations.length > 0" #reservations>
      <div class="reservations">
        <ReservationCard
          v-for="reservation in rubrique.reservations"
          :key="reservation.id"
          :reservation="reservation"
          @click="navigateTo(`/app/events/${eventId}/reservations/${reservation.id}`)"
        />
      </div>
    </template>
  </EventRubriqueDetail>
  <Sk v-else class="cover-skeleton" light />
</template>

<script setup lang="ts">
import ReservationCard from '~/components/app/ReservationCard.vue'
import Sk from '~/components/basics/Sk.vue'
import EventRubriqueDetail from '~/components/evenement/EventRubriqueDetail.vue'
import { useEvent } from '~/data/evenement/useEvenement'

definePageMeta({ layout: 'app' })

const route = useRoute()
const eventId = route.params.eventId as string

const { event, rubriques, pending } = useEvent(eventId)

// ── Rubrique ─────────────────────────────────────────────────────────────────
// Clé absente des rubriques de l'événement (URL tapée à la main) : retour à l'event board.
const rubrique = computed(
  () => rubriques.value.find((candidate) => candidate.key === route.params.rubrique) ?? null,
)

watch(pending, (loading) => {
  if (!loading && !rubrique.value) navigateTo(`/app/events/${eventId}`, { replace: true })
})

function backToBoard() {
  navigateTo(`/app/events/${eventId}`)
}

// ── Flow ajout prestataire ────────────────────────────────────────────────────
const { start } = useFlow()
const startAddPrestataireFlow = () => {
  start('add-prestataire', `Ajouter à ${event.value?.title ?? "l'événement"}`, {
    id: event.value?.id,
    nom: event.value?.title,
    date: event.value?.date ?? null,
    ville: event.value?.ville,
    ambiance: event.value?.ambiance,
    invites: event.value?.nbInvites,
  })
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.reservations {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

.cover-skeleton {
  height: 12.5rem;
}
</style>
