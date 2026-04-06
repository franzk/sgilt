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

const initials = currentUser.fullName
  .split(' ')
  .map((n) => n.charAt(0).toUpperCase())
  .join('')

const cssVars = computed(() => ({
  '--ua-size': `${props.size}rem`,
  '--ua-font-size': `${props.size * 0.36}rem`,
}))
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.user-avatar {
  width: var(--ua-size);
  height: var(--ua-size);
  border-radius: 50%;
  background: $brand-accent;
  box-shadow: 0 0 0 2px $brand-accent;
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
    font-family: 'Inter', sans-serif;
    font-size: var(--ua-font-size);
    font-weight: 700;
    color: $brand-primary;
    line-height: 1;
    user-select: none;
  }
}
</style>
