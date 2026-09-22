<template>
  <button class="rubrique-item" type="button">
    <span class="icon-wrap" aria-hidden="true">
      <component :is="ICONS_MAP[rubrique.key]" class="icon" />
    </span>
    <span class="name">{{ $t(`evenement.rubriques.${rubrique.key}`) }}</span>
    <span class="count">{{ rubrique.itemCount }}</span>
  </button>
</template>

<script setup lang="ts">
import { markRaw, type Component } from 'vue'
import {
  Building2Icon,
  FlowerIcon,
  GroupIcon,
  HotelBedIcon,
  Music2Icon,
  RestaurantIcon,
} from '@remixicons/vue/line'
import type { EventRubrique, RubriqueKey } from '~/constants/event-rubriques'

defineProps<{
  rubrique: EventRubrique
}>()

const ICONS_MAP: Record<RubriqueKey, Component> = {
  invites: markRaw(GroupIcon),
  lieu: markRaw(Building2Icon),
  restauration: markRaw(RestaurantIcon),
  'musique-animation': markRaw(Music2Icon),
  decoration: markRaw(FlowerIcon),
  hebergement: markRaw(HotelBedIcon),
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.rubrique-item {
  appearance: none;
  display: flex;
  align-items: center;
  gap: $spacing-s;
  width: 100%;
  padding: $spacing-m;
  border: none;
  border-radius: $radius-lg;
  background: $surface-white;
  box-shadow: 0 0.125rem 0.75rem rgba(47, 42, 37, 0.06);
  font-family: 'Inter', sans-serif;
  text-align: left;
  color: $brand-primary;
  cursor: pointer;
  transition: background 120ms ease;

  &:active {
    background: $surface-soft;
  }

  .icon-wrap {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 2.5rem;
    height: 2.5rem;
    border-radius: 50%;
    background: $surface-soft;
    border: 1px solid $divider-color;
    flex-shrink: 0;

    .icon {
      width: 1.25rem;
      height: 1.25rem;
    }
  }

  .name {
    flex: 1;
    font-size: 0.95rem;
    font-weight: 600;
  }

  .count {
    min-width: 1.5rem;
    padding: 0.125rem 0.5rem;
    border-radius: 999px;
    background: $surface-soft;
    border: 1px solid $divider-color;
    font-size: 0.75rem;
    font-weight: 600;
    text-align: center;
    color: $text-secondary;
  }
}
</style>
