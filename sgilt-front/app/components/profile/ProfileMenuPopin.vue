<template>
  <Teleport to="body">
    <Transition name="popin">
      <div v-if="open" ref="popinRef" class="profile-popin" role="menu" aria-label="Menu compte">
        <!-- ── Header : avatar + nom + email ──────────────────────────────────── -->
        <div class="header">
          <UserAvatar :size="2.25" />
          <div class="header-info">
            <span class="header-name">{{ currentUser.fullName }}</span>
            <span class="header-email">{{ currentUser.email }}</span>
          </div>
        </div>

        <div class="separator" />

        <!-- ── Section 1 : compte ─────────────────────────────────────────────── -->
        <div class="group">
          <NuxtLink to="/account/profile" class="item" role="menuitem" @click="close">
            <UserIcon class="item-icon" />
            <span>{{ $t('profile.menu.my-info') }}</span>
          </NuxtLink>
          <button class="item item--danger" role="menuitem" type="button" @click="logout">
            <LogoutBoxRIcon class="item-icon" />
            <span>{{ $t('profile.menu.logout') }}</span>
          </button>
        </div>

        <div class="separator" />

        <!-- ── Section 2 : aide + contact ────────────────────────────────────── -->
        <div class="group">
          <a href="#" class="item" role="menuitem" @click.prevent="close">
            <QuestionIcon class="item-icon" />
            <span>{{ $t('profile.menu.help-center') }}</span>
          </a>
          <a href="#" class="item" role="menuitem" @click.prevent="close">
            <MailIcon class="item-icon" />
            <span>{{ $t('profile.menu.contact-sgilt') }}</span>
          </a>
        </div>

        <div class="separator" />

        <!-- ── Section 3 : légal ──────────────────────────────────────────────── -->
        <div class="group group--legal">
          <a href="#" class="item" role="menuitem" @click.prevent="close">
            <span>{{ $t('profile.menu.terms') }}</span>
          </a>
          <a href="#" class="item" role="menuitem" @click.prevent="close">
            <span>{{ $t('profile.menu.privacy') }}</span>
          </a>
          <a href="#" class="item" role="menuitem" @click.prevent="close">
            <span>{{ $t('profile.menu.about') }}</span>
          </a>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import {
  UserIcon,
  LogoutBoxRIcon,
  QuestionIcon,
  MailIcon,
} from '@remixicons/vue/line'
import { onClickOutside } from '@vueuse/core'
import UserAvatar from '~/components/basics/UserAvatar.vue'
import { useCurrentUser } from '~/composables/useCurrentUser'

const props = defineProps<{ open: boolean; anchorEl?: HTMLElement | null }>()
const emit = defineEmits<{ close: [] }>()

const popinRef = ref<HTMLElement | null>(null)

onClickOutside(popinRef, () => emit('close'), {
  ignore: [() => props.anchorEl ?? null],
})

const currentUser = useCurrentUser()

function close() {
  emit('close')
}

function logout() {
  // TODO : appel Keycloak logout
  useFlow().reset()
  navigateTo('/')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.profile-popin {
  position: fixed;
  top: calc($app-header-height + 6px);
  right: $spacing-m;
  z-index: $z-dropdown;
  width: 240px;
  background: #fff;
  border-radius: $radius-lg;
  box-shadow:
    0 4px 6px rgba(0, 0, 0, 0.07),
    0 12px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid $divider-color;
  overflow: hidden;
  padding: $spacing-xs 0;
}

// ── Header ─────────────────────────────────────────────────────────────────────

.header {
  display: flex;
  align-items: center;
  gap: $spacing-s;
  padding: $spacing-s $spacing-m;
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 1px;
  min-width: 0;
}

.header-name {
  font-family: 'Inter', sans-serif;
  font-size: 0.82rem;
  font-weight: 600;
  color: $text-primary;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.header-email {
  font-family: 'Inter', sans-serif;
  font-size: 0.7rem;
  color: $text-secondary;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

// ── Séparateur ─────────────────────────────────────────────────────────────────

.separator {
  height: 1px;
  background: $divider-color;
  margin: $spacing-xs 0;
}

// ── Groupes ────────────────────────────────────────────────────────────────────

.group {
  display: flex;
  flex-direction: column;
}

// ── Items ──────────────────────────────────────────────────────────────────────

.item {
  display: flex;
  align-items: center;
  gap: $spacing-s;
  padding: $spacing-xs $spacing-m;
  font-family: 'Inter', sans-serif;
  font-size: 0.85rem;
  font-weight: 400;
  color: $text-primary;
  text-decoration: none;
  cursor: pointer;
  background: none;
  border: none;
  width: 100%;
  text-align: left;
  transition: background 120ms ease;

  &:hover {
    background: $surface-soft;
  }

  &--danger {
    color: $state-error;

    .item-icon {
      color: $state-error;
    }
  }

  .item-icon {
    width: 1rem;
    height: 1rem;
    color: $text-secondary;
    flex-shrink: 0;
  }
}

.group--legal .item {
  font-size: 0.75rem;
  color: $text-secondary;
  padding-top: 5px;
  padding-bottom: 5px;
}

// ── Transition ─────────────────────────────────────────────────────────────────

.popin-enter-active,
.popin-leave-active {
  transition:
    opacity 150ms ease,
    transform 150ms ease;
}
.popin-enter-from,
.popin-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}
</style>
