<template>
  <div class="user-avatar" :style="cssVars">
    <img v-if="currentUser.photo" :src="currentUser.photo" alt="" class="photo" />
    <span v-else class="initials">{{ initials }}</span>
  </div>
</template>

<script setup lang="ts">
import { useCurrentUser } from '~/composables/useCurrentUser'

const props = defineProps<{ size: number }>()

const currentUser = useCurrentUser()

const initials = computed(
  () =>
    (currentUser.firstName ? currentUser.firstName.charAt(0).toUpperCase() : '') +
    (currentUser.lastName ? currentUser.lastName.charAt(0).toUpperCase() : ''),
)

const cssVars = computed(() => ({
  '--ua-size': `${props.size}rem`,
  '--ua-font-size': `${props.size * 0.45}rem`,
}))
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.user-avatar {
  width: var(--ua-size);
  height: var(--ua-size);
  border-radius: 50%;

  background:
    radial-gradient(
      circle at 35% 25%,
      rgba(255, 255, 255, 0.34) 0%,
      rgba(255, 255, 255, 0.12) 32%,
      transparent 58%
    ),
    linear-gradient(145deg, #ffd24a 0%, #ffc71f 42%, #ffbf00 100%);

  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;

  .photo {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .initials {
    font-family: 'Cormorant Garamond', serif;
    font-size: var(--ua-font-size);
    font-weight: 600;
    color: $brand-primary;
    line-height: 1;
    user-select: none;
  }
}
</style>
