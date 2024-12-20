<template>
  <div class="filter-date">
    <div class="filter-date-from">
      <span>De</span>
      <datepicker language="fr" class="date-event"></datepicker>
    </div>
    <div class="filter-date-to">
      <span>À</span>
      <datepicker language="fr" class="date-event"></datepicker>
    </div>
  </div>
</template>

<script setup lang="ts">
import datepicker from 'vuejs3-datepicker'
</script>

<style scoped lang="scss"></style>
