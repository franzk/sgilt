<script setup lang="ts">
import { APP_CATEGORIES } from '~/utils/constants'

const props = defineProps<{
  categoryId: string
  activeSubcats: string[]
  counts: Record<string, number>
}>()

const emit = defineEmits(['toggle'])

// On récupère dynamiquement les sous-catégories de la catégorie parente choisie
const subcategories = computed(() => {
  const cat = APP_CATEGORIES.find((c) => c.id === props.categoryId)
  return cat?.subcategories || []
})
</script>

<template>
  <Transition name="slide-fade">
    <div v-if="subcategories.length > 0" class="sub-category-wrapper">
      <div class="sub-category-grid">
        <button
          v-for="sub in subcategories"
          :key="sub.id"
          type="button"
          class="sub-pill"
          :class="{ active: activeSubcats.includes(sub.id) }"
          @click="emit('toggle', sub.id)"
        >
          <span class="label">{{ sub.name }}</span>
          <span v-if="counts[sub.id]" class="count"> ({{ counts[sub.id] }}) </span>
        </button>
      </div>
    </div>
  </Transition>
</template>

<style scoped lang="scss">
.sub-category-wrapper {
  padding: 0.75rem 1rem;
  width: 100%;
  overflow-x: auto;
  // Masque la scrollbar tout en gardant le défilement
  -ms-overflow-style: none;
  scrollbar-width: none;
  &::-webkit-scrollbar {
    display: none;
  }
}

.sub-category-grid {
  display: flex;
  gap: 0.6rem;
  align-items: center;
}

.sub-pill {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  padding: 0.5rem 1.1rem;
  border-radius: 50px;
  border: 1px solid #e2e8f0;
  background-color: #f8fafc;
  white-space: nowrap;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  outline: none;

  .label {
    font-size: 0.85rem;
    color: #64748b;
    font-weight: 500;
  }

  .count {
    font-size: 0.75rem;
    color: #94a3b8;
  }

  &:hover {
    border-color: #cbd5e1;
    background-color: #f1f5f9;
  }

  &.active {
    background-color: #fefce8; // Jaune très pâle d'activation
    border-color: #fde047; // Jaune Sgilt
    box-shadow: 0 2px 4px rgba(253, 224, 71, 0.2);

    .label {
      color: #000;
      font-weight: 600;
    }
    .count {
      color: #854d0e;
    }
  }
}

// Petite transition fluide pour l'apparition des sous-catégories
.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}
.slide-fade-leave-active {
  transition: all 0.2s ease-in;
}
.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(-8px);
  opacity: 0;
}
</style>
