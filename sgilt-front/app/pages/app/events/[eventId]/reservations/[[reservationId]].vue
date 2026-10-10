<template>
  <!-- Réservation ouverte depuis la liste (ou un mail, une notification) : à la place de la liste. -->
  <ReservationDetail
    v-if="reservationId"
    :key="reservationId"
    :reservation-id="reservationId"
    :event-title="event?.title"
    @back="navigateTo(listPath)"
  />
  <div v-else class="reservations-list">
    <div v-if="!pending" class="column">
      <h1 class="title">{{ $t('evenement.prestataires.title') }}</h1>

      <ReservationList
        v-if="reservations.length > 0"
        :reservations="reservations"
        @open="(id) => navigateTo(`${listPath}/${id}`)"
      />
      <p v-else class="empty">{{ $t('evenement.prestataires.empty') }}</p>
    </div>
    <Sk v-else class="skeleton" />
  </div>
</template>

<script setup lang="ts">
// Liste plate des réservations de l'événement, toutes rubriques confondues (menu « Prestataires »
// de l'événement), ou la réservation ouverte depuis elle (/reservations/:id).
import ReservationDetail from '~/components/app/ReservationDetail.vue'
import ReservationList from '~/components/app/ReservationList.vue'
import Sk from '~/components/basics/Sk.vue'
import { useEventContext } from '~/data/evenement/useEventContext'

definePageMeta({ layout: 'app' })

const { t } = useI18n()
useHead({ title: t('evenement.prestataires.page-title') })

const { eventId, event, rubriques, pending } = useEventContext()

const route = useRoute()
const listPath = `/app/events/${eventId}/reservations`
const reservationId = computed(() =>
  typeof route.params.reservationId === 'string' ? route.params.reservationId : null,
)

// Dans l'ordre des rubriques de l'événement.
const reservations = computed(() => rubriques.value.flatMap((rubrique) => rubrique.reservations))
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.reservations-list {
  display: flex;
  justify-content: center;
  flex: 1;
  background: $surface-white;

  .column {
    display: flex;
    flex-direction: column;
    gap: $spacing-m;
    width: 100%;
    // Même largeur que la page rubrique (EventRubriqueDetail) : cartes réservation identiques.
    max-width: 45rem;
    padding: $spacing-m $section-padding-x $spacing-xl;

    .title {
      margin: 0;
      font-family: 'Cormorant Garamond', serif;
      font-size: 1.6rem;
      font-weight: 600;
      color: $text-primary;
    }

    .empty {
      margin: 0;
      color: $text-secondary;
      font-size: $font-size-sm;
    }
  }

  .skeleton {
    width: 100%;
    max-width: 45rem;
    height: 20rem;
    margin: $spacing-m;
  }
}
</style>
