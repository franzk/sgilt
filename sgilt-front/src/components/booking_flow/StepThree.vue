<template>
  <div class="recap-container">
    <aside class="timeline-bar">
      <h3 @click="onTimelineTitleClick">
        {{ $t('booking-flow.step-3.timeline-title') }}
        <span v-if="!showTimeline">🔽</span>
      </h3>
      <RecapTimeLine v-if="showTimeline" />
    </aside>

    <div v-if="mobileView && !showRecap" class="see-recap" @click="onSeeRecapClick">
      👉 Revoir ma réservation
    </div>

    <!-- Colonne droite : Récap -->
    <div class="recap-section" v-if="!mobileView || showRecap">
      <h3 v-if="mobileView">{{ $t('booking-flow.step-3.recap-title.mobile') }}</h3>
      <h3 class="recap-section-title" v-else>
        {{ $t('booking-flow.step-3.recap-title.desktop') }}
      </h3>

      <!-- infos : location & date-time -->
      <div class="infos">
        <RecapCard :title="reservationStore.location!!" icon="Place" class="location" />
        <RecapCard :title="dateTimeReservation" icon="Calendar" class="datetime" />
      </div>

      <!-- booked partner -->
      <PartnerItem :partner="reservationStore.partner!!" hidePrice />

      <!-- price section -->
      <RecapCard icon="Price" :title="priceTitle">
        <span v-if="!!priceDetail" v-html="priceDetail" />
      </RecapCard>

      <!-- user infos -->
      <RecapCard icon="Arobase" :title="$t('booking-flow.step-3.titles.email')">
        {{ reservationStore.email }}
      </RecapCard>

      <!-- message -->
      <RecapCard
        v-if="reservationStore.message"
        icon="Mail"
        :title="$t('booking-flow.step-3.titles.message')"
      >
        {{ reservationStore.message }}
      </RecapCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useReservationStore } from '@/stores/reservation.store'
import RecapTimeLine from '@/components/booking_flow/RecapTimeLine.vue'
import PartnerItem from '@/components/partner/PartnerItem.vue'
import RecapCard from '@/components/booking_flow/RecapCard.vue'
import dayjs from 'dayjs'
import { computed, ref } from 'vue'
import { mobileView } from '@/utils/StyleUtils'

const reservationStore = useReservationStore()

const showRecap = ref(false)
const showTimeline = ref(true)
const onTimelineTitleClick = () => {
  if (mobileView) showTimeline.value = true
}
const onSeeRecapClick = () => {
  if (mobileView) {
    showRecap.value = true
    showTimeline.value = false
  }
}

// date & time
const dateTimeReservation = computed(
  () =>
    `${dayjs(reservationStore.dateReservation).format('DD/MM/YYYY')} à ${reservationStore.timeReservation}`,
)

// price
const priceTitle = `Tarif : <strong>${reservationStore.totalPrice} €</strong>`
const priceDetail = computed(() => {
  if (reservationStore.quantity) {
    if (reservationStore.quantity) {
      return `
      💳 ${reservationStore.price?.title}

      ${reservationStore.price?.price} € / ${reservationStore.price?.unity} * ${reservationStore.quantity} ${reservationStore.price?.unity} = <strong>${reservationStore.totalPrice} €</strong>
      `
    }
  }
  return null
})
</script>

<style scoped lang="scss">
.recap-container {
  display: flex;

  @include respond-to(mobile) {
    flex-direction: column;
    padding: 0 $spacing-m;
    gap: $spacing-s;
    h3 {
      margin: $spacing-s 0;
    }
  }

  gap: $spacing-l;
  padding: $spacing-m;

  .timeline-bar {
    border-right: 2px solid $color-divider;
    @include respond-to(mobile) {
      border-right: none;
    }

    width: 20rem;
    height: auto;
  }

  .see-recap {
    display: flex;
    justify-content: center;
    margin-top: $spacing-m;
    text-decoration: underline;
  }

  .recap-section {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: $spacing-m;

    @include respond-to(mobile) {
      border-top: 1px solid $color-divider;
    }

    .recap-section-title {
      margin-bottom: 0.3rem;
    }

    .infos {
      display: flex;
      flex-direction: row;
      gap: $spacing-m;

      @include respond-to(tablet) {
        flex-direction: column;
      }

      .datetime {
        width: 12rem;
        @include respond-to(tablet) {
          width: initial;
        }
        justify-content: center;
      }

      .location {
        flex: 1;
      }
    }
  }
}
</style>
