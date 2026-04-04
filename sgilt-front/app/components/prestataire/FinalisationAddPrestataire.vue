<template>
  <SgiltDialog v-model:open="open" :title="$t('tunnel.bottom-sheet.title')" max-width="520px">
    <div class="contact-form">
      <!-- Résumé événement (lecture seule) -->
      <div v-if="flowPayload" class="event-summary">
        <p class="summary-title">{{ flowPayload.nom }}</p>
        <div class="summary-details">
          <span v-if="flowPayload.date"> 📅 {{ formatDate(flowPayload.date) }} </span>
          <span v-if="flowPayload.ville"> 📍 {{ flowPayload.ville }} </span>
          <span v-if="flowPayload.invites"> 👥 {{ flowPayload.invites }} {{ $t('prestataire.finalisation.guests') }} </span>
        </div>
      </div>

      <!-- Prestataire cible -->
      <div class="prestataire-target">
        <span class="target-label">{{ $t('prestataire.finalisation.target-label') }}</span>
        <span class="target-name">{{ prestataireName }}</span>
      </div>

      <!-- Message optionnel -->
      <div class="field">
        <label class="label">{{ $t('prestataire.finalisation.message-label') }} <span class="optional">{{ $t('prestataire.finalisation.optional') }}</span></label>
        <textarea
          v-model="message"
          class="textarea"
          :placeholder="$t('prestataire.finalisation.message-placeholder')"
          rows="4"
        />
      </div>

      <div class="form-actions">
        <SgiltButton variant="secondary" @click="open = false">{{ $t('common.cancel') }}</SgiltButton>
        <SgiltButton :loading="sending" @click="submit">{{ $t('prestataire.finalisation.submit') }}</SgiltButton>
      </div>
    </div>
  </SgiltDialog>
</template>

<script setup lang="ts">
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'

defineProps<{ prestataireName: string }>()

const open = defineModel<boolean>('open', { required: true })

const { flowPayload, onFlowSuccess } = useFlow()

const message = ref('')
const sending = ref(false)

async function submit() {
  sending.value = true
  // TODO: appel API réel
  await new Promise((r) => setTimeout(r, 600))
  sending.value = false
  open.value = false
  onFlowSuccess()
}

</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.contact-form {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: $spacing-m $spacing-l $spacing-l;
}

.event-summary {
  background: $surface-soft;
  border-radius: $radius-md;
  padding: $spacing-s $spacing-m;

  .summary-title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.1rem;
    font-weight: 600;
    color: $brand-primary;
    margin: 0 0 $spacing-xxs;
  }

  .summary-details {
    display: flex;
    flex-wrap: wrap;
    gap: $spacing-xs;
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    color: $text-secondary;
  }
}

.prestataire-target {
  display: flex;
  align-items: center;
  gap: $spacing-xs;
  font-family: 'Inter', sans-serif;

  .target-label {
    font-size: 0.75rem;
    color: $text-secondary;
    text-transform: uppercase;
    letter-spacing: 0.06em;
    font-weight: 600;
  }

  .target-name {
    font-size: 0.95rem;
    font-weight: 600;
    color: $text-primary;
  }
}

.field {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  .label {
    font-family: 'Inter', sans-serif;
    font-size: 0.75rem;
    font-weight: 600;
    letter-spacing: 0.04em;
    text-transform: uppercase;
    color: $text-secondary;

    .optional {
      font-weight: 400;
      text-transform: none;
      letter-spacing: 0;
      opacity: 0.7;
    }
  }

  .textarea {
    width: 100%;
    padding: $spacing-s $spacing-m;
    border: 1px solid $divider-color;
    border-radius: $radius-sm;
    font-family: inherit;
    font-size: 0.95rem;
    color: $text-primary;
    background: $surface-soft;
    resize: vertical;
    outline: none;
    box-sizing: border-box;
    line-height: 1.6;
    transition:
      border-color 150ms ease,
      box-shadow 150ms ease;

    &:focus {
      border-color: $input-focus-border-color;
      box-shadow: $input-focus-box-shadow;
      background: #fff;
    }

    &::placeholder {
      color: $text-secondary;
      opacity: 0.5;
    }
  }
}

.form-actions {
  display: flex;
  gap: $spacing-s;
  justify-content: flex-end;
}
</style>
