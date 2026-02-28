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

const date = defineModel<Date | null>()
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
@import '@vuepic/vue-datepicker/dist/main.css';

// input
// -- contenu
$input-padding: 0.75rem;
$input-font-weight: 500;
$input-letter-spacing: 0.005rem;
$placeholder-text-opacity: 1;

// -- icon
$icon-color: $color-accent;
$input-text-color: rgba($color-primary, 0.8);
$icon-width: 2.5rem;
$icon-stroke-width: 1px;

// calendar
// -- global
$calendar-text-color: $color-primary;

// -- cell
$today-border-color: $color-accent;
$cell-hover-color: rgba($color-accent, 0.14);
$cell-border-radius: 0.5em;
$cell-padding: 1.2rem;
$cell-padding-small: 1rem;

// -- menu
$menu-border-radius: 1.4rem;
$menu-box-shadow:
  0 0.25rem 0.5rem rgba(0, 0, 0, 0.1),
  0 0.75rem 1.75rem rgba(0, 0, 0, 0.08);
$menu-background:
  radial-gradient(
    1100px 520px at 50% -10%,
    rgba($color-accent, 0.22) 0%,
    rgba(255, 255, 255, 0) 55%
  ),
  linear-gradient(180deg, #fffdf6 0%, #ffffff 60%);

// style
.sgilt-date-picker {
  width: 100%;
  * {
    font-size: inherit;
  }
}

.dp__theme_light {
  --dp-border-radius: #{$input-border-radius};
  --dp-border-color: #{$input-border-color};
  --dp-input-padding: #{$input-padding};
  --dp-font-size: inherit;
  --dp-text-color: #{$calendar-text-color};

  --dp-primary-color: #{$today-border-color};
  --dp-hover-color: #{$cell-hover-color};
  --dp-icon-color: #{$icon-color};
  --dp-success-color: #{$state-option}; // TODO : à vérifier
  --dp-cell-border-radius: #{$cell-border-radius};
  --dp-cell-padding: #{$cell-padding};
}

@media (min-width: 850px) {
  .dp__theme_light {
    --dp-cell-padding: #{$cell-padding-small};
  }
}

@media (max-width: 300px) {
  .dp__theme_light {
    --dp-cell-padding: #{$cell-padding-small};
  }
}

.dp__input {
  text-align: center;
  padding-right: 2.5rem;
  font-size: inherit;
  color: $input-text-color;
  font-weight: $input-font-weight;
  height: 100%;
  box-shadow: $input-box-shadow;

  font-family: inherit;
  line-height: 1;
  letter-spacing: $input-letter-spacing;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;

  &::placeholder {
    font-weight: $input-font-weight;
    font-family: inherit;
    opacity: $placeholder-text-opacity;
    font-size: inherit;
  }
}

.dp__input_icon {
  width: $icon-width;
  margin: 0;
  padding: 0;
  stroke-width: $icon-stroke-width;
}

.dp__input_wrap {
  font-size: inherit;
}

.dp__menu {
  border-radius: $menu-border-radius;
  box-shadow: $menu-box-shadow;
  background: $menu-background;
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
  border-color: $input-focus-border-color;
  box-shadow: $input-focus-box-shadow;
}

.dp__active_date {
  font-weight: bold;
}

/* TODO : à revoir quand on fera les states

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
} */
</style>
