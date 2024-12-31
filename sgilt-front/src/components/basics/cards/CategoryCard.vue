<template>
  <div class="category-card">
    <div class="category-card-image">
      <div class="category-card-chip">
        <CategoryChip :categoryName="categoryName" />
      </div>

      <img :src="imageUrl" alt="Category image" />
      <!-- TODO : Add a fallback image et partout où y a des images -->
    </div>
    <div class="category-card-content">
      <h3 class="category-card-description">
        {{ $t(`categories.${categoryName}.description`) }} &#8594;
      </h3>
      <!--span>&#8594;</!--span -->
    </div>
  </div>
</template>

<script setup lang="ts">
import CategoryChip from '@/components/basics/chips/CategoryChip.vue'

defineProps<{
  categoryName: string
  imageUrl?: string
}>()
</script>

<style scoped lang="scss">
$br: $border-radius-s;

.category-card {
  display: flex;
  position: relative;
  flex-direction: column;
  gap: $spacing-s;

  cursor: pointer;
  color: $color-primary;

  &:hover {
    img {
      transform: scale(1.01);
      filter: brightness(0.8);
    }
  }

  .category-card-chip {
    display: flex;
    position: absolute;
    justify-content: end;
    z-index: 1;
    width: 100%;
  }

  .category-card-image {
    img {
      width: 100%;
      aspect-ratio: 1.4;
      object-fit: cover;
      border-radius: $br $br;
      transition:
        transform 0.3s ease,
        filter 0.3s ease;
    }
  }

  .category-card-description {
    margin: 0;
  }
}
</style>
