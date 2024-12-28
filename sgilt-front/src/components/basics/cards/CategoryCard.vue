<template>
  <div class="category-card">
    <div class="category-card-image">
      <img :src="imageUrl" alt="Category image" />
    </div>
    <div class="category-card-content">
      <p><CategoryIcon :categoryName="categoryName" /></p>
      <h3 class="category-card-title">{{ $t(`categories.${categoryName}`) }}</h3>
    </div>
  </div>
</template>

<script setup lang="ts">
import CategoryIcon from '@/components/basics/icons/CategoryIcon.vue'

defineProps<{
  categoryName: string
  imageUrl: string
}>()
</script>

<style scoped lang="scss">
$br: $border-radius-xs;

.category-card {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;

  box-shadow: 0 1px 3px $ghost;
  border-radius: $br;

  cursor: pointer;

  &:hover {
    img {
      transform: scale(1.01);
      filter: brightness(0.8);
    }
  }

  .category-card-image {
    img {
      width: 100%;
      aspect-ratio: 1.4;
      object-fit: cover;
      border-radius: $br $br 0 0;
      transition:
        transform 0.3s ease,
        filter 0.3s ease;
    }
  }

  .category-card-content {
    display: flex;
    flex-direction: row;
    line-height: 0;
    gap: $spacing-s;
    justify-content: center;
    align-items: center;

    padding: 0 $spacing-m $spacing-m $spacing-m;

    p {
      margin: 0;
    }

    .category-card-title {
      margin: 0;
    }

    .category-card-description {
      margin: 0;
    }
  }
}
</style>
