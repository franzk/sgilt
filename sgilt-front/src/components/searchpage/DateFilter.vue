<template>
  <div class="filter-date">
    <SgiltDatePicker language="fr" class="date-event" v-model="date" />
  </div>
</template>

<script setup lang="ts">
import SgiltDatePicker from '@/components/basics/SgiltDatePicker.vue'

const date = defineModel<Date>()
</script>

<style scoped lang="scss"></style>
