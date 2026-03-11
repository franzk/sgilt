<template>
  <div v-if="prestataire" class="demande-page">
    <div class="demande-container">
      <!-- Finalisation -->
      <template v-if="submitted">
        <DemandeFinalisation
          :prestataire-name="prestataire.name"
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
        />
      </template>

      <!-- Tunnel -->
      <template v-else>
        <DemandeSheetHeader
          :etape="etapeActuelle"
          :submitted="submitted"
          @back="back"
          @close="navigateTo(`/${slug}`)"
          @go-to="goTo"
        />

        <div class="demande-content">
          <div class="demande-content__form">
            <Transition
              :name="direction === 'forward' ? 'slide-forward' : 'slide-back'"
              mode="out-in"
            >
              <div :key="etapeActuelle" class="step-wrapper">
                <DemandeEtape1 v-if="etapeActuelle === 1" :state="state" @change="next" />
                <DemandeEtape2 v-else-if="etapeActuelle === 2" :state="state" @change="next" />
                <DemandeEtape3 v-else-if="etapeActuelle === 3" :state="state" @change="next" />
                <DemandeEtape4 v-else-if="etapeActuelle === 4" :state="state" @change="next" />
                <DemandeEtape5Desktop
                  v-else-if="etapeActuelle === 5"
                  :state="state"
                  @change="next"
                />
              </div>
            </Transition>
          </div>

          <aside class="demande-content__aside">
            <DemandeCommentCaMarche v-if="etapeActuelle === 1" />
            <DemandeRecap
              v-else
              :event-type-label="eventTypeLabel"
              :event-type-emoji="eventTypeEmoji"
              :ambiance-label="ambianceLabel"
              :ambiance-emoji="ambianceEmoji"
              :moment-cle-label="momentCleLabel"
              :moment-cle-emoji="momentCleEmoji"
              :date="state.date"
              :ville="state.ville"
              :nb-invites="state.nbInvites"
            />
          </aside>
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
import DemandeSheetHeader from '~/components/demande/DemandeSheetHeader.vue'
import DemandeRecap from '~/components/demande/DemandeRecap.vue'
import DemandeCommentCaMarche from '~/components/demande/DemandeCommentCaMarche.vue'
import DemandeEtape1 from '~/components/demande/DemandeEtape1.vue'
import DemandeEtape2 from '~/components/demande/DemandeEtape2.vue'
import DemandeEtape3 from '~/components/demande/DemandeEtape3.vue'
import DemandeEtape4 from '~/components/demande/DemandeEtape4.vue'
import DemandeEtape5Desktop from '~/components/demande/DemandeEtape5Desktop.vue'
import DemandeFinalisation from '~/components/demande/DemandeFinalisation.vue'
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

const canProceed = computed(() => {
  switch (etapeActuelle.value) {
    case 1:
      return !!state.eventType
    case 2:
      return !!state.ambiance
    case 3:
      return !!state.momentCle
    case 4:
      return true
    case 5:
      return !!state.date && !!state.ville.trim() && !!state.nbInvites.trim()
    default:
      return false
  }
})
</script>

<style scoped lang="scss">
.demande-page {
  min-height: 100dvh;
  padding: 0 $spacing-m;
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
  max-width: 940px;
  margin: 0 auto;
}

.demande-content {
  margin-top: $spacing-l;
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: $spacing-xxl;
  align-items: start;
}

.demande-content__aside {
  position: sticky;
  top: $spacing-l;

  :deep(.recap) {
    margin-top: 0;
  }
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
