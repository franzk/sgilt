<template>
  <div class="mobile-tunnel">
    <!-- Finalisation (après soumission) -->
    <div v-if="submitted" class="mobile-body mobile-body--full">
      <DemandeFinalisation :prestataire-name="prestataireName" @close="closeMobile" />
    </div>

    <!-- Phase stepper : étapes 1 → 3 -->
    <template v-else-if="mobilePhase === 'stepper'">
      <DemandeSheetHeader
        :etape="etapeActuelle"
        :submitted="false"
        :steps="4"
        @back="back"
        @close="closeMobile"
        @go-to="goTo"
      />

      <div ref="bodyRef" class="mobile-body">
        <Transition :name="direction === 'forward' ? 'slide-forward' : 'slide-back'" mode="out-in">
          <div :key="etapeActuelle">
            <EvenementTypeStep v-if="etapeActuelle === 1" />
            <EvenementAmbianceStep v-else-if="etapeActuelle === 2" />
            <EvenementMomentCleStep v-else-if="etapeActuelle === 3" />
            <EvenementDescriptionStep v-else-if="etapeActuelle === 4" />
          </div>
        </Transition>
      </div>

      <div v-if="etapeActuelle === 4" class="mobile-footer">
        <SgiltButton @click="next">{{ $t('tunnel.footer.continue') }}</SgiltButton>
      </div>
    </template>

    <!-- Phase récap : liste éditable + bouton submit -->
    <DemandeMobileRecap v-else :slug="props.slug" @cancel="closeMobile" />
  </div>
</template>

<script setup lang="ts">
import EvenementTypeStep from '~/components/evenement/EvenementTypeStep.vue'
import EvenementAmbianceStep from '~/components/evenement/EvenementAmbianceStep.vue'
import EvenementMomentCleStep from '~/components/evenement/EvenementMomentCleStep.vue'
import EvenementDescriptionStep from '~/components/evenement/EvenementDescriptionStep.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import DemandeFinalisation from '~/components/demande/DemandeFinalisation.vue'
import DemandeSheetHeader from '~/components/demande/DemandeSheetHeader.vue'
import DemandeMobileRecap from '~/components/demande/DemandeMobileRecap.vue'
import { useDemande } from '~/composables/useDemande'

const props = defineProps<{ prestataireName: string; prestataireImage: string; slug: string }>()

const router = useRouter()

const { etapeActuelle, direction, submitted, state, next, back, goTo, reset } = useDemande()

const mobilePhase = ref<'stepper' | 'recap'>(etapeActuelle.value >= 5 ? 'recap' : 'stepper')

watch(etapeActuelle, (n) => {
  if (n >= 5) mobilePhase.value = 'recap'
})

const bodyRef = ref<HTMLElement | null>(null)
watch(etapeActuelle, () => nextTick(() => bodyRef.value?.scrollTo({ top: 0, behavior: 'smooth' })))

const closeMobile = () => {
  reset()
  router.back()
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// ─── Conteneur ────────────────────────────────────────────────────────────────
.mobile-tunnel {
  display: flex;
  flex-direction: column;
  height: $viewport-below-header;
}

.mobile-body {
  flex: 1;
  overflow-y: auto;
  padding: $spacing-m;
  overscroll-behavior: contain;
  position: relative;
  padding-bottom: 70px; // pour éviter que le contenu soit caché derrière le footer fixe

  &--full {
    position: static;
  }
}

.mobile-footer {
  flex: 1;
  padding: $spacing-s $spacing-m $spacing-m;
  padding-bottom: calc(#{$spacing-m} + var(--keyboard-offset, 0px));
  background: #fff;
  border-top: 1px solid $divider-color;
  transition: padding-bottom 120ms ease;

  :deep(button) {
    width: 100%;
    justify-content: center;
  }
}

// ─── Transitions slide ────────────────────────────────────────────────────────
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
