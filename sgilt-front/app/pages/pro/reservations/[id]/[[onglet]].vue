<!--
  FICHE D'UNE RÉSERVATION CÔTÉ PRESTATAIRE (espace pro). Exemple : réservation 123.
    /pro/reservations/123             onglet Suivi (URL des mails)
    /pro/reservations/123/details     onglet Détails (idem /documents, /prestataire)
    /pro/reservations/123/actions     page « Actions spéciales »
  [[onglet]] (doubles crochets) = segment facultatif. Tout l'affichage est dans ReservationBoard,
  la même fiche que côté client ; ce fichier ne fait que lui passer la réservation et le retour.
-->
<template>
  <ReservationBoard
    :key="reservationId"
    role="prestataire"
    :reservation-id="reservationId"
    :reservation-path="reservationPath"
    :segment="segment"
    @back="router.back()"
  />
</template>

<script setup lang="ts">
// Fiche d'une réservation côté prestataire (voir le commentaire en tête).
import ReservationBoard from '~/components/shared/ReservationBoard.vue'

definePageMeta({ layout: 'pro' })

const route = useRoute()
const router = useRouter()

const reservationId = computed(() => String(route.params.id))
const reservationPath = computed(() => `/pro/reservations/${reservationId.value}`)
// Segment après la réservation : un onglet, 'actions', ou rien (Suivi).
const segment = computed(() =>
  typeof route.params.onglet === 'string' ? route.params.onglet : undefined,
)
</script>
