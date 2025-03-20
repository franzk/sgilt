<template>
  <div class="event-board-mobile">
    <h1 class="event-title">{{ sgiltEvent?.title }}</h1>
    <GlassCard class="dashboard-container">
      <swiper
        :modules="[Navigation, Pagination]"
        :slides-per-view="1"
        :space-between="20"
        :pagination="{ clickable: true }"
        class="swiper-container"
      >
        <swiper-slide v-for="(screen, index) in screens" :key="index">
          <component :is="screen" />
        </swiper-slide>
      </swiper>
    </GlassCard>
  </div>
</template>

<script setup lang="ts">
import GlassCard from '@/components/event_board/mobile/GlassCard.vue'
import MiniDashboard from '@/components/event_board/mobile/MiniDashboard.vue'
import { Swiper, SwiperSlide } from 'swiper/vue'
import 'swiper/css'
import 'swiper/css/pagination'
import { Navigation, Pagination } from 'swiper/modules'
import MobileReservationsBoard from '@/components/event_board/mobile/MobileReservationsBoard.vue'
import { computed, ref } from 'vue'
import { useEventStore } from '@/stores/event.store'

const eventStore = useEventStore()
const sgiltEvent = computed(() => eventStore.sgiltEvent)

const screens = ref([MiniDashboard, MobileReservationsBoard])
</script>

<style scoped lang="scss">
$max-lines: 2;

.event-board-mobile {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: calc(100% - 3rem);
  padding: 1.5rem 0;
  background: linear-gradient(to bottom left, #ffbf00, #ff9900, #ff7f50);

  position: relative;
  align-items: center;
  color: $color-white;
}

.event-title {
  flex: 1 0 calc(1.2em * $max-lines);
  max-height: calc(1.2em * $max-lines);
  width: calc(100% - ($spacing-s * 2));
  font-size: clamp(1.5rem, 4vw, 1.9rem);
  line-height: 1.2;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: $max-lines;
  line-clamp: $max-lines;
  overflow: hidden;
  word-wrap: break-word;
  text-align: center;
  text-transform: uppercase;
  font-weight: 700;
  color: #fff;
  text-shadow:
    0 0 10px rgba(255, 255, 255, 0.7),
    2px 2px 5px rgba(0, 0, 0, 0.2);
}

.dashboard-container {
  flex: 1;
  width: calc(100vw - 4rem);
  padding: 1rem;

  .swiper-container {
    width: 100%;
    height: 100%;
  }

  .swiper-slide {
    display: flex;
    justify-content: center;
    text-align: center;
    height: calc(100vh - 20em);
    overflow: scroll;
  }
}
</style>
