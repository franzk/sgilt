<template>
  <div class="partner-card">
    <div class="partner-category">
      <CategoryChip :categoryName="partner.category.name" />
    </div>
    <img :src="partner.imageUrl" :alt="partner.title" />

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
.partner-card {
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
    aspect-ratio: 1.3;
    object-fit: cover;
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
    font-size: $font-size-m;
    line-height: $line-height-m;
    font-weight: 500;
  }

  .partner-enter-price {
    font-weight: 300;
  }

  .partner-category {
    display: flex;
    position: absolute;
    justify-content: end;
    z-index: 1;
    width: 100%;
  }
}
</style>
