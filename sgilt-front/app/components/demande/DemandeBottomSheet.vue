<template>
  <SgiltBottomSheet
    v-model:open="drawerOpen"
    title="Envoyer une demande"
    description="Remplissez les étapes pour contacter ce prestataire."
    class="demande-bottom-sheet"
  >
    <div class="sheet-inner">
      <!-- Header -->
      <DemandeSheetHeader
        :etape="etapeActuelle"
        :submitted="submitted"
        @back="back"
        @close="drawerOpen = false"
        @go-to="goTo"
      />

      <!-- Body -->
      <div class="sheet-body" ref="bodyRef">
        <div v-if="submitted">
          <DemandeFinalisation
            :event-type-label="eventTypeLabel"
            :event-type-emoji="eventTypeEmoji"
            :ambiance-label="ambianceLabel"
            :ambiance-emoji="ambianceEmoji"
            :moment-cle-label="momentCleLabel"
            :moment-cle-emoji="momentCleEmoji"
            :date="state.date"
            :ville="state.ville"
            :nb-invites="state.nbInvites"
            :lieu="state.lieu"
            :prestataireName="prestataireName"
          />
        </div>

        <template v-else>
          <Transition
            :name="direction === 'forward' ? 'slide-forward' : 'slide-back'"
            mode="out-in"
          >
            <div :key="etapeActuelle">
              <DemandeEtape1 v-if="etapeActuelle === 1" :state="state" @change="next" />
              <DemandeEtape2 v-else-if="etapeActuelle === 2" :state="state" @change="next" />
              <DemandeEtape3 v-else-if="etapeActuelle === 3" :state="state" @change="next" />
              <DemandeEtape4 v-else-if="etapeActuelle === 4" :state="state" @change="next" />
              <DemandeEtape5
                v-else-if="etapeActuelle === 5"
                :state="state"
                :event-type-label="eventTypeLabel"
                :event-type-emoji="eventTypeEmoji"
                :ambiance-label="ambianceLabel"
                :ambiance-emoji="ambianceEmoji"
                :moment-cle-label="momentCleLabel"
                :moment-cle-emoji="momentCleEmoji"
                @change="next"
              />
              <DemandeEtape6
                v-else-if="etapeActuelle === 6"
                showRecap
                :state="state"
                :event-type-label="eventTypeLabel"
                :event-type-emoji="eventTypeEmoji"
                :ambiance-label="ambianceLabel"
                :ambiance-emoji="ambianceEmoji"
                :moment-cle-label="momentCleLabel"
                :moment-cle-emoji="momentCleEmoji"
                @change="submit"
              />
            </div>
          </Transition>

          <DemandeRecap
            v-if="etapeActuelle >= 2 && etapeActuelle < 5"
            :event-type-label="eventTypeLabel"
            :event-type-emoji="eventTypeEmoji"
            :ambiance-label="ambianceLabel"
            :ambiance-emoji="ambianceEmoji"
            :moment-cle-label="momentCleLabel"
            :moment-cle-emoji="momentCleEmoji"
          />
        </template>
      </div>
    </div>
  </SgiltBottomSheet>
</template>

<script setup lang="ts">
import SgiltBottomSheet from '~/components/basics/sheets/SgiltBottomSheet.vue'
import { useDemande } from '~/composables/useDemande'

const props = defineProps<{
  isOpen: boolean
  prestataireName: string
  prestataireSlug: string
  initialDate?: Date | null
}>()

const emit = defineEmits<{ (e: 'close'): void }>()

// ─── Controlled v-model bridge ─────────────────────────────────────────────────
const drawerOpen = computed({
  get: () => props.isOpen,
  set: (v) => {
    if (!v) emit('close')
  },
})

const {
  etapeActuelle,
  direction,
  submitted,
  submitting,
  state,
  next,
  back,
  goTo,
  submit,
  eventTypeLabel,
  eventTypeEmoji,
  ambianceLabel,
  ambianceEmoji,
  momentCleLabel,
  momentCleEmoji,
} = useDemande(props.initialDate)

// ─── Scroll to top on step change ─────────────────────────────────────────────
const bodyRef = ref<HTMLElement | null>(null)
watch(etapeActuelle, () => {
  nextTick(() => bodyRef.value?.scrollTo({ top: 0, behavior: 'smooth' }))
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

:global(.sgilt-sheet__content) {
  height: calc(100vh - $app-header-height);
}

$shadow-sheet: 0 -28px 80px rgba(0, 0, 0, 0.28);

.sheet-inner {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;

  background:
    radial-gradient(
      1100px 520px at 50% -10%,
      rgba($color-accent, 0.22) 0%,
      rgba(255, 255, 255, 0) 55%
    ),
    linear-gradient(180deg, #fffdf6 0%, #ffffff 60%);

  border-top: 1px solid rgba(255, 255, 255, 0.7);
  box-shadow: $shadow-sheet;
  padding-bottom: calc(1.1rem + env(safe-area-inset-bottom, 0px));
}

.sheet-body {
  flex: 1;
  overflow-y: auto;
  padding: $spacing-m;
  overscroll-behavior: contain;
  position: relative;
}

.sheet-footer {
  flex-shrink: 0;
  padding: $spacing-s $spacing-m calc($spacing-m + env(safe-area-inset-bottom, 0px));
  background: rgba(255, 255, 255, 0.95);
  border-top: 1px solid $divider-color;
}

.sheet-cta {
  width: 100%;
  height: 3rem;
  border-radius: 2rem;
  border: none;
  background: linear-gradient(to bottom, #ffd84d 0%, #f2c200 100%);
  color: #fff;
  font-size: 1rem;
  font-weight: 700;
  font-family: inherit;
  cursor: pointer;
  text-shadow: 0 1px 0 rgba(0, 0, 0, 0.1);
  box-shadow:
    0 4px 8px rgba(0, 0, 0, 0.14),
    0 12px 28px rgba(242, 194, 0, 0.18);
  transition:
    opacity 150ms ease,
    transform 100ms ease;

  &:active {
    transform: scale(0.99);
  }

  &--disabled {
    opacity: 0.45;
    cursor: not-allowed;
    pointer-events: none;
  }
}

// ─── Step slide transitions ────────────────────────────────────────────────────
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
