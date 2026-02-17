<template>
  <div
    class="custom-select"
    :tabindex="focusable ? 0 : -1"
    @blur="open = false"
    @keydown="onkeydown"
  >
    <div class="selected" :class="{ open: open }" @click="open = !open">
      <div class="left-icon"><slot name="left-icon" /></div>
      <div class="selected-text">{{ selectedLabel }}</div>
      <div class="right-icon" v-if="open"><ChevronIcon /></div>
      <div class="right-icon" v-else><ChevronIcon /></div>
    </div>

    <div class="items" :class="{ selectHide: !open }">
      <div
        v-for="(option, i) of options"
        :key="i"
        @click="click(option)"
        :class="{ 'selected-option': option.value === selectedOption?.value }"
      >
        {{ option.label }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import ChevronIcon from '../icons/ChevronIcon.vue'

// selected value
const selectedOption = defineModel<SgiltSelectOption>()

// option interface
export interface SgiltSelectOption {
  value: string
  label: string
}

// props
const props = defineProps<{
  options: SgiltSelectOption[]
  focusable?: boolean
}>()

// display the selected label
const selectedLabel = computed(() => selectedOption?.value?.label || '')

// is the select opened ?
const open = ref(false)

onMounted(() => {
  if (!selectedOption.value && props.options.length > 0) {
    selectedOption.value = props.options[0]
  }
})

// when an option is clicked
const click = (option: SgiltSelectOption) => {
  selectedOption.value = option // update the model
  open.value = false // close the select
}

// keyboard navigation
const onkeydown = (e: KeyboardEvent) => {
  if (['Enter', 'Space'].includes(e.code)) {
    e.preventDefault()
    open.value = !open.value
  } else if (e.code === 'ArrowDown') {
    e.preventDefault()
    const index = props.options.findIndex((o) => o.value === selectedOption.value?.value)
    if (index < props.options.length - 1) {
      selectedOption.value = props.options[index + 1]
    }
  } else if (e.code === 'ArrowUp') {
    e.preventDefault()
    const index = props.options.findIndex((o) => o.value === selectedOption.value?.value)
    if (index > 0) {
      selectedOption.value = props.options[index - 1]
    }
  } else if (e.code === 'Escape') {
    e.preventDefault()
    open.value = false
  }
}
</script>

<style lang="scss" scoped>
@use '@/assets/styles/base' as *;
@use '@/assets/styles/borders' as *;

$color-1: $color-white; // bg
$color-2: $color-accent; // border & bg hover
$color-3: $color-primary;
$color-4: $color-secondary; // border not selected

$br: 0.875rem;
$bc: $input-border-color;

.custom-select {
  position: relative;
  line-height: 45px;
  outline: none;
  width: 100%;
  font-size: 1.125rem;

  &:focus-visible {
    outline: $focus-outline;
    outline-offset: $focus-outline-offset;
    border-radius: $br;
  }

  .selected {
    display: flex;
    flex-direction: row;

    background-color: $color-1;
    border-radius: $br;
    border: 1px solid $bc;
    color: $color-3;
    height: 100%;
    align-items: center;

    cursor: pointer;
    user-select: none;

    box-shadow:
      0 0.0625rem 0 rgba(0, 0, 0, 0.04),
      0 0.5rem 1.25rem rgba(0, 0, 0, 0.05);

    .selected-text {
      flex: 1;
      text-align: center;
    }

    .left-icon {
      display: flex;
      flex-wrap: wrap;
      width: 2.5em;
      align-content: center;
      justify-content: center;
      * {
        width: 1.5em;
        height: 1.5em;
      }
    }

    .right-icon {
      display: flex;
      width: 2.5em;
      text-align: center;
      align-content: center;
      justify-content: center;
      align-items: center;
      // font-size: $font-size-base;

      color: rgba(0, 0, 0, 0.5);
      transition: transform 160ms ease;
    }

    &.open {
      border: 1px solid $color-2;
      border-bottom: none;
      border-radius: $br $br 0px 0px;
      .right-icon {
        transform: rotate(180deg);
      }
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
    z-index: $z-dropdown;

    .selected-option {
      background-color: $color-2;
      color: $color-white;
    }

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
