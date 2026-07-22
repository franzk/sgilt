<template>
  <div class="ia-block" :class="{ open }">
    <button type="button" class="header" @click="open = !open">
      <span class="title">{{ title }}</span>
      <span class="chevron" aria-hidden="true">{{ open ? '−' : '+' }}</span>
    </button>
    <div v-if="open" class="body">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  title: string
  defaultOpen: boolean
}>()

const open = ref(props.defaultOpen)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.ia-block {
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: $radius-md;
  background: $surface-white;
  overflow: hidden;

  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    padding: $spacing-m;
    background: none;
    border: none;
    font-family: inherit;
    text-align: left;
    cursor: pointer;

    .title {
      font-family: 'Cormorant Garamond', serif;
      font-size: 1.15rem;
      font-weight: 600;
      color: $color-primary;
    }

    .chevron {
      font-size: 1.2rem;
      color: $text-secondary;
      line-height: 1;
    }
  }

  .body {
    display: flex;
    flex-direction: column;
    gap: $spacing-m;
    padding: 0 $spacing-m $spacing-m;
  }
}
</style>
