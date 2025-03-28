<template>
  <div class="reservation-price">
    <!-- NORMAL MODE -->
    <template v-if="!compactMode">
      <!-- title -->
      <PriceItem v-if="price?.title" :label="price.title" icon="Star" />
      <!-- quantity -->
      <PriceItem v-if="quantity" :label="`${quantity} ${price?.unity || ''}`" icon="Profile" />
      <!-- unit price -->
      <PriceItem
        v-if="quantity && price?.price"
        :label="`${price.price.toString()} / ${price.unity}`"
        icon="More"
      />
      <!-- total price -->
      <PriceItem v-if="totalPrice" :label="`${totalPrice.toString()} €`" icon="Price" />
    </template>
    <template v-else>
      <!-- COMPACT MODE -->
      <div class="compact-price-zone">
        <!-- picto -->
        <span class="compact-price-icon"><SgiltIcon icon="Star" /></span>
        <div class="compact-price-summary">
          <!-- title -->
          <span>{{ price?.title }} : </span>
          <!-- quantity -->
          <template v-if="quantity">
            <br />
            <!-- quantity -->
            <span>{{ quantity }} {{ price?.unity || '' }} X {{ price?.price || '' }} = </span>
          </template>
          <!-- total price -->
          <span
            ><strong>{{ totalPrice }} €</strong></span
          >
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import type { Price } from '@/data/domain/Partner'
import PriceItem from '@/components/event_board/reservation_card/PriceItem.vue'
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'

defineProps<{
  price?: Price
  quantity?: number
  totalPrice?: number
  compactMode?: boolean
}>()
</script>

<style scoped lang="scss">
svg {
  width: 1.5rem;
  height: 1.5rem;
}
.reservation-price {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: $font-size-base;

  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: $border-radius-s;
  padding: $spacing-mm $spacing-m;
}

.compact-price-zone {
  display: flex;
  align-items: center;
  gap: $spacing-s;

  .compact-price-icon {
    display: flex;
  }

  .compact-price-summary {
    flex: 1;
    display: block;
    line-height: calc($line-height-base * 1.2);
    font-size: $font-size-base;
    text-align: center;
  }
}
</style>
