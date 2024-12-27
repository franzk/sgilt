<template>
  <div class="partner-thumbnail">
    <div class="partner-category">
      <CategoryChip :categoryName="partner.category.name" class="category-chip" />
    </div>
    <img :src="partner.imageUrl" alt="Partenaire 1" />

    <h3>{{ partner.title }}</h3>

    <p class="partner-description">{{ partner.description }}</p>
    <p class="partner-enter-price">{{ `${$t('texts.a-partir-de')}  ${partner.entryPrice}` }} €</p>
  </div>
</template>

<script setup lang="ts">
import CategoryChip from '@/components/basics/chips/CategoryChip.vue'
import type { Partner } from '@/domain/Partner'

defineProps<{
  partner: Partner
}>()
</script>

<style lang="scss" scoped>
.partner-thumbnail {
  display: flex;
  flex-direction: column;
  position: relative;

  cursor: pointer;

  background: $color-secondary;
  color: $color-primary;

  font-size: $font-size-s;

  &:hover {
    img {
      transform: scale(1.01);
      filter: brightness(0.8);
    }
  }

  img {
    width: 100%;
    height: auto;
    border-radius: $border-radius-s;
    transition:
      transform 0.3s ease,
      filter 0.3s ease;
  }

  h3,
  p {
    margin: 0;
    padding: $spacing-xxs;
  }

  h3 {
    font-size: $font-size-l;
    font-weight: 500;
    margin-top: $spacing-xs;
  }

  .partner-enter-price {
    font-weight: 300;
  }

  .partner-category {
    position: absolute;
    justify-content: end;
    z-index: 1;

    width: 100%;
    display: flex;
    border-radius: $border-radius-s $border-radius-s 0 0;

    .category-chip {
      color: $color-secondary;
      background-color: $shadow;
      border: $border-width-s solid $color-secondary;
      gap: $spacing-xs;
      margin: $spacing-s;
    }
  }
}
</style>
