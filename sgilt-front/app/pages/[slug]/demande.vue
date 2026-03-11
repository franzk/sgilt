<template>
  <div v-if="prestataire" class="demande-layout">
    <!-- Finalisation -->
    <template v-if="submitted">
      <DemandeFinalisation
        class="demande-layout__finalisation"
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

    <!-- Accordion tunnel -->
    <template v-else>
      <!-- Center: accordion -->
      <main class="demande-main">
        <div class="demande-main__top">
          <button class="main-back" type="button" @click="navigateTo(`/${slug}`)">← Retour</button>
        </div>

        <div class="demande-accordion">
          <div
            v-for="n in 6"
            :key="n"
            class="accordion-block"
            :class="{
              'accordion-block--active': n === etapeActuelle,
              'accordion-block--done': n < etapeActuelle,
              'accordion-block--locked': n > etapeActuelle,
            }"
          >
            <!-- Header -->
            <button
              class="accordion-block__header"
              type="button"
              :disabled="n > etapeActuelle"
              @click="n < etapeActuelle ? goTo(n) : undefined"
            >
              <span
                class="accordion-block__dot"
                :class="{
                  'accordion-block__dot--active': n === etapeActuelle,
                  'accordion-block__dot--done': n < etapeActuelle,
                }"
              >
                <span v-if="n < etapeActuelle">✓</span>
                <span v-else>{{ n }}</span>
              </span>

              <span class="accordion-block__header-text">
                <span class="accordion-block__label">{{ stepLabels[n - 1] }}</span>
                <span
                  v-if="n < etapeActuelle && stepDoneSummary(n)"
                  class="accordion-block__summary"
                >
                  {{ stepDoneSummary(n) }}
                </span>
              </span>

              <span v-if="n < etapeActuelle" class="accordion-block__edit">Modifier</span>
            </button>

            <!-- Body -->
            <Transition name="accordion-body">
              <div v-if="n === etapeActuelle" class="accordion-block__body">
                <DemandeEtape1 v-if="n === 1" :state="state" @change="next" />
                <DemandeEtape2 v-else-if="n === 2" :state="state" @change="next" />
                <DemandeEtape3 v-else-if="n === 3" :state="state" @change="next" />
                <DemandeEtape4 v-else-if="n === 4" :state="state" @change="next" />
                <DemandeEtape5Desktop v-else-if="n === 5" :state="state" @change="next" />
                <DemandeEtape6
                  v-else-if="n === 6"
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
          </div>
        </div>
      </main>

      <!-- Right panel -->
      <aside class="demande-panel">
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
    </template>
  </div>

  <div v-else-if="!loading" class="not-found">
    <p>Ce prestataire est introuvable.</p>
    <NuxtLink to="/search">← Retour à la recherche</NuxtLink>
  </div>
</template>

<script setup lang="ts">
import DemandeRecap from '~/components/demande/DemandeRecap.vue'
import DemandeCommentCaMarche from '~/components/demande/DemandeCommentCaMarche.vue'
import DemandeEtape1 from '~/components/demande/DemandeEtape1.vue'
import DemandeEtape2 from '~/components/demande/DemandeEtape2.vue'
import DemandeEtape3 from '~/components/demande/DemandeEtape3.vue'
import DemandeEtape4 from '~/components/demande/DemandeEtape4.vue'
import DemandeEtape5Desktop from '~/components/demande/DemandeEtape5Desktop.vue'
import DemandeEtape6 from '~/components/demande/DemandeEtape6.vue'
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
  submitted,
  submitting,
  state,
  next,
  goTo,
  submit,
  eventTypeLabel,
  eventTypeEmoji,
  ambianceLabel,
  ambianceEmoji,
  momentCleLabel,
  momentCleEmoji,
} = useDemande(dateModel.value)

const stepLabels = [
  "Type d'événement",
  'Ambiance',
  'Moment clé',
  'Description',
  'Détails pratiques',
  'Contact',
]

