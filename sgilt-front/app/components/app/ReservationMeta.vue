<template>
  <div
    ref="bannerRef"
    class="cover-banner"
    :style="!loading && reservation?.prestatairePhoto
      ? { backgroundImage: `url(${reservation.prestatairePhoto})` }
      : {}"
  >
    <div class="cover-banner__overlay" />

    <button class="back-btn" type="button" @click="$emit('back')">
      {{ $t('common.back') }}
    </button>

    <div class="cover-banner__bottom">
      <div class="cover-banner__info">
        <span class="cover-banner__category">
          <span v-if="loading" class="skeleton skeleton--sm" />
          <template v-else>{{ reservation?.category }}</template>
        </span>
        <span class="cover-banner__name">
          <span v-if="loading" class="skeleton skeleton--lg" />
          <template v-else>{{ reservation?.prestataireName }}</template>
        </span>
      </div>
      <span
        v-if="!loading && reservation"
        class="cover-banner__badge"
        :style="getStatusOverlayStyle(reservation.status)"
      >
        {{ $t(`reservation.statut.${reservation.status}`) }}
      </span>
      <span v-else-if="loading" class="skeleton skeleton--badge" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { getStatusOverlayStyle } from '~/constants/reservation-status'
import type { ReservationMeta } from '~/data/reservation/domain/ReservationMeta'

defineProps<{
  reservation: ReservationMeta | null
  loading: boolean
}>()

defineEmits<{ back: [] }>()

const bannerRef = ref<HTMLElement | null>(null)
let rafId: number | null = null

function onScroll() {
  if (rafId !== null) return
  rafId = requestAnimationFrame(() => {
    if (bannerRef.value) {
      bannerRef.value.style.backgroundPositionY = `calc(50% + ${window.scrollY * 0.4}px)`
    }
    rafId = null
  })
}

onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
  if (rafId !== null) cancelAnimationFrame(rafId)
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;

.cover-banner {
  position: relative;
  height: 200px;
  background-size: cover;
  background-position: center 50%;
  background-color: $brand-subtle;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: $spacing-s $spacing-m $spacing-m;

  @media (min-width: $desktop) {
    height: 280px;
    padding: $spacing-m $spacing-xl $spacing-l;
  }

  &__overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(to bottom, rgba(47, 42, 37, 0.1), rgba(47, 42, 37, 0.65));
    pointer-events: none;
  }

  &__bottom {
    position: relative;
    display: flex;
    align-items: flex-end;
    justify-content: space-between;
    gap: $spacing-s;
  }

  &__info {
    display: flex;
    flex-direction: column;
    gap: 3px;
  }

  &__category {
    font-family: 'Inter', sans-serif;
    font-size: 11px;
    font-weight: 600;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: rgba(255, 255, 255, 0.75);
  }

  &__name {
    font-family: 'Cormorant Garamond', serif;
    font-size: 28px;
    font-weight: 600;
    color: #fff;
    line-height: 1.1;
    text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);

    @media (min-width: $desktop) {
      font-size: 38px;
    }
  }

  &__badge {
    flex-shrink: 0;
    position: relative;
    font-size: 0.625rem;
    font-weight: 600;
    padding: 3px 9px;
    border-radius: 2rem;
    white-space: nowrap;
    backdrop-filter: blur(4px);
  }
}

.back-btn {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 0;
  border: none;
  background: none;
  font-family: 'Inter', sans-serif;
  font-size: 0.875rem;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.85);
  cursor: pointer;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
  transition: color 150ms ease;

  &:active { color: #fff; }
}

.skeleton {
  display: inline-block;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.25);
  animation: shimmer 1.4s infinite;

  &--sm  { width: 80px;  height: 10px; }
  &--lg  { width: 200px; height: 28px; }
  &--badge { width: 60px; height: 20px; border-radius: 2rem; }
}

@keyframes shimmer {
  0%, 100% { opacity: 0.5; }
  50%       { opacity: 1;   }
}
</style>
