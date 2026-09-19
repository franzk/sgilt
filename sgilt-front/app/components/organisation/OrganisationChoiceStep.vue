<template>
  <div ref="root" class="choice-step">
    <div class="options">
      <template v-for="option in options" :key="option.value">
        <!-- Ouvert, le mini formulaire prend la place de la carte "Autre". -->
        <form
          v-if="option.value === AUTRE_VALUE && autreOpen"
          class="autre-form"
          @submit.prevent="validateAutre"
        >
          <input
            ref="autreInput"
            v-model="autreDraft"
            class="autre-field"
            type="text"
            :placeholder="autrePlaceholder"
          />
          <button class="autre-validate" type="submit">
            {{ $t('organisation.autre-validate') }}
          </button>
        </form>
        <button
          v-else
          class="option"
          :class="{ selected: modelValue === option.value, autre: option.value === AUTRE_VALUE }"
          type="button"
          @click="option.value === AUTRE_VALUE ? openAutre() : choose(option.value)"
        >
          <span class="emoji">{{ option.emoji }}</span>
          <span class="label">{{ option.label }}</span>
        </button>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { DemandeOption } from '~/types/demande'

const AUTRE_VALUE = 'autre'

const props = defineProps<{
  options: DemandeOption[]
  modelValue: string | null
  autreValue: string
  autrePlaceholder: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', v: string | null): void
  (e: 'update:autreValue', v: string): void
  (e: 'next'): void
}>()

// Le texte "Autre" est un brouillon local : il n'entre dans le state qu'à la
// validation, pour qu'une saisie abandonnée ne soit pas comptée comme réponse.
const autreOpen = ref(props.modelValue === AUTRE_VALUE)
const autreDraft = ref(props.autreValue)
const root = ref<HTMLElement | null>(null)

function choose(value: string) {
  emit('update:modelValue', value)
  emit('update:autreValue', '')
  emit('next')
}

// Ouvrir "Autre" désélectionne la carte précédemment choisie : le state suit ce
// qui est affiché, donc un skip de tunnel à ce stade ne garde pas l'ancien choix.
function openAutre() {
  emit('update:modelValue', null)
  emit('update:autreValue', '')
  autreOpen.value = true
  nextTick(() => root.value?.querySelector<HTMLInputElement>('.autre-field')?.focus())
}

// "Autre" validé avec un texte vide reste un choix à part entière.
function validateAutre() {
  emit('update:modelValue', AUTRE_VALUE)
  emit('update:autreValue', autreDraft.value.trim())
  emit('next')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.choice-step {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;

  .options {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: $spacing-s;

    .option {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: $spacing-xs;
      min-height: 6.5rem;
      padding: $spacing-s;
      border: 1.5px solid $divider-color;
      border-radius: $radius-lg;
      background: $surface-white;
      color: $text-primary;
      font-family: inherit;
      text-align: center;
      cursor: pointer;
      transition:
        border-color 180ms ease,
        background 180ms ease;

      &:hover {
        border-color: rgba($brand-accent, 0.7);
      }

      &.selected {
        border-color: $brand-accent;
        background: rgba($brand-accent, 0.14);

        .label {
          font-weight: $font-weight-semibold;
        }
      }

      &.autre {
        grid-column: 1 / -1;
        min-height: 4.25rem;
      }

      .emoji {
        font-size: 1.6rem;
        line-height: 1;
      }

      .label {
        font-size: $font-size-sm;
        line-height: 1.3;
      }
    }

    .autre-form {
      grid-column: 1 / -1;
      display: flex;
      align-items: center;
      gap: $spacing-xs;
      min-height: 4.25rem; // même hauteur que la carte "Autre" qu'il remplace
      animation: appear 200ms ease;

      .autre-field {
        flex: 1;
        min-width: 0;
        height: 3rem;
        padding: 0 $spacing-m;
        border: 1.5px solid $divider-color;
        border-radius: $radius-lg;
        background: $surface-white;
        color: $text-primary;
        font-family: inherit;
        font-size: $font-size-sm;
        outline: none;
        transition: border-color 180ms ease;

        &:focus {
          border-color: $brand-accent;
        }

        &::placeholder {
          color: $text-secondary;
          opacity: 0.6;
        }
      }

      .autre-validate {
        height: 3rem;
        padding: 0 $spacing-l;
        border: none;
        border-radius: 9999px;
        background: $brand-accent;
        color: $brand-primary;
        font-family: inherit;
        font-size: $font-size-sm;
        font-weight: $font-weight-bold;
        cursor: pointer;
      }
    }
  }
}

@keyframes appear {
  from {
    opacity: 0;
  }
}
</style>
