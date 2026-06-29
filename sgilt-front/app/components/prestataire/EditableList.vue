<template>
  <!-- DISPLAY MODE -->
  <ul v-if="!isEdit" :class="['editable-list', marker && `marker-${marker}`]">
    <li v-for="(item, i) in modelValue" :key="i" class="list-item">
      <slot name="item" :item="item" :index="i" :is-editing="false" :update="getUpdateFn(i)" :register-ref="setItemRef(i)" />
    </li>
  </ul>

  <!-- EDIT MODE -->
  <div
    v-else
    ref="containerRef"
    class="editable-list-edit"
    :class="{ 'is-active': isListEditing }"
    @click="!isListEditing ? enterListEdit() : undefined"
  >
    <!-- GHOST STATE: empty list, not editing -->
    <template v-if="modelValue.length === 0 && !isListEditing">
      <ul :class="['editable-list', marker && `marker-${marker}`, 'ghost-list']">
        <li v-for="(gitem, i) in ghostItems" :key="i" class="list-item">
          <slot name="ghost-item" :ghost-text="gitem" :ghost-index="i">{{ gitem }}</slot>
        </li>
      </ul>
    </template>

    <!-- EDITING OR PREVIEW WITH ITEMS -->
    <template v-else>
      <p v-if="isListEditing" class="list-prompt">{{ listPrompt }}</p>

      <ul :class="['editable-list', marker && `marker-${marker}`]">
        <li
          v-for="(id, index) in itemIds"
          :key="id"
          class="list-item"
          :class="{ 'is-editing': isListEditing }"
          @click="onItemClick($event, index)"
        >
          <div class="item-content">
            <slot
              name="item"
              :item="modelValue[index]"
              :index="index"
              :is-editing="isListEditing"
              :update="getUpdateFn(index)"
              :register-ref="setItemRef(index)"
            />
          </div>
          <button
            v-if="isListEditing"
            type="button"
            class="remove-btn"
            aria-label="Supprimer"
            @click.stop="removeItem(index)"
          >
            ×
          </button>
        </li>
      </ul>

      <div v-if="isListEditing" class="list-footer">
        <button type="button" class="add-btn" @click.stop="addItem">
          <span aria-hidden="true">+</span> Ajouter
        </button>
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

<script setup lang="ts" generic="T">
import { onClickOutside } from '@vueuse/core'
import type { ComponentPublicInstance } from 'vue'
import type { DisplayMode } from '~/types/prestataire'

const props = defineProps<{
  marker?: 'check' | 'dash'
  field: string
  displayMode: DisplayMode
  newItem: () => T
  isEmpty: (item: T) => boolean
}>()

defineSlots<{
  item(props: {
    item: T
    index: number
    isEditing: boolean
    update: (val: T) => void
    registerRef: (el: ComponentPublicInstance | Element | null) => void
  }): unknown
  'ghost-item'(props: { ghostText: string; ghostIndex: number }): unknown
}>()

const modelValue = defineModel<T[]>({ required: true })
const isEdit = computed(() => props.displayMode === 'edit')

const { t, tm, rt } = useI18n()
type RtArg = Parameters<typeof rt>[0]
const ghostItems = computed<string[]>(() => {
  const raw: unknown = tm(`provider.editable.${props.field}.ghost-items`)
  return Array.isArray(raw) ? (raw as RtArg[]).map((item) => rt(item)) : []
})
const listPrompt = computed(() => t(`provider.editable.${props.field}.prompt`))

const isListEditing = ref(false)
const containerRef = ref<HTMLElement | null>(null)
onClickOutside(containerRef, () => {
  if (isListEditing.value) commitEdit()
})

// ── Item IDs (stable keys, independent of content) ───────────────────────────
const itemIds = ref<string[]>([])
watch(
  () => modelValue.value.length,
  (newLen) => {
    while (itemIds.value.length < newLen) itemIds.value.push(crypto.randomUUID())
    if (itemIds.value.length > newLen) itemIds.value = itemIds.value.slice(0, newLen)
  },
  { immediate: true },
)

// ── Item refs for programmatic focus ─────────────────────────────────────────
type ItemRef = { startEdit: () => void }
const itemRefs = ref<(ItemRef | null)[]>([])
const focusedInputIndex = ref<number | null>(null)

function setItemRef(index: number) {
  return (el: ComponentPublicInstance | Element | null) => {
    itemRefs.value[index] = el as ItemRef | null
  }
}

watch(focusedInputIndex, (idx) => {
  if (idx === null) return
  itemRefs.value[idx]?.startEdit()
  focusedInputIndex.value = null
}, { flush: 'post' })

// ── Actions ───────────────────────────────────────────────────────────────────
function enterListEdit() {
  if (isListEditing.value) return
  isListEditing.value = true
  if (modelValue.value.length === 0) addItem()
}

