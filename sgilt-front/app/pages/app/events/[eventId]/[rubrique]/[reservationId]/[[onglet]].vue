<!--
  FICHE D'UNE RÉSERVATION, ouverte depuis la page d'une rubrique. Exemple : événement 42, rubrique
  lieu, réservation 123.
    /app/events/42/lieu/123           onglet Suivi
    /app/events/42/lieu/123/details   onglet Détails (idem /documents, /prestataire)
    /app/events/42/lieu/123/actions   page « Actions spéciales »
  [[onglet]] (doubles crochets) = segment facultatif. Tout l'affichage est dans ReservationBoard ;
  ce fichier ne fait que lui passer la réservation et le retour vers la rubrique.
-->
<template>
  <ReservationBoard
    :key="reservationId"
    role="client"
    :reservation-id="reservationId"
    :reservation-path="reservationPath"
    :segment="segment"
    :event-title="event?.title"
    @back="navigateTo(backPath)"
  />
</template>

<script setup lang="ts">
// Fiche d'une réservation ouverte depuis une rubrique (voir le commentaire en tête).
import ReservationBoard from '~/components/shared/ReservationBoard.vue'
import { useEventContext } from '~/data/evenement/useEventContext'

definePageMeta({ layout: 'app' })

const route = useRoute()
const { eventId, event } = useEventContext()

// Retour : la page de la rubrique.
const backPath = `/app/events/${eventId}/${route.params.rubrique}`
const reservationId = computed(() => String(route.params.reservationId))
const reservationPath = computed(() => `${backPath}/${reservationId.value}`)
// Segment après la réservation : un onglet, 'actions', ou rien (Suivi).
const segment = computed(() =>
  typeof route.params.onglet === 'string' ? route.params.onglet : undefined,
)
</script>