function stepDoneSummary(n: number): string {
  switch (n) {
    case 1:
      return eventTypeLabel.value ? `${eventTypeEmoji.value} ${eventTypeLabel.value}` : ''
    case 2:
      return ambianceLabel.value ? `${ambianceEmoji.value} ${ambianceLabel.value}` : ''
    case 3:
      return momentCleLabel.value ? `${momentCleEmoji.value} ${momentCleLabel.value}` : ''
    case 4:
      return state.description
        ? state.description.slice(0, 60) + (state.description.length > 60 ? '…' : '')
        : ''
    case 5: {
      const parts = [state.ville, state.nbInvites ? `${state.nbInvites} invités` : ''].filter(
        Boolean,
      )
      return parts.join(' · ')
    }
    default:
      return ''
  }
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// ─── Layout ───────────────────────────────────────────────────────────────────
.demande-layout {
  display: grid;
  grid-template-columns: 1fr 280px;
  min-height: calc(100dvh - $app-header-height);
}

.demande-layout__finalisation {
  grid-column: 1 / -1;
}

// ─── Center ───────────────────────────────────────────────────────────────────
.demande-main {
  display: flex;
  flex-direction: column;
  padding-bottom: $spacing-xxxl;
}

.demande-main__top {
  display: flex;
  justify-content: flex-start;
  padding: $spacing-m $spacing-l;
}

.main-back {
  display: inline-flex;
  align-items: center;
  gap: $spacing-xs;
  padding: $spacing-xs $spacing-s;
  background: none;
  border: none;
  font-size: 0.9rem;
  font-family: inherit;
  color: $text-secondary;
  cursor: pointer;
  transition: color 150ms ease;

  &:hover {
    color: $text-primary;
  }
}

// ─── Accordion ────────────────────────────────────────────────────────────────
.demande-accordion {
  padding: 0 $spacing-xxl;
  max-width: 640px;
  display: flex;
  flex-direction: column;
}

.accordion-block {
  border-bottom: 1px solid $divider-color;

  &:first-child {
    border-top: 1px solid $divider-color;
  }

  &--locked {
    opacity: 0.4;
  }
}

.accordion-block__header {
  width: 100%;
  display: flex;
  align-items: center;
  gap: $spacing-m;
  padding: $spacing-m 0;
  background: none;
  border: none;
  cursor: pointer;
  text-align: left;
  font-family: inherit;

  &:disabled {
    cursor: default;
  }

  &:not(:disabled):hover .accordion-block__edit {
    opacity: 1;
  }
}

.accordion-block__dot {
  width: 1.75rem;
  height: 1.75rem;
  border-radius: 50%;
  border: 2px solid $divider-color;
  background: #fff;
  font-size: 0.75rem;
  font-weight: 700;
  color: $text-secondary;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition:
    background 200ms ease,
    border-color 200ms ease,
    color 200ms ease;

  &--active {
    background: $brand-accent;
    border-color: $brand-accent;
    color: #fff;
  }

  &--done {
    background: $brand-accent;
    border-color: $brand-accent;
    color: #fff;
    font-size: 0.7rem;
  }
}

.accordion-block__header-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.accordion-block__label {
  font-size: 0.95rem;
  font-weight: 600;
  color: $text-primary;

  .accordion-block--locked & {
    font-weight: 500;
    color: $text-secondary;
  }
}

.accordion-block__summary {
  font-size: 0.825rem;
  color: $text-secondary;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.accordion-block__edit {
  font-size: 0.8rem;
  color: $brand-accent;
  font-weight: 500;
  opacity: 0;
  transition: opacity 150ms ease;
  flex-shrink: 0;
}

.accordion-block__body {
  padding-bottom: $spacing-l;
}

// ─── Accordion body transition ────────────────────────────────────────────────
.accordion-body-enter-active,
.accordion-body-leave-active {
  transition:
    opacity 220ms ease,
    transform 220ms ease;
}

.accordion-body-enter-from,
.accordion-body-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

// ─── Right panel ──────────────────────────────────────────────────────────────
.demande-panel {
  padding: $spacing-xl $spacing-l;
  border-left: 1px solid $divider-color;
  position: sticky;
  top: 3rem;
  height: calc(100dvh - $app-header-height);
  overflow-y: auto;

  :global(.recap) {
    margin-top: 0;

    @media (min-width: $breakpoint-desktop) {
      width: 600px;
      align-self: center;
    }
  }
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
