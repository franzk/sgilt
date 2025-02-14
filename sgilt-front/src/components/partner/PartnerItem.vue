<template>
  <div class="partner-item" @click="selectPartner" :class="{ selectable: selectable }">
    <!-- Image -->
    <img :src="partner.imageUrl" :alt="`Image de ${partner.title}`" class="partner-image" />

    <!-- Infos -->
    <div class="partner-info">
      <h3 class="partner-name">{{ partner.title }}</h3>
      <p class="partner-category">{{ partner.tags.map((t) => t.name).join(',') }}</p>
      <p class="partner-price" v-if="!hidePrice">À partir de {{ partner.entryPrice }} €</p>
      <PartnerAvailability v-if="partner.availability" :availability="partner.availability" />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { PartnerSearchViewModel } from '@/data/domain/viewmodels/PartnerSearchViewModel'
import PartnerAvailability from '@/components/partner/PartnerAvailability.vue'

const props = defineProps<{
  partner: PartnerSearchViewModel
  selectable?: boolean
  hidePrice?: boolean
}>()

const emit = defineEmits(['select-partner'])

const selectPartner = () => {
  if (props.selectable) {
    emit('select-partner', props.partner)
  }
}
</script>

<style scoped lang="scss">
.selectable {
  cursor: pointer;
}
.partner-item {
  display: flex;
  align-items: center;
  background-color: $color-secondary;
  border-radius: $border-radius-m;
  box-shadow: 0 4px 10px $shadow-s;
  overflow: hidden;
  padding: $spacing-m;
  transition:
    transform 0.2s ease-in-out,
    box-shadow 0.2s ease-in-out;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px $shadow-m;
  }

  /* Image Section */
  .partner-image {
    flex-shrink: 0;
    width: 30%;
    aspect-ratio: 1.3;
    object-fit: cover;
    border-radius: $border-radius-s;
    object-fit: cover;
    margin-right: $spacing-m;
  }

  /* Info Section */
  .partner-info {
    flex: 1;

    .partner-name {
      font-size: $font-size-h3;
      font-weight: bold;
      color: $color-primary;
      margin: 0;
    }

    .partner-category {
      font-size: $font-size-base;
      color: $color-subtext;
      margin: $spacing-xs 0;
    }

    .partner-price {
      font-size: $font-size-base;
      color: $color-accent;
      font-weight: bold;
      margin: 0;
    }
  }
}
</style>
