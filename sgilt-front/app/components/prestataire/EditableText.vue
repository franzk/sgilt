<template>
  <component
    :is="as ?? 'div'"
    class="editable-text"
    :class="{ 'is-edit': isEdit }"
    @click="startEdit"
  >
    <p v-if="focused" class="prompt">{{ promptText }}</p>
    <textarea
      v-if="focused"
      ref="textareaRef"
      :value="localValue"
      class="input"
      rows="1"
      @input="onInput"
      @keydown.enter.prevent="handleEnter"
      @blur="onBlur"
    />
    <span v-else :class="{ ghost: !modelValue }">{{ modelValue || ghostText }}</span>
    <button v-if="isEdit && !focused" type="button" class="pencil" @click.stop="startEdit">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
        width="13"
        height="13"
        aria-hidden="true"
      >
        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
      </svg>
    </button>
  </component>
</template>

<script lang="ts">
export const newItem = (): string => ''
export const isEmpty = (s: string | null): boolean => !s?.trim()
</script>

<script setup lang="ts">
const emit = defineEmits<{ enter: [] }>()

const props = defineProps<{
  /** Balise sémantique de rendu (ex. 'blockquote', 'p'). Défaut : 'div' */
  as?: string
  /** Identifiant du champ, résout les clés i18n `provider.editable.{field}.ghost/prompt` */
  field: string
  /** Active le mode édition (crayon, clic, prompt au focus). Défaut : false */
  editable?: boolean
}>()

/** Valeur courante — `null` affiche le texte fantôme */
const modelValue = defineModel<string | null>()

const isEdit = computed(() => props.editable)

const focused = ref(false)
const localValue = ref('')
const textareaRef = ref<HTMLTextAreaElement | null>(null)

const { t } = useI18n()
const ghostText = computed(() => t(`provider.editable.${props.field}.ghost`))
const promptText = computed(() => t(`provider.editable.${props.field}.prompt`))

function autoResize(el: HTMLTextAreaElement) {
  el.style.height = 'auto'
  el.style.height = el.scrollHeight + 'px'
}

function startEdit() {
  if (!isEdit.value || focused.value) return
  localValue.value = modelValue.value ?? ''
  focused.value = true
  nextTick(() => {
    const el = textareaRef.value
    if (!el) return
    autoResize(el)
    el.focus()
    el.setSelectionRange(el.value.length, el.value.length)
  })
}

function onInput(e: Event) {
  const el = e.target as HTMLTextAreaElement
  localValue.value = el.value
  autoResize(el)
}

function handleEnter() {
  textareaRef.value?.blur()
  emit('enter')
}

function onBlur() {
  focused.value = false
  if (localValue.value !== (modelValue.value ?? '')) {
    modelValue.value = localValue.value
  }
}

defineExpose({ startEdit })
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.editable-text {
  &.is-edit {
    position: relative;
    cursor: text;
    min-height: 1.5em;
    min-width: 80px;

    &:hover .pencil {
      opacity: 1;
    }
  }
}

.input {
  display: block;
  width: 100%;
  border: none;
  background: transparent;
  padding: 0;
  margin: 0;
  outline: none;
  resize: none;
  overflow: hidden;
  font: inherit;
  color: inherit;
  line-height: inherit;
}

.pencil {
  position: absolute;
  top: 0;
  right: -1.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 1.25rem;
  height: 1.25rem;
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  color: $text-secondary;
  opacity: 0;
  transition: opacity 150ms ease;
}

.ghost {
  opacity: 0.4;
}

.prompt {
  font-family: 'Inter', sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 0.72rem;
  color: $text-secondary;
  opacity: 0.75;
  letter-spacing: 0.03em;
  margin: 0 0 0.4rem;
}
</style>
