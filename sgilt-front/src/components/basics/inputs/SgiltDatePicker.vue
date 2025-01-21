<template>
  <VueDatePicker
    v-model="date"
    :auto-apply="true"
    :enable-time-picker="false"
    locale="fr"
    :format="format"
    class="sgilt-date-picker"
    :day-class="getDayClass"
  >
    <template #action-extra="">
      <p class="extra-info">
        <span class="dot-booked" /> réservé <span class="dot-option" /> sous option
      </p>
    </template>
  </VueDatePicker>
</template>

<script setup lang="ts">
import VueDatePicker from '@vuepic/vue-datepicker'
import dayjs from 'dayjs'
import 'dayjs/locale/fr'

const date = defineModel<Date>()
const format = (date: Date) => dayjs(date).locale('fr').format('dddd DD MMM YYYY')

// dates to highlight
const props = defineProps<{
  bookedDates?: Date[]
  optionDates?: Date[]
}>()

const getDayClass = (date: Date) => {
  const date_ = dayjs(date)
  if (props.bookedDates?.some((d) => dayjs(d).isSame(date_, 'day'))) return 'booked-date'
  if (props.optionDates?.some((d) => dayjs(d).isSame(date_, 'day'))) return 'option-date'
  return ''
}
</script>

<style lang="scss">
@import '@vuepic/vue-datepicker/dist/main.css';

.sgilt-date-picker {
  width: 100%;
}

.dp__theme_light {
  --dp-border-radius: 1em;
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

.dp__active_date {
  font-weight: bold;
}

.option-date {
  font-weight: bold;
  &::after {
    content: '.';
    position: absolute;
    color: orange;
    font-size: 4rem;
  }
}

.booked-date {
  font-weight: bold;
  &::after {
    content: '.';
    position: absolute;
    color: red;
    font-size: 4rem;
  }
}

.dot-booked {
  width: 1em;
  height: 1em;
  border-radius: 50%;
  background: red;
  margin: 0 $spacing-s;
}

.dot-option {
  width: 1em;
  height: 1em;
  border-radius: 50%;
  background: orange;
  margin: 0 $spacing-s;
}

.extra-info {
  font-size: $font-size-base;
  color: $color-subtext;
  margin-left: $spacing-s;
  display: flex;
  flex-direction: row;
  align-items: start;
}
</style>
