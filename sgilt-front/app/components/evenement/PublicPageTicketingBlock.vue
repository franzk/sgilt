<!--
  Contenu de l'onglet Réglages de l'écran Billetterie du board : statut (pas encore ouverte, ouverte,
  fermée), prix d'un billet, mention facultative après le prix (« Tout compris ») et jauge, enregistrés par son propre bouton ; le premier enregistrement
  crée la billetterie. Affiché seulement si le back-office l'a activée. Vérifie avant d'émettre
  `save` : montants valides, ouverture seulement sur une page publiée (une billetterie restée
  ouverte après une dépublication garde son statut), jauge pas sous les places vendues ; les refus
  du back arrivent par `failure`.
-->
<template>
  <section class="ticketing-block">
    <fieldset class="statuses">
      <legend class="label">{{ $t('evenement.page-publique.ticketing.status') }}</legend>
      <label v-for="option in TICKETING_STATUSES" :key="option" class="status" :class="{ selected: status === option }">
        <input v-model="status" type="radio" name="ticketing-status" :value="option" />
        <span class="text">
          <span class="name">{{ $t(`evenement.page-publique.ticketing.statuses.${option}.label`) }}</span>
          <span class="description">{{ $t(`evenement.page-publique.ticketing.statuses.${option}.description`) }}</span>
        </span>
      </label>
    </fieldset>

    <label class="field">
      <span class="label">{{ $t('evenement.page-publique.ticketing.price') }}</span>
      <input v-model.number="unitPriceEuros" type="number" inputmode="decimal" min="0" step="0.01" />
    </label>

    <label class="field">
      <span class="label">{{ $t('evenement.page-publique.ticketing.price-note') }}</span>
      <input v-model="priceNote" type="text" :placeholder="$t('evenement.page-publique.ticketing.price-note-placeholder')" />
      <span class="hint">{{ $t('evenement.page-publique.ticketing.price-note-hint') }}</span>
    </label>

    <label class="field">
      <span class="label">{{ $t('evenement.page-publique.ticketing.capacity') }}</span>
      <input v-model.number="capacity" type="number" inputmode="numeric" min="0" step="1" />
      <span v-if="soldTickets" class="hint">
        {{ $t('evenement.page-publique.ticketing.capacity-hint', soldTickets) }}
      </span>
    </label>

    <p class="hint">{{ $t('evenement.page-publique.ticketing.auto-close') }}</p>

    <div class="actions">
      <SgiltButton :loading="saving" @click="onSave">
        {{ $t('evenement.page-publique.ticketing.save') }}
      </SgiltButton>
      <p v-if="failure" class="failure" role="alert">{{ $t(`evenement.page-publique.errors.${failure}`) }}</p>
      <p v-else-if="saved" class="success" role="status">{{ $t('evenement.page-publique.ticketing.saved') }}</p>
    </div>
  </section>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import type { TicketingSettings } from '~/data/ticketing/domain/OrganizerPublicPage'
import type { TicketingStatus } from '~/data/ticketing/domain/PublicEvent'

const props = defineProps<{
  // Absente tant que l'organisateur ne l'a pas enregistrée.
  ticketing?: TicketingSettings
  // Places vendues (0 sans billetterie) : la jauge ne descend pas en dessous.
  soldTickets: number
  // La page est publiée : sinon, la billetterie ne s'ouvre pas.
  pagePublished: boolean
  saving: boolean
  saved: boolean
  // Motif du dernier échec (suffixe de clé i18n evenement.page-publique.errors.*), ou null.
  failure: string | null
}>()

const emit = defineEmits<{
  save: [settings: TicketingSettings]
  // Refus détecté ici, sans appel au back.
  fail: [reason: string]
}>()

const TICKETING_STATUSES: TicketingStatus[] = ['NOT_OPEN', 'OPEN', 'CLOSED']

// Copie de travail, réinitialisée à chaque billetterie reçue. Prix saisi en euros ; un champ
// nombre vidé vaut '' (v-model.number).
const status = ref<TicketingStatus>('NOT_OPEN')
const unitPriceEuros = ref<number | ''>('')
const priceNote = ref('')
const capacity = ref<number | ''>('')

watch(
  () => props.ticketing,
  (ticketing) => {
    status.value = ticketing?.status ?? 'NOT_OPEN'
    unitPriceEuros.value = ticketing ? ticketing.unitPriceCents / 100 : ''
    priceNote.value = ticketing?.priceNote ?? ''
    capacity.value = ticketing?.capacity ?? ''
  },
  { immediate: true },
)

const isNonNegativeInteger = (value: unknown): value is number =>
  typeof value === 'number' && Number.isInteger(value) && value >= 0

function onSave() {
  const euros = unitPriceEuros.value
  const seats = capacity.value
  if (typeof euros !== 'number' || Number.isNaN(euros) || euros < 0) return emit('fail', 'INVALID_PRICE')
  if (!isNonNegativeInteger(seats)) return emit('fail', 'INVALID_CAPACITY')
  if (seats < props.soldTickets) return emit('fail', 'CAPACITY_BELOW_SOLD')
  const opening = status.value === 'OPEN' && props.ticketing?.status !== 'OPEN'
  if (opening && !props.pagePublished) return emit('fail', 'OPEN_WITHOUT_PUBLISHED_PAGE')
  emit('save', {
    status: status.value,
    unitPriceCents: Math.round(euros * 100),
    priceNote: priceNote.value.trim(),
    capacity: seats,
  })
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.ticketing-block {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;

  .label {
    font-size: $font-size-sm;
    font-weight: 600;
    color: $text-primary;
  }

  .hint {
    margin: 0;
    font-size: 0.8rem;
    color: $text-secondary;
  }

  .statuses {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
    margin: 0;
    padding: 0;
    border: none;

    .label {
      margin-bottom: 0.3rem;
      padding: 0;
    }

    .status {
      display: flex;
      align-items: flex-start;
      gap: $spacing-s;
      padding: $spacing-s;
      border: 1px solid $divider-color;
      border-radius: $radius-md;
      cursor: pointer;

      &.selected {
        border-color: $brand-primary;
        background: $surface-soft;
      }

      input {
        margin: 0.2rem 0 0;
        accent-color: $brand-primary;
      }

      .text {
        display: flex;
        flex-direction: column;
        gap: 0.1rem;

        .name {
          font-size: $font-size-sm;
          font-weight: 600;
          color: $text-primary;
        }

        .description {
          font-size: 0.8rem;
          color: $text-secondary;
        }
      }
    }
  }

  .field {
    display: flex;
    flex-direction: column;
    gap: 0.3rem;

    input {
      width: 100%;
      padding: $spacing-xs $spacing-s;
      border: 1px solid $divider-color;
      border-radius: $radius-md;
      background: $surface-white;
      font-family: inherit;
      font-size: 1rem;
      color: $text-primary;
    }
  }

  .actions {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;

    .failure {
      margin: 0;
      font-size: $font-size-sm;
      color: $state-error;
    }

    .success {
      margin: 0;
      font-size: $font-size-sm;
      color: $state-success;
    }
  }
}
</style>
