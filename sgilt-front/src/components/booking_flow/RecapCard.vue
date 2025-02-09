<template>
  <div class="recap-card">
    <div class="recap-card-title">
      <p class="left-icon">
        <component :is="iconComponent" />
      </p>
      <span v-html="title" />
    </div>
    <div class="recap-card-content" v-if="$slots.default">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineAsyncComponent } from 'vue'

const props = defineProps<{
  icon?: string
  title: string
}>()

const iconComponent = props.icon
  ? defineAsyncComponent(() => import(`../icons/Icon${props.icon}.vue`))
  : null
</script>

<style scoped lang="scss">
.recap-card {
  background: #f8f8f8;
  padding: $spacing-m;
  border-radius: $border-radius-m;
  box-shadow: $box-shadow;

  &:hover {
    // TODO : variabliser tout ça
    background: #f1f1f1; // Légère variation du fond
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15); // Ombre plus marquée
    transform: translateY(-2px); // Léger soulèvement
    // cursor: pointer;
  }
}

.recap-card-title {
  display: flex;
  flex-direction: row;
  gap: $spacing-m;

  align-items: center;

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
}

.recap-card-content {
  border-top: 1px solid #ddd;
  white-space: pre-wrap;
  margin-left: 1rem;
}
</style>
