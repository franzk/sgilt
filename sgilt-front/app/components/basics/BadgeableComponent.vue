<template>
  <div class="badgeable">
    <slot />
    <span
      v-if="count > 0"
      class="badgeable__count"
      :style="{ minWidth: `${size}px`, height: `${size}px`, lineHeight: `${size}px` }"
    >{{ count }}</span>
  </div>
</template>

<script setup lang="ts">
withDefaults(defineProps<{ count?: number; size?: number }>(), { size: 18 })
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
  padding: 0 5px;
  border-radius: 999px;
  background: #d93025;
  color: #fff;
  font-family: 'Inter', sans-serif;
  font-size: 0.688rem;
  font-weight: 700;
  text-align: center;
  white-space: nowrap;
  pointer-events: none;
  z-index: 2;
}
</style>
