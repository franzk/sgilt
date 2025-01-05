<template>
  <VDatePicker v-model="date">
    <template #default="{ togglePopover }">
      <div class="sgilt-date-picker" @click="togglePopover">
        <div class="left-icon"><IconRocket /></div>
        <div class="selected-date">{{ formattedDate }}</div>
      </div>
    </template>
  </VDatePicker>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'
import 'dayjs/locale/fr'
import { computed } from 'vue'
import IconRocket from '@/components/icons/IconRocket.vue'

const date = defineModel<Date>()

const format = (date: Date) => dayjs(date).locale('fr').format('dddd DD MMM YYYY')

const formattedDate = computed(() => (date.value ? format(date.value) : 'Select a date'))
</script>

<style lang="scss">
.sgilt-date-picker {
  width: 17em;
  line-height: 3em;
  border-radius: $input-border-radius;
  color: $color-primary;
  background-color: $color-white;
  text-align: center;

  display: flex;
  flex-direction: row;

  cursor: pointer;

  &:focus {
    outline: $focus-outline;
    outline-offset: $focus-outline-offset;
  }

  .left-icon {
    display: flex;
    padding-left: 0.5em;
    svg {
      width: initial;
      height: initial;
    }
  }

  .selected-date {
    flex: 1;
    padding-right: 2.5em;
  }
}
</style>
