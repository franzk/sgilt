<template>
  <div class="field-row">
    <div class="header">
      <span class="label">{{ label }}</span>
      <div class="actions">
        <SgiltButton
          v-if="!isList || !hasExistingContent"
          variant="secondary"
          :loading="isApplying('REMPLACER')"
          @click="$emit('apply', section, 'REMPLACER')"
        >
          {{ $t('provider.edit.ia.action.copy') }}
        </SgiltButton>
        <template v-else>
          <SgiltButton
            variant="secondary"
            :loading="isApplying('REMPLACER')"
            @click="$emit('apply', section, 'REMPLACER')"
          >
            {{ $t('provider.edit.ia.action.replace') }}
          </SgiltButton>
          <SgiltButton
            variant="secondary"
            :loading="isApplying('AJOUTER')"
            @click="$emit('apply', section, 'AJOUTER')"
          >
            {{ $t('provider.edit.ia.action.add') }}
          </SgiltButton>
        </template>
      </div>
    </div>
    <div class="comparison">
      <div class="column proposal">
        <span class="column-label">{{ $t('provider.edit.ia.proposal-label') }}</span>
        <div class="column-content">
          <slot />
        </div>
      </div>
      <div class="column current">
        <span class="column-label">{{ $t('provider.edit.ia.current-label') }}</span>
        <div class="column-content">
          <slot v-if="hasExistingContent" name="current" />
          <p v-else class="empty">{{ $t('provider.edit.ia.no-current-content') }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import type { FicheIaSection, FicheIaAction } from '~/data/ficheia/dto/FicheIaApplyDto'

const props = defineProps<{
  label: string
  section: FicheIaSection
  isList: boolean
  /** Le champ réel du prestataire a-t-il déjà du contenu ? Contrôle aussi l'affichage de la colonne "Actuel". */
  hasExistingContent: boolean
  /** Clé "SECTION:ACTION" en cours d'application côté parent, pour cibler le bon spinner. */
  applyingKey: string | null
}>()

defineEmits<{ apply: [section: FicheIaSection, action: FicheIaAction] }>()

function isApplying(action: FicheIaAction): boolean {
  return props.applyingKey === `${props.section}:${action}`
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.field-row {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
  padding-top: $spacing-m;
  border-top: 1px solid rgba(0, 0, 0, 0.06);

  &:first-child {
    padding-top: 0;
    border-top: none;
  }

  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $spacing-m;
    flex-wrap: wrap;
  }

  .label {
    font-size: 0.85rem;
    font-weight: 600;
    color: $text-secondary;
  }

  .actions {
    display: flex;
    gap: $spacing-xs;
  }

  .comparison {
    display: flex;
    flex-direction: column;
    gap: $spacing-m;
  }

  .column {
    display: flex;
    flex-direction: column;
    gap: $spacing-xxs;
  }

  .column-label {
    font-size: 0.75rem;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    color: $text-secondary;
    opacity: 0.6;
  }

  .column-content {
    font-size: 0.95rem;
    color: $text-primary;
    line-height: 1.5;

    .empty {
      color: $text-secondary;
      font-style: italic;
      margin: 0;
    }
  }

  .column.current .column-content {
    font-style: italic;
    opacity: 0.6;
  }
}
</style>
