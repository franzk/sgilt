<template>
  <div class="demande-field">
    <!-- ── État display ─────────────────────────────────────────────────────── -->
    <button
      v-if="!editing"
      class="demande-field__display"
      type="button"
      @click="$emit('update:editing', true)"
    >
      <span class="demande-field__label">{{ label }}</span>
      <span class="demande-field__sep">:</span>
      <span class="demande-field__value" :class="{ 'demande-field__value--empty': !modelValue }">
        {{ modelValue || '—' }}
      </span>
      <span class="demande-field__icon" aria-hidden="true">✏</span>
    </button>

    <!-- ── État édition ─────────────────────────────────────────────────────── -->
    <div v-else class="demande-field__edit">
      <p class="demande-field__edit-label">{{ label }}</p>
      <p class="demande-field__edit-row">
        <input
          ref="inputRef"
          class="demande-field__input"
          :type="type"
          :placeholder="placeholder"
          :autocomplete="autocomplete"
          :enterkeyhint="enterkeyhint"
          :value="modelValue"
          @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
          @keydown.enter.prevent="$emit('validate')"
        />
        <button class="demande-field__ok" type="button" @click="$emit('validate')">OK</button>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  label: string
  modelValue: string
  editing: boolean
  type?: string
  placeholder?: string
  autocomplete?: string
  enterkeyhint?: string
}>()

defineEmits<{
  'update:modelValue': [value: string]
  'update:editing': [value: boolean]
  validate: []
}>()

const inputRef = ref<HTMLInputElement | null>(null)

watchEffect(() => {
  if (props.editing) nextTick(() => inputRef.value?.focus())
})

defineExpose({ focus: () => inputRef.value?.focus() })
</script>

<style lang="scss" scoped>
@use '@/assets/styles/base' as *;

.demande-field {
  border-bottom: 1px solid $divider-color;

  &:last-child {
    border-bottom: none;
  }

  // ── Display ──────────────────────────────────────────────────────────────────
  &__display {
    width: 100%;
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    padding: $spacing-m;
    background: none;
    border: none;
    cursor: pointer;
    text-align: left;
    font-family: inherit;
  }

  &__label {
    font-size: 0.8rem;
    font-weight: 600;
    color: $text-secondary;
    text-transform: uppercase;
    letter-spacing: 0.04em;
    flex-shrink: 0;
  }

  &__sep {
    color: $text-secondary;
    flex-shrink: 0;
  }

  &__value {
    flex: 1;
    font-size: 0.95rem;
    color: $text-primary;
    font-weight: 500;

    &--empty {
      color: $text-secondary;
      opacity: 0.4;
    }
  }

  &__icon {
    font-size: 0.8rem;
    color: $text-secondary;
    opacity: 0.4;
    flex-shrink: 0;
  }

  // ── Edit ─────────────────────────────────────────────────────────────────────
  &__edit {
    padding: $spacing-s $spacing-m $spacing-m;
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
  }

  &__edit-label {
    font-size: 0.8rem;
    font-weight: 600;
    color: $text-secondary;
    text-transform: uppercase;
    letter-spacing: 0.04em;
    margin: 0;
  }

  &__edit-row {
    display: flex;
    align-items: center;
    gap: $spacing-s;
    margin: 0;
  }

  &__input {
    flex: 1;
    padding: $spacing-xs $spacing-s;
    border: 1.5px solid $brand-accent;
    border-radius: $radius-sm;
    font-size: 0.95rem;
    font-family: inherit;
    color: $text-primary;
    background: #fff;
    outline: none;
    box-sizing: border-box;

    &::placeholder {
      color: $text-secondary;
      opacity: 0.4;
    }
  }

  &__ok {
    flex-shrink: 0;
    padding: $spacing-xs $spacing-s;
    background: $brand-accent;
    color: #fff;
    border: none;
    border-radius: $radius-sm;
    font-size: 0.875rem;
    font-weight: 600;
    font-family: inherit;
    cursor: pointer;
    white-space: nowrap;
  }
}
</style>
