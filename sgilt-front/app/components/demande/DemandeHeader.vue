<template>
  <div class="demande-header">
    <button class="back" type="button" @click="$emit('back')">
      <ArrowLeftIcon class="icon" aria-hidden="true" />
      {{ $t('tunnel.demande-header.back') }}
    </button>
    <span class="prestataire">
      <span class="prestataire-name">
        {{ $t('tunnel.demande-header.request-to', { name: prestataireName }) }}
      </span>
      <SgiltImage
        class="prestataire-avatar"
        :src="prestataireImage"
        :alt="prestataireName"
        width="96"
        height="96"
      />
    </span>
  </div>
</template>

<script setup lang="ts">
import { ArrowLeftIcon } from '@remixicons/vue/line'
import SgiltImage from '~/components/basics/media/SgiltImage.vue'

defineProps<{
  prestataireName: string
  prestataireImage: string
}>()

defineEmits<{
  back: []
}>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.demande-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-m;

  .back {
    display: inline-flex;
    align-items: center;
    gap: $spacing-xxs;
    padding: 0;
    border: none;
    background: none;
    color: $text-primary;
    font-family: inherit;
    font-size: $font-size-sm;
    cursor: pointer;

    .icon {
      width: 1.125rem;
      height: 1.125rem;
    }
  }

  .prestataire {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    min-width: 0;

    .prestataire-name {
      overflow: hidden;
      color: $text-secondary;
      font-size: $font-size-xs;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .prestataire-avatar {
      flex-shrink: 0;
      width: 2.5rem;
      height: 2.5rem;
      border-radius: 50%;
      overflow: hidden;
    }
  }
}
</style>
