<template>
  <VueDatePicker
    v-model="model"
    :auto-apply="true"
    multi-dates
    :enable-time-picker="false"
    locale="fr"
    :format="format"
    class="sgilt-date-picker"
    @update:model-value="onChange"
  />
</template>

<script setup lang="ts">
import VueDatePicker from '@vuepic/vue-datepicker'
import dayjs from 'dayjs'
import 'dayjs/locale/fr'
import { ref } from 'vue'

const date = defineModel<Date>()

const model = ref(date.value ? [date.value] : [])

const format = (date: Date) => dayjs(date).locale('fr').format('dddd DD MMM YYYY')

const onChange = (value: Date[]) => {
  if (!value || value.length === 0) {
    date.value = undefined
  } else {
    const selectedDate = value[value.length - 1]
    date.value = selectedDate
    model.value = [selectedDate]
  }
}
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
