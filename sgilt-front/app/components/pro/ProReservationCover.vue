<template>
  <div
    v-if="reservation"
    ref="bannerRef"
    class="cover-banner"
    :style="{ backgroundImage: `url(${coverImage})` }"
  >
    <div class="overlay" />
    <button class="back-btn" type="button" @click="$emit('back')">
      {{ $t('pro.reservation-detail.back') }}
    </button>
    <div class="bottom">
      <span class="name">{{ reservation.event.title }}</span>
      <span class="badge" :style="getStatusOverlayStyle(reservation.status)">
        {{ $t(`reservation.statut.${reservation.status}`) }}
      </span>
    </div>
  </div>
  <Sk v-else class="cover-banner-sk" />
</template>

<script setup lang="ts">
// Bandeau de la fiche d'une réservation côté prestataire : couverture de l'événement, titre de
// l'événement, statut (pendant de ReservationMeta côté client).
import Sk from '~/components/basics/Sk.vue'
import { getStatusOverlayStyle } from '~/constants/reservation-status'
import type { ProReservationDetail } from '~/data/reservation/domain/ProReservationDetail'
import { defaultCoverPath } from '~/utils/eventCovers'

const props = defineProps<{
  reservation: ProReservationDetail | null
}>()

defineEmits<{ back: [] }>()

const { toUrl } = useImageUrl()
const coverImage = computed(() => {
  if (!props.reservation) return ''
  const { coverImage, eventType } = props.reservation.event
  return toUrl(coverImage || defaultCoverPath(eventType))
})

// ── Parallax ───────────────────────────────────────────────────────────────────
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
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 200px;
  padding: $spacing-s $spacing-m $spacing-m;
  background-color: $brand-subtle;
  background-size: cover;
  background-position: center 50%;

  @media (min-width: $desktop) {
    height: 280px;
    max-height: 30vh;
    padding: $spacing-m $spacing-xl $spacing-l;
  }

  .overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(to bottom, rgba(47, 42, 37, 0.1), rgba(47, 42, 37, 0.65));
    pointer-events: none;
  }

  .back-btn {
    position: relative;
    align-self: flex-start;
    padding: 0;
    border: none;
    background: none;
    color: rgba(255, 255, 255, 0.85);
    font-family: 'Inter', sans-serif;
    font-size: 0.875rem;
    font-weight: 500;
    text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
    cursor: pointer;
    transition: color 150ms ease;

    &:active {
      color: #fff;
    }
  }

  .bottom {
    position: relative;
    display: flex;
    align-items: flex-end;
    justify-content: space-between;
    gap: $spacing-s;

    .name {
      max-width: 85%;
      font-family: 'Cormorant Garamond', serif;
      font-size: 26px;
      font-weight: 600;
      color: #fff;
      line-height: 1.1;
      text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);

      @media (min-width: $desktop) {
        font-size: 36px;
      }
    }

    .badge {
      position: relative;
      flex-shrink: 0;
      padding: 3px 9px;
      border-radius: 2rem;
      font-size: 0.625rem;
      font-weight: 600;
      white-space: nowrap;
      backdrop-filter: blur(4px);
    }
  }
}

.cover-banner-sk {
  height: 200px;

  @media (min-width: $desktop) {
    height: 280px;
    max-height: 30vh;
  }
}
</style>
