<template>
  <VueDatePicker
    v-model="date"
    auto-apply
    :enable-time-picker="false"
    locale="fr"
    :format="format"
    class="sgilt-date-picker"
    :day-class="getDayClass"
    :state="choiceState"
  >
    <template #action-extra>
      <!-- Extra info : color legend -->
      <p class="extra-info" v-if="showExtraInfo">
        <span class="dot booked" /> {{ $t('date-picker.booked') }} <span class="dot option" />
        {{ $t('date-picker.option') }}
      </p>
    </template>
  </VueDatePicker>
</template>

<script setup lang="ts">
import VueDatePicker from '@vuepic/vue-datepicker'
import dayjs from 'dayjs'
import 'dayjs/locale/fr'
import { computed } from 'vue'
import { dateArrayContains } from '@/utils/ArrayUtils'

const date = defineModel<Date>()
const format = (date: Date) => dayjs(date).locale('fr').format('dddd DD MMM YYYY')

// dates to highlight
const props = defineProps<{
  bookedDates?: Date[]
  optionDates?: Date[]
}>()

const getDayClass = (date: Date) => {
  if (dateArrayContains(props.bookedDates || [], date)) return 'date booked'
  if (dateArrayContains(props.optionDates || [], date)) return 'date option'
  return ''
}

const showExtraInfo = computed(
  () => (props.bookedDates?.length || 0) > 0 || (props.optionDates?.length || 0) > 0,
)

const choiceState = computed(() => {
  if (date.value && dateArrayContains(props.bookedDates || [], date.value)) return false
  if (date.value && dateArrayContains(props.optionDates || [], date.value)) return true
  return undefined
})
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
  --dp-success-color: #{$option-color};
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

.date {
  font-weight: bold;
  &::after {
    content: '.';
    position: absolute;
    font-size: 4rem;
  }

  &.booked::after {
    color: $color-booked;
  }

  &.option::after {
    color: $color-option;
  }
}

.dot {
  width: 1em;
  height: 1em;
  border-radius: 50%;
  margin: 0 $spacing-s;

  &.booked {
    background: $color-booked;
  }

  &.option {
    background: $color-option;
  }
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
