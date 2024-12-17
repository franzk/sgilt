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

  gap: 0.5rem;
  padding: 0.2rem 0.5rem;

  border-radius: 10rem;

  p {
    margin: 0;
    padding: 0;
    font-weight: 600;
  }

  .icon {
    margin: 0;
    padding: 0;
    svg {
      width: 1rem;
      height: 1rem;
    }
  }
}
</style>
