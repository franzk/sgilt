<template>
  <a :href="mailtoHref" class="resume-link">Reprendre contact</a>
</template>

<script setup lang="ts">
defineProps<{ mailtoHref: string }>()
</script>

<style scoped lang="scss">
.resume-link {
  display: inline-block;
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  color: $text-secondary;
  text-decoration: underline;
  text-underline-offset: 2px;
  transition: color 150ms ease;
  padding: $spacing-xs 0;

  &:hover {
    color: $text-primary;
  }
}
</style>
