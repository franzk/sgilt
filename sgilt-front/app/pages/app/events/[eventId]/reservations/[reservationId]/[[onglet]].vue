<!--
  FICHE D'UNE RÉSERVATION, ouverte depuis la liste « Prestataires » de l'événement (ou depuis un mail,
  une notification). Exemple : événement 42, réservation 123.
    /app/events/42/reservations/123           onglet Suivi
    /app/events/42/reservations/123/details   onglet Détails (idem /documents, /prestataire)
    /app/events/42/reservations/123/actions   page « Actions spéciales »
  [[onglet]] (doubles crochets) = segment facultatif. Tout l'affichage est dans ReservationBoard ;
  ce fichier ne fait que lui passer la réservation et le retour vers la liste.
  ?from=invitation : arrivée directe après l'activation du compte d'un client invité depuis le BO —
  le retour mène alors à l'event board, que le client découvre.
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
// Fiche d'une réservation ouverte depuis la liste « Prestataires » (voir le commentaire en tête).
import ReservationBoard from '~/components/shared/ReservationBoard.vue'
import { useEventContext } from '~/data/evenement/useEventContext'

definePageMeta({ layout: 'app' })

const route = useRoute()
const { eventId, event } = useEventContext()

// La liste « Prestataires », d'où la fiche est normalement ouverte.
const listPath = `/app/events/${eventId}/reservations`
// Retour : la liste, sauf à l'arrivée d'un client invité (?from=invitation) — l'event board.
const backPath = route.query.from === 'invitation' ? `/app/events/${eventId}` : listPath
const reservationId = computed(() => String(route.params.reservationId))
const reservationPath = computed(() => `${listPath}/${reservationId.value}`)
// Segment après la réservation : un onglet, 'actions', ou rien (Suivi).
const segment = computed(() =>
  typeof route.params.onglet === 'string' ? route.params.onglet : undefined,
)
</script>
