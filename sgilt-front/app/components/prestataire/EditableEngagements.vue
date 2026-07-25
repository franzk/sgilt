<script setup lang="ts">
import { onClickOutside } from '@vueuse/core'
import type { DisplayMode } from '~/types/prestataire'
import type { EngagementKey } from '~/utils/constants'
import EngagementBadge from './EngagementBadge.vue'
import { useEngagementCatalogue } from '~/data/prestataire/useEngagementCatalogue'

const { t } = useI18n()

const props = defineProps<{
  displayMode: DisplayMode
}>()

const emit = defineEmits<{ commit: [value: EngagementKey[]] }>()

const modelValue = defineModel<EngagementKey[]>({ required: true })

/** Le champ est modifiable sur cette page (fiche en mode édition vs lecture seule). */
const isEditable = computed(() => props.displayMode === 'edit')

/** Aucun engagement n'est encore sélectionné. */
const isEmpty = computed(() => modelValue.value.length === 0)

/** L'éditeur (grille de sélection du catalogue) est ouvert. */
const isEditorOpen = ref(false)

const containerRef = ref<HTMLElement | null>(null)
onClickOutside(containerRef, () => {
  if (isEditorOpen.value) closeEditor()
})

/** Dernière valeur connue comme sauvegardée, pour ne commit que si la sélection a changé. */
let lastCommitted = JSON.stringify(modelValue.value)

/** Filet de sécurité pour le changement d'onglet (voir EditableList.vue::onFocusOut). */
function onFocusOut(e: FocusEvent) {
  if (!isEditorOpen.value) return
  const next = e.relatedTarget as Node | null
  if (!next || !containerRef.value?.contains(next)) closeEditor()
}

const { catalogue, load } = useEngagementCatalogue()

// API mirrors the Java Engagement enum — keys are trusted EngagementKey values
const catalogueKeys = computed(() => catalogue.value as EngagementKey[])

function openEditor() {
  isEditorOpen.value = true
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

function closeEditor() {
  isEditorOpen.value = false
  const current = JSON.stringify(modelValue.value)
  if (current !== lastCommitted) {
    lastCommitted = current
    emit('commit', modelValue.value)
  }
}
</script>

<template>
  <!-- LECTURE SEULE -->
  <div v-if="!isEditable" class="badges">
    <EngagementBadge v-for="key in modelValue" :key="key" :engagement-key="key" />
  </div>

  <!-- MODIFIABLE -->
  <div
    v-else
    ref="containerRef"
    class="editable-engagements"
    :class="{ 'is-active': isEditorOpen }"
    @focusout="onFocusOut"
  >
    <!-- Fermé, rien de sélectionné: bouton d'appel -->
    <div v-if="!isEditorOpen && isEmpty" class="badges-empty">
      <button type="button" class="choose-btn" @mousedown.prevent @click="openEditor">
        {{ t('provider.editable.badges.choose') }}
      </button>
    </div>

    <!-- Fermé, engagements déjà sélectionnés: aperçu compact, cliquable pour rouvrir -->
    <div v-else-if="!isEditorOpen" class="badges" @click="openEditor">
      <EngagementBadge v-for="key in modelValue" :key="key" :engagement-key="key" />
    </div>

    <!-- Ouvert: grille complète du catalogue, mécanique de sélection -->
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
        <button type="button" class="commit-btn" aria-label="Valider" @click.stop="closeEditor">
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
}

.badges-empty {
  display: flex;
  justify-content: center;
}

.choose-btn {
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  font-weight: 600;
  color: $text-secondary;
  background: none;
  border: 1px dashed $divider-color;
  border-radius: $radius-md;
  padding: $spacing-s $spacing-m;
  cursor: pointer;
  transition:
    color 150ms ease,
    border-color 150ms ease;

  &:hover {
    color: $color-primary;
    border-color: $color-primary;
  }
}

.editable-engagements {
  border-radius: $radius-md;

  &:not(.is-active) .badges {
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
