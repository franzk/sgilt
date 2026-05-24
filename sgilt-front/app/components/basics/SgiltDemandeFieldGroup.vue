<template>
  <div class="field-group">
    <SgiltDemandeField
      v-for="(subField, index) in modelValue.subFields"
      :ref="(el) => setFieldRef(el, index)"
      :key="subField.key"
      :model-value="subField.value ?? ''"
      :editing="editingIndex === index"
      :label="subField.label"
      :placeholder="subField.placeholder"
      :type="subField.type"
      :autocomplete="subField.autocomplete"
      :enterkeyhint="subField.enterkeyhint"
      :error="fieldErrors[index]"
      @update:model-value="onUpdateValue(index, $event)"
      @update:editing="
        (v) => {
          if (v) editingIndex = index
        }
      "
      @validate="onValidate(index)"
    />
  </div>
</template>

<script setup lang="ts">
import SgiltDemandeField from '~/components/basics/SgiltDemandeField.vue'

interface SubField {
  key: string
  label: string
  placeholder?: string
  value: string | null
  type?: string
  autocomplete?: string
  enterkeyhint?: string
  validate?: (value: string) => string | null
  [key: string]: unknown
}

interface GroupItem {
  subFields: SubField[]
  [key: string]: unknown
}

const props = defineProps<{
  modelValue: GroupItem
  title?: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value: GroupItem]
  complete: []
}>()

const editingIndex = ref(0)
const fieldErrors = ref<Record<number, string | null>>({})
const fieldRefs = ref<Array<{ focus: () => void } | null>>([])

function setFieldRef(el: unknown, index: number) {
  fieldRefs.value[index] = el as { focus: () => void } | null
}

function onUpdateValue(index: number, value: string) {
  if (fieldErrors.value[index]) fieldErrors.value[index] = null
  emit('update:modelValue', {
    ...props.modelValue,
    subFields: props.modelValue.subFields.map((sf, i) => (i === index ? { ...sf, value } : sf)),
  })
}

function onValidate(index: number) {
  const sf = props.modelValue.subFields[index]
  const error = sf?.validate?.(sf.value ?? '') ?? null
  if (error) {
    fieldErrors.value[index] = error
    fieldRefs.value[index]?.focus()
    return
  }
  fieldErrors.value[index] = null
  if (index < props.modelValue.subFields.length - 1) {
    editingIndex.value = index + 1
  } else {
    emit('complete')
  }
}
</script>

<style lang="scss" scoped>
.field-group {
  display: flex;
  flex-direction: column;
}
</style>
