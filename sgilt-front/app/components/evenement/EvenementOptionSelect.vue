<template>
  <div class="evenement-option-select">
    <Transition name="fade-down">
      <div v-if="autreOpen" class="autre-input-container" @click.self="closeAutre">
        <div class="autre-input">
          <input
            v-model="autreDraft"
            class="field"
            type="text"
            :placeholder="autrePlaceholder"
            @input="$emit('update:modelValue', autreDraft.trim() || AUTRE_VALUE)"
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
        :class="{ selected: selectedOption === option.value }"
        type="button"
        @click="select(option.value)"
      >
        <span class="emoji">{{ option.emoji }}</span>
        <span class="label">{{ option.label }}</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { EvenementOption } from '~/types/evenement'
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'

const AUTRE_VALUE = 'autre'

const props = defineProps<{
  options: EvenementOption[]
  // Valeur unique : un choix de la liste, 'autre' sans précision, ou le texte libre de « Autre ».
  modelValue: string | null
  autrePlaceholder?: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', v: string | null): void
  (e: 'change'): void
}>()

const isFreeText = (value: string | null) =>
  !!value && !props.options.some((option) => option.value === value)

// Carte mise en avant : le texte libre est affiché sous « Autre ».
const selectedOption = computed(() =>
  isFreeText(props.modelValue) ? AUTRE_VALUE : props.modelValue,
)
const autreOpen = ref(selectedOption.value === AUTRE_VALUE)
const autreDraft = ref(isFreeText(props.modelValue) ? (props.modelValue ?? '') : '')

function closeAutre() {
  autreOpen.value = false
  emit('update:modelValue', null)
}

function select(value: string) {
  autreOpen.value = value === AUTRE_VALUE
  if (value !== AUTRE_VALUE) {
    emit('update:modelValue', value)
    emit('change')
  } else {
    // Un texte déjà saisi sous « Autre » est conservé.
    emit('update:modelValue', autreDraft.value.trim() || AUTRE_VALUE)
    // focus input when "autre" is selected
    nextTick(() => {
      const input = document.querySelector('.field') as HTMLInputElement | null
      input?.focus()
      input?.scrollIntoView({ behavior: 'smooth', block: 'center' })
    })
  }
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.evenement-option-select {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

.option-select {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-xs;

  @media (min-width: $breakpoint-desktop) {
    display: flex;
    gap: $spacing-s;
  }
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

  &.selected {
    border-color: $brand-accent;
    background: rgba($brand-accent, 0.08);

    .label {
      font-weight: 600;
      color: $text-primary;
    }
  }

  .emoji {
    font-size: 1.3rem;
    flex-shrink: 0;
    width: 1.8rem;
    text-align: center;
  }

  .label {
    font-size: 0.95rem;
    color: $text-primary;
    line-height: 1.3;
  }
}

.autre-input-container {
  display: grid;
  padding: $spacing-xs;

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
    }

    .field {
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
