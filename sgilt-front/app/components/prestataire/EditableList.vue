<template>
  <!-- DISPLAY MODE: pixel-identical to current rendering -->
  <ul v-if="!isEdit" :class="['editable-list', `marker-${marker}`]">
    <li v-for="(item, i) in modelValue" :key="i" class="list-item">{{ item }}</li>
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
      <ul :class="['editable-list', `marker-${marker}`, 'ghost-list']">
        <li v-for="(gitem, i) in ghostItems" :key="i" class="list-item">{{ gitem }}</li>
      </ul>
    </template>

    <!-- EDITING OR PREVIEW WITH ITEMS -->
    <template v-else>
      <p v-if="isListEditing" class="list-prompt">{{ listPrompt }}</p>

      <ul :class="['editable-list', `marker-${marker}`]">
        <li
          v-for="(id, index) in itemIds"
          :key="id"
          class="list-item"
          :class="{ 'is-editing': isListEditing }"
          @click="onItemClick($event, index)"
        >
          <EditableText
            :ref="setItemRef(index)"
            :field="`${field}.item`"
            :editable="isListEditing"
            :model-value="modelValue[index] ?? ''"
            @update:model-value="updateItem(index, $event)"
          />
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

<script setup lang="ts">
import { onClickOutside } from '@vueuse/core'
import type { ComponentPublicInstance } from 'vue'
import EditableText from '~/components/prestataire/EditableText.vue'
import type { DisplayMode } from '~/types/prestataire'

const props = defineProps<{
  marker: 'check' | 'dash'
  field: string
  displayMode: DisplayMode
}>()

const modelValue = defineModel<string[]>({ required: true })

const isEdit = computed(() => props.displayMode === 'edit')

const { t, tm, rt } = useI18n()

// ghost items
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
type EditableTextRef = { startEdit: () => void }
const itemRefs = ref<(EditableTextRef | null)[]>([])
const focusedInputIndex = ref<number | null>(null)

function setItemRef(index: number) {
  return (el: ComponentPublicInstance | Element | null) => {
    itemRefs.value[index] = el as EditableTextRef | null
  }
}

watch(focusedInputIndex, (idx) => {
  if (idx === null) return
  nextTick(() => {
    itemRefs.value[idx]?.startEdit()
    focusedInputIndex.value = null
  })
})

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
  itemIds.value = [...itemIds.value, crypto.randomUUID()]
  modelValue.value = [...modelValue.value, '']
  focusedInputIndex.value = modelValue.value.length - 1
}

function removeItem(index: number) {
  itemIds.value = itemIds.value.filter((_, i) => i !== index)
  modelValue.value = modelValue.value.filter((_, i) => i !== index)
}

function updateItem(index: number, value: string | null) {
  const next = [...modelValue.value]
  next[index] = value ?? ''
  modelValue.value = next
}

function commitEdit() {
  if (!isListEditing.value) return
  const keptIndices = modelValue.value.reduce<number[]>((acc, s, i) => {
    if (s.trim()) acc.push(i)
    return acc
  }, [])
  itemIds.value = keptIndices.map((i) => itemIds.value[i]!)
  modelValue.value = keptIndices.map((i) => modelValue.value[i]!)
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

  &.marker-check {
    gap: 1rem;

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

      &.is-editing {
        :deep(.editable-text) {
          flex: 1;
        }
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
  transition:
    opacity 120ms ease,
    color 120ms ease;

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
  transition:
    border-color 120ms ease,
    color 120ms ease;

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
