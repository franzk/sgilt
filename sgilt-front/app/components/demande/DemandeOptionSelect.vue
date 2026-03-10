<template>
  <div class="demande-option-select">
    <Transition name="fade-down">
      <div
        v-if="modelValue === 'autre'"
        class="autre-input-container"
        @click.self="$emit('update:modelValue', null)"
      >
        <div class="autre-input">
          <input
            :value="autreValue"
            class="autre-input__field"
            type="text"
            :placeholder="autrePlaceholder"
            @input="$emit('update:autreValue', $event.target.value)"
          />
          <SgiltButton @click="$emit('change')" class="autre-value-button"> → </SgiltButton>
        </div>
      </div>
    </Transition>
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
    </div>
  </div>
</template>

<script setup lang="ts">
import type { DemandeOption } from '~/types/demande'
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'

defineProps<{
  options: DemandeOption[]
  modelValue: string | null
  autreValue: string
  autrePlaceholder?: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', v: string | null): void
  (e: 'update:autreValue', v: string): void
  (e: 'change'): void
}>()

function select(value: string) {
  emit('update:modelValue', value)
  if (value !== 'autre') {
    emit('change')
  } else {
    // focus input when "autre" is selected
    nextTick(() => {
      const input = document.querySelector('.autre-input__field') as HTMLInputElement | null
      input?.focus()
      input?.scrollIntoView({ behavior: 'smooth', block: 'center' })
    })
  }
}
</script>

<style scoped lang="scss">
.demande-option-select {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

.option-select {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-xs;
}

.option {
  display: flex;
  flex-direction: column;
  aspect-ratio: 1.4;
  align-items: center;
  justify-content: center;
  gap: $spacing-m;
  width: 100%;

  border: 1.5px solid $divider-color;
  border-radius: $radius-md;
  background: #fff;
  cursor: pointer;
  text-align: center;
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

.autre-input-container {
  display: grid;
  padding: $spacing-xs;
  // align-items: center;
  // justify-content: center;
  // overflow: hidden;

  .autre-input {
    display: flex;
    align-items: center;
    height: 3rem;
    width: 100%;
    gap: 0;
    padding: 0 $spacing-xs;

    .autre-value-button {
      height: 100%;
      margin: 0;
      aspect-ratio: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: $radius-md;
      // padding: 0 $spacing-s;
    }

    &__field {
      flex: 1;
      width: 100%;
      height: 100%;
      padding: 0 $spacing-m;
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
}

.fade-down-enter-active,
.fade-down-leave-active {
  display: grid;
  grid-template-rows: 1fr;
  transition:
    grid-template-rows 220ms ease,
    opacity 220ms ease;
}

.fade-down-enter-from,
.fade-down-leave-to {
  grid-template-rows: 0fr;
  opacity: 0;
}
</style>
