<template>
  <!-- Réservation ouverte depuis la rubrique : affichée à la place de la rubrique. -->
  <ReservationDetail
    v-if="reservationId"
    :key="reservationId"
    :reservation-id="reservationId"
    :event-title="event?.title"
    @back="navigateTo(rubriquePath)"
  />
  <EventRubriqueDetail
    v-else-if="event && rubrique"
    :rubrique-key="rubrique.key"
    @back="backToBoard"
    @search="startAddPrestataireFlow"
  >
    <template v-if="rubrique.reservations.length > 0" #reservations>
      <ReservationList
        :reservations="rubrique.reservations"
        @open="(id) => navigateTo(`${rubriquePath}/${id}`)"
      />
    </template>
  </EventRubriqueDetail>
  <Sk v-else class="cover-skeleton" light />
</template>

<script setup lang="ts">
// Page d'une rubrique, ou la réservation ouverte depuis elle (/:rubrique/:id).
import ReservationDetail from '~/components/app/ReservationDetail.vue'
import ReservationList from '~/components/app/ReservationList.vue'
import Sk from '~/components/basics/Sk.vue'
import EventRubriqueDetail from '~/components/evenement/EventRubriqueDetail.vue'
import { useEventContext } from '~/data/evenement/useEventContext'

definePageMeta({ layout: 'app' })

const route = useRoute()

const { eventId, event, rubriques, pending } = useEventContext()

// ── Rubrique ─────────────────────────────────────────────────────────────────
// Clé absente des rubriques de l'événement (URL tapée à la main) : retour à l'event board.
const rubrique = computed(
  () => rubriques.value.find((candidate) => candidate.key === route.params.rubrique) ?? null,
)
const rubriquePath = computed(() => `/app/events/${eventId}/${route.params.rubrique}`)
const reservationId = computed(() =>
  typeof route.params.reservationId === 'string' ? route.params.reservationId : null,
)

watch(
  pending,
  (loading) => {
    if (!loading && !rubrique.value) navigateTo(`/app/events/${eventId}`, { replace: true })
  },
  { immediate: true },
)

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

.cover-skeleton {
  height: 12.5rem;
}
</style>
