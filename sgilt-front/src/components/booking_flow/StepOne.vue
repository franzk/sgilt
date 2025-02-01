<template>
  <SgiltFormGroup title="Lieu de l’événement" :error-message="reservationStore.locationError">
    <SgiltInput
      type="text"
      v-model:text-model="reservationStore.location"
      placeholder="Ex : Strasbourg, Rathsamhausen, Breuschwickersheim..."
    >
      <template #icon>
        <IconPlace />
      </template>
    </SgiltInput>
  </SgiltFormGroup>

  <div class="date-time">
    <SgiltFormGroup title="Date de l’événement">
      <SgiltDatePicker v-model="reservationStore.dateReservation" disabled />
    </SgiltFormGroup>

    <SgiltFormGroup title="Heure de l’événement" :error-message="reservationStore.timeError">
      <input type="time" v-model="reservationStore.timeReservation" />
    </SgiltFormGroup>
  </div>

  <SgiltFormGroup title="Envoyez un message à Marcel (optionnel)">
    <textarea placeholder="Ajoutez un message pour préciser vos attentes..."></textarea>
  </SgiltFormGroup>
</template>

<script setup lang="ts">
import { useReservationStore } from '@/stores/reservation.store'
import SgiltFormGroup from '@/components/basics/inputs/SgiltFormGroup.vue'
import SgiltDatePicker from '@/components/basics/inputs/SgiltDatePicker.vue'
import SgiltInput from '@/components/basics/inputs/SgiltInput.vue'
import IconPlace from '@/components/icons/IconPlace.vue'

const reservationStore = useReservationStore()
</script>

<style scoped lang="scss">
textarea {
  min-height: 4rem;
}

.date-time {
  display: flex;
  gap: $spacing-m;
  & > * {
    flex: 1;
  }
}
</style>
