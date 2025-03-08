<template>
  <div class="partner-title">
    <!-- Partner avatar -->
    <img :src="partner?.imageUrl" class="partner-image" />

    <!-- Partner name -->
    <span class="partner-name">{{ partner?.title }}</span>

    <!-- Kebab Button for compact mode -->
    <button v-if="compactMode" class="menu-kebab-btn" @click="toggleMenu">
      <span class="menu-icons"><SgiltIcon icon="MoreVert" /></span>
    </button>

    <!-- Dropdown Menu for compact mode -->
    <transition name="fade">
      <div v-if="isMenuOpen" class="menu" ref="menuRef">
        <button @click="$emit('pay')">
          <span class="menu-icons"><SgiltIcon icon="CreditCard" /></span>
          {{ $t('event.reservation.action.pay') }}
        </button>
        <button @click="$emit('message')">
          <span class="menu-icons"><SgiltIcon icon="ChatBubble" /></span>
          {{ $t('event.reservation.action.message') }}
        </button>
        <button class="danger" @click="$emit('cancel')">
          <span class="menu-icons"><SgiltIcon icon="Cancel" /></span>
          {{ $t('event.reservation.action.cancel') }}
        </button>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import type { Partner } from '@/data/domain/Partner'
import { ref } from 'vue'
import { onClickOutside } from '@vueuse/core'
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'

defineProps<{
  partner?: Partner
  compactMode?: boolean
}>()

defineEmits(['pay', 'message', 'cancel'])

// display/hide dropdown menu
const isMenuOpen = ref(false)
const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}

// close dropdown menu when clicking outside
const menuRef = ref<HTMLElement | null>(null)
onClickOutside(menuRef, () => (isMenuOpen.value = false))
</script>

<style scoped lang="scss">
.partner-title {
  display: flex;
  align-items: center;
  gap: $spacing-m;
  padding: $spacing-mm 0 $spacing-m $spacing-m;
  margin: 0;
  position: relative;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  background: #fff;
  border-radius: $border-radius-s $border-radius-s 0 0;

  .partner-image {
    height: 2.5rem;
    width: 2.5rem;
    border-radius: 50%;
    object-fit: cover;
  }

  .partner-name {
    flex: 1;
    font-size: 1rem;
    font-weight: 500;
    color: #333;
  }

  .menu-kebab-btn {
    background: none;
    border: none;
    cursor: pointer;
    padding: 0.5rem;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: background 0.2s ease-in-out;
    border-radius: 50%;

    &:hover {
      background: rgba(0, 0, 0, 0.1);
    }

    .material-icons {
      font-size: 1.5rem;
      color: #666;
    }
  }

  .menu {
    position: absolute;
    top: 100%;
    right: 0;
    background: #fff;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
    border-radius: 0.5rem;
    overflow: hidden;
    min-width: 160px;
    z-index: 10;
    display: flex;
    flex-direction: column;

    button {
      all: unset;
      padding: 0.75rem 1rem;
      display: flex;
      align-items: center;
      gap: 0.5rem;
      cursor: pointer;
      transition: background 0.2s ease-in-out;
      font-size: 0.9rem;
      color: #333;

      &:hover {
        background: rgba(0, 0, 0, 0.05);
        font-weight: 500;
        transform: scale(1.02);
        transition: scale 0.2s ease-in-out;
      }

      .menu-icons {
        font-size: 1.2rem;
        color: inherit;
      }

      &.danger {
        color: $color-danger;

        &:hover {
          background: rgba(231, 76, 60, 0.1);
        }
      }
    }
  }
}

.fade-enter-active,
.fade-leave-active {
  transition:
    opacity 0.2s ease-out,
    transform 0.2s ease-out;
}

.fade-enter {
  opacity: 0;
  transform: translateY(-5px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}
</style>
