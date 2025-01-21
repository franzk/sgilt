<template>
  <div class="partner-card">
    <RouterLink
      :to="{ name: 'partner', params: { id: partner.slug || 'aa' } }"
      class="partner-tail"
    >
      <img :src="partner.imageUrl" :alt="partner.title" />

      <section class="content">
        <div class="partner-infos">
          <h3>{{ partner.title }}</h3>
          <div class="partner-enter-price">
            <p>&gt;=</p>
            <p class="price">{{ partner.entryPrice }} €</p>
          </div>
        </div>
        <div class="tags">{{ partner.tags.map((t) => t.name).join(',') }}</div>
      </section>
    </RouterLink>
  </div>
</template>

<script setup lang="ts">
import type { Partner } from '@/data/domain/Partner'

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

  .partner-tail {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    padding: $spacing-s;
    border-radius: $border-radius-s;
    transition: background 0.3s ease;
    align-items: center;
  }

  .content {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: $spacing-s;
  }

  .partner-infos {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }

  .tags {
    font-size: $font-size-base;
    color: $color-subtext;
    flex: 1;
  }

  img {
    width: 30%;
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
}
</style>
