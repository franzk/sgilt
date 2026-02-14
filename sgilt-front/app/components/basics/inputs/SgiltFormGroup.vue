<template>
  <div class="form-group">
    <p v-if="title">{{ title }}</p>
    <slot />
    <p class="error-msg">{{ errorMessage }}&nbsp;</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title?: string
  errorMessage?: string
}>()
</script>

<style scoped lang="scss">
.form-group {
  display: flex;
  flex-direction: column;

  .error-msg {
    text-align: left;
    color: $color-error;
    font-size: $font-size-base;
    margin-top: $spacing-s;
    font-weight: 500;
  }

  p {
    font-weight: bold;
    margin-bottom: $spacing-s;
    display: block;
  }
}
</style>
