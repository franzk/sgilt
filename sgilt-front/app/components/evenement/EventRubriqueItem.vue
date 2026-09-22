<template>
  <button class="rubrique-item" type="button">
    <span class="icon-wrap" :style="{ background: accentColor }" aria-hidden="true">
      <component :is="ICONS_MAP[rubrique.key]" class="icon" />
    </span>
    <span class="text">
      <span class="name">{{ $t(`evenement.rubriques.${rubrique.key}`) }}</span>
      <span class="count">
        {{ t('evenement.rubriques.item-count', rubrique.itemCount, { n: rubrique.itemCount }) }}
      </span>
    </span>
    <ArrowRightSIcon class="chevron" aria-hidden="true" />
  </button>
</template>

<script setup lang="ts">
import { markRaw, type Component } from 'vue'
import {
  ArrowRightSIcon,
  Building2Icon,
  FlowerIcon,
  GroupIcon,
  HotelBedIcon,
  Music2Icon,
  RestaurantIcon,
} from '@remixicons/vue/line'
import type { EventRubrique, RubriqueKey } from '~/constants/event-rubriques'

const props = defineProps<{
  rubrique: EventRubrique
}>()

const { t } = useI18n()

const ICONS_MAP: Record<RubriqueKey, Component> = {
  invites: markRaw(GroupIcon),
  lieu: markRaw(Building2Icon),
  restauration: markRaw(RestaurantIcon),
  'musique-animation': markRaw(Music2Icon),
  decoration: markRaw(FlowerIcon),
  hebergement: markRaw(HotelBedIcon),
}

// Une couleur d'accent par rubrique — sert uniquement à les distinguer visuellement dans la
// liste, pas des tokens de design partagés (usage local à ce composant).
const ACCENT_MAP: Record<RubriqueKey, string> = {
  invites: '#c96a4e',
  lieu: '#a3334a',
  restauration: '#d68c00',
  'musique-animation': '#b0447e',
  decoration: '#5a8f6b',
  hebergement: '#2f6f73',
}

const accentColor = computed(() => ACCENT_MAP[props.rubrique.key])
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
    flex-shrink: 0;

    .icon {
      width: 1.25rem;
      height: 1.25rem;
      color: #fff;
    }
  }

  .text {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 0.125rem;
    min-width: 0;
  }

  .name {
    font-size: 0.95rem;
    font-weight: 600;
  }

  .count {
    font-size: 0.8rem;
    color: $text-secondary;
  }

  .chevron {
    width: 1.125rem;
    height: 1.125rem;
    flex-shrink: 0;
    color: $text-secondary;
  }
}
</style>
