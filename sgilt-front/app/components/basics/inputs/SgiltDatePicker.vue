<template>
  <ClientOnly>
    <VueDatePicker
      v-model="date"
      auto-apply
      :disabled="disabled"
      :time-config="{ enableTimePicker: false }"
      :locale="fr"
      :formats="format"
      class="sgilt-date-picker"
      :day-class="getDayClass"
      :state="choiceState"
      :placeholder="placeholder"
      teleport="body"
    >
      <template #action-extra>
        <!-- Extra info : color legend -->
        <p class="extra-info" v-if="showExtraInfo">
          <span class="dot booked" /> {{ $t('date-picker.booked') }} <span class="dot option" />
          {{ $t('date-picker.option') }}
        </p>
      </template>
    </VueDatePicker>
    <template #fallback>
      <div class="select-skeleton">
        <div class="skeleton-icon" />
        <div class="skeleton-text" />
      </div>
    </template>
  </ClientOnly>
</template>

<script setup lang="ts">
import { VueDatePicker } from '@vuepic/vue-datepicker'
import dayjs from 'dayjs'
import 'dayjs/locale/fr'
import { fr } from 'date-fns/locale/fr'
import { computed } from 'vue'
import { dateArrayContains } from '@/utils/ArrayUtils'

const date = defineModel<Date>()
const format = { input: (date: Date) => dayjs(date).locale('fr').format('dddd DD MMM YYYY') }

// dates to highlight
const props = defineProps<{
  bookedDates?: Date[]
  optionDates?: Date[]
  disabled?: boolean
  placeholder?: string
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
@use '@/assets/styles/base' as *;
@use '@/assets/styles/borders' as *;
@use '@/assets/styles/colors' as *;
@import '@vuepic/vue-datepicker/dist/main.css';

$border-radius: 1.4rem;

.sgilt-date-picker {
  width: 100%;
  * {
    font-size: inherit;
  }
}

.dp__theme_light {
  --dp-border-radius: #{$border-radius};
  --dp-border-color: #{$shadow-m};
  --dp-input-padding: 0.75rem;
  --dp-font-size: inherit;
  --dp-text-color: #{$color-primary};

  --dp-primary-color: #{$color-accent};
  --dp-hover-color: rgba(255, 191, 0, 0.14);
  --dp-icon-color: #{$color-accent};
  --dp-success-color: #{$state-option};
  --dp-cell-border-radius: 0.5em;
  --dp-cell-padding: 1.2rem;
}

@media (min-width: 850px) {
  .dp__theme_light {
    --dp-cell-padding: 1rem;
  }
}

@media (max-width: 300px) {
  .dp__theme_light {
    --dp-cell-padding: 1rem;
  }
}

.dp__input {
  text-align: center;
  padding-right: 2.5rem;
  font-size: inherit;
  color: #6b635c;
  font-weight: 500;
  height: 100%;
  box-shadow:
    0 0.125rem 0.375rem rgba(0, 0, 0, 0.04),
    0 0.0625rem 0.125rem rgba(0, 0, 0, 0.03);

  font-family: inherit;
  line-height: 1;
  letter-spacing: 0.0005rem;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;

  &::placeholder {
    font-weight: 500;
    font-family: inherit;
    opacity: 1;
    color: #6b635c;
    font-size: inherit;
  }
}

.dp__input_icon {
  width: 2.5rem;
  margin: 0;
  padding: 0;
  stroke-width: 1px;
}

.dp__input_wrap {
  font-size: inherit;
}

.dp__menu {
  border-radius: $border-radius;
  box-shadow:
    0 0.25rem 0.5rem rgba(0, 0, 0, 0.1),
    0 0.75rem 1.75rem rgba(0, 0, 0, 0.08);
  background:
    radial-gradient(
      1100px 520px at 50% -10%,
      rgba($color-accent, 0.22) 0%,
      rgba(255, 255, 255, 0) 55%
    ),
    linear-gradient(180deg, #fffdf6 0%, #ffffff 60%);
}

.dp__month_year_wrap {
  font-size: inherit;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.dp__calendar_header_item {
  padding-top: 0;
}

.dp__input:focus {
  // outline: $focus-outline;
  // outline-offset: $focus-outline-offset;
  border-color: rgba(255, 191, 0, 0.75);
  box-shadow: 0 0 0 3px rgba(255, 191, 0, 0.25); /* halo doux */
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
    color: $state-booked;
  }

  &.option::after {
    color: $state-option;
  }
}

.dot {
  width: 1em;
  height: 1em;
  border-radius: 50%;
  margin: 0 $spacing-s;

  &.booked {
    background: $state-booked;
  }

  &.option {
    background: $state-option;
  }
}

.extra-info {
  // font-size: $font-size-base;
  color: $color-subtext;
  margin-left: $spacing-s;
  display: flex;
  flex-direction: row;
  align-items: start;
}
</style>
