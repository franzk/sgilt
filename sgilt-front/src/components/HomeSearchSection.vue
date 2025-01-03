<template>
  <section id="search-banner">
    <div class="title">
      <img src="@/assets/images/home_bg.jpg" class="bg-mobile" />
      <div class="title-text">
        <span class="title-thin">{{ $t('home.search-banner.title-part-1') }}</span>
        <span class="title-bold">{{ $t('home.search-banner.title-part-2') }}</span>
      </div>
    </div>
    <div class="search-fields">
      <SgiltDatePicker v-model="date" class="date-picker" />
      <SgiltSelect :options="options" v-model="selectedValue" class="select-event" />
      <SgiltSimpleButton @click="search">{{ $t('home.search-banner.button') }}</SgiltSimpleButton>
    </div>
  </section>
  {{}}
</template>

<script setup lang="ts">
import SgiltDatePicker from '@/components/basics/inputs/SgiltDatePicker.vue'
import SgiltSelect from '@/components/basics/inputs/SgiltSelect.vue'
import SgiltSimpleButton from '@/components/basics/buttons/SgiltSimpleButton.vue'

import router from '@/router'
import { ref } from 'vue'
import type { LocationQueryRaw } from 'vue-router'
import { useEventTypeStore } from '@/stores/event-type.store'
import dayjs from 'dayjs'

// Filtre date
const date = ref<Date>()

// Types d'événements
const options = useEventTypeStore().events.map((eventType) => ({
  value: eventType.id,
  label: eventType.name,
}))
const selectedValue = ref<string>()

// Recherche
const search = () => {
  const query: LocationQueryRaw = {}
  if (date.value) {
    query.date = dayjs(date.value).format('YYYY-MM-DD')
  }
  if (selectedValue.value) {
    query.event = selectedValue.value
  }
  router.push({ path: '/search', query })
}
</script>

<style lang="scss">
#search-banner {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  position: relative;

  align-items: center;
  justify-content: center;

  // padding: $spacing-m;

  background-color: $shadow-m;
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
      background-color: $shadow-m;
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

.search-fields {
  display: flex;
  flex-direction: row;

  justify-content: center;

  padding: 0 $spacing-m;
  margin-bottom: $spacing-m;

  .select-event {
    line-height: 45px;
    width: 17em;
  }

  @include respond-to(tablet) {
    flex-direction: column;
    align-items: center;
  }

  gap: $spacing-xl;
  @include respond-to(mobile) {
    gap: $spacing-m;
    padding: 0;
    align-items: initial;
    .date-picker,
    .select-event {
      margin: 0;
      padding: 0;
    }
  }
}
</style>
