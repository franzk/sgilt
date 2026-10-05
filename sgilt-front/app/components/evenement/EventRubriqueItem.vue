<template>
  <button class="rubrique-item" type="button">
    <span class="icon-wrap" :style="{ background: accentColor }" aria-hidden="true">
      <component :is="rubriqueIcon(rubriqueKey)" class="icon" />
    </span>
    <span class="text">
      <span class="name">{{ $t(`evenement.rubriques.${rubriqueKey}`) }}</span>
      <span class="count">
        {{ t('evenement.rubriques.reservation-count', { n: count }, count) }}
      </span>
    </span>
    <ArrowRightSIcon class="chevron" aria-hidden="true" />
  </button>
</template>

<script setup lang="ts">
import { ArrowRightSIcon } from '@remixicons/vue/line'
import { rubriqueAccent, rubriqueIcon } from '~/constants/event-rubriques'

const props = defineProps<{
  rubriqueKey: string
  // Nombre de réservations de la rubrique.
  count: number
}>()

const { t } = useI18n()

const accentColor = computed(() => rubriqueAccent(props.rubriqueKey))
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
  background: $surface-white;
  font-family: 'Inter', sans-serif;
  text-align: left;
  color: $brand-primary;
  cursor: pointer;
  transition: background 120ms ease;

  & + & {
    border-top: 1px solid $divider-color;
  }

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
