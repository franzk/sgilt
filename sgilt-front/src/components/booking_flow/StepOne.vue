<template>
  <SgiltFormGroup
    :title="$t('booking-flow.step-1.location.label')"
    :error-message="reservationStore.locationError"
  >
    <!-- location -->
    <SgiltInput
      type="text"
      v-model:text-model="reservationStore.location"
      :placeholder="$t('booking-flow.step-1.location.placeholder')"
    >
      <template #icon>
        <IconPlace />
      </template>
    </SgiltInput>
  </SgiltFormGroup>

  <div class="date-time">
    <!-- date -->
    <SgiltFormGroup :title="$t('booking-flow.step-1.date.label')">
      <SgiltDatePicker v-model="reservationStore.dateReservation" disabled />
    </SgiltFormGroup>

    <!-- time -->
    <SgiltFormGroup
      :title="$t('booking-flow.step-1.time.label')"
      :error-message="reservationStore.timeError"
    >
      <SgiltTimeInput v-model="reservationStore.timeReservation" />
    </SgiltFormGroup>
  </div>

  <!-- message -->
  <SgiltFormGroup
    :title="
      $t('booking-flow.step-1.message.label', { partnerName: reservationStore.partner?.title })
    "
  >
    <textarea :placeholder="$t('booking-flow.step-1.message.placeholder')"></textarea>
  </SgiltFormGroup>
</template>

<script setup lang="ts">
import { useReservationStore } from '@/stores/reservation.store'
import SgiltFormGroup from '@/components/basics/inputs/SgiltFormGroup.vue'
import SgiltDatePicker from '@/components/basics/inputs/SgiltDatePicker.vue'
import SgiltInput from '@/components/basics/inputs/SgiltInput.vue'
import IconPlace from '@/components/icons/IconPlace.vue'
import SgiltTimeInput from '@/components/basics/inputs/SgiltTimeInput.vue'

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
