<template>
  <div class="partner-card">
    <RouterLink :to="{ name: 'partner', params: { id: partner.slug || 'aa' } }">
      <div class="partner-category">
        <CategoryChip :categoryName="partner.tags[0]?.category || ''" />
      </div>
      <img :src="partner.imageUrl" :alt="partner.title" />

      <h3>{{ partner.title }}</h3>

    <p class="partner-description">{{ partner.description }}</p>
    <div class="partner-enter-price">
      <p>{{ $t('texts.a-partir-de') }}</p>
      <p class="price">{{ partner.entryPrice }} €</p>
    </div>
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

  &:hover {
    img {
      transform: scale(1.01);
      filter: brightness(0.8);
    }
  }

  img {
    width: 100%;
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
    margin-top: $spacing-xs;
    font-weight: 600;
  }

  .partner-enter-price {
    font-weight: 300;
    display: flex;
    flex-direction: row;
    .price {
      font-weight: 600;
      margin-left: $spacing-xs;
    }
  }

  .partner-category {
    display: flex;
    position: absolute;
    justify-content: end;
    z-index: 1;
    width: 100%;
  }

  .partner-description {
    font-weight: 300;
  }
}
</style>
