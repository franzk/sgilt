<template>
  <Teleport to="body">
    <Transition name="popin">
      <div v-if="open" ref="popinRef" class="profile-popin" role="menu" aria-label="Menu compte">
        <!-- Mon profil + Déconnexion -->
        <div class="group">
          <NuxtLink to="/account/profile" class="item" role="menuitem" @click="close">
            <UserIcon class="item-icon" />
            <span>Mon profil</span>
          </NuxtLink>
          <button class="item" role="menuitem" type="button" @click="logout">
            <LogoutBoxRIcon class="item-icon" />
            <span>Déconnexion</span>
          </button>
        </div>

        <div class="separator" />

        <!-- Liens informatifs -->
        <div class="group">
          <a href="#" class="item" role="menuitem" @click.prevent="close">
            <QuestionIcon class="item-icon" />
            <span>Aide</span>
          </a>
          <a href="#" class="item" role="menuitem" @click.prevent="close">
            <FileTextIcon class="item-icon" />
            <span>Conditions générales</span>
          </a>
          <a href="#" class="item" role="menuitem" @click.prevent="close">
            <ShieldIcon class="item-icon" />
            <span>Politique de confidentialité</span>
          </a>
          <a href="#" class="item" role="menuitem" @click.prevent="close">
            <InformationIcon class="item-icon" />
            <span>À propos de SGILT</span>
          </a>
        </div>

        <div class="separator" />

        <!-- Contact -->
        <div class="group">
          <a href="#" class="item" role="menuitem" @click.prevent="close">
            <MailIcon class="item-icon" />
            <span>Nous contacter</span>
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
  FileTextIcon,
  ShieldIcon,
  InformationIcon,
  MailIcon,
} from '@remixicons/vue/line'
import { onClickOutside } from '@vueuse/core'

const props = defineProps<{ open: boolean; anchorEl?: HTMLElement | null }>()
const emit = defineEmits<{ close: [] }>()

const popinRef = ref<HTMLElement | null>(null)

onClickOutside(popinRef, () => emit('close'), {
  ignore: [() => props.anchorEl ?? null],
})

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
  width: 220px;
  background: #fff;
  border-radius: $radius-lg;
  box-shadow:
    0 4px 6px rgba(0, 0, 0, 0.07),
    0 12px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid $divider-color;
  overflow: hidden;
  padding: $spacing-xs 0;
}

.separator {
  height: 1px;
  background: $divider-color;
  margin: $spacing-xs 0;
}

.group {
  display: flex;
  flex-direction: column;
}

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

  .item-icon {
    width: 1rem;
    height: 1rem;
    color: $text-secondary;
    flex-shrink: 0;
  }
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
