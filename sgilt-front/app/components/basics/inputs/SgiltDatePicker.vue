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
      :ui="uiConfig"
      :state="choiceState"
      :placeholder="placeholder"
      :teleport="inline ? false : 'body'"
      :inline="inline"
      @update-month-year="emit('update-month-year', $event)"
    >
      <template v-if="showExtraInfo" #action-extra>
        <p class="extra-info">
          <span v-if="props.bookedDates?.length" class="dot booked" />
          <span v-if="props.bookedDates?.length && !props.indisponibleDates?.length">{{
            $t('date-picker.indisponible')
          }}</span>
          <span v-else-if="props.bookedDates?.length">{{ $t('date-picker.booked') }}</span>

          <span v-if="props.indisponibleDates?.length" class="dot indisponible" />
          <span v-if="props.indisponibleDates?.length">{{ $t('date-picker.indisponible') }}</span>
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

const emit = defineEmits<{
  'update-month-year': [value: { month: number; year: number }]
}>()

const format = { input: (date: Date) => dayjs(date).locale('fr').format('dddd DD MMM YYYY') }

const props = defineProps<{
  bookedDates?: Date[]
  indisponibleDates?: Date[]
  disabled?: boolean
  placeholder?: string
  inline?: boolean
}>()

const getDayClass = (date: Date) => {
  if (dateArrayContains(props.bookedDates || [], date)) return 'date booked'
  if (dateArrayContains(props.indisponibleDates || [], date)) return 'date indisponible'
  return ''
}

const uiConfig = computed(() => ({ dayClass: getDayClass }))

const showExtraInfo = computed(
  () => (props.bookedDates?.length || 0) > 0 || (props.indisponibleDates?.length || 0) > 0,
)

const choiceState = computed(() => {
  if (date.value && dateArrayContains(props.bookedDates || [], date.value)) return false
  return undefined
})
</script>

<style lang="scss">
@use '@/assets/styles/base' as *;

@import '@vuepic/vue-datepicker/dist/main.css';

// ─── Tokens ───────────────────────────────────────────────────────────────────

// input
$input-padding: 0.75rem;
$input-font-weight: 500;
$input-letter-spacing: 0.005rem;
$placeholder-text-opacity: 1;
$icon-color: $color-accent;
$input-text-color: rgba($color-primary, 0.8);
$icon-width: 2.5rem;
$icon-stroke-width: 1px;

// calendar
$calendar-text-color: $color-primary;
$today-border-color: $color-accent;
$cell-hover-color: rgba($color-accent, 0.14);
$cell-border-radius: 0.5em;
$cell-padding: 1.2rem;
$cell-padding-small: 1rem;
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

// ─── Composant ────────────────────────────────────────────────────────────────

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
  --dp-success-color: #{$state-option};
  --dp-cell-border-radius: #{$cell-border-radius};
  --dp-cell-padding: #{$cell-padding};
}

@media (min-width: $breakpoint-desktop) {
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

  &:focus {
    border-color: $input-focus-border-color;
    box-shadow: $input-focus-box-shadow;
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
.dp__active_date {
  font-weight: bold;
}

// ─── Points colorés sur les jours ────────────────────────────────────────────

.date {
  position: relative;

  // Point centré sous le chiffre du jour
  &::after {
    content: '';
    position: absolute;
    bottom: 0px;
    left: 50%;
    transform: translateX(-50%);
    width: 5px;
    height: 5px;
    border-radius: 50%;
  }

  &.booked::after {
    background: $state-booked;
  }

  &.indisponible::after {
    background: $text-secondary;
    opacity: 0.45;
  }
}

// ─── Légende (action-extra) ───────────────────────────────────────────────────

.dot {
  display: inline-block;
  width: 0.55em;
  height: 0.55em;
  border-radius: 50%;
  margin: 0 0.3em;
  vertical-align: middle;

  &.booked {
    background: $state-booked;
  }
  &.indisponible {
    background: $text-secondary;
    opacity: 0.45;
  }
}

.extra-info {
  color: $color-subtext;
  margin-left: $spacing-s;
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 0.2em;
  font-size: 0.8rem;
}
</style>
