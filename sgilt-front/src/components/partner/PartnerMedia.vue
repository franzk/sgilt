<template>
  <div class="partner-video">
    <SgiltVideo :youtubeId="partnerStore.partner?.youtubeId" />
  </div>

  <!-- Photo gallery -->
  <div class="photo-gallery">
    <ul class="gallery-grid">
      <li v-for="(photo, index) in photos" :key="index" class="gallery-item">
        <img :src="photo" :alt="`Photo ${index + 1}`" @click="displayedPhoto = photo" />
      </li>
    </ul>
  </div>

  <SgiltModalGallery v-model="displayedPhoto" :photos="photos" />
</template>

<script setup lang="ts">
import SgiltVideo from '@/components/basics/media/SgiltVideo.vue'
import SgiltModalGallery from '@/components/basics/media/SgiltModalGallery.vue'

import { ref } from 'vue'
import { usePartnerStore } from '@/stores/partner.store'
defineProps<{
  photos: string[]
}>()

const partnerStore = usePartnerStore()

const displayedPhoto = ref()
</script>

<style scoped lang="scss">
.partner-video {
  width: 100%;
  display: flex;
  justify-content: center;
}

.gallery-grid {
  display: grid;
  gap: $spacing-m;
  padding: $spacing-m;
  grid-template-columns: repeat(4, 1fr);
  align-content: center;
  margin-top: 0;

  .gallery-item {
    list-style: none;
    text-align: center;

    img {
      width: 100%;
      max-width: 30rem;
      aspect-ratio: 1.3;
      object-fit: cover;
      border-radius: $border-radius-s;
      transition:
        transform 0.3s ease,
        box-shadow 0.3s ease;
      cursor: pointer;
      &:hover {
        transform: scale(1.01);
        filter: brightness(0.8);
      }
    }
  }
}

/* responsive */
@include respond-to(tablet) {
  .photo-gallery {
    margin-top: $spacing-s;
  }
  .gallery-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@include respond-to(mobile) {
  .photo-gallery {
    margin-top: initial;
  }
}
</style>
