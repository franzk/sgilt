<template>
  <div class="organisation">
    <div class="column">
      <div class="top-row">
        <button v-if="step > 1" class="back" type="button" @click="goBack">
          <ArrowLeftIcon class="icon" aria-hidden="true" />
          {{ $t('organisation.back') }}
        </button>
        <button class="skip-tunnel" type="button" @click="finish">
          {{ $t('organisation.skip-tunnel') }}
        </button>
      </div>

      <div class="progress">
        <progress
          class="bar"
          :max="STEP_COUNT"
          :value="step"
          :aria-label="$t('organisation.progress-label')"
        />
        <span class="counter">{{ step }}/{{ STEP_COUNT }}</span>
      </div>

      <Transition name="step" mode="out-in">
        <div :key="step" class="step">
          <p class="eyebrow">{{ $t(`organisation.steps.${stepKey}.eyebrow`) }}</p>
          <h1 class="title">{{ title }}</h1>
          <p v-if="subtitle" class="subtitle">{{ subtitle }}</p>

          <div class="body">
            <OrganisationChoiceStep
              v-if="step === 1"
              :options="AMBIANCE_OPTIONS"
              :model-value="localEvent.ambiance"
              :autre-value="localEvent.ambianceAutre"
              :autre-placeholder="$t('organisation.steps.ambiance.autre-placeholder')"
              @update:model-value="localEvent.ambiance = $event"
              @update:autre-value="localEvent.ambianceAutre = $event"
              @next="next"
            />
            <OrganisationChoiceStep
              v-else-if="step === 2"
              :options="MOMENT_CLE_OPTIONS"
              :model-value="localEvent.momentCle"
              :autre-value="localEvent.momentCleAutre"
              :autre-placeholder="$t('organisation.steps.moment-cle.autre-placeholder')"
              @update:model-value="localEvent.momentCle = $event"
              @update:autre-value="localEvent.momentCleAutre = $event"
              @next="next"
            />
            <OrganisationLieuStep v-else-if="step === 3" />
            <OrganisationInvitesStep v-else-if="step === 4" />
            <OrganisationPetitPlusStep v-else />
          </div>

          <button v-if="hasCta" class="cta" type="button" @click="onCta">
            {{ $t(`organisation.steps.${stepKey}.cta`) }}
            <ArrowRightIcon class="icon" aria-hidden="true" />
          </button>

          <button v-if="step < STEP_COUNT" class="skip-question" type="button" @click="skipQuestion">
            {{ $t('organisation.skip-question') }}
            <ArrowRightSIcon class="icon" aria-hidden="true" />
          </button>
        </div>
      </Transition>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowLeftIcon, ArrowRightIcon, ArrowRightSIcon } from '@remixicons/vue/line'
import { AMBIANCE_OPTIONS, MOMENT_CLE_OPTIONS } from '~/types/demande'

useHead({ title: 'Votre événement - Sgilt' })

const STEP_COUNT = 5
const STEP_KEYS = ['ambiance', 'moment-cle', 'lieu', 'invites', 'petit-plus'] as const
// Les 2 premiers écrans avancent au clic sur une carte ; les suivants ont un CTA.
const FIRST_STEP_WITH_CTA = 3

const route = useRoute()
const { t } = useI18n()
const { localEvent } = useLocalEvent()

// Accès sans type choisi (arrivée directe sur l'URL) : renvoie au choix du type.
onMounted(() => {
  if (!localEvent.eventType) {
    navigateTo('/fete')
  }
})

// L'étape courante vit dans l'URL (?step=) : refresh et retour navigateur fonctionnent
// sans état supplémentaire. Toute valeur invalide retombe sur la première étape.
const step = computed(() => {
  const raw = typeof route.query.step === 'string' ? Number(route.query.step) : 1
  return Number.isInteger(raw) && raw >= 1 && raw <= STEP_COUNT ? raw : 1
})
const stepKey = computed(() => STEP_KEYS[step.value - 1] ?? STEP_KEYS[0])

const title = computed(() =>
  stepKey.value === 'moment-cle' && localEvent.eventType === 'soiree_privee'
    ? t('organisation.steps.moment-cle.title-soiree')
    : t(`organisation.steps.${stepKey.value}.title`),
)

// Certains écrans n'ont pas de sous-titre : la clé i18n est alors vide.
const subtitle = computed(() => t(`organisation.steps.${stepKey.value}.subtitle`))

const hasCta = computed(() => step.value >= FIRST_STEP_WITH_CTA)

async function goToStep(target: number) {
  await navigateTo({ path: route.path, query: { step: target } })
  window.scrollTo({ top: 0 })
}

function next() {
  goToStep(step.value + 1)
}

function goBack() {
  goToStep(step.value - 1)
}

function onCta() {
  if (step.value === STEP_COUNT) {
    finish()
  } else {
    next()
  }
}

