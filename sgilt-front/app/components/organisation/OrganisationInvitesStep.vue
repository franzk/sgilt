<template>
  <div ref="root" class="invites-step">
    <button
      v-for="tranche in NB_INVITES_OPTIONS"
      :key="tranche"
      class="tranche"
      :class="{ selected: nbInvites === tranche }"
      type="button"
      :aria-pressed="nbInvites === tranche"
      @click="choose(tranche)"
    >
      {{ tranche }}
    </button>

    <!-- Ouvert, le mini formulaire prend la place de la carte "Nombre exact". -->
    <form v-if="exactOpen" class="exact-form" @submit.prevent="validateExact">
      <input
        :value="exactDraft"
        class="exact-field"
        type="text"
        inputmode="numeric"
        :placeholder="$t('organisation.invites-exact-placeholder')"
        @input="onExactInput"
      />
      <button class="exact-validate" type="submit">
        {{ $t('organisation.autre-validate') }}
      </button>
    </form>
    <button v-else class="tranche wide" type="button" @click="openExact">
      {{ $t('organisation.invites-exact') }}
    </button>

    <button
      class="tranche wide"
      :class="{ selected: nbInvites === NB_INVITES_UNKNOWN }"
      type="button"
      :aria-pressed="nbInvites === NB_INVITES_UNKNOWN"
      @click="choose(NB_INVITES_UNKNOWN)"
    >
      {{ $t('event-info.nb-invites-unknown') }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { NB_INVITES_OPTIONS, NB_INVITES_UNKNOWN } from '~/types/evenement'

// Valeur unique : une tranche de la liste, « je ne sais pas encore », ou le nombre exact saisi.
const nbInvites = defineModel<string>({ required: true })

const emit = defineEmits<{
  (e: 'next'): void
}>()

const isExact = (value: string) =>
  !!value && value !== NB_INVITES_UNKNOWN && !NB_INVITES_OPTIONS.includes(value)

// Le nombre exact est un brouillon local : il n'entre dans le state qu'à la validation, pour
// qu'une saisie abandonnée ne soit pas comptée comme réponse.
const exactOpen = ref(isExact(nbInvites.value))
const exactDraft = ref(isExact(nbInvites.value) ? nbInvites.value : '')
const root = ref<HTMLElement | null>(null)

// Choisir une tranche (ou « je ne sais pas encore ») valide aussitôt, comme les autres choix par
// cartes.
function choose(tranche: string) {
  nbInvites.value = tranche
  exactOpen.value = false
  emit('next')
}

// Ouvrir "Nombre exact" désélectionne la tranche précédemment choisie : le state suit ce qui
// est affiché, donc un skip de tunnel à ce stade ne garde pas l'ancien choix.
function openExact() {
  nbInvites.value = ''
  exactOpen.value = true
  nextTick(() => root.value?.querySelector<HTMLInputElement>('.exact-field')?.focus())
}

// Chiffres seulement, sans zéro en tête : un nombre entier supérieur à 0.
function onExactInput(event: Event) {
  const input = event.target as HTMLInputElement // cible d'un @input posé sur un <input>
  input.value = input.value.replace(/\D/g, '').replace(/^0+/, '')
  exactDraft.value = input.value
}

// Validé vide, "Nombre exact" ne renseigne rien.
function validateExact() {
  nbInvites.value = exactDraft.value
  emit('next')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.invites-step {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-s;

  .tranche {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 4.25rem;
    padding: $spacing-xs;
    border: 1.5px solid $divider-color;
    border-radius: $radius-lg;
    background: $surface-white;
    color: $text-primary;
    font-family: inherit;
    font-size: $font-size-sm;
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
      font-weight: $font-weight-semibold;
    }

    &.wide {
      grid-column: 1 / -1;
    }
  }

  .exact-form {
    grid-column: 1 / -1;
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    min-height: 4.25rem; // même hauteur que la carte "Nombre exact" qu'il remplace
    animation: appear 200ms ease;

    .exact-field {
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

    .exact-validate {
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

@keyframes appear {
  from {
    opacity: 0;
  }
}
</style>
