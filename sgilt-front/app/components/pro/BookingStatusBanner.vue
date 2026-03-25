<template>
  <div class="status-banner" :class="`status-banner--${status}`">
    <!-- eslint-disable-next-line vue/no-v-html -->
    <p class="status-banner__phrase" v-html="phraseInfoState" />
  </div>
</template>

<script setup lang="ts">
import type { ReservationStatus } from '~/types/event'

defineProps<{
  phraseInfoState: string
  status: ReservationStatus
}>()
</script>

<style scoped lang="scss">
.status-banner {
  border-radius: $radius-md;
  padding: $spacing-s $spacing-m;

  &--nouvelle {
    background: rgba(201, 48, 44, 0.08);
    border: 1px solid rgba(201, 48, 44, 0.2);
    .status-banner__phrase { color: #9b2c28; }
  }

  &--en_discussion {
    background: rgba(230, 126, 34, 0.08);
    border: 1px solid rgba(230, 126, 34, 0.2);
    .status-banner__phrase { color: #924e13; }
  }

  &--confirmee {
    background: rgba(46, 125, 50, 0.07);
    border: 1px solid rgba(46, 125, 50, 0.2);
    .status-banner__phrase { color: #1b5e20; }
  }

  &--refusee,
  &--annulee,
  &--realisee {
    background: rgba(107, 107, 107, 0.07);
    border: 1px solid rgba(107, 107, 107, 0.2);
    .status-banner__phrase { color: #4a4a4a; }
  }

  &__phrase {
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    line-height: 1.4;
    margin: 0;

    :deep(strong) { font-weight: 700; }
  }
}
</style>
