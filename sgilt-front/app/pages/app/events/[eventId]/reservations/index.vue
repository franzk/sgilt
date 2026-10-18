<template>
  <div class="reservations-list">
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
// Liste de toutes les réservations de l'événement, toutes rubriques confondues : l'écran du menu
// « Prestataires » de l'événement (/app/events/:eventId/reservations). Une réservation s'ouvre dans
// reservations/[reservationId]/[[onglet]].vue.
import ReservationList from '~/components/app/ReservationList.vue'
import Sk from '~/components/basics/Sk.vue'
import { useEventContext } from '~/data/evenement/useEventContext'

definePageMeta({ layout: 'app' })

const { t } = useI18n()
useHead({ title: t('evenement.prestataires.page-title') })

const { eventId, rubriques, pending } = useEventContext()

const listPath = `/app/events/${eventId}/reservations`

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
