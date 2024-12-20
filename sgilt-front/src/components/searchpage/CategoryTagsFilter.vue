<template>
  <div class="category-tags-filter">
    <div class="category" v-for="category in categories" :key="category.id">
      <input type="checkbox" :id="category.id" :value="category.id" v-model="category.checked" />
      <label :for="category.id">{{ category.name }}</label>
      <template v-if="category.checked">
        <div class="tag" v-for="tag in category.tags" :key="tag">
          <input type="checkbox" :id="tag" :value="tag" />
          <label :for="tag">{{ tag }}</label>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const categories = ref([
  { id: '1', name: 'music', checked: false, tags: ['jazz', 'pop-rock', 'dj'] },
  { id: '2', name: 'food', checked: false, tags: ['traiteur', 'foodtruck', 'boissons'] },
  { id: '3', name: 'place', checked: false, tags: ['salle', 'hébergement', 'restaurant'] },
  { id: '4', name: 'photo', checked: false, tags: ['photographe', 'photobooth'] },
])
</script>

<style scoped lang="scss">
.category-tags-filter {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  .tag {
    margin-left: $spacing-m;
  }
}
</style>
