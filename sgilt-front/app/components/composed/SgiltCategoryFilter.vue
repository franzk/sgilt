<script setup lang="ts">
import { APP_CATEGORIES } from '~/utils/constants'
import SgiltCategoryIcon from '~/components/basics/icons/CategoryIcon.vue'

const modelValue = defineModel<string>() // L'ID de la catégorie active

const emit = defineEmits(['update:modelValue'])
</script>

<template>
  <nav class="category-filter">
    <button
      v-for="cat in APP_CATEGORIES"
      :key="cat.id"
      class="category-btn"
      :class="{ active: cat.id === modelValue }"
      @click="$emit('update:modelValue', cat.id)"
    >
      <div class="icon-circle">
        <SgiltCategoryIcon :categoryId="cat.id" class="icon-svg" />
      </div>
      <span class="name">{{ cat.name }}</span>
    </button>
  </nav>
</template>

<style scoped lang="scss">
.category-filter {
  display: flex;
  justify-content: space-between; // Répartition égale sur 100%
  width: 100%;
  padding: 1rem 0.5rem;
  border-bottom: 1px solid #f0f0f0;
}

.category-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex: 1; // Chaque bouton prend la même largeur
  background: none;
  border: none;
  cursor: pointer;
  min-width: 0; // Sécurité pour le texte long

  .icon-circle {
    width: 44px;
    height: 44px;
    border-radius: 50%;
    border: 1px solid #e0e0e0;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #757575;
    transition: all 0.2s ease;
  }

  .name {
    font-size: 11px;
    color: #757575;
    font-weight: 500;
  }

  &.active {
    .icon-circle {
      background-color: #ffd700; // Ton jaune Sgilt
      border-color: #ffd700;
      color: #000;
    }
    .name {
      color: #000;
      font-weight: 700;
    }
  }

  .icon-svg {
    width: 22px;
    height: 22px;
  }
}
</style>
