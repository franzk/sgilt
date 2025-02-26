<template>
  <div class="reservation-price">
    <PriceItem v-if="price?.title" :label="price.title" icon="Star" />
    <PriceItem v-if="quantity" :label="`${quantity} ${price?.unity || ''}`" icon="Profile" />
    <PriceItem
      v-if="quantity && price?.price"
      :label="`${price.price.toString()} / ${price.unity}`"
      icon="More"
    />
    <PriceItem v-if="totalPrice" :label="`${totalPrice.toString()} €`" icon="Price" />
  </div>
</template>

<script setup lang="ts">
import type { Price } from '@/data/domain/Partner'
import PriceItem from '@/components/event_board/reservation_card/PriceItem.vue'

defineProps<{
  price?: Price
  quantity?: number
  totalPrice?: number
}>()
</script>

<style scoped lang="scss">
.reservation-price {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: $font-size-base;

  background: #f8f9fa; //rgba(255, 255, 255, 0.6); // Un fond très léger, plus doux que le gris
  border: 1px solid #dee2e6; // rgba(0, 0, 0, 0.08); // Une bordure subtile pour structurer
  border-radius: 8px;
  padding: 10px 15px;
}
</style>
