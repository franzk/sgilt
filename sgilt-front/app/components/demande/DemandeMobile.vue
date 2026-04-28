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
            <DemandeEtape1 v-if="etapeActuelle === 1" />
            <DemandeEtape2 v-else-if="etapeActuelle === 2" />
            <DemandeEtape3 v-else-if="etapeActuelle === 3" />
            <DemandeEtape4 v-else-if="etapeActuelle === 4" />
          </div>
        </Transition>
      </div>

      <div v-if="etapeActuelle === 4" class="mobile-footer">
        <SgiltButton @click="next">{{ $t('tunnel.footer.continue') }}</SgiltButton>
      </div>
    </template>

    <!-- Phase récap : liste éditable + bouton submit -->
    <DemandeMobileRecap
      v-else
      :prestataire-name="props.prestataireName"
      :prestataire-image="props.prestataireImage"
      :slug="props.slug"
      @cancel="closeMobile"
    />
  </div>
</template>

<script setup lang="ts">
import DemandeEtape1 from '~/components/demande/DemandeEtape1.vue'
import DemandeEtape2 from '~/components/demande/DemandeEtape2.vue'
import DemandeEtape3 from '~/components/demande/DemandeEtape3.vue'
import DemandeEtape4 from '~/components/demande/DemandeEtape4.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import DemandeFinalisation from '~/components/demande/DemandeFinalisation.vue'
import DemandeSheetHeader from '~/components/demande/DemandeSheetHeader.vue'
import DemandeMobileRecap from '~/components/demande/DemandeMobileRecap.vue'
import { useDemande } from '~/composables/useDemande'

const props = defineProps<{ prestataireName: string; prestataireImage: string; slug: string }>()

const router = useRouter()

const { etapeActuelle, direction, submitted, next, back, goTo, reset } = useDemande()

const mobilePhase = ref<'stepper' | 'recap'>(etapeActuelle.value >= 5 ? 'recap' : 'stepper')

watch(etapeActuelle, (n) => {
  if (n >= 5) mobilePhase.value = 'recap'
})

const bodyRef = ref<HTMLElement | null>(null)
watch(etapeActuelle, () => nextTick(() => bodyRef.value?.scrollTo({ top: 0, behavior: 'smooth' })))

const closeMobile = () => {
  if (submitted.value) reset()
  navigateTo(`/${props.slug}`)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// ─── Conteneur ────────────────────────────────────────────────────────────────
.mobile-tunnel {
  display: flex;
  flex-direction: column;
  height: calc(100dvh - $app-header-height);
}

.mobile-body {
  flex: 1;
  overflow-y: auto;
  padding: $spacing-m;
  overscroll-behavior: contain;
  position: relative;

  &--full {
    position: static;
  }
}

.mobile-footer {
  flex-shrink: 0;
  padding: $spacing-s $spacing-m $spacing-m;
  background: #fff;
  border-top: 1px solid $divider-color;

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
