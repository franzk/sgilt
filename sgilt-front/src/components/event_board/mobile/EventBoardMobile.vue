<template>
  <div class="event-board-mobile">
    <h1 class="event-title">L'event qui déchire</h1>
    <GlassCard>
      <p>
        Lorem Elsass ipsum gravida dolor eleifend Miss Dahlias habitant barapli blottkopf, geïz
        ftomi! sit non lacus rhoncus Chulien mänele hopla gal in, quam, sit bredele semper senectus
        tellus vielmols, Yo dû. nullam kuglopf Pellentesque Heineken und baeckeoffe ornare porta
        elit tellus schnaps turpis Huguette id vulputate dui wurscht jetz gehts los ac adipiscing
        leo hopla eget suspendisse Pfourtz !
      </p>
    </GlassCard>
  </div>
</template>

<script setup lang="ts">
import GlassCard from '@/components/event_board/mobile/GlassCard.vue'
</script>

<style scoped lang="scss">
.event-board-mobile {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding: $spacing-m;
  background:
    linear-gradient(to bottom, rgba(0, 0, 0, 0.05), transparent),
    url('@/assets/images/mobile/background.svg') center/cover no-repeat;
  position: relative;
}

.event-title {
  font-size: 2.1rem;
  font-weight: 700;
  color: #fff;
  text-transform: uppercase;
  text-align: center;

  text-shadow:
    0 0 10px rgba(255, 255, 255, 0.7),
    2px 2px 5px rgba(0, 0, 0, 0.2);
}
</style>
