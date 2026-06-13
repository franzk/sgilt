<template>
  <Teleport to="body">
    <Transition name="popin">
      <div v-if="open" ref="popinRef" class="account-popin" role="menu" aria-label="Menu">
        <!-- ── Section 1 : compte ─────────────────────────────────────────────── -->
        <div class="group">
          <NuxtLink v-if="isAuthenticated" to="#" class="item" role="menuitem" @click="close">
            <UserIcon class="item-icon" />
            <span>{{ $t('profile.menu.my-info') }}</span>
          </NuxtLink>
          <button v-if="isAuthenticated" class="item item--danger" role="menuitem" type="button" @click="logout">
            <LogoutBoxRIcon class="item-icon" />
            <span>{{ $t('profile.menu.logout') }}</span>
          </button>
          <button v-else class="item" role="menuitem" type="button" @click="handleLogin">
            <LoginBoxIcon class="item-icon" />
            <span>{{ $t('profile.menu.login') }}</span>
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
          <NuxtLink to="/m/cgu" class="item" role="menuitem" @click="close">
            <span>{{ $t('profile.menu.terms') }}</span>
          </NuxtLink>
          <NuxtLink to="/m/confidentialite" class="item" role="menuitem" @click="close">
            <span>{{ $t('profile.menu.privacy') }}</span>
          </NuxtLink>
          <NuxtLink to="/m/mentions-legales" class="item" role="menuitem" @click="close">
            <span>{{ $t('profile.menu.mentions-legales') }}</span>
          </NuxtLink>
          <a href="#" class="item" role="menuitem" @click.prevent="close">
            <span>{{ $t('profile.menu.about') }}</span>
          </a>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { UserIcon, LogoutBoxRIcon, LoginBoxIcon, QuestionIcon, MailIcon } from '@remixicons/vue/line'
import { onClickOutside } from '@vueuse/core'
import { useKeycloak } from '~/composables/useKeycloak'

const props = defineProps<{ open: boolean; anchorEl?: HTMLElement | null }>()
const emit = defineEmits<{ close: [] }>()

const popinRef = ref<HTMLElement | null>(null)

onClickOutside(popinRef, () => emit('close'), {
  ignore: [() => props.anchorEl ?? null],
})

const { isAuthenticated, login, logout: kcLogout } = useKeycloak()

function close() {
  emit('close')
}

function handleLogin() {
  close()
  login({ redirectUri: window.location.origin + '/auth/redirect' })
}

async function logout() {
  close()
  useFlow().reset()
  useDemande().reset()
  await kcLogout()
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.account-popin {
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
