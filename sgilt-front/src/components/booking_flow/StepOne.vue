<template>
  <div class="step-1">
    <SgiltFormGroup
      :title="mobileView ? '' : $t('booking-flow.step-1.location.label')"
      :error-message="reservationStore.locationError"
    >
      <!-- location -->
      <SgiltInput
        type="text"
        v-model:text-model="reservationStore.location"
        updateOnBlur
        :placeholder="
          mobileView
            ? $t('booking-flow.step-1.location.label')
            : $t('booking-flow.step-1.location.placeholder')
        "
      >
        <template #icon>
          <IconPlace />
        </template>
      </SgiltInput>
    </SgiltFormGroup>

    <div class="date-time">
      <!-- date -->
      <SgiltFormGroup :title="mobileView ? '' : $t('booking-flow.step-1.date.label')">
        <SgiltDatePicker v-model="reservationStore.dateReservation" disabled />
      </SgiltFormGroup>

      <!-- time -->
      <SgiltFormGroup
        :title="mobileView ? '' : $t('booking-flow.step-1.time.label')"
        :error-message="reservationStore.timeError"
      >
        <SgiltTimeInput v-model="reservationStore.timeReservation" />
      </SgiltFormGroup>
    </div>

    <!-- message -->
    <SgiltFormGroup
      :title="
        mobileView
          ? ''
          : $t('booking-flow.step-1.message.label', {
              partnerName: reservationStore.partner?.title,
            })
      "
    >
      <SgiltTextArea
        :placeholder="
          mobileView
            ? $t('booking-flow.step-1.message.label', {
                partnerName: reservationStore.partner?.title,
              })
            : $t('booking-flow.step-1.message.placeholder')
        "
        v-model="reservationStore.message"
      />
    </SgiltFormGroup>
  </div>
</template>

<script setup lang="ts">
import { useReservationStore } from '@/stores/reservation.store'
import SgiltFormGroup from '@/components/basics/inputs/SgiltFormGroup.vue'
import SgiltDatePicker from '@/components/basics/inputs/SgiltDatePicker.vue'
import SgiltInput from '@/components/basics/inputs/SgiltInput.vue'
import IconPlace from '@/components/icons/IconPlace.vue'
import SgiltTimeInput from '@/components/basics/inputs/SgiltTimeInput.vue'
import SgiltTextArea from '@/components/basics/inputs/SgiltTextArea.vue'
import { mobileView } from '@/utils/StyleUtils'

const reservationStore = useReservationStore()
</script>

<style scoped lang="scss">
.step-1 {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  @include respond-to(mobile) {
    gap: 0;
  }
}

textarea {
  min-height: 4rem;
}

.date-time {
  display: flex;
  @include respond-to(mobile) {
    flex-direction: column;
    gap: $spacing-s;
  }
  gap: $spacing-m;
  & > * {
    flex: 1;
  }
}
</style>
