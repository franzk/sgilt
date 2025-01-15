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
  background-color: rgba(0, 0, 0, 0.8);
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
}

.banner {
  width: 100%;
  height: 60%;
  background-color: $color-secondary;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: row;
  overflow: hidden;
  justify-content: center;
  position: relative;
  align-items: center;
}

.close {
  position: absolute;
  top: 0;
  right: 0;
  padding: $spacing-s;
  font-size: 3rem;
  color: $color-accent;
  cursor: pointer;
}

.previous,
.next {
  width: 10%;
  height: 80%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 5rem;
  color: $color-accent;
  cursor: pointer;

  &:has(span):hover {
    background-color: $shadow-s;
  }
}

.photo {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  justify-content: center;
  align-items: center;
  height: 100%;

  .spacer {
    flex: 1;
  }

  .displayed-photo {
    flex: 0 0 80%;
    overflow: hidden;
    img {
      height: 100%;
    }
  }

  .progression {
    font-size: 2.5em;
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    span {
      cursor: pointer;
      padding: 0 $spacing-s;
    }
  }
}

/* responsive */
@include respond-to(tablet) {
  .banner {
    height: 50%;
    padding: 0;
  }

  .close {
    padding-top: $spacing-xs;
    padding-right: $spacing-xxs;
  }

  .photo {
    .displayed-photo {
      display: flex;
      overflow: hidden;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      // width: 100%;
      img {
        width: 100%;
        height: auto;
      }
    }
  }
}

@include respond-to(mobile) {
  .banner {
    height: 40%;
  }
}
</style>
