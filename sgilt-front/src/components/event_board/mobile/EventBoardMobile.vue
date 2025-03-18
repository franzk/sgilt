<template>
  <div class="event-board-mobile">
    <h1 class="event-title">{{ sgiltEvent?.title }}</h1>
    <GlassCard class="dashboard-container">
      <MiniDashboard :sgiltEvent="sgiltEvent" />
    </GlassCard>
  </div>
</template>

<script setup lang="ts">
import GlassCard from '@/components/event_board/mobile/GlassCard.vue'
import MiniDashboard from '@/components/event_board/mobile/MiniDashboard.vue'
import type { SgiltEvent } from '@/data/domain/SgiltEvent'

defineProps<{
  sgiltEvent?: SgiltEvent
}>()
</script>

<style scoped lang="scss">
$max-lines: 2;

.event-board-mobile {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: calc(100% - 3rem);
  padding: 1.5rem 2rem;
  background: linear-gradient(to bottom left, #ffbf00, #ff9900, #ff7f50);

  position: relative;
  align-items: center;
  color: $color-white;
}

.event-title {
  flex: 1 0 calc(1.2em * $max-lines);
  max-height: calc(1.2em * $max-lines);
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
  width: 100%;
  max-width: 400px;
  padding: 1rem;
}
</style>