// "Je ne sais pas encore" : efface la réponse courante (y compris une réponse donnée
// avant un retour arrière) puis passe à l'écran suivant.
function skipQuestion() {
  switch (step.value) {
    case 1:
      localEvent.ambiance = null
      localEvent.ambianceAutre = ''
      break
    case 2:
      localEvent.momentCle = null
      localEvent.momentCleAutre = ''
      break
    case 3:
      localEvent.ville = ''
      localEvent.lieu = ''
      break
    case 4:
      localEvent.nbInvites = ''
      break
  }
  next()
}

// Sortie du stepper (CTA final ou skip de tunnel) avec les valeurs déjà renseignées.
function finish() {
  // TODO(brief séparé) : création réelle de l'événement en base pas encore livrée,
  // la vue d'ensemble travaille sur un état purement local.
  navigateTo('/evenement')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.organisation {
  display: flex;
  justify-content: center;
  min-height: $viewport-below-header;
  background: $surface-white;

  .column {
    width: 100%;
    max-width: 30rem;
    padding: $spacing-m $section-padding-x $spacing-xl;

    .top-row {
      display: flex;
      align-items: flex-start;
      justify-content: space-between;
      gap: $spacing-m;
      margin-bottom: $spacing-m;

      .back,
      .skip-tunnel {
        padding: 0;
        border: none;
        background: none;
        color: $text-primary;
        font-family: inherit;
        font-size: $font-size-xs;
        cursor: pointer;
      }

      .back {
        display: inline-flex;
        align-items: center;
        gap: $spacing-xxs;
        font-size: $font-size-sm;

        .icon {
          width: 1.125rem;
          height: 1.125rem;
        }
      }

      .skip-tunnel {
        max-width: 11rem;
        margin-left: auto;
        text-align: right;
        text-decoration: underline;
      }
    }

    .progress {
      display: flex;
      align-items: center;
      gap: $spacing-m;
      margin-bottom: $spacing-l;

      // <progress> natif : les pseudo-éléments sont propres à chaque moteur de rendu,
      // ils ne peuvent pas être regroupés dans un même sélecteur.
      .bar {
        flex: 1;
        height: 0.5rem;
        border: none;
        border-radius: 9999px;
        background: $brand-subtle;
        overflow: hidden;
        appearance: none;

        &::-webkit-progress-bar {
          background: $brand-subtle;
        }

        &::-webkit-progress-value {
          border-radius: 9999px;
          background: $brand-accent;
          transition: width 300ms ease;
        }

        &::-moz-progress-bar {
          border-radius: 9999px;
          background: $brand-accent;
        }
      }

      .counter {
        color: $text-secondary;
        font-size: $font-size-sm;
      }
    }

    .step {
      display: flex;
      flex-direction: column;

      .eyebrow {
        margin: 0;
        color: $text-primary;
        font-size: $font-size-xs;
        font-weight: $font-weight-semibold;
        letter-spacing: 0.14em;
        text-transform: uppercase;
      }

      .title {
        margin: $spacing-xs 0 0;
        color: $text-primary;
        font-family: 'Cormorant Garamond', serif;
        font-size: clamp(1.75rem, 7vw, 2.25rem);
        font-weight: $font-weight-medium;
        line-height: 1.2;
      }

      .subtitle {
        margin: $spacing-xs 0 0;
        color: $text-secondary;
        font-size: $font-size-md;
        line-height: $line-height-normal;
      }

      .body {
        margin-top: $spacing-l;
      }

      .cta {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        gap: $spacing-xs;
        width: 100%;
        height: 3.25rem;
        margin-top: $spacing-l;
        border: none;
        border-radius: 9999px;
        background: $brand-accent;
        color: $brand-primary;
        font-family: inherit;
        font-size: $font-size-md;
        font-weight: $font-weight-bold;
        cursor: pointer;
        box-shadow: 0 0.25rem 0.625rem rgba($brand-primary, 0.18);
        transition:
          transform 160ms ease,
          box-shadow 160ms ease;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 0.375rem 1rem rgba($brand-primary, 0.22);
        }

        &:focus-visible {
          outline: 3px solid $brand-primary;
          outline-offset: 4px;
        }

        .icon {
          width: 1.25rem;
          height: 1.25rem;
        }
      }

      .skip-question {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        gap: $spacing-xxs;
        align-self: center;
        margin-top: $spacing-l;
        padding: 0;
        border: none;
        background: none;
        color: $text-primary;
        font-family: inherit;
        font-size: $font-size-sm;
        text-decoration: underline;
        cursor: pointer;

        .icon {
          width: 1.125rem;
          height: 1.125rem;
        }
      }
    }
  }
}

.step-enter-active,
.step-leave-active {
  transition:
    opacity 160ms ease,
    transform 160ms ease;
}

.step-enter-from {
  opacity: 0;
  transform: translateX(1rem);
}

.step-leave-to {
  opacity: 0;
  transform: translateX(-1rem);
}
</style>
