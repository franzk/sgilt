<template>
  <ReservationDetail :reservation-id="reservationId" :event-title="event?.title" @back="back" />
</template>

<script setup lang="ts">
// Réservation ouverte depuis une rubrique (ou un mail, une notification).
import ReservationDetail from '~/components/app/ReservationDetail.vue'
import { useEvent } from '~/data/evenement/useEvenement'

definePageMeta({ layout: 'app' })

const route = useRoute()
const eventId = route.params.eventId as string
const reservationId = route.params.reservationId as string

const { event, rubriques } = useEvent(eventId)

// Retour à la rubrique où la réservation est rangée (au board si elle n'y est pas trouvée).
function back() {
  const rubrique = rubriques.value.find((candidate) =>
    candidate.reservations.some((reservation) => reservation.id === reservationId),
  )
  navigateTo(rubrique ? `/app/events/${eventId}/${rubrique.key}` : `/app/events/${eventId}`)
}
</script>
