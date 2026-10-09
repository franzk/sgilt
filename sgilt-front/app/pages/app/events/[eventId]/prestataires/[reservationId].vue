<template>
  <ReservationDetail
    :reservation-id="reservationId"
    :event-title="event?.title"
    @back="navigateTo(`/app/events/${eventId}/prestataires`)"
  />
</template>

<script setup lang="ts">
// Réservation ouverte depuis la liste des prestataires de l'événement.
import ReservationDetail from '~/components/app/ReservationDetail.vue'
import { useEvent } from '~/data/evenement/useEvenement'

definePageMeta({ layout: 'app' })

const route = useRoute()
const eventId = route.params.eventId as string
const reservationId = route.params.reservationId as string

const { event } = useEvent(eventId)
</script>
