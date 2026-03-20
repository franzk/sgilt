<template>
  <div class="sgilt-card" :class="{ 'sgilt-card--clickable': clickable }">
    <div class="sgilt-card__media">
      <img :src="image" alt="" />
      <div class="sgilt-card__gradient" aria-hidden="true" />
      <div class="sgilt-card__overlay">
        <slot name="overlay" />
      </div>
    </div>
    <div v-if="$slots.footer" class="sgilt-card__footer">
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

  &--clickable {
    cursor: pointer;

    .sgilt-card__media img {
      transition: transform 300ms ease;
    }

    &:hover .sgilt-card__media img {
      transform: scale(1.03);
    }
  }

  &__media {
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

  &__gradient {
    position: absolute;
    inset: 0;
    background: linear-gradient(to bottom, transparent 35%, rgba(0, 0, 0, 0.65) 100%);
    pointer-events: none;
  }

  &__overlay {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    padding: $spacing-s;
  }

  &__footer {
    padding: $spacing-s;
    background: #fff;
  }
}
</style>
