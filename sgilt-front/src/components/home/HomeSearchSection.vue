<template>
  <section id="search-banner">
    <div class="title">
      <!-- background image -->
      <img src="@/assets/images/home_bg.jpg" class="bg-mobile" />
      <!-- title -->
      <div class="title-text">
        <span class="title-thin">{{ $t('home.search-banner.title-part-1') }}</span>
        <span class="title-bold">{{ $t('home.search-banner.title-part-2') }}</span>
        <h3 class="tagline">{{ $t('home.search-banner.tagline') }}</h3>
      </div>
    </div>
    <!-- Search fields -->
    <div class="search-fields">
      <!-- Date picker -->
      <SgiltDatePicker v-model="reservationStore.dateReservation" class="date-picker" />
      <!-- Event type select -->
      <SgiltSelect
        :options="options"
        v-model="reservationStore.eventType"
        focusable
        class="select-event-type"
      >
        <template v-slot:left-icon>
          <IconRocket />
        </template>
      </SgiltSelect>
      <!-- Search button -->
      <SgiltButton @click="search">{{ $t('home.search-banner.button') }}</SgiltButton>
    </div>
  </section>
</template>

<script setup lang="ts">
import SgiltDatePicker from '@/components/basics/inputs/SgiltDatePicker.vue'
import SgiltSelect from '@/components/basics/inputs/SgiltSelect.vue'
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'
import router from '@/router'
import { useEventTypeStore } from '@/stores/event-type.store'
import IconRocket from '@/components/icons/IconRocket.vue'
import { useReservationStore } from '@/stores/reservation.store'

const reservationStore = useReservationStore()

// Types d'événements
const options = [
  ...[{ value: '-1', label: 'Votre événement' }],
  ...useEventTypeStore().events.map((eventType) => ({
    value: eventType.id,
    label: eventType.name,
  })),
]

// Recherche
const search = () => {
  router.push({ name: 'search' })
}
</script>

<style lang="scss">
$overlay: $shadow-l;

#search-banner {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  position: relative;

  align-items: center;
  justify-content: center;

  background-color: $overlay;
  @include respond-to(mobile) {
    background-color: $color-secondary;
  }

  color: $color-secondary;
}

.title {
  text-align: center;
  font-size: 3em;
  line-height: 2em;
  position: relative;

  .title-text {
    text-shadow: 1px 1px 10px $color-primary;
  }

  // Region title in mobile view
  .bg-mobile {
    display: none;
    @include respond-to(mobile) {
      display: block;
      width: 100%;
      height: auto;
    }
  }

  @include respond-to(mobile) {
    .title-text {
      position: absolute;
      top: 0;
      bottom: 0;
      left: 0;
      right: 0;
      display: flex;
      gap: $spacing-s;
      flex-direction: column;
      justify-content: center;
      background-color: $overlay;
      color: $color-white;
      padding: $spacing-m;
      .title-thin {
        font-size: 0.5em;
        line-height: 1em;
      }
      .title-bold {
        font-size: 1em;
        line-height: 1em;
      }
    }
  }
  // End region title in mobile view

  .title-text {
    padding: 0;
    margin: 0;
  }

  .title-thin {
    font-weight: 300;
  }
  .title-bold {
    font-weight: bold;
  }
}

.tagline {
  text-align: center;
  font-weight: 400;
  margin: 0;

  @include respond-to(desktop) {
    margin: $spacing-m 0;
  }
}

.search-fields {
  display: flex;
  flex-direction: row;

  justify-content: center;

  padding: 0 $spacing-m;
  margin-bottom: $spacing-xl;

  .date-picker,
  .select-event-type {
    flex: 1;
  }

  @include respond-to(desktop) {
    .date-picker,
    .select-event-type {
      width: 18em;
    }
  }

  @include respond-to(tablet) {
    flex-direction: column;
    align-items: center;
    width: 20rem;
  }

  gap: $spacing-xl;
  @include respond-to(mobile) {
    gap: $spacing-m;
    padding: 0;
    margin-bottom: $spacing-m;
    align-items: initial;
    .date-picker,
    .select-event-type {
      margin: 0;
      padding: 0;
    }
  }
}
</style>
