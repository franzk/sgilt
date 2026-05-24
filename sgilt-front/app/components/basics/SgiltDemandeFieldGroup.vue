<template>
  <div class="field-group">
    <SgiltDemandeField
      v-for="(subField, index) in modelValue.subFields"
      :key="subField.key"
      :model-value="subField.value ?? ''"
      :editing="editingIndex === index"
      :label="subField.label"
      :placeholder="subField.placeholder"
      :type="subField.type"
      :autocomplete="subField.autocomplete"
      :enterkeyhint="subField.enterkeyhint"
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

function onUpdateValue(index: number, value: string) {
  emit('update:modelValue', {
    ...props.modelValue,
    subFields: props.modelValue.subFields.map((sf, i) => (i === index ? { ...sf, value } : sf)),
  })
}

function onValidate(index: number) {
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
