<script setup lang="ts">
import { onClickOutside } from '@vueuse/core'
import type { DisplayMode } from '~/types/prestataire'
import type { EngagementKey } from '~/utils/constants'
import EngagementBadge from './EngagementBadge.vue'
import { useEngagementCatalogue } from '~/data/prestataire/useEngagementCatalogue'

const { t, tm, rt } = useI18n()
type RtArg = Parameters<typeof rt>[0]

const props = defineProps<{
  displayMode: DisplayMode
}>()

const modelValue = defineModel<EngagementKey[]>({ required: true })
const isEdit = computed(() => props.displayMode === 'edit')

const isEditing = ref(false)
const containerRef = ref<HTMLElement | null>(null)
onClickOutside(containerRef, () => {
  if (isEditing.value) commitEdit()
})

const { catalogue, load } = useEngagementCatalogue()

// API mirrors the Java Engagement enum — keys are trusted EngagementKey values
const catalogueKeys = computed(() => catalogue.value as EngagementKey[])

const ghostKeys = computed<EngagementKey[]>(() => {
  const raw: unknown = tm('provider.editable.badges.ghost-keys')
  return Array.isArray(raw) ? (raw as RtArg[]).map((item) => rt(item) as EngagementKey) : []
})

function enterEdit() {
  if (isEditing.value) return
  isEditing.value = true
  load()
}

function toggle(key: EngagementKey) {
  const isSelected = modelValue.value.includes(key)
  if (isSelected) {
    modelValue.value = modelValue.value.filter((k) => k !== key)
  } else {
    if (modelValue.value.length >= 3) return
    modelValue.value = [...modelValue.value, key]
  }
}

function commitEdit() {
  isEditing.value = false
}
</script>

<template>
  <!-- DISPLAY MODE -->
  <div v-if="!isEdit" class="badges">
    <EngagementBadge v-for="key in modelValue" :key="key" :engagement-key="key" />
  </div>

  <!-- EDIT MODE -->
  <div
    v-else
    ref="containerRef"
    class="editable-engagements"
    :class="{ 'is-active': isEditing }"
    @click="!isEditing ? enterEdit() : undefined"
  >
    <!-- Ghost: 0 badges, pas en édition -->
    <div v-if="modelValue.length === 0 && !isEditing" class="badges ghost">
      <EngagementBadge v-for="key in ghostKeys" :key="key" :engagement-key="key" />
    </div>

    <!-- Preview: badges sélectionnés, pas encore en édition -->
    <div v-else-if="!isEditing" class="badges">
      <EngagementBadge v-for="key in modelValue" :key="key" :engagement-key="key" />
    </div>

    <!-- Édition active: tous les badges du catalogue -->
    <template v-else>
      <div class="edit-header">
        <p class="edit-prompt">{{ t('provider.editable.badges.prompt') }}</p>
        <span class="edit-counter">{{ modelValue.length }} / 3</span>
      </div>

      <div class="badges-grid">
        <button
          v-for="key in catalogueKeys"
          :key="key"
          type="button"
          class="badge-btn"
          :class="{
            'is-selected': modelValue.includes(key),
            'is-disabled': !modelValue.includes(key) && modelValue.length >= 3,
          }"
          :disabled="!modelValue.includes(key) && modelValue.length >= 3"
          @click.stop="toggle(key)"
        >
          <EngagementBadge :engagement-key="key" />
        </button>
      </div>

      <div class="edit-footer">
        <button type="button" class="commit-btn" aria-label="Valider" @click.stop="commitEdit">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2.5"
            stroke-linecap="round"
            stroke-linejoin="round"
            width="14"
            height="14"
            aria-hidden="true"
          >
            <polyline points="20 6 9 17 4 12" />
          </svg>
        </button>
      </div>
    </template>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$badge-width: 6rem;

.badges {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 1rem;

  > * {
    width: $badge-width;
  }

  &.ghost {
    > * {
      opacity: 0.35;
    }
  }
}

.editable-engagements {
  border-radius: $radius-md;

  &:not(.is-active) {
    cursor: pointer;
  }

  &.is-active {
    border: 1px solid $divider-color;
    padding: $spacing-s $spacing-m;
    background: $surface-soft;
    box-shadow: 0 0 0 3px rgba($brand-accent, 0.15);
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
  }
}

.edit-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-s;
}

.edit-prompt {
  font-family: 'Inter', sans-serif;
  font-size: 0.72rem;
  font-weight: 400;
  color: $text-secondary;
  opacity: 0.75;
  letter-spacing: 0.03em;
  margin: 0;
}

.edit-counter {
  font-size: 0.72rem;
  font-weight: 600;
  color: $text-secondary;
  opacity: 0.6;
  flex-shrink: 0;
}

.badges-grid {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.badge-btn {
  background: none;
  border: none;
  padding: 0;
  width: $badge-width;
  cursor: pointer;
  transition: opacity 150ms ease;

  &.is-selected {
    :deep(.engagement-badge) {
      box-shadow:
        0 0 0 2px $color-primary,
        0 1px 2px rgba(0, 0, 0, 0.04),
        inset 0 1px 0 rgba(255, 255, 255, 0.9);
    }
  }

  &:not(.is-selected):not(.is-disabled) {
    opacity: 0.45;

    &:hover {
      opacity: 0.75;
    }
  }

  &.is-disabled {
    opacity: 0.18;
    cursor: not-allowed;

    :deep(.engagement-badge:hover) {
      transform: none;
      box-shadow:
        0 1px 2px rgba(0, 0, 0, 0.04),
        inset 0 1px 0 rgba(255, 255, 255, 0.9);
    }
  }
}

.edit-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: $spacing-xs;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.commit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 1.75rem;
  height: 1.75rem;
  border-radius: 50%;
  background: $color-primary;
  color: $text-inverted;
  border: none;
  cursor: pointer;
  transition: background 120ms ease;

  &:hover {
    background: #1a1714;
  }
}
</style>
