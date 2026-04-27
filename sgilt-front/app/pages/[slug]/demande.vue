<template>
  <div v-if="prestataire" class="demande-layout">

    <!-- ── DESKTOP ──────────────────────────────────────────────────────────── -->
    <template v-if="!isMobile">
      <template v-if="submitted">
        <DemandeFinalisation
          class="finalisation"
          :prestataire-name="prestataire.name"
          @close="closeAndFinish"
        />
      </template>

      <template v-else>
        <main class="demande-main">
          <div class="top">
            <button class="main-back" type="button" @click="navigateTo(`/${slug}`)">
              {{ $t('tunnel.desktop.back') }}
            </button>
          </div>

          <div class="demande-accordion">
            <div
              v-for="n in 6"
              :key="n"
              class="accordion-block"
              :class="{
                active: n === etapeActuelle,
                done: n < etapeActuelle,
                locked: n > etapeActuelle,
              }"
            >
              <button
                class="header"
                type="button"
                :disabled="n > etapeActuelle"
                @click="n < etapeActuelle ? goTo(n) : undefined"
              >
                <span
                  class="dot"
                  :class="{
                    active: n === etapeActuelle,
                    done: n < etapeActuelle,
                  }"
                >
                  <span v-if="n < etapeActuelle">✓</span>
                  <span v-else>{{ n }}</span>
                </span>

                <span class="header-text">
                  <span class="label">{{ stepLabels[n - 1] }}</span>
                  <span v-if="n < etapeActuelle && stepDoneSummary(n)" class="summary">
                    {{ stepDoneSummary(n) }}
                  </span>
                </span>

                <span v-if="n < etapeActuelle" class="edit">{{ $t('tunnel.desktop.edit') }}</span>
              </button>

              <Transition name="accordion-body">
                <div v-if="n === etapeActuelle" class="body">
                  <DemandeEtape1 v-if="n === 1" />
                  <DemandeEtape2 v-else-if="n === 2" />
                  <DemandeEtape3 v-else-if="n === 3" />
                  <DemandeEtape4 v-else-if="n === 4" />
                  <DemandeEtape5Desktop v-else-if="n === 5" />
                  <DemandeEtape6 v-else-if="n === 6" />
                </div>
              </Transition>
            </div>
          </div>
        </main>

        <aside class="demande-panel">
          <DemandeCommentCaMarche v-if="etapeActuelle === 1" />
          <DemandeRecap v-else />
        </aside>
      </template>
    </template>

    <!-- ── MOBILE : page dédiée ─────────────────────────────────────────────── -->
    <div v-else class="mobile-tunnel">
      <DemandeSheetHeader
        :etape="etapeActuelle"
        :submitted="submitted"
        @back="back"
        @close="closeMobile"
        @go-to="goTo"
      />

      <div ref="bodyRef" class="mobile-body">
        <div v-if="submitted">
          <DemandeFinalisation :prestataire-name="prestataire.name" @close="closeMobile" />
        </div>

        <template v-else>
          <Transition :name="direction === 'forward' ? 'slide-forward' : 'slide-back'" mode="out-in">
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

      <div
        v-if="!submitted"
        class="mobile-footer"
        :class="{ 'actions-etape4': etapeActuelle === 4, 'actions-etape5': etapeActuelle === 5 }"
      >
        <template v-if="etapeActuelle === 4">
          <SgiltButton @click="next">{{ $t('tunnel.footer.continue') }}</SgiltButton>
          <SgiltButton variant="tertiary" @click="next">{{ $t('tunnel.footer.skip') }}</SgiltButton>
        </template>
        <template v-if="etapeActuelle === 5">
          <SgiltButton :disabled="!step5Valid" @click="next">{{ $t('tunnel.footer.continue') }}</SgiltButton>
        </template>
      </div>
    </div>
  </div>

  <div v-else-if="!loading" class="not-found">
    <p>{{ $t('provider.not-found') }}</p>
    <NuxtLink to="/search">{{ $t('provider.back-to-search') }}</NuxtLink>
  </div>
</template>

<script setup lang="ts">
import DemandeRecap from '~/components/demande/DemandeRecap.vue'
import DemandeCommentCaMarche from '~/components/demande/DemandeCommentCaMarche.vue'
import DemandeEtape1 from '~/components/demande/DemandeEtape1.vue'
import DemandeEtape2 from '~/components/demande/DemandeEtape2.vue'
import DemandeEtape3 from '~/components/demande/DemandeEtape3.vue'
import DemandeEtape4 from '~/components/demande/DemandeEtape4.vue'
import DemandeEtape5 from '~/components/demande/DemandeEtape5.vue'
import DemandeEtape5Desktop from '~/components/demande/DemandeEtape5Desktop.vue'
import DemandeEtape6 from '~/components/demande/DemandeEtape6.vue'
import DemandeFinalisation from '~/components/demande/DemandeFinalisation.vue'
import DemandeSheetHeader from '~/components/demande/DemandeSheetHeader.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import { useDemande } from '~/composables/useDemande'
import { fetchPrestataireBySlug } from '~/data/prestataire/service/prestataireService'
import type { PrestataireDetail } from '~/data/prestataire/domain/prestataire'

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const slug = route.params.slug as string

