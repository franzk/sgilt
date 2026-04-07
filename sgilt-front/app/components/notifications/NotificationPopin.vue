<template>
  <Teleport to="body">
    <Transition name="popin">
      <div v-if="open" ref="popinRef" class="notif-popin" role="dialog" aria-label="Notifications">
        <div class="notif-popin__header">
          <span class="notif-popin__title">Notifications</span>
          <button
            v-if="store.unreadCount > 0"
            class="notif-popin__mark-all"
            type="button"
            @click="store.markAllAsRead()"
          >
            Tout marquer comme lu
          </button>
        </div>

        <div class="notif-popin__list">
          <p v-if="store.notifications.length === 0" class="notif-popin__empty">
            Aucune notification.
          </p>
          <NotificationItem
            v-for="n in preview"
            :key="n.id"
            :notification="n"
            @click="onItemClick"
          />
        </div>

        <NuxtLink to="/account/notifications" class="notif-popin__footer" @click="emit('close')">
          Voir toutes les notifications →
        </NuxtLink>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import NotificationItem from './NotificationItem.vue'
import { useNotificationStore } from '~/stores/notification'
import { onClickOutside } from '@vueuse/core'

const PREVIEW_COUNT = 8

const props = defineProps<{ open: boolean; anchorEl?: HTMLElement | null }>()
const emit = defineEmits<{ close: [] }>()

const store = useNotificationStore()
const popinRef = ref<HTMLElement | null>(null)

const preview = computed(() => store.notifications.slice(0, PREVIEW_COUNT))

// Fermeture au clic extérieur
onClickOutside(popinRef, () => emit('close'), {
  ignore: [() => props.anchorEl ?? null],
})

// Marque les visibles comme lus à l'ouverture
watch(
  () => props.open,
  (val) => {
    if (val) preview.value.forEach((n) => store.markAsRead(n.id))
  },
)

function onItemClick(id: string) {
  store.markAsRead(id)
  const n = store.notifications.find((n) => n.id === id)
  if (n?.href) navigateTo(n.href)
  emit('close')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.notif-popin {
  position: fixed;
  top: calc($app-header-height + 6px);
  right: $spacing-m;
  z-index: $z-dropdown;
  width: min(380px, calc(100vw - 2 * $spacing-m));
  background: #fff;
  border-radius: $radius-lg;
  box-shadow:
    0 4px 6px rgba(0, 0, 0, 0.07),
    0 12px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid $divider-color;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  // ── En-tête ──────────────────────────────────────────────────────────────────
  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $spacing-s $spacing-m;
    border-bottom: 1px solid $divider-color;
  }

  &__title {
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    font-weight: 700;
    letter-spacing: 0.06em;
    text-transform: uppercase;
    color: $text-secondary;
  }

  &__mark-all {
    background: none;
    border: none;
    font-family: inherit;
    font-size: 0.72rem;
    color: $brand-primary;
    cursor: pointer;
    padding: 0;
    opacity: 0.7;
    transition: opacity 150ms ease;

    &:hover {
      opacity: 1;
    }
  }

  // ── Liste ────────────────────────────────────────────────────────────────────
  &__list {
    overflow-y: auto;
    max-height: 480px;
  }

  &__empty {
    font-size: 0.85rem;
    color: $text-secondary;
    font-style: italic;
    text-align: center;
    padding: $spacing-l;
    margin: 0;
  }

  // ── Pied ─────────────────────────────────────────────────────────────────────
  &__footer {
    display: block;
    text-align: center;
    padding: $spacing-s $spacing-m;
    border-top: 1px solid $divider-color;
    font-family: 'Inter', sans-serif;
    font-size: 0.78rem;
    font-weight: 500;
    color: $brand-primary;
    text-decoration: none;
    transition: background 120ms ease;

    &:hover {
      background: $surface-soft;
    }
  }
}

// ── Transition ────────────────────────────────────────────────────────────────
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
