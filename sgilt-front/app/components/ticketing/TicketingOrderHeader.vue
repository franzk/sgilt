<template>
  <header class="order-header">
    <button type="button" class="back-btn" @click="router.back()">
      <ArrowLeftSIcon class="icon" />
      {{ $t('ticketing.order.back') }}
    </button>
    <h1 class="title">{{ title }}</h1>
    <span class="secure-badge">
      <LockIcon class="icon" />
      {{ $t('ticketing.order.secure-payment') }}
    </span>
  </header>
</template>

<script setup lang="ts">
import { ArrowLeftSIcon, LockIcon } from '@remixicons/vue/line'

defineProps<{ title: string }>()

const router = useRouter()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-s;

  .back-btn {
    display: inline-flex;
    align-items: center;
    gap: 0.2rem;
    padding: 0;
    border: none;
    background: none;
    font-family: inherit;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    color: $text-primary;
    cursor: pointer;
    flex-shrink: 0;

    .icon {
      width: 1.25rem;
      height: 1.25rem;
    }
  }

  .title {
    margin: 0;
    font-size: $font-size-md;
    font-weight: $font-weight-bold;
    color: $text-primary;
    text-align: center;
  }

  .secure-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.3rem;
    flex-shrink: 0;
    font-size: $font-size-xs;
    color: $text-secondary;
    white-space: nowrap;

    .icon {
      width: 0.9rem;
      height: 0.9rem;
    }
  }
}
</style>
