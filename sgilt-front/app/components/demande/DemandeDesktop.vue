<template>
  <div class="demande-desktop">
    <template v-if="submitted">
      <DemandeFinalisation
        class="finalisation"
        :prestataire-name="prestataireName"
        @close="closeAndFinish"
      />
    </template>

    <template v-else>
      <main class="demande-main">
        <div class="top">
          <button class="main-back" type="button" @click="showCancelDialog = true">
            <span class="main-back-icon">✕</span>
            {{ $t('tunnel.cancel.label') }}
          </button>
        </div>

        <div class="presta-header">
          <img class="presta-img" :src="props.prestataireImage" :alt="props.prestataireName" />
          <div class="presta-info">
            <span class="presta-name">{{ props.prestataireName }}</span>
            <span v-if="state.date" class="presta-date">{{ formatDate(state.date) }}</span>
          </div>
        </div>

        <div class="demande-accordion">
          <div
            v-for="n in 6"
            :key="n"
            :ref="
              (el) => {
                if (el) blockRefs[n - 1] = el as HTMLElement
              }
            "
            class="accordion-block"
            :class="{
              active: n === etapeActuelle,
              done: n <= maxReached && n !== etapeActuelle,
              locked: n > maxReached,
            }"
          >
            <button
              class="header"
              type="button"
              :disabled="n > maxReached"
              @click="n <= maxReached && n !== etapeActuelle ? goTo(n) : undefined"
            >
              <span
                class="dot"
                :class="{
                  active: n === etapeActuelle,
                  done: n <= maxReached && n !== etapeActuelle,
                }"
              >
                <span v-if="n <= maxReached && n !== etapeActuelle">✓</span>
                <span v-else>{{ n }}</span>
              </span>

              <span class="header-text">
                <span class="label">{{ stepLabels[n - 1] }}</span>
                <span
                  v-if="n <= maxReached && n !== etapeActuelle && stepDoneSummary(n)"
                  class="summary"
                >
                  {{ stepDoneSummary(n) }}
                </span>
              </span>

              <span v-if="n <= maxReached && n !== etapeActuelle" class="edit">{{
                $t('tunnel.desktop.edit')
              }}</span>
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
        <DemandeRecap
          v-else
          :prestataire-name="props.prestataireName"
          :prestataire-image="props.prestataireImage"
          :full-details="etapeActuelle >= 6"
        />
      </aside>
    </template>

    <SgiltConfirmDialog
      v-model:open="showCancelDialog"
      :title="$t('tunnel.cancel.dialog-title')"
      :message="$t('tunnel.cancel.dialog-body')"
      :confirm-label="$t('tunnel.cancel.confirm')"
      :cancel-label="$t('tunnel.cancel.back')"
      @confirm="closeAndFinish"
    />
  </div>
</template>

<script setup lang="ts">
import DemandeRecap from '~/components/demande/DemandeRecap.vue'
import DemandeCommentCaMarche from '~/components/demande/DemandeCommentCaMarche.vue'
import SgiltConfirmDialog from '~/components/basics/dialogs/SgiltConfirmDialog.vue'
import DemandeEtape1 from '~/components/demande/DemandeEtape1.vue'
import DemandeEtape2 from '~/components/demande/DemandeEtape2.vue'
import DemandeEtape3 from '~/components/demande/DemandeEtape3.vue'
import DemandeEtape4 from '~/components/demande/DemandeEtape4.vue'
import DemandeEtape5Desktop from '~/components/demande/DemandeEtape5Desktop.vue'
import DemandeEtape6 from '~/components/demande/DemandeEtape6.vue'
import DemandeFinalisation from '~/components/demande/DemandeFinalisation.vue'
import { useDemande } from '~/composables/useDemande'

const props = defineProps<{ slug: string; prestataireName: string; prestataireImage: string }>()

const router = useRouter()
const { t } = useI18n()

const {
  etapeActuelle,
  submitted,
  state,
  goTo,
  reset,
  eventTypeLabel,
  eventTypeEmoji,
  ambianceLabel,
  ambianceEmoji,
  momentCleLabel,
  momentCleEmoji,
} = useDemande()

const blockRefs = ref<HTMLElement[]>([])
const showCancelDialog = ref(false)

const maxReached = ref(etapeActuelle.value)
watch(etapeActuelle, (n) => {
  if (n > maxReached.value) maxReached.value = n
})

watch(etapeActuelle, (n) => {
  setTimeout(() => {
    const block = blockRefs.value[n - 1]
    if (!block) return
    const rect = block.getBoundingClientRect()
    if (rect.bottom > window.innerHeight) {
      block.scrollIntoView({ behavior: 'smooth', block: 'end' })
    }
  }, 250)
})

const closeAndFinish = () => {
  reset()
  router.back()
}

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
      const parts = [state.ville, state.lieu, state.nbInvites ?? ''].filter(Boolean)
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
.demande-desktop {
  min-height: calc(100dvh - $app-header-height);
  display: grid;
  grid-template-columns: 1fr 320px;

  .finalisation {
    grid-column: 1 / -1;
  }
}

// ─── Colonne principale ───────────────────────────────────────────────────────
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

  .main-back-icon {
    font-size: 0.75rem;
    font-weight: 700;
    line-height: 1;
  }
}

// ─── Card prestataire ─────────────────────────────────────────────────────────
.presta-header {
  display: flex;
  align-items: center;
  gap: $spacing-m;
  margin: 0 $spacing-l $spacing-m;
  padding: $spacing-m $spacing-l;
  background: #fff;
  border-radius: $radius-lg;
  box-shadow:
    0 2px 6px $shadow-s,
    0 8px 24px $shadow-m;
}

.presta-img {
  width: 3.5rem;
  height: 3.5rem;
  border-radius: $radius-md;
  object-fit: cover;
  flex-shrink: 0;
}

.presta-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.presta-name {
  font-size: 1rem;
  font-weight: 600;
  color: $text-primary;
}

.presta-date {
  font-size: 0.85rem;
  color: $text-secondary;
}

// ─── Accordion ────────────────────────────────────────────────────────────────
.demande-accordion {
  padding: 0 $spacing-l;
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

// ─── Panneau droit ────────────────────────────────────────────────────────────
.demande-panel {
  padding: $spacing-l $spacing-m;
  border-left: 1px solid $divider-color;
  position: sticky;
  top: 0;
  height: 100dvh;
  overflow-y: auto;
}
</style>
