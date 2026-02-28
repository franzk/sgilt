<template>
  <ClientOnly>
    <div class="sgilt-date-filter">
      <span class="sgilt-date-filter_prev" @click="prevDay()">&lt;</span>
      <span class="sgilt-date-filter_date">
        <SgiltDatePicker v-model="modelValue" />
      </span>
      <span class="sgilt-date-filter_next" @click="nextDay()">&gt;</span>
    </div>
  </ClientOnly>
</template>

<script setup lang="ts">
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import dayjs from 'dayjs'

const modelValue = defineModel<Date | null>()

const prevDay = () => {
  if (modelValue.value) modelValue.value = dayjs(modelValue.value).subtract(1, 'day').toDate()
}

const nextDay = () => {
  if (modelValue.value) modelValue.value = dayjs(modelValue.value).add(1, 'day').toDate()
}
</script>

<style scoped lang="scss">
.sgilt-date-filter {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.5rem;
  width: 100%;

  &_prev,
  &_next {
    cursor: pointer;
    font-size: 1.5rem;
    color: rgba($color-primary, 0.8);
    user-select: none;
  }

  &_date {
    flex: 1;

    :deep(.sgilt-date-picker) .dp__input {
      border-radius: 0;
    }
  }
}
</style>
