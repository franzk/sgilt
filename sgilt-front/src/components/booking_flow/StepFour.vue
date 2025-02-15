<template>
  <div class="step-4">
    <!-- informations -->
    <div class="infos">
      <p>
        {{ $t('booking-flow.step-4.infos.first.sub-1') }}
        <strong>{{ reservationStore.email }}</strong>
        {{ $t('booking-flow.step-4.infos.first.sub-2') }}
        <b>{{ $t('booking-flow.step-4.infos.first.sub-3') }}</b>
      </p>
      <p>
        <b>{{ $t('booking-flow.step-4.infos.second.sub-1') }}</b>
        {{ $t('booking-flow.step-4.infos.second.sub-2') }}
        <strong>{{ reservationStore.partner?.title }}</strong>
        {{ $t('booking-flow.step-4.infos.second.sub-3') }}
      </p>
    </div>

    <!-- email action buttons  -->
    <div class="cta-email">
      <SgiltButton @click="resendEmail" :disabled="isResending">
        {{
          isResending ? $t('booking-flow.step-4.cta.sending') : $t('booking-flow.step-4.cta.resend')
        }}
      </SgiltButton>

      <SgiltButton @click="toggleEmailInput" class="toggle-email-btn" variant="secondary">
        {{ $t('booking-flow.step-4.cta.new-mail') }}
      </SgiltButton>
    </div>

    <!-- email change form -->
    <SgiltFormGroup v-if="showEmailChange" :title="$t('booking-flow.step-4.change-mail.title')">
      <div class="change-mail-form">
        <SgiltInput
          type="text"
          v-model="newEmail"
          :placeholder="$t('booking-flow.step-4.change-mail.placeholder')"
        />
        <SgiltButton @click="resendEmail" class="btn send-new" :disabled="isResending">
          {{
            isResending
              ? $t('booking-flow.step-4.change-mail.sending')
              : $t('booking-flow.step-4.change-mail.send')
          }}
        </SgiltButton>
      </div>
    </SgiltFormGroup>

    <!-- validation info -->
    <div class="validation-info">
      <p>
        {{ $t('booking-flow.step-4.validation-info.start') }}
        <strong>{{ reservationStore.partner?.title }}</strong>
        {{ $t('booking-flow.step-4.validation-info.end') }}
      </p>
    </div>

    <!-- illustration -->
    <div class="sgilt-illustration">
      <BookingFlowIllustration />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useReservationStore } from '@/stores/reservation.store'
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'
import SgiltFormGroup from '@/components/basics/inputs/SgiltFormGroup.vue'
import SgiltInput from '@/components/basics/inputs/SgiltInput.vue'
import BookingFlowIllustration from './BookingFlowIllustration.vue'

const reservationStore = useReservationStore()

const isResending = ref(false)
const showEmailChange = ref(false)
const newEmail = ref('')

const toggleEmailInput = () => {
  showEmailChange.value = !showEmailChange.value
}

const resendEmail = () => console.log('Renvoyer l’email')
</script>

<style scoped lang="scss">
.step-4 {
  flex: 1;
  padding: $spacing-xl $spacing-xl 0 $spacing-xl;
  @include respond-to(mobile) {
    padding: 0;
  }
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  text-align: center;
  line-height: $line-height-h3;
}

.infos {
  @include respond-to(mobile) {
    text-align: left;
  }
}

.sgilt-illustration {
  flex: 1;
  display: flex;
  justify-content: center;
  padding-top: $spacing-m;
  @include respond-to(mobile) {
    padding-top: 0;
  }
  img {
    height: 5rem;
  }
  @include respond-to(tablet-only) {
    padding: 0;
    align-items: center;
    img {
      height: unset;
    }
  }
}

.cta-email {
  display: flex;
  justify-content: center;
  gap: $spacing-xxl;
  @include respond-to(mobile) {
    gap: $spacing-m;
  }
  > * {
    width: 11rem;
    @include respond-to(mobile) {
      width: unset;
    }
  }
}

.change-mail-form {
  display: flex;
  gap: $spacing-m;

  :first-child {
    flex: 1;
  }
}

.validation-info {
  font-size: $font-size-base;
  color: $color-subtext;
  font-style: italic;
  margin-top: $spacing-s;
  display: flex;
  flex-direction: column;
}
</style>
