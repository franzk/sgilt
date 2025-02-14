<template>
  <div
    class="recap-card"
    :class="{ 'width-fit-content': widthFitContent, 'row-direction': direction === 'row' }"
  >
    <div class="recap-card-title">
      <p class="left-icon" v-if="title">
        <component :is="iconComponent" />
      </p>
      <span v-html="title" />
    </div>
    <div class="recap-card-content" v-if="$slots.default">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineAsyncComponent } from 'vue'

const props = defineProps<{
  icon?: string
  title: string
  widthFitContent?: boolean
  direction?: 'column' | 'row'
}>()

const iconComponent = props.icon
  ? defineAsyncComponent(() => import(`../icons/Icon${props.icon}.vue`))
  : null
</script>

<style scoped lang="scss">
.recap-card {
  background: $color-secondary;
  padding: $spacing-m;
  border-radius: $border-radius-m;
  box-shadow: $box-shadow;

  &:hover {
    background: darken($color-secondary, 5%);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
    transform: translateY(-2px);
  }

  .recap-card-title {
    display: flex;
    flex-direction: row;
    gap: $spacing-m;
    align-items: center;

    span {
      flex: 1;
      font-weight: bold;
    }

    p {
      margin: 0;
      padding: 0;
    }

    .left-icon svg {
      width: 1.5rem;
      height: 1.5rem;
    }
  }

  .recap-card-content {
    white-space: pre-wrap;
    margin: 0 $spacing-m;
    line-height: 1.1rem;

    &:not(:empty) {
      border-top: 1px solid $color-divider;
      padding-top: $spacing-m;
    }
  }

  &.row-direction {
    display: flex;
    flex-direction: row;
    align-items: center;
    .recap-card-content {
      border-top: 0;
      padding-top: 0;
    }
  }
}

.width-fit-content {
  width: fit-content;
}
</style>
