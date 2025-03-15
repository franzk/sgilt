<template>
  <div class="glass-card">
    <slot />
  </div>
</template>

<script setup lang="ts"></script>

<style scoped lang="scss">
.glass-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  padding: 1.5rem;
  color: $color-secondary;
  text-align: center;
  line-height: 1.4;
  font-size: 1.4rem;

  font-weight: 400;

  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow:
    inset 0px 2px 4px rgba(255, 255, 255, 0.2),
    0px 6px 12px rgba(0, 0, 0, 0.15);

  text-shadow: 1px 1px 4px rgba(80, 80, 80, 0.3);

  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0px 6px 15px rgba(0, 0, 0, 0.2);
  }
}
</style>
