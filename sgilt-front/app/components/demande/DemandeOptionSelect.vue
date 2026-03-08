<template>
  <div class="option-select">
    <button
      v-for="option in options"
      :key="option.value"
      class="option"
      :class="{ 'option--selected': modelValue === option.value }"
      type="button"
      @click="select(option.value)"
    >
      <span class="option__emoji">{{ option.emoji }}</span>
      <span class="option__label">{{ option.label }}</span>
    </button>

    <Transition name="fade-down">
      <div v-if="modelValue === 'autre'" class="autre-input">
        <input
          :value="autreValue"
          class="autre-input__field"
          type="text"
          :placeholder="autrePlaceholder"
          @input="$emit('update:autreValue', ($event.target as HTMLInputElement).value)"
        />
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import type { DemandeOption } from '~/types/demande'

defineProps<{
  options: DemandeOption[]
  modelValue: string | null
  autreValue: string
  autrePlaceholder?: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', v: string): void
  (e: 'update:autreValue', v: string): void
}>()

function select(value: string) {
  emit('update:modelValue', value)
}
</script>

<style scoped lang="scss">
.option-select {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
}

.option {
  display: flex;
  align-items: center;
  gap: $spacing-m;
  width: 100%;
  padding: $spacing-m $spacing-m;
  border: 1.5px solid $divider-color;
  border-radius: $radius-md;
  background: #fff;
  cursor: pointer;
  text-align: left;
  transition:
    border-color 180ms ease,
    background 180ms ease,
    transform 100ms ease;

  &:hover {
    border-color: rgba($brand-accent, 0.5);
    background: rgba($brand-accent, 0.04);
  }

  &:active {
    transform: scale(0.99);
  }

  &--selected {
    border-color: $brand-accent;
    background: rgba($brand-accent, 0.08);

    .option__label {
      font-weight: 600;
      color: $text-primary;
    }
  }

  &__emoji {
    font-size: 1.3rem;
    flex-shrink: 0;
    width: 1.8rem;
    text-align: center;
  }

  &__label {
    font-size: 0.95rem;
    color: $text-primary;
    line-height: 1.3;
  }
}

.autre-input {
  padding: 0 $spacing-xs;

  &__field {
    width: 100%;
    padding: $spacing-s $spacing-m;
    border: 1.5px solid $divider-color;
    border-radius: $radius-md;
    font-size: 0.95rem;
    font-family: inherit;
    color: $text-primary;
    background: #fff;
    outline: none;
    transition: border-color 180ms ease;
    box-sizing: border-box;

    &:focus {
      border-color: $brand-accent;
    }

    &::placeholder {
      color: $text-secondary;
      opacity: 0.6;
    }
  }
}

.fade-down-enter-active,
.fade-down-leave-active {
  transition:
    opacity 180ms ease,
    transform 180ms ease;
}

.fade-down-enter-from,
.fade-down-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}
</style>
