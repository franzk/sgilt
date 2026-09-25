<template>
  <NuxtLink :to="`/${reservation.prestataireSlug}`" class="reservation-item">
    <SgiltImage
      class="thumb"
      :src="reservation.prestataireImage"
      :alt="reservation.prestataireName"
      width="160"
      height="160"
    />
    <span class="text">
      <span class="name">{{ reservation.prestataireName }}</span>
      <span class="status">
        {{ $t(`evenement.rubrique.status.${reservation.status}`) }}
      </span>
      <span class="date">{{ formatDateShort(reservation.sentAt) }}</span>
    </span>
    <ArrowRightSIcon class="chevron" aria-hidden="true" />
  </NuxtLink>
</template>

<script setup lang="ts">
import { ArrowRightSIcon } from '@remixicons/vue/line'
import SgiltImage from '~/components/basics/media/SgiltImage.vue'
import type { RubriqueReservation } from '~/constants/event-rubriques'
import { formatDateShort } from '~/utils/dateUtils'

defineProps<{
  reservation: RubriqueReservation
}>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.reservation-item {
  display: flex;
  align-items: center;
  gap: $spacing-s;
  padding: $spacing-s;
  border: 1px solid $divider-color;
  border-radius: $radius-lg;
  background: $surface-white;
  color: $text-primary;
  text-decoration: none;
  transition: background 120ms ease;

  &:active {
    background: $surface-soft;
  }

  .thumb {
    flex-shrink: 0;
    width: 4.5rem;
    height: 4.5rem;
    border-radius: $radius-md;
    overflow: hidden;
  }

  .text {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
    min-width: 0;

    .name {
      font-size: $font-size-sm;
      font-weight: $font-weight-semibold;
    }

    .status {
      padding: 0.125rem 0.5rem;
      border-radius: 9999px;
      background: rgba($brand-accent, 0.18);
      color: $brand-primary;
      font-size: $font-size-xs;
      font-weight: $font-weight-semibold;
    }

    .date {
      color: $text-secondary;
      font-size: $font-size-xs;
    }
  }

  .chevron {
    flex-shrink: 0;
    width: 1.125rem;
    height: 1.125rem;
    color: $text-secondary;
  }
}
</style>
