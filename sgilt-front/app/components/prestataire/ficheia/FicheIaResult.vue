<template>
  <div class="ia-result">
    <SgiltButton class="bulk-btn" @click="overwriteConfirmOpen = true">
      {{ $t('provider.edit.ia.bulk-overwrite') }}
    </SgiltButton>
    <p v-if="applyError" class="error">{{ $t('provider.edit.ia.apply-error') }}</p>

    <SgiltConfirmDialog
      v-model:open="overwriteConfirmOpen"
      :title="$t('provider.edit.ia.bulk-overwrite-dialog.title')"
      :message="$t('provider.edit.ia.bulk-overwrite-dialog.message')"
      :confirm-label="$t('provider.edit.ia.bulk-overwrite-dialog.confirm')"
      :cancel-label="$t('provider.edit.ia.bulk-overwrite-dialog.cancel')"
      :confirm-loading="applying === 'ALL'"
      destructive
      max-width="480px"
      @confirm="applyAll"
    />

    <FicheIaApplyDialog
      v-model:open="applyDialogOpen"
      :is-list="dialogIsList"
      @confirm="onDialogConfirm"
      @replace="onDialogReplace"
      @add="onDialogAdd"
    />

    <div v-if="result" class="preview-layout">
      <div class="preview-main">
        <!-- BASELINE : au-dessus du bloc aperçu, pas dans le flux standard des champs de contenu -->
        <section v-if="result.baseline" class="section baseline-section">
          <div class="section-header">
            <h2 class="title">{{ $t('provider.edit.ia.field.baseline') }}</h2>
            <SgiltButton variant="secondary" @click="openApplyDialog('BASELINE')">
              {{ $t('provider.edit.ia.apply-field') }}
            </SgiltButton>
          </div>
          <p class="baseline-caption">{{ $t('provider.edit.ia.baseline-caption') }}</p>
          <p class="baseline-text">{{ result.baseline }}</p>
        </section>

        <FicheIaPreview :content="result" @apply="openApplyDialog" />
      </div>

      <!-- TARIFS : sidebar sticky sur desktop, comme dans l'onglet Aperçu -->
      <aside v-if="result.budget" class="preview-sidebar">
        <section class="section budget-section">
          <div class="section-header">
            <h2 class="title">{{ $t('provider.details.rates') }}</h2>
            <SgiltButton variant="secondary" @click="openApplyDialog('BUDGET')">
              {{ $t('provider.edit.ia.apply-field') }}
            </SgiltButton>
          </div>
          <p class="budget-text">{{ result.budget }}</p>
        </section>
      </aside>
    </div>

    <!-- APERÇU CARD RECHERCHE : même card que la fiche réelle, légende remplacée par celle générée -->
    <section v-if="cardPreview" class="section card-preview-section">
      <div class="section-header">
        <h2 class="title">{{ $t('provider.edit.ia.card-preview-title') }}</h2>
      </div>
      <PrestataireCard :provider="cardPreview" selectable class="card-preview" />
      <SgiltButton
        class="card-preview-apply-btn"
        variant="secondary"
        @click="openApplyDialog('SHORT_DESCRIPTION')"
      >
        {{ $t('provider.edit.ia.apply-field') }}
      </SgiltButton>
    </section>

    <div class="relaunch">
      <button
        v-if="triesLeft && triesLeft > 0"
        type="button"
        class="link"
        @click="$emit('relaunch')"
      >
        {{ $t('provider.edit.ia.relaunch-link', { count: triesLeft }, triesLeft ?? 0) }}
      </button>
      <p v-else class="quota-exhausted">{{ $t('provider.edit.ia.error-quota') }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import SgiltConfirmDialog from '~/components/basics/dialogs/SgiltConfirmDialog.vue'
import FicheIaPreview from './FicheIaPreview.vue'
import FicheIaApplyDialog from './FicheIaApplyDialog.vue'
import PrestataireCard from '~/components/cards/PrestataireCard.vue'
import { mapPrestataireDetailToCard } from '~/data/prestataire/mapper/prestataireMapper'
import type { PrestataireCardDetail } from '~/data/prestataire/domain/PrestataireCardDetail'
import { FICHE_IA_LIST_SECTIONS, type FicheIaSection } from '~/data/ficheia/dto/FicheIaApplyDto'

defineEmits<{ relaunch: [] }>()

const { result, triesLeft, applying, applyError, applyOne, applyAll } = useFicheIa()
const { prestataire } = usePrestataire()

const overwriteConfirmOpen = ref(false)

/** Card réelle (photo, nom, catégorie), légende remplacée par celle proposée par l'IA. */
const cardPreview = computed<PrestataireCardDetail | undefined>(() => {
  if (!prestataire.value || !result.value?.shortDescription) return undefined
  return {
    ...mapPrestataireDetailToCard(prestataire.value),
    shortDescription: result.value.shortDescription,
  }
})

// ── Modale d'application par champ ────────────────────────────────────────────
const applyDialogOpen = ref(false)
const pendingSection = ref<FicheIaSection | null>(null)
const dialogIsList = computed(() =>
  pendingSection.value ? FICHE_IA_LIST_SECTIONS.includes(pendingSection.value) : false,
)

function openApplyDialog(section: FicheIaSection): void {
  pendingSection.value = section
  applyDialogOpen.value = true
}

function onDialogConfirm(): void {
  if (pendingSection.value) applyOne(pendingSection.value, 'REMPLACER')
}
function onDialogReplace(): void {
  if (pendingSection.value) applyOne(pendingSection.value, 'REMPLACER')
}
function onDialogAdd(): void {
  if (pendingSection.value) applyOne(pendingSection.value, 'AJOUTER')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.ia-result {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}

.relaunch {
  .link {
    background: none;
    border: none;
    padding: 0;
    font-family: inherit;
    font-size: $font-size-sm;
    color: $color-primary;
    text-decoration: underline;
    cursor: pointer;
  }

  .quota-exhausted {
    font-size: $font-size-sm;
    color: $text-secondary;
    margin: 0;
  }
}

.bulk-btn {
  align-self: flex-start;
}

.error {
  font-size: $font-size-sm;
  color: $state-error;
  margin: 0;
}

// ── Layout deux colonnes (main + sidebar tarifs), comme l'onglet Aperçu ────────
.preview-layout {
  display: flex;
  flex-direction: column;
  gap: 2.5rem;

  @media (min-width: $breakpoint-desktop) {
    display: grid;
    grid-template-columns: 1fr 360px;
    grid-template-areas: 'main sidebar';
    gap: 3rem;
    align-items: start;
  }
}

.preview-main {
  display: flex;
  flex-direction: column;
  gap: 2.5rem;
  min-width: 0;

  @media (min-width: $breakpoint-desktop) {
    grid-area: main;
  }
}

.preview-sidebar {
  @media (min-width: $breakpoint-desktop) {
    grid-area: sidebar;
    position: sticky;
    top: 5rem;
    padding: 1.5rem;
    background: #fff;
    border: 1px solid rgba(0, 0, 0, 0.08);
    border-radius: $radius-md;
    box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  }
}

.section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-m;
  flex-wrap: wrap;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);

  .title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.4rem;
    font-weight: 600;
    color: $color-primary;
    margin: 0;
  }
}

.baseline-section {
  .baseline-caption {
    font-size: 0.8rem;
    font-style: italic;
    color: $text-secondary;
    opacity: 0.7;
    margin: 0;
  }

  .baseline-text {
    font-size: 1rem;
    color: $text-primary;
    margin: 0;
  }
}

.budget-text {
  font-size: 0.95rem;
  color: $text-secondary;
  line-height: 1.6;
  margin: 0;
  white-space: pre-line;
}

.card-preview-section {
  .card-preview {
    align-self: center;
    width: 100%;
    max-width: 220px;
  }
}

.card-preview-apply-btn {
  align-self: center;
}
</style>
