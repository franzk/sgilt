<template>
  <div class="sgilt-card" :class="{ clickable: clickable }">
    <div class="media">
      <img :src="image" alt="" />
      <div class="gradient" aria-hidden="true" />
      <div class="overlay">
        <slot name="overlay" />
      </div>
    </div>
    <div v-if="$slots.footer" class="footer">
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    image: string
    ratio?: string
    clickable?: boolean
  }>(),
  {
    ratio: '4/3',
    clickable: true,
  },
)
</script>

<style scoped lang="scss">
.sgilt-card {
  display: flex;
  flex-direction: column;
  border-radius: $radius-lg;
  overflow: hidden;

  &.clickable {
    cursor: pointer;

    .media img {
      transition: transform 300ms ease;
    }

    &:hover .media img {
      transform: scale(1.03);
    }
  }

  .media {
    position: relative;
    aspect-ratio: v-bind(ratio);
    overflow: hidden;

    img {
      position: absolute;
      inset: 0;
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .gradient {
    position: absolute;
    inset: 0;
    background: linear-gradient(to bottom, transparent 35%, rgba(0, 0, 0, 0.65) 100%);
    pointer-events: none;
  }

  .overlay {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    padding: $spacing-s;
  }

  .footer {
    background: #fff;
  }
}
</style>
