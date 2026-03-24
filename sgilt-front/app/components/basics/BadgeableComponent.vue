<template>
  <div class="badgeable">
    <slot />
    <span v-if="count > 0" class="badgeable__count">{{ count }}</span>
  </div>
</template>

<script setup lang="ts">
defineProps<{ count?: number }>()
</script>

<style scoped lang="scss">
.badgeable {
  position: relative;
  display: block;
}

.badgeable__count {
  position: absolute;
  top: -($spacing-xs);
  right: -($spacing-xs);
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  border-radius: 999px;
  background: #d93025;
  color: #fff;
  font-family: 'Inter', sans-serif;
  font-size: 0.688rem;
  font-weight: 700;
  line-height: 18px;
  text-align: center;
  white-space: nowrap;
  pointer-events: none;
  z-index: 2;
}
</style>
