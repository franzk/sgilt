<template>
  <div v-if="prestataire" class="demande-page">
    <!-- Lien retour -->
    <NuxtLink :to="`/${slug}`" class="back-link">← Retour à la fiche</NuxtLink>

    <div class="demande-container">
      <!-- Confirmation -->
      <template v-if="submitted">
        <DemandeConfirmation
          :prestataire-name="prestataire.name"
          :email="state.email"
          @close="navigateTo(`/${slug}`)"
        />
      </template>

      <!-- Tunnel -->
      <template v-else>
        <DemandeStepper :etape="etapeActuelle" @go-to="goTo" />

        <div class="demande-content">
          <Transition
            :name="direction === 'forward' ? 'slide-forward' : 'slide-back'"
            mode="out-in"
          >
            <div :key="etapeActuelle" class="step-wrapper">
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
        </div>

        <!-- Navigation -->
        <div class="demande-nav">
          <button
            v-if="etapeActuelle > 1"
            class="nav-back"
            type="button"
            @click="back"
          >
            ← Retour
          </button>

          <button
            class="nav-cta"
            :class="{ 'nav-cta--disabled': !canProceed }"
            :disabled="!canProceed || submitting"
            type="button"
            @click="etapeActuelle < 5 ? next() : submit()"
          >
            <span v-if="submitting">Envoi en cours…</span>
            <span v-else-if="etapeActuelle < 5">Continuer →</span>
            <span v-else>Envoyer la demande →</span>
          </button>
        </div>
      </template>
    </div>
  </div>

  <div v-else-if="!loading" class="not-found">
    <p>Ce prestataire est introuvable.</p>
    <NuxtLink to="/search">← Retour à la recherche</NuxtLink>
  </div>
</template>

<script setup lang="ts">
import DemandeStepper from '~/components/demande/DemandeStepper.vue'
import DemandeRecap from '~/components/demande/DemandeRecap.vue'
import DemandeEtape1 from '~/components/demande/DemandeEtape1.vue'
import DemandeEtape2 from '~/components/demande/DemandeEtape2.vue'
import DemandeEtape3 from '~/components/demande/DemandeEtape3.vue'
import DemandeEtape4 from '~/components/demande/DemandeEtape4.vue'
import DemandeEtape5 from '~/components/demande/DemandeEtape5.vue'
import DemandeConfirmation from '~/components/demande/DemandeConfirmation.vue'
import { useDemande } from '~/composables/useDemande'
import { SearchMockService } from '~/services/search.mock'
import type { PrestataireDetail } from '~/types/prestataire'

const route = useRoute()
const slug = route.params.slug as string

const prestataire = ref<PrestataireDetail | null>(null)
const loading = ref(true)

const { dateModel } = useSearchUi()

onMounted(async () => {
  prestataire.value = (await SearchMockService.getBySlug(slug)) ?? null
  loading.value = false
})

watchEffect(() => {
  if (!prestataire.value) return
  useSeoMeta({
    title: `Demande à ${prestataire.value.name} — Sgilt`,
  })
})

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
} = useDemande(dateModel.value)
</script>

<style scoped lang="scss">
.demande-page {
  min-height: 100dvh;
  padding: $spacing-l $spacing-m;

  @media (min-width: 640px) {
    padding: $spacing-xxl $spacing-l;
  }
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: $spacing-xs;
  font-size: 0.9rem;
  color: $text-secondary;
  text-decoration: none;
  margin-bottom: $spacing-l;
  transition: color 150ms ease;

  &:hover {
    color: $text-primary;
  }
}

.demande-container {
  max-width: 600px;
  margin: 0 auto;
}

.demande-content {
  margin-top: $spacing-l;
}

.step-wrapper {
  // placeholder for transition
}

.demande-nav {
  display: flex;
  align-items: center;
  gap: $spacing-m;
  margin-top: $spacing-xl;
  padding-top: $spacing-l;
  border-top: 1px solid $divider-color;
}

.nav-back {
  padding: $spacing-s $spacing-l;
  border: 1.5px solid $divider-color;
  border-radius: 2rem;
  background: #fff;
  font-size: 0.95rem;
  font-family: inherit;
  color: $text-secondary;
  cursor: pointer;
  transition: background 150ms ease;
  white-space: nowrap;

  &:hover {
    background: $surface-soft;
  }
}

.nav-cta {
  flex: 1;
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

// ─── Step transitions ──────────────────────────────────────────────────────────
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

// ─── 404 ──────────────────────────────────────────────────────────────────────
.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 50vh;
  gap: 1rem;
  font-size: 1rem;
  color: $text-secondary;

  a {
    color: $color-primary;
    text-decoration: none;
    font-weight: 500;
  }
}
</style>
