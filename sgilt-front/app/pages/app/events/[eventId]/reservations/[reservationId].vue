<template>
  <ReservationDetail :reservation-id="reservationId" :event-title="event?.title" @back="back" />
</template>

<script setup lang="ts">
// Réservation ouverte depuis une rubrique (ou un mail, une notification).
import ReservationDetail from '~/components/app/ReservationDetail.vue'
import { useEventContext } from '~/data/evenement/useEventContext'

definePageMeta({ layout: 'app' })

const route = useRoute()
const reservationId = route.params.reservationId as string

const { eventId, event, rubriques } = useEventContext()

// Retour à la rubrique où la réservation est rangée (au board si elle n'y est pas trouvée).
function back() {
  const rubrique = rubriques.value.find((candidate) =>
    candidate.reservations.some((reservation) => reservation.id === reservationId),
  )
  navigateTo(rubrique ? `/app/events/${eventId}/${rubrique.key}` : `/app/events/${eventId}`)
}
</script>
