<template>
  <div class="sidebar-body">
    <div
      v-if="displayMode === 'display' || displayMode === 'preview'"
      class="sidebar-block"
      :class="{ 'preview-only-desktop': displayMode === 'preview' }"
    >
      <SgiltDatePicker
        v-model="dateModel"
        :disabled="disableDate || displayMode === 'preview'"
        :placeholder="$t('provider.details.verify-date')"
        :error="!!dateError"
      />
      <Transition name="fade">
        <p v-if="dateError" class="date-error">{{ dateError }}</p>
      </Transition>
    </div>

    <div v-if="isEdit || prestataire.budget" class="sidebar-block sidebar-budget">
      <h3 class="title">{{ $t('provider.details.rates') }}</h3>
      <EditableText
        as="p"
        v-model="prestataire!.budget"
        field="budget"
        :editable="isEdit"
        multiline
        class="text"
        @commit="saveField('budget', $event)"
      />
    </div>

    <SgiltButton
      v-if="displayMode === 'display' || displayMode === 'preview'"
      class="sidebar-cta"
      :disabled="displayMode === 'preview'"
      @click="emit('select-intent')"
    >
      {{ $t('provider.details.send-request') }}
    </SgiltButton>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import EditableText from '~/components/prestataire/EditableText.vue'
import type { PrestataireDetail } from '~/data/prestataire/domain/PrestataireDetail'
import type { DisplayMode } from '~/types/prestataire'

const props = defineProps<{
  prestataire: PrestataireDetail
  displayMode: DisplayMode
  disableDate?: boolean
  dateError: string | null
}>()

const { prestataire, saveField } = usePrestataire()
const isEdit = computed(() => props.displayMode === 'edit')

const emit = defineEmits<{
  'select-intent': []
}>()

// La date est celle de l'événement local. Dans le flow « ajouter un prestataire », le sélecteur
// est désactivé et affiche celle de l'événement existant.
const { localEvent } = useLocalEvent()
const { flowPayload } = useFlow()

const dateModel = computed<Date | undefined>({
  get: () => (props.disableDate ? flowPayload.value?.date : localEvent.date),
  set: (value) => {
    localEvent.date = value
  },
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.sidebar-body {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  padding: $spacing-m;

  @media (min-width: $breakpoint-desktop) {
    padding: 1.5rem;
  }
}

.sidebar-block {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;

  // Onglet Aperçu : chrome de réservation visible en desktop uniquement (pour se faire une
  // idée du rendu), toujours masqué en mobile comme avant.
  &.preview-only-desktop {
    display: none;

    @media (min-width: $breakpoint-desktop) {
      display: flex;
    }
  }
}

.sidebar-budget {
  display: none;

  @media (min-width: $breakpoint-desktop) {
    display: unset;
    padding-top: 1.25rem;
    border-top: 1px solid rgba(0, 0, 0, 0.08);

    .title {
      font-family: 'Cormorant Garamond', serif;
      font-size: 1.1rem;
      font-weight: 600;
      color: $color-primary;
      margin: 0 0 0.5rem;
    }

    .text {
      font-size: 0.9rem;
      color: $text-secondary;
      line-height: 1.6;
      margin: 0;
    }
  }
}

.sidebar-cta {
  display: none;

  @media (min-width: $breakpoint-desktop) {
    display: flex;
    align-self: center;
  }
}

.date-error {
  font-size: 0.82rem;
  color: $state-error;
  margin: 0;
}

.fade-enter-active,
.fade-leave-active {
  transition:
    opacity 200ms ease,
    transform 200ms ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>
