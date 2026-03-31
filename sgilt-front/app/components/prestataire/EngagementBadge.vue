<script setup lang="ts">
import type { EngagementBadge } from '~/utils/constants'
import IconClock from '~/components/icons/IconClock.vue'
import IconTune from '~/components/icons/IconTune.vue'
import IconHandshake from '~/components/icons/IconHandshake.vue'
import IconInventory2 from '~/components/icons/IconInventory2.vue'
import IconPersonCheck from '~/components/icons/IconPersonCheck.vue'
import IconEco from '~/components/icons/IconEco.vue'
import type { Component } from 'vue'

defineProps<{
  badge: EngagementBadge
}>()

const iconMap: Record<string, Component> = {
  Clock: IconClock,
  Tune: IconTune,
  Handshake: IconHandshake,
  Inventory_2: IconInventory2,
  Person_Check: IconPersonCheck,
  Eco: IconEco,
}
</script>

<template>
  <div class="engagement-badge" :class="{ eco: badge.color === '#22C55E' }">
    <div class="icon-wrap">
      <component :is="iconMap[badge.icon]" class="icon" />
    </div>
    <div class="body">
      <span class="label">{{ badge.label }}</span>
      <span v-if="badge.description" class="description">
        {{ badge.description }}
      </span>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:color';
@use '@/assets/styles/base' as *;

.engagement-badge {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.75rem;
  padding: 1.25rem 0.75rem 1rem;
  border-radius: 12px;
  background: linear-gradient(160deg, #fffbea 0%, #fff8d6 100%);
  border: 1px solid rgba(#facc15, 0.35);
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.04),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  text-align: center;
  transition:
    transform 180ms ease,
    box-shadow 180ms ease;
  overflow: hidden;

  // Ligne dorée en haut — signature artisanale
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 16px;
    right: 16px;
    height: 2px;
    border-radius: 0 0 2px 2px;
    background: linear-gradient(90deg, transparent, #facc15, transparent);
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow:
      0 6px 16px rgba(#facc15, 0.18),
      inset 0 1px 0 rgba(255, 255, 255, 0.9);
  }

  // Variante éco — vert
  &.eco {
    background: linear-gradient(160deg, #f0fdf4 0%, #dcfce7 100%);
    border-color: rgba(#22c55e, 0.3);

    &::before {
      background: linear-gradient(90deg, transparent, #22c55e, transparent);
    }

    &:hover {
      box-shadow:
        0 6px 16px rgba(#22c55e, 0.15),
        inset 0 1px 0 rgba(255, 255, 255, 0.9);
    }

    .icon-wrap {
      background: rgba(#22c55e, 0.12);
      color: #16a34a;
    }
  }

  // Icône dans un cercle doux
  .icon-wrap {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 2.75rem;
    height: 2.75rem;
    border-radius: 50%;
    background: rgba(#facc15, 0.18);
    color: color.adjust(#facc15, $lightness: -22%);
    flex-shrink: 0;
    transition: background 180ms ease;
  }

  .icon {
    width: 1.3rem;
    height: 1.3rem;
  }

  .body {
    display: flex;
    flex-direction: column;
    gap: 0.3rem;
  }

  .label {
    font-size: 0.8rem;
    font-weight: 700;
    color: $color-primary;
    line-height: 1.2;
    letter-spacing: 0.01em;
  }

  .description {
    font-size: 0.72rem;
    font-weight: 400;
    color: $text-secondary;
    line-height: 1.4;
    opacity: 0.85;
  }
}
</style>