function onItemClick(event: MouseEvent, index: number) {
  if (isListEditing.value) return
  event.stopPropagation()
  isListEditing.value = true
  focusedInputIndex.value = index
}

function addItem() {
  const newIndex = modelValue.value.length
  itemIds.value = [...itemIds.value, crypto.randomUUID()]
  modelValue.value = [...modelValue.value, props.newItem()]
  focusedInputIndex.value = newIndex
}

function removeItem(index: number) {
  itemIds.value = itemIds.value.filter((_, i) => i !== index)
  modelValue.value = modelValue.value.filter((_, i) => i !== index)
}

function updateItem(index: number, val: T) {
  const next = [...modelValue.value]
  next[index] = val
  modelValue.value = next
}

function getUpdateFn(index: number): (val: T) => void {
  return (val) => updateItem(index, val)
}

/**
 * Ferme l'édition et purge les items vides (via isEmpty).
 * Appelé sur clic ✓, clic en dehors, ou ouverture d'une autre liste.
 */
function commitEdit() {
  if (!isListEditing.value) return
  const pairs = modelValue.value.map((item, i) => ({ item, id: itemIds.value[i]! }))
  const kept = pairs.filter(({ item }) => !props.isEmpty(item))
  itemIds.value = kept.map(({ id }) => id)
  modelValue.value = kept.map(({ item }) => item)
  isListEditing.value = false
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$color-success: #2e7d32;

// ── Shared list base ──────────────────────────────────────────────────────────

.editable-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;

  &.marker-check {
    .list-item {
      display: flex;
      align-items: flex-start;
      gap: 0.5rem;
      font-size: 0.95rem;
      line-height: 1.35;
      color: $text-secondary;

      &::before {
        content: '';
        flex-shrink: 0;
        width: 16px;
        height: 16px;
        margin-top: 0.2rem;
        background-color: $color-success;
        -webkit-mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='black' stroke-width='3' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='20 6 9 17 4 12'%3E%3C/polyline%3E%3C/svg%3E");
        mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='black' stroke-width='3' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='20 6 9 17 4 12'%3E%3C/polyline%3E%3C/svg%3E");
        mask-repeat: no-repeat;
        mask-size: contain;
      }

      :deep(.editable-text) {
        flex: 1;
      }
    }
  }

  &.marker-dash {
    gap: 0.4rem;

    .list-item {
      font-size: 0.9rem;
      line-height: 1.5;
      color: $text-secondary;
      padding-left: 1rem;
      position: relative;

      &::before {
        content: '–';
        position: absolute;
        left: 0;
        opacity: 0.4;
      }

      &.is-editing {
        display: flex;
        align-items: flex-start;
        gap: 0.5rem;

        :deep(.editable-text) {
          flex: 1;
        }
      }
    }
  }
}

// ── Edit-mode item layout (applies to all markers, including none) ────────────

.list-item.is-editing {
  display: flex;
  align-items: flex-start;
  gap: 0.5rem;

  .item-content {
    flex: 1;
    min-width: 0;
  }
}

// ── Ghost list ────────────────────────────────────────────────────────────────

.ghost-list {
  .list-item {
    opacity: 0.35;
  }
}

// ── Edit wrapper ──────────────────────────────────────────────────────────────

.editable-list-edit {
  border-radius: $radius-md;

  &:not(.is-active) {
    cursor: text;
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

// ── Prompt ────────────────────────────────────────────────────────────────────

.list-prompt {
  font-family: 'Inter', sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 0.72rem;
  color: $text-secondary;
  opacity: 0.75;
  letter-spacing: 0.03em;
  margin: 0;
}

// ── Remove button ─────────────────────────────────────────────────────────────

.remove-btn {
  flex-shrink: 0;
  background: none;
  border: none;
  cursor: pointer;
  color: $text-secondary;
  opacity: 0.35;
  font-size: 1rem;
  line-height: 1;
  padding: 0.15rem 0.25rem;
  align-self: flex-start;
  transition: opacity 120ms ease, color 120ms ease;

  &:hover {
    opacity: 1;
    color: $state-error;
  }
}

// ── Footer ────────────────────────────────────────────────────────────────────

.list-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-s;
  padding-top: $spacing-xs;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.add-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.3rem;
  background: none;
  border: 1px dashed rgba(0, 0, 0, 0.2);
  border-radius: $radius-sm;
  padding: 0.2rem 0.6rem;
  font-size: 0.82rem;
  color: $text-secondary;
  cursor: pointer;
  transition: border-color 120ms ease, color 120ms ease;

  &:hover {
    border-color: $text-secondary;
    color: $text-primary;
  }
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
