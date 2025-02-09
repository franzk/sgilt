<template>
  <div class="recap-card">
    <p class="left-icon">
      <slot />
    </p>
    <span>{{ text }}</span>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  text: string
}>()
</script>

<style scoped lang="scss">
.recap-card {
  display: flex;
  flex-direction: row;
  gap: $spacing-m;

  align-items: center;

  background: #f8f8f8;
  padding: $spacing-m;
  border-radius: $border-radius-m;
  box-shadow: $box-shadow;

  span {
    flex: 1;
  }

  p {
    margin: 0;
    padding: 0;
  }

  .left-icon svg {
    width: 1.5rem;
    height: 1.5rem;
  }

  &:hover {
    // TODO : variabliser tout ça
    background: #f1f1f1; // Légère variation du fond
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15); // Ombre plus marquée
    transform: translateY(-2px); // Léger soulèvement
    // cursor: pointer;
  }
}
</style>
