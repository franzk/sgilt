<template>
  <div class="prestataires">
    <div v-if="!pending" class="column">
      <h1 class="title">{{ $t('evenement.prestataires.title') }}</h1>

      <div v-if="reservations.length > 0" class="list">
        <ReservationCard
          v-for="reservation in reservations"
          :key="reservation.id"
          :reservation="reservation"
          @click="navigateTo(`/app/events/${eventId}/prestataires/${reservation.id}`)"
        />
      </div>
      <p v-else class="empty">{{ $t('evenement.prestataires.empty') }}</p>
    </div>
    <Sk v-else class="skeleton" />
  </div>
</template>

<script setup lang="ts">
// Liste plate des réservations de l'événement, toutes rubriques confondues (onglet
// « Prestataires » de la navigation de l'événement).
import ReservationCard from '~/components/app/ReservationCard.vue'
import Sk from '~/components/basics/Sk.vue'
import { useEvent } from '~/data/evenement/useEvenement'

definePageMeta({ layout: 'app' })

const { t } = useI18n()
useHead({ title: t('evenement.prestataires.page-title') })

const route = useRoute()
const eventId = route.params.eventId as string

const { rubriques, pending } = useEvent(eventId)

// Dans l'ordre des rubriques de l'événement.
const reservations = computed(() => rubriques.value.flatMap((rubrique) => rubrique.reservations))
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.prestataires {
  display: flex;
  justify-content: center;
  flex: 1;
  background: $surface-white;

  .column {
    display: flex;
    flex-direction: column;
    gap: $spacing-m;
    width: 100%;
    max-width: 30rem;
    padding: $spacing-m $section-padding-x $spacing-xl;

    .title {
      margin: 0;
      font-family: 'Cormorant Garamond', serif;
      font-size: 1.6rem;
      font-weight: 600;
      color: $text-primary;
    }

    .list {
      display: flex;
      flex-direction: column;
      gap: $spacing-s;
    }

    .empty {
      margin: 0;
      color: $text-secondary;
      font-size: $font-size-sm;
    }
  }

  .skeleton {
    width: 100%;
    max-width: 30rem;
    height: 20rem;
    margin: $spacing-m;
  }
}
</style>
