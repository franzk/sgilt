<template>
  <Teleport to="body">
    <Transition name="overlay">
      <div v-if="isOpen" class="sheet-overlay" @click.self="$emit('close')" />
    </Transition>

    <Transition name="sheet">
      <div
        v-if="isOpen"
        class="sheet"
        role="dialog"
        aria-modal="true"
        aria-label="Envoyer une demande"
        @touchstart.passive="onTouchStart"
        @touchmove.passive="onTouchMove"
        @touchend.passive="onTouchEnd"
      >
        <!-- Header -->
        <div class="sheet__header">
          <button
            v-if="etapeActuelle > 1 && !submitted"
            class="sheet__back"
            type="button"
            aria-label="Étape précédente"
            @click="back"
          >
            ←
          </button>
          <span v-else class="sheet__back-placeholder" />

          <DemandeStepper
            v-if="!submitted"
            :etape="etapeActuelle"
            @go-to="goTo"
          />

          <button class="sheet__close" type="button" aria-label="Fermer" @click="$emit('close')">
            ✕
          </button>
        </div>

        <!-- Contenu des étapes -->
        <div class="sheet__body" ref="bodyRef">
          <div v-if="submitted" class="sheet__step">
            <DemandeConfirmation
              :prestataire-name="prestataireName"
              :email="state.email"
              @close="$emit('close')"
            />
          </div>

          <template v-else>
            <Transition :name="direction === 'forward' ? 'slide-forward' : 'slide-back'" mode="out-in">
              <div :key="etapeActuelle" class="sheet__step">
                <DemandeEtape1 v-if="etapeActuelle === 1" :state="state" />
                <DemandeEtape2 v-else-if="etapeActuelle === 2" :state="state" />
                <DemandeEtape3 v-else-if="etapeActuelle === 3" :state="state" />
                <DemandeEtape4 v-else-if="etapeActuelle === 4" :state="state" />
                <DemandeEtape5
                  v-else-if="etapeActuelle === 5"
                  :state="state"
                  :event-type-label="eventTypeLabel"
                  :event-type-emoji="eventTypeEmoji"
                  :ambiance-label="ambianceLabel"
                  :ambiance-emoji="ambianceEmoji"
                  :moment-cle-label="momentCleLabel"
                  :moment-cle-emoji="momentCleEmoji"
                />
              </div>
            </Transition>

            <!-- Récap progressif (étapes 2-4) -->
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

        <!-- Footer CTA -->
        <div v-if="!submitted" class="sheet__footer">
          <button
            class="sheet__cta"
            :class="{ 'sheet__cta--disabled': !canProceed }"
            :disabled="!canProceed || submitting"
            type="button"
            @click="etapeActuelle < 5 ? next() : submit()"
          >
            <span v-if="submitting">Envoi en cours…</span>
            <span v-else-if="etapeActuelle < 5">Continuer →</span>
            <span v-else>Envoyer la demande →</span>
          </button>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { useDemande } from '~/composables/useDemande'

const props = defineProps<{
  isOpen: boolean
  prestataireName: string
  prestataireSlug: string
  initialDate?: Date | null
}>()

const emit = defineEmits<{ (e: 'close'): void }>()

const {
  etapeActuelle,
  direction,
  submitted,
  submitting,
  state,
  canProceed,
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

// ─── Swipe down to close ───────────────────────────────────────────────────────
const touchStartY = ref(0)
const touchCurrentY = ref(0)

function onTouchStart(e: TouchEvent) {
  touchStartY.value = e.touches[0]?.clientY ?? 0
}

function onTouchMove(e: TouchEvent) {
  touchCurrentY.value = e.touches[0]?.clientY ?? 0
}

function onTouchEnd() {
  const delta = touchCurrentY.value - touchStartY.value
  if (delta > 80) {
    emit('close')
  }
}
</script>

<style scoped lang="scss">
$sheet-radius: 16px 16px 0 0;

.sheet-overlay {
  position: fixed;
  inset: 0;
  background: $overlay-dark-medium;
  z-index: $z-overlay;
}

.sheet {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: $z-modal;
  background: #fff;
  border-radius: $sheet-radius;
  max-height: 90dvh;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $spacing-s $spacing-m 0;
    flex-shrink: 0;
    gap: $spacing-xs;
  }

  &__back,
  &__close {
    width: 2.2rem;
    height: 2.2rem;
    border-radius: 50%;
    border: 1px solid $divider-color;
    background: #fff;
    font-size: 1rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    transition: background 150ms ease;
    color: $text-primary;

    &:hover {
      background: $surface-soft;
    }
  }

  &__back-placeholder {
    width: 2.2rem;
    height: 2.2rem;
    flex-shrink: 0;
  }

  &__body {
    flex: 1;
    overflow-y: auto;
    padding: $spacing-m $spacing-m;
    overscroll-behavior: contain;
  }

  &__step {
    // prevent layout shift during transition
  }

  &__footer {
    flex-shrink: 0;
    padding: $spacing-s $spacing-m $spacing-m;
    background: rgba(255, 255, 255, 0.95);
    border-top: 1px solid $divider-color;
  }

  &__cta {
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
}

// ─── Overlay transition ────────────────────────────────────────────────────────
.overlay-enter-active,
.overlay-leave-active {
  transition: opacity 300ms ease;
}

.overlay-enter-from,
.overlay-leave-to {
  opacity: 0;
}

// ─── Sheet slide-up transition ────────────────────────────────────────────────
.sheet-enter-active,
.sheet-leave-active {
  transition: transform 300ms ease-out;
}

.sheet-enter-from,
.sheet-leave-to {
  transform: translateY(100%);
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
