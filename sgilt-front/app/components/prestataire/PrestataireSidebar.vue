<template>
  <div class="sidebar-body">
    <div v-if="!isEdit" class="sidebar-block">
      <SgiltDatePicker
        v-model="dateModel"
        :booked-dates="unavailableDatesAsDate"
        :disabled="disableDate"
        :placeholder="$t('provider.details.verify-date')"
        :error="!!dateError"
      />
      <Transition name="fade">
        <p v-if="dateError" class="date-error">{{ dateError }}</p>
        <!-- reintroduce when we handle availability
        <div v-else-if="dateModel" class="availability-badge" :class="availabilityClass">
          <span class="icon">{{ availabilityIcon }}</span>
          <span>{{ availabilityLabel }}</span>
        </div-->
      </Transition>
    </div>

    <div v-if="isEdit || prestataire.budget" class="sidebar-block sidebar-budget">
      <h3 class="title">{{ $t('provider.details.rates') }}</h3>
      <EditableText
        as="p"
        v-model="prestataire!.budget"
        field="budget"
        :editable="isEdit"
        class="text"
        @commit="saveField('budget', $event)"
      />
    </div>

    <SgiltButton v-if="!isEdit" class="sidebar-cta" @click="emit('select-intent')">
      {{ $t('provider.details.send-request') }}
    </SgiltButton>
    <EditActionsBar v-else class="sidebar-edit-actions" />
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import EditableText from '~/components/prestataire/EditableText.vue'
import EditActionsBar from '~/components/prestataire/EditActionsBar.vue'
import type { PrestataireDetail } from '~/data/prestataire/domain/PrestataireDetail'
import type { DisplayMode } from '~/types/prestataire'

const { t } = useI18n()

const props = defineProps<{
  prestataire: PrestataireDetail
  displayMode: DisplayMode
  disableDate?: boolean
  dateError: string | null
}>()

const { prestataire, saveField } = usePrestataire()
const isEdit = computed(() => props.displayMode === 'edit')

const emit = defineEmits<{
  'select-intent': []
}>()

const { dateModel } = useSearchUi()

const unavailableDatesAsDate = computed<Date[]>(() =>
  props.prestataire.unavailableDates.map((d) => new Date(d)),
)

const isUnavailable = computed(() => {
  if (!dateModel.value) return false
  const iso = dateModel.value.toISOString().slice(0, 10)
  return props.prestataire.unavailableDates.includes(iso)
})

const availabilityIcon = computed(() => (isUnavailable.value ? '✗' : '✓'))
const availabilityLabel = computed(() =>
  isUnavailable.value
    ? t('provider.details.availability.unavailable')
    : t('provider.details.availability.available'),
)
const availabilityClass = computed(() => (isUnavailable.value ? 'unavailable' : 'available'))
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.sidebar-body {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  padding: $spacing-m;

  @media (min-width: $breakpoint-desktop) {
    padding: 1.5rem;
  }
}

.sidebar-block {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
}

.sidebar-budget {
  display: none;

  @media (min-width: $breakpoint-desktop) {
    display: unset;
    padding-top: 1.25rem;
    border-top: 1px solid rgba(0, 0, 0, 0.08);

    .title {
      font-family: 'Cormorant Garamond', serif;
      font-size: 1.1rem;
      font-weight: 600;
      color: $color-primary;
      margin: 0 0 0.5rem;
    }

    .text {
      font-size: 0.9rem;
      color: $text-secondary;
      line-height: 1.6;
      margin: 0;
    }
  }
}

.sidebar-cta,
.sidebar-edit-actions {
  display: none;

  @media (min-width: $breakpoint-desktop) {
    display: flex;
  }
}

.sidebar-cta {
  @media (min-width: $breakpoint-desktop) {
    align-self: center;
  }
}

.date-error {
  font-size: 0.82rem;
  color: $state-error;
  margin: 0;
}

.availability-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.35rem 0.8rem;
  border-radius: 2rem;
  font-size: 0.82rem;
  font-weight: 600;
  width: fit-content;

  &.available {
    background: rgba(#2d9e6b, 0.1);
    color: #1e7a51;
  }
  &.unavailable {
    background: rgba(#c0392b, 0.1);
    color: #a93226;
  }

  .icon {
    font-size: 0.75rem;
    font-weight: 700;
  }
}

.fade-enter-active,
.fade-leave-active {
  transition:
    opacity 200ms ease,
    transform 200ms ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>
