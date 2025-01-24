<template>
  <div class="sgilt-modal" v-if="displayedPhoto" @click.self="closeModal">
    <div class="modal-overlay">
      <!-- Close Button -->
      <button class="close-btn" @click="closeModal">&times;</button>

      <!-- Previous Button -->
      <button class="nav-btn previous" @click.stop="previousPhoto" v-if="!isFirstPhoto">
        <span>&lt;</span>
      </button>

      <!-- Photo Section -->
      <div class="photo-container" @click.stop>
        <img :src="displayedPhoto" class="photo" />
        <div class="progression">
          <span
            v-for="(photo, index) in photos"
            :key="index"
            class="dot"
            :class="{ active: photo === displayedPhoto }"
            @click="displayedPhoto = photo"
          ></span>
        </div>
      </div>

      <!-- Next Button -->
      <button class="nav-btn next" @click.stop="nextPhoto" v-if="!isLastPhoto">
        <span>&gt;</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, watch } from 'vue'

const props = defineProps<{
  photos: string[]
}>()

const displayedPhoto = defineModel<string>()

const displayedPhotoIndex = computed(() =>
  displayedPhoto.value ? props.photos.indexOf(displayedPhoto.value) : -1,
)

const isFirstPhoto = computed(() => displayedPhotoIndex.value === 0)
const isLastPhoto = computed(() => displayedPhotoIndex.value === props.photos.length - 1)

const previousPhoto = () => {
  if (!isFirstPhoto.value) {
    displayedPhoto.value = props.photos[displayedPhotoIndex.value - 1]
  }
}

const nextPhoto = () => {
  if (!isLastPhoto.value) {
    displayedPhoto.value = props.photos[displayedPhotoIndex.value + 1]
  }
}

const closeModal = () => (displayedPhoto.value = '')

const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'ArrowLeft') previousPhoto()
  if (event.key === 'ArrowRight') nextPhoto()
  if (event.key === 'Escape') closeModal()
}

watch(
  () => displayedPhoto.value,
  () => {
    if (displayedPhoto.value) {
      window.addEventListener('keydown', handleKeydown)
    } else {
      window.removeEventListener('keydown', handleKeydown)
    }
  },
)

onUnmounted(() => window.removeEventListener('keydown', handleKeydown))
</script>

<style scoped lang="scss">
/* Main Modal */
.sgilt-modal {
  position: fixed;
  z-index: $z-modal;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;

  display: flex;
  justify-content: center;
  align-items: center;

  background-color: $modal-overlay;

  .modal-overlay {
    position: relative;

    width: 100%;
    height: auto;

    display: flex;
    flex-direction: column;
    justify-content: center;

    background-color: $color-secondary;
  }
}

/* Photo Container - Full Width */
.photo-container {
  position: relative;

  display: flex;
  overflow: hidden;
  align-items: center;
  justify-content: center;

  width: 100%;
  max-width: none;
  height: 60vh;

  background-color: $color-secondary;

  .photo {
    max-height: 100%;
    width: auto;
    border-radius: none;
    transition:
      transform 0.3s ease,
      opacity 0.3s ease;
  }

  .progression {
    position: absolute;
    bottom: $spacing-m;

    width: 100%;

    display: flex;
    justify-content: center;
    gap: $spacing-s;

    .dot {
      width: 1rem;
      height: 1rem;
      background-color: $shadow-s;
      border-radius: 50%;
      cursor: pointer;
      transition: background-color 0.3s ease;

      &.active {
        background-color: $color-accent;
      }

      &:hover {
        background-color: lighten($color-accent, 10%);
      }
    }
  }
}

/* Navigation Buttons */
.nav-btn {
  position: absolute;
  z-index: $z-modal;

  display: flex;
  align-items: center;
  justify-content: center;

  width: 50px;
  height: 50px;

  background-color: $color-primary;
  color: $color-white;
  border: none;

  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 4px 10px $shadow-s;

  transition:
    transform 0.2s ease,
    background-color 0.3s ease;

  &:hover {
    background-color: $color-accent;
    transform: scale(1.1);
  }

  &.previous {
    left: $spacing-l;
  }

  &.next {
    right: $spacing-l;
  }
}

/* Close Button */
.close-btn {
  position: absolute;

  top: $spacing-m;
  right: $spacing-m;
  background: none;
  border: none;
  font-size: 1.5rem;
  color: $color-subtext;
  cursor: pointer;
  transition:
    transform 0.3s ease,
    color 0.3s ease;

  &:hover {
    transform: scale(1.2);
    color: $color-accent;
  }
}

/* Responsive */
@include respond-to(tablet) {
  .photo-container {
    height: 40vh;
  }

  .nav-btn {
    width: 40px;
    height: 40px;
    &.previous {
      left: $spacing-m;
    }
    &.next {
      right: $spacing-m;
    }
  }
}

@include respond-to(mobile) {
  .photo-container {
    height: 40vh;
  }

  .nav-btn {
    width: 35px;
    height: 35px;
    font-size: 1.2rem;
  }
}
</style>
