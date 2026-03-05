<script setup lang="ts">
import IconPhotoBreak from '~/components/icons/IconPhotoBreak.vue'

const props = defineProps<{
  src?: string
  alt?: string
  width?: string | number
  height?: string | number
  loading?: 'lazy' | 'eager'
  fit?: 'cover' | 'contain'
}>()

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
    <div v-if="!isLoaded && !hasError" class="image-skeleton shimmer-container"></div>

    <div v-if="hasError" class="image-error">
      <IconPhotoBreak />
    </div>

    <NuxtImg
      v-show="!hasError"
      :src="src || '/images/placeholder-default.jpg'"
      :alt="alt"
      :width="width"
      :height="height"
      :loading="loading || 'lazy'"
      :fit="fit || 'cover'"
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
