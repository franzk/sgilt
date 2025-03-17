<template>
  <div class="glass-card">
    <slot />
  </div>
</template>

<script setup lang="ts"></script>

<style scoped lang="scss">
.glass-card {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow:
    inset 0px 2px 4px rgba(255, 255, 255, 0.1),
    0px 8px 16px rgba(0, 0, 0, 0.15),
    0px 0px 15px rgba(255, 255, 255, 0.2);
  padding: 1.2rem;
  text-align: center;
}
</style>