const prestataire = ref<PrestataireDetail | null>(null)
const loading = ref(true)

const { isMobile } = useDevice()

onMounted(async () => {
  prestataire.value = await fetchPrestataireBySlug(slug)
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
  state,
  next,
  back,
  goTo,
  eventTypeLabel,
  eventTypeEmoji,
  ambianceLabel,
  ambianceEmoji,
  momentCleLabel,
  momentCleEmoji,
  reset,
} = useDemande()

onMounted(() => {
  if (etapeActuelle.value === 1 && state.eventType && state.eventType.toUpperCase() !== 'AUTRE') {
    goTo(2)
  }
})

const closeAndFinish = () => {
  reset()
  navigateTo(`/${slug}`)
}

const closeMobile = () => {
  if (submitted.value) reset()
  router.back()
}

// ── Mobile ────────────────────────────────────────────────────────────────────
const step5Valid = computed(() => !!state.date && !!state.ville.trim() && !!state.nbInvites.trim())

const bodyRef = ref<HTMLElement | null>(null)
watch(etapeActuelle, () => nextTick(() => bodyRef.value?.scrollTo({ top: 0, behavior: 'smooth' })))

// ── Desktop accordion ─────────────────────────────────────────────────────────
const stepLabels = computed(() => [
  t('tunnel.steps.labels.event-type'),
  t('tunnel.steps.labels.ambiance'),
  t('tunnel.steps.labels.moment-cle'),
  t('tunnel.steps.labels.description'),
  t('tunnel.steps.labels.details'),
  t('tunnel.steps.labels.coordonnees'),
])

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
      const parts = [
        state.ville,
        state.nbInvites ? t('tunnel.recap.guests', { n: state.nbInvites }) : '',
      ].filter(Boolean)
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
  min-height: calc(100dvh - $app-header-height);

  @media (min-width: $breakpoint-desktop) {
    display: grid;
    grid-template-columns: 1fr 280px;
  }

  .finalisation {
    grid-column: 1 / -1;
  }
}

// ─── Desktop center ───────────────────────────────────────────────────────────
.demande-main {
  display: flex;
  flex-direction: column;
  padding-bottom: $spacing-xxxl;

  .top {
    display: flex;
    justify-content: flex-start;
    padding: $spacing-m $spacing-l;
  }
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

  &.locked {
    opacity: 0.4;
  }

  .header {
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

    &:not(:disabled):hover .edit {
      opacity: 1;
    }
  }

  .dot {
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

    &.active {
      background: $brand-accent;
      border-color: $brand-accent;
      color: #fff;
    }

    &.done {
      background: $brand-accent;
      border-color: $brand-accent;
      color: #fff;
      font-size: 0.7rem;
    }
  }

  .header-text {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 2px;
    min-width: 0;
  }

  .label {
    font-size: 0.95rem;
    font-weight: 600;
    color: $text-primary;

    .locked & {
      font-weight: 500;
      color: $text-secondary;
    }
  }

  .summary {
    font-size: 0.825rem;
    color: $text-secondary;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .edit {
    font-size: 0.8rem;
    color: $brand-accent;
    font-weight: 500;
    opacity: 0;
    transition: opacity 150ms ease;
    flex-shrink: 0;
  }

  .body {
    padding-bottom: $spacing-l;
  }
}

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

// ─── Desktop right panel ──────────────────────────────────────────────────────
.demande-panel {
  padding: $spacing-xl $spacing-l;
  border-left: 1px solid $divider-color;
  position: sticky;
  top: 0;
  height: calc(100dvh - $app-header-height);
  overflow-y: auto;

  :global(.recap) {
    margin-top: 0;
  }
}

// ─── Mobile page ──────────────────────────────────────────────────────────────
.mobile-tunnel {
  display: flex;
  flex-direction: column;
  height: calc(100dvh - $app-header-height);

  .mobile-body {
    flex: 1;
    overflow-y: auto;
    padding: $spacing-m;
    overscroll-behavior: contain;
    position: relative;
  }

  .mobile-footer {
    flex-shrink: 0;
    padding: $spacing-s $spacing-m $spacing-m;
    background: #fff;
    border-top: 1px solid $divider-color;

    &.actions-etape5 {
      :deep(button) {
        width: 100%;
      }
    }

    &.actions-etape4 {
      display: flex;
      flex-direction: row;
      gap: $spacing-s;

      :deep(button) {
        flex: 1;
      }
    }
  }
}

// ─── Slide transitions (mobile) ───────────────────────────────────────────────
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
