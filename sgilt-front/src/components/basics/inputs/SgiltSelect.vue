<template>
  <div class="custom-select" :tabindex="tabindex" @blur="open = false">
    <div class="selected" :class="{ open: open }" @click="open = !open">
      {{ selected }}
    </div>
    <div class="items" :class="{ selectHide: !open }">
      <div v-for="(option, i) of options" :key="i" @click="click(option)">
        {{ option }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps<{
  options: string[]
  default?: string
  tabindex?: number
}>()

const emit = defineEmits(['selection'])

const selected = ref(
  props.default ? props.default : props.options.length > 0 ? props.options[0] : null,
)
const open = ref(false)

const click = (option: string) => {
  selected.value = option
  open.value = false
  emit('selection', option)
}
</script>

<style lang="scss" scoped>
$color-1: $color-white; // bg
$color-2: $color-accent; // border & bg hover
$color-3: $color-primary;
$color-4: $color-secondary; // border not selected

$br: $input-border-radius;

.custom-select {
  position: relative;
  width: 100%;
  text-align: left;
  outline: none;
  height: 47px;
  line-height: 47px;

  .selected {
    background-color: $color-1;
    border-radius: $br;
    border: 1px solid $color-4;
    color: $color-3;
    padding-left: 1em;
    cursor: pointer;
    user-select: none;

    &.open {
      border: 1px solid $color-2;
      border-radius: $br $br 0px 0px;
    }

    &:after {
      position: absolute;
      content: '';
      top: 22px;
      right: 1em;
      width: 0;
      height: 0;
      border: 5px solid transparent;
      border-color: $color-3 transparent transparent transparent;
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
