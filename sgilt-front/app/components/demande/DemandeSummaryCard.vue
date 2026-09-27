<template>
  <div class="summary-card">
    <p v-if="eyebrow" class="eyebrow">{{ eyebrow }}</p>
    <div class="prestataire">
      <SgiltImage
        class="avatar"
        :src="summary.prestataireImage"
        :alt="summary.prestataireName"
        width="128"
        height="128"
      />
      <span class="text">
        <span class="name">{{ summary.prestataireName }}</span>
        <span class="category">{{ summary.prestataireCategoryLine }}</span>
      </span>
    </div>
    <div class="event">
      <span class="info">
        <CalendarEventIcon class="icon" aria-hidden="true" />
        <span class="lines">
          <span v-if="summary.evenement.eventTypeLabel">{{ summary.evenement.eventTypeLabel }}</span>
          <span>{{ formatDate(summary.evenement.date) }}</span>
        </span>
      </span>
      <span v-if="summary.evenement.nbInvites" class="info">
        <GroupIcon class="icon" aria-hidden="true" />
        {{ $t('tunnel.summary.invites-value', { value: summary.evenement.nbInvites }) }}
      </span>
      <span class="info">
        <MapPin2Icon class="icon" aria-hidden="true" />
        {{ summary.evenement.ville }}
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { CalendarEventIcon, GroupIcon, MapPin2Icon } from '@remixicons/vue/line'
import SgiltImage from '~/components/basics/media/SgiltImage.vue'
import type { DemandeSummary } from '~/types/demande'
import { formatDate } from '~/utils/dateUtils'

defineProps<{
  summary: DemandeSummary
  // Petit titre au-dessus du prestataire (écran de confirmation).
  eyebrow?: string
}>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.summary-card {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
  padding: $spacing-m;
  border-radius: $radius-lg;
  background: $surface-soft;

  .eyebrow {
    margin: 0;
    color: $text-secondary;
    font-size: $font-size-xs;
    font-weight: $font-weight-semibold;
    letter-spacing: 0.14em;
    text-transform: uppercase;
  }

  .prestataire {
    display: flex;
    align-items: center;
    gap: $spacing-s;
    padding-bottom: $spacing-s;
    border-bottom: 1px solid $divider-color;

    .avatar {
      flex-shrink: 0;
      width: 3.5rem;
      height: 3.5rem;
      border-radius: 50%;
      overflow: hidden;
    }

    .text {
      display: flex;
      flex-direction: column;
      gap: 0.125rem;
      min-width: 0;

      .name {
        color: $text-primary;
        font-size: $font-size-md;
        font-weight: $font-weight-bold;
      }

      .category {
        color: $text-secondary;
        font-size: $font-size-xs;
      }
    }
  }

  .event {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    justify-content: space-between;
    gap: $spacing-s;

    .info {
      display: inline-flex;
      align-items: center;
      gap: $spacing-xs;
      color: $text-primary;
      font-size: $font-size-xs;

      .icon {
        flex-shrink: 0;
        width: 1.375rem;
        height: 1.375rem;
      }

      .lines {
        display: flex;
        flex-direction: column;
      }
    }
  }
}
</style>
