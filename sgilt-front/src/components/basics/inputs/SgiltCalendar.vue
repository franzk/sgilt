<template>
  <VueDatePicker
    v-model="date"
    inline
    six-weeks
    :enable-time-picker="false"
    locale="fr"
    class="calendar-component"
  >
    <template #action-buttons>
      <SgiltSimpleButton @click="$emit('select-date', date!)">Réserver</SgiltSimpleButton>
    </template>
  </VueDatePicker>
</template>

<script setup lang="ts">
import VueDatePicker from '@vuepic/vue-datepicker'
import SgiltSimpleButton from '@/components/basics/buttons/SgiltSimpleButton.vue'
import { ref } from 'vue'

const date = ref(null)

// define an emit function to emit the select-date event with the date value
defineEmits<{ 'select-date': [Date] }>()
</script>

<style lang="scss">
.dp__flex_display {
  display: block;
}
</style>
