<template>
  <div class="sgilt-chip">
    <p class="icon">
      <slot />
    </p>
    <p class="label">{{ label }}</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  label: string
  link?: string
}>()
</script>

<style lang="scss" scoped>
.sgilt-chip {
  display: flex;
  flex-direction: row;
  align-items: center;
  align-content: center;

  gap: 0.5em;
  padding: 0.2em 0.5em;

  border-radius: $border-radius-xxl;

  p {
    margin: 0;
    padding: 0;
    font-weight: 600;
  }

  .icon {
    margin: 0;
    padding: 0;
    width: 1.5em;
    height: 1.5em;
  }
}
</style>
