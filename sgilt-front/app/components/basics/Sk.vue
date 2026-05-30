<template>
  <div class="sk" :class="{ light }" :style="elStyle" />
</template>

<script setup lang="ts">
const props = defineProps<{
  width?: string
  height?: string
  radius?: string
  light?: boolean
}>()

const elStyle = computed(() => {
  const s: Record<string, string> = {}
  if (props.width !== undefined) s.width = props.width
  if (props.height !== undefined) s.height = props.height
  if (props.radius !== undefined) s.borderRadius = props.radius
  return s
})
</script>

<style scoped lang="scss">
@keyframes sk-shimmer {
  to {
    transform: translateX(100%);
  }
}

.sk {
  display: block;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
  background: rgba($color-accent, 0.1);

  &::after {
    content: '';
    position: absolute;
    inset: 0;
    transform: translateX(-100%);
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.55), transparent);
    animation: sk-shimmer 1s ease-in-out infinite;
  }

  &.light {
    background: rgba(255, 255, 255, 0.18);

    &::after {
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.28), transparent);
    }
  }
}
</style>
