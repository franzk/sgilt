<template>
  <div class="sgilt-modal" v-if="displayedPhoto" @click="closeModal">
    <div class="banner" @click.stop>
      <!-- Close button -->
      <div class="close" @click="closeModal">&times;</div>

      <!-- Previous button -->
      <div class="previous" @click.stop="previousPhoto">
        <span v-if="!isFirstPhoto">&lt;</span>
      </div>

      <!-- Photo & progression -->
      <div class="photo">
        <div class="spacer"></div>
        <div class="displayed-photo">
          <img :src="displayedPhoto" alt="Selected photo" />
        </div>
        <div class="progression">
          <span v-for="(photo, index) in photos" :key="index" @click="displayedPhoto = photo">
            {{ photo === displayedPhoto ? '●' : '○' }}
          </span>
        </div>
      </div>

      <!-- Next button -->
      <div class="next" @click.stop="nextPhoto"><span v-if="!isLastPhoto">&gt;</span></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted } from 'vue'

const props = defineProps<{
  photos: string[]
}>()

const displayedPhoto = defineModel<string>()

const displayedPhotoIndex = computed(() =>
  displayedPhoto.value ? props.photos.indexOf(displayedPhoto.value) : -1,
)

// computed properties
const isFirstPhoto = computed(() => displayedPhotoIndex.value === 0)
const isLastPhoto = computed(() => displayedPhotoIndex.value === props.photos.length - 1)

// actions
const previousPhoto = () => {
  if (props.photos.length > 0) {
    const previousIndex = displayedPhotoIndex.value - 1
    if (previousIndex >= 0) {
      displayedPhoto.value = props.photos[previousIndex]
    }
  }
}

const nextPhoto = () => {
  const nextIndex = displayedPhotoIndex.value + 1
  if (nextIndex < props.photos.length) {
    displayedPhoto.value = props.photos[nextIndex]
  }
}

const closeModal = () => {
  displayedPhoto.value = ''
}

// keyboard navigation
const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'ArrowLeft') {
    previousPhoto()
  } else if (event.key === 'ArrowRight') {
    nextPhoto()
  } else if (event.key === 'Escape') {
    closeModal()
  }
}
// add and remove event listeners
onMounted(() => {
  window.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped lang="scss">
.sgilt-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.85); // Background légèrement transparent
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;

  .banner {
    position: relative;
    width: 90%;
    max-width: 900px;
    height: 60%;
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.4);
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;

    // Close button
    .close {
      position: absolute;
      top: 1rem;
      right: 1rem;
      font-size: 2rem;
      color: #666;
      cursor: pointer;
      transition: transform 0.2s ease;

      &:hover {
        transform: scale(1.2);
        color: #000;
      }
    }

    // Previous button
    .previous,
    .next {
      position: absolute;
      top: 50%;
      transform: translateY(-50%);
      font-size: 3rem;
      color: #999;
      cursor: pointer;
      user-select: none;
      transition: all 0.3s ease;

      &:hover {
        color: #000;
      }

      span {
        display: inline-block;
      }
    }

    .previous {
      left: 1rem;
    }

    .next {
      right: 1rem;
    }

    // Photo container
    .photo {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;

      .spacer {
        flex: 1;
      }

      .displayed-photo {
        flex: 5;
        max-height: 100%;
        display: flex;
        justify-content: center;
        align-items: center;

        img {
          max-width: 100%;
          max-height: 100%;
          object-fit: cover;
          border-radius: 4px;
          transition:
            opacity 0.5s ease-in-out,
            transform 0.5s ease-in-out;
        }
      }

      // Progression dots
      .progression {
        flex: 1;
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 0.5rem;

        span {
          font-size: 1.5rem;
          color: #ddd;
          cursor: pointer;
          transition:
            color 0.3s ease,
            transform 0.3s ease;

          &:hover {
            color: #000;
            transform: scale(1.2);
          }

          &.active {
            color: #000;
          }
        }
      }
    }
  }

  // Animations
  @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }

  // Apply animation
  animation: fadeIn 0.3s ease;

  // Responsive adjustments
  @media (max-width: 768px) {
    .banner {
      width: 100%;
      height: 70%;

      .previous,
      .next {
        font-size: 2rem;
      }

      .photo .progression span {
        font-size: 1rem;
      }
    }
  }
}
</style>
