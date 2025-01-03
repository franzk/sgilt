<template>
  <div class="custom-select" :tabindex="tabindex" @blur="open = false">
    <div class="selected" :class="{ open: open }" @click="open = !open">
      <div class="left-icon"><IconRocket /></div>
      <div class="selected-text">{{ selectedLabel }}</div>
      <div class="right-icon" v-if="open">&#x25B2;</div>
      <div class="right-icon" v-else>&#x25BC;</div>
    </div>

    <div class="items" :class="{ selectHide: !open }">
      <div v-for="(option, i) of options" :key="i" @click="click(option)">
        {{ option.label }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import IconRocket from '@/components/icons/IconRocket.vue'

// selected value
const selectedValue = defineModel<string>()

// option interface
export interface SgiltSelectOption {
  value: string
  label: string
}

// props
const props = defineProps<{
  options: SgiltSelectOption[]
  default?: string
  tabindex?: number
}>()

// display the selected label
const selectedLabel = ref(
  props.default ? props.default : props.options.length > 0 ? props.options[0].label : null,
)

// is the select opened ?
const open = ref(false)

// when an option is clicked
const click = (option: SgiltSelectOption) => {
  selectedLabel.value = option.label // update the selected label
  selectedValue.value = option.value // update the model
  open.value = false // close the select
}
</script>

<style lang="scss" scoped>
$color-1: $color-white; // bg
$color-2: $color-accent; // border & bg hover
$color-3: $color-primary;
$color-4: $color-secondary; // border not selected

$br: calc($input-border-radius * 0.75);
$bc: $input-border-color;

.custom-select {
  position: relative;
  text-align: left;
  outline: none;

  .selected {
    display: flex;
    flex-direction: row;

    background-color: $color-1;
    border-radius: $br;
    border: 1px solid $bc;
    color: $color-3;

    cursor: pointer;
    user-select: none;

    .selected-text {
      flex: 1;
      text-align: center;
    }

    .left-icon {
      padding-left: 0.5em;
    }

    .right-icon {
      padding-right: 1em;
      font-size: $font-size-base;
    }

    &.open {
      border: 1px solid $color-2;
      border-bottom: none;
      border-radius: $br $br 0px 0px;
    }
  }

  .items {
    color: $color-3;
    border-radius: 0px 0px $br $br;
    overflow: hidden;
    border-right: 1px solid $color-2;
    border-left: 1px solid $color-2;
    border-bottom: 1px solid $color-2;
    position: absolute;
    background-color: $color-1;
    left: 0;
    right: 0;
    z-index: 1;

    div {
      color: $color-3;
      padding-left: 1em;
      cursor: pointer;
      user-select: none;

      &:hover {
        background-color: $color-2;
        color: $color-white;
      }
    }
  }
}

.selectHide {
  display: none;
}
</style>
