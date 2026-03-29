<template>
  <div class="status-banner" :class="`${status}`">
    <p class="phrase">
      <strong v-if="showActionRecquise" class="action">ACTION REQUISE</strong>
      <span class="sep"> · </span
      ><!-- eslint-disable-next-line vue/no-v-html -->
      <span v-html="phraseInfoState" />
    </p>
  </div>
</template>

<script setup lang="ts">
import type { ReservationStatus } from '~/types/event'

const props = defineProps<{
  phraseInfoState: string
  status: ReservationStatus
  showActionRecquise?: boolean
}>()
</script>

<style scoped lang="scss">
.status-banner {
  border-radius: $radius-md;
  padding: $spacing-s $spacing-m;
  display: flex;
  align-items: center;
  gap: $spacing-xs;

  .icon {
    flex-shrink: 0;
  }

  &.nouvelle {
    background: rgba(201, 48, 44, 0.08);
    border: 1px solid rgba(201, 48, 44, 0.2);
    .phrase {
      color: #9b2c28;
    }
  }

  &.en_discussion {
    background: rgba(230, 126, 34, 0.08);
    border: 1px solid rgba(230, 126, 34, 0.2);
    .phrase {
      color: #924e13;
    }
  }

  &.confirmee {
    background: rgba(46, 125, 50, 0.07);
    border: 1px solid rgba(46, 125, 50, 0.2);
    .phrase {
      color: #1b5e20;
    }
  }

  &.refusee,
  &.annulee,
  &.realisee {
    background: rgba(107, 107, 107, 0.07);
    border: 1px solid rgba(107, 107, 107, 0.2);
    .phrase {
      color: #4a4a4a;
    }
  }

  .action {
    font-weight: 800;
    letter-spacing: 0.04em;
  }

  .phrase {
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    font-weight: 600;
    line-height: 1.4;
    margin: 0;

    :deep(strong) {
      font-weight: 800;
    }
  }
}
</style>
