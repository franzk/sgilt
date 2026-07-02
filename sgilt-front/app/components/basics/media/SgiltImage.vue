<script setup lang="ts">
import { ImageIcon } from '@remixicons/vue/line'
import Sk from '~/components/basics/Sk.vue'

const props = defineProps<{
  src?: string
  alt?: string
  width?: string | number
  height?: string | number
  loading?: 'lazy' | 'eager'
  fit?: 'cover' | 'contain'
}>()

const { toUrl } = useImageUrl()

const resolvedSrc = computed(() =>
  props.src ? toUrl(props.src) : '/images/placeholder-default.jpg',
)

const isLoaded = ref(false)
const hasError = ref(false)

// On réinitialise si la source change (ex: navigation)
watch(
  () => props.src,
  () => {
    isLoaded.value = false
    hasError.value = false
  },
)
</script>

<template>
  <div class="sgilt-image-container" :class="{ 'is-loading': !isLoaded && !hasError }">
    <Sk v-if="!isLoaded && !hasError" class="image-skeleton" />

    <div v-if="hasError" class="image-error">
      <ImageIcon />
    </div>

    <img
      v-show="!hasError"
      :src="resolvedSrc"
      :alt="alt"
      :loading="loading || 'lazy'"
      @load="isLoaded = true"
      @error="hasError = true"
      :class="{ 'is-visible': isLoaded }"
    />
  </div>
</template>

<style scoped lang="scss">
.sgilt-image-container {
  position: relative;
  overflow: hidden;
  width: 100%;
  height: 100%;
  background-color: #f1f5f9;

  .image-skeleton {
    position: absolute;
    inset: 0;
    z-index: 1;
    width: auto;
    height: auto;
    border-radius: 0;
  }

  img {
    width: 100%;
    height: 100%;
    display: block;
    object-fit: cover;
    opacity: 0;
    transition: opacity 0.5s ease;

    &.is-visible {
      opacity: 1;
    }
  }

  .image-error {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    color: #cbd5e1;
  }
}
</style>
