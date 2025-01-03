<template>
  <VueDatePicker
    v-model="date"
    :auto-apply="true"
    :enable-time-picker="false"
    locale="fr"
    :format="format"
    class="sgilt-date-picker"
  />
</template>

<script setup lang="ts">
import VueDatePicker from '@vuepic/vue-datepicker'
import dayjs from 'dayjs'
import 'dayjs/locale/fr'

const date = defineModel<Date>()

const format = (date: Date) => dayjs(date).locale('fr').format('dddd DD MMM YYYY')
</script>

<style lang="scss">
@import '@vuepic/vue-datepicker/dist/main.css';

.sgilt-date-picker {
  width: 17em;
}

.dp__theme_light {
  --dp-border-radius: #{$input-border-radius};
  --dp-border-color: #{$shadow-m};
  --dp-input-padding: 0.75rem;
  --dp-font-size: 1em;
  --dp-text-color: #{$color-primary};

  --dp-primary-color: #{$color-accent};
  --dp-icon-color: #{$color-primary};
}

.dp__input {
  text-align: center;
  padding-right: 2.5rem;
}

.dp__input:focus {
  outline: $focus-outline;
  outline-offset: $focus-outline-offset;
}
</style>
