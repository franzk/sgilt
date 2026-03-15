<template>
  <SgiltBottomSheet
    v-model:open="drawerOpen"
    title="Envoyer une demande"
    description="Remplissez les étapes pour contacter ce prestataire."
  >
    <template #header>
      <DemandeSheetHeader
        :etape="etapeActuelle"
        :submitted="submitted"
        @back="back"
        @close="closeAndFinish"
        @go-to="goTo"
      />
    </template>

    <div ref="bodyRef" class="sheet-body">
      <div v-if="submitted">
        <DemandeFinalisation :prestataire-name="prestataireName" />
      </div>

      <template v-else>
        <Transition
          :name="direction === 'forward' ? 'slide-forward' : 'slide-back'"
          mode="out-in"
        >
          <div :key="etapeActuelle">
            <DemandeEtape1 v-if="etapeActuelle === 1" />
            <DemandeEtape2 v-else-if="etapeActuelle === 2" />
            <DemandeEtape3 v-else-if="etapeActuelle === 3" />
            <DemandeEtape4 v-else-if="etapeActuelle === 4" />
            <DemandeEtape5 v-else-if="etapeActuelle === 5" />
            <DemandeEtape6 v-else-if="etapeActuelle === 6" show-recap />
          </div>
        </Transition>

        <DemandeRecap v-if="etapeActuelle >= 2 && etapeActuelle < 5" />
      </template>
    </div>
  </SgiltBottomSheet>
</template>

<script setup lang="ts">
import SgiltBottomSheet from '~/components/basics/sheets/SgiltBottomSheet.vue'
import { useDemande } from '~/composables/useDemande'

const props = defineProps<{
  isOpen: boolean
  prestataireName: string
}>()

const emit = defineEmits<{ close: [] }>()

const drawerOpen = computed({
  get: () => props.isOpen,
  set: (v) => {
    if (!v) emit('close')
  },
})

const { etapeActuelle, direction, submitted, state, back, goTo, reset } = useDemande()

watch(() => props.isOpen, (open) => {
  if (open && etapeActuelle.value === 1 && state.eventType && state.eventType.toUpperCase() !== 'AUTRE') {
    goTo(2)
  }
})

const closeAndFinish = () => {
  reset()
  emit('close')
}

const bodyRef = ref<HTMLElement | null>(null)
watch(etapeActuelle, () => nextTick(() => bodyRef.value?.scrollTo({ top: 0, behavior: 'smooth' })))
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

:global(.sgilt-sheet__content) {
  height: calc(100vh - $app-header-height);
}

.sheet-body {
  flex: 1;
  overflow-y: auto;
  padding: $spacing-m;
  overscroll-behavior: contain;
  position: relative;
}

.slide-forward-enter-active,
.slide-forward-leave-active,
.slide-back-enter-active,
.slide-back-leave-active {
  transition:
    transform 250ms ease,
    opacity 250ms ease;
}

.slide-forward-enter-from {
  transform: translateX(40px);
  opacity: 0;
}

.slide-forward-leave-to {
  transform: translateX(-40px);
  opacity: 0;
}

.slide-back-enter-from {
  transform: translateX(-40px);
  opacity: 0;
}

.slide-back-leave-to {
  transform: translateX(40px);
  opacity: 0;
}
</style>
