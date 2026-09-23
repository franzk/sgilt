<template>
  <div class="parametres">
    <div class="column">
      <button class="back" type="button" @click="backToBoard">
        <ArrowLeftIcon class="icon" aria-hidden="true" />
        {{ $t('evenement.settings.back') }}
      </button>

      <!-- ── Nom ──────────────────────────────────────────────────────────────── -->
      <section class="section">
        <label class="section-title" for="event-title">
          {{ $t('evenement.settings.field-title') }}
        </label>
        <input
          id="event-title"
          ref="titleInput"
          v-model="draft.title"
          class="title-input"
          :class="{ invalid: titleError }"
          type="text"
          :placeholder="$t('evenement.settings.title-placeholder')"
          @input="titleError = false"
        />
        <p v-if="titleError" class="error">{{ $t('evenement.settings.title-required') }}</p>
      </section>

      <!-- ── Champs du tunnel /organisation ───────────────────────────────────── -->
      <section class="section">
        <h2 class="section-title">{{ $t('organisation.steps.ambiance.eyebrow') }}</h2>
        <OrganisationChoiceStep
          v-model="draft.ambiance"
          v-model:autre-value="draft.ambianceAutre"
          :options="AMBIANCE_OPTIONS"
          :autre-placeholder="$t('organisation.steps.ambiance.autre-placeholder')"
        />
      </section>

      <section class="section">
        <h2 class="section-title">{{ $t('organisation.steps.moment-cle.eyebrow') }}</h2>
        <OrganisationChoiceStep
          v-model="draft.momentCle"
          v-model:autre-value="draft.momentCleAutre"
          :options="MOMENT_CLE_OPTIONS"
          :autre-placeholder="$t('organisation.steps.moment-cle.autre-placeholder')"
        />
      </section>

      <section class="section">
        <h2 class="section-title">{{ $t('organisation.steps.lieu.eyebrow') }}</h2>
        <OrganisationLieuStep v-model:ville="draft.ville" v-model:lieu="draft.lieu" />
      </section>

      <section class="section">
        <h2 class="section-title">{{ $t('organisation.steps.invites.eyebrow') }}</h2>
        <OrganisationInvitesStep v-model="draft.nbInvites" />
      </section>

      <section class="section">
        <h2 class="section-title">{{ $t('organisation.steps.petit-plus.eyebrow') }}</h2>
        <OrganisationPetitPlusStep v-model="draft.description" />
      </section>

      <!-- ── Actions ──────────────────────────────────────────────────────────── -->
      <div class="actions">
        <SgiltButton variant="secondary" @click="backToBoard">
          {{ $t('common.cancel') }}
        </SgiltButton>
        <SgiltButton @click="save">{{ $t('common.save') }}</SgiltButton>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowLeftIcon } from '@remixicons/vue/line'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import { AMBIANCE_OPTIONS, MOMENT_CLE_OPTIONS } from '~/types/demande'

definePageMeta({ layout: 'evenement' })

const { t } = useI18n()
useHead({ title: t('evenement.settings.page-title') })

const { localEvent } = useLocalEvent()

// ── Brouillon ─────────────────────────────────────────────────────────────────
// Les modifications ne touchent localEvent qu'à l'enregistrement : annuler ou quitter
// la page les abandonne.
const draft = reactive({
  title: localEvent.title,
  ambiance: localEvent.ambiance,
  ambianceAutre: localEvent.ambianceAutre,
  momentCle: localEvent.momentCle,
  momentCleAutre: localEvent.momentCleAutre,
  ville: localEvent.ville,
  lieu: localEvent.lieu,
  nbInvites: localEvent.nbInvites,
  description: localEvent.description,
})

// ── Actions ───────────────────────────────────────────────────────────────────
const titleError = ref(false)
const titleInput = ref<HTMLInputElement | null>(null)

function backToBoard() {
  navigateTo('/evenement')
}

function save() {
  const title = draft.title.trim()
  if (!title) {
    titleError.value = true
    // Le champ est en haut du formulaire, loin du bouton : on le ramène à l'écran.
    titleInput.value?.scrollIntoView({ behavior: 'smooth', block: 'center' })
    titleInput.value?.focus({ preventScroll: true })
    return
  }
  Object.assign(localEvent, { ...draft, title })
  backToBoard()
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.parametres {
  display: flex;
  justify-content: center;
  flex: 1;
  background: $surface-white;

  .column {
    display: flex;
    flex-direction: column;
    gap: $spacing-l;
    width: 100%;
    max-width: 30rem;
    padding: $spacing-m $section-padding-x $spacing-xl;

    .back {
      display: inline-flex;
      align-items: center;
      align-self: flex-start;
      gap: $spacing-xxs;
      padding: 0;
      border: none;
      background: none;
      color: $text-primary;
      font-family: inherit;
      font-size: $font-size-sm;
      cursor: pointer;

      .icon {
        width: 1.125rem;
        height: 1.125rem;
      }
    }

    .section {
      display: flex;
      flex-direction: column;
      gap: $spacing-s;

      .section-title {
        margin: 0;
        color: $text-primary;
        font-size: $font-size-xs;
        font-weight: $font-weight-semibold;
        letter-spacing: 0.14em;
        text-transform: uppercase;
      }

      .title-input {
        height: 3.25rem;
        padding: 0 $spacing-m;
        border: 1.5px solid $divider-color;
        border-radius: $radius-lg;
        background: $surface-white;
        color: $brand-primary;
        font-family: 'Cormorant Garamond', serif;
        font-size: 1.25rem;
        font-weight: 600;
        outline: none;
        transition: border-color 180ms ease;

        &:focus {
          border-color: $brand-accent;
        }

        &.invalid {
          border-color: $state-error;
        }

        &::placeholder {
          color: $text-secondary;
          opacity: 0.6;
        }
      }

      .error {
        margin: 0;
        color: $state-error;
        font-size: $font-size-xs;
      }
    }

    .actions {
      display: flex;
      justify-content: flex-end;
      gap: $spacing-s;
    }
  }
}
</style>
