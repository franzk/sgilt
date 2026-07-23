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
    <div class="value">
      <slot />
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
  /** Le champ réel du prestataire a-t-il déjà du contenu ? Ignoré si isList est faux. */
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

  .value {
    font-size: 0.95rem;
    color: $text-primary;
    line-height: 1.5;
  }
}
</style>
