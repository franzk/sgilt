<!--
  Résultat du scan d'un e-billet (onglet Scan), en grand par-dessus la caméra figée :
    vert   VALIDATED      Entrée validée · Commande de X · N places
    orange ALREADY_USED   Déjà utilisé · Commande de X · N places · entrée à HHhMM
    rouge  CANCELLED / NOT_PAID (avec l'acheteur : commande de cet événement), UNKNOWN (sans rien),
           ou vérification impossible (failed : réseau, serveur).
  Le nom est celui de l'acheteur, pas forcément du porteur : pour l'accueil, pas un contrôle
  d'identité. « Scanner le suivant » émet next ; sur le vert, l'écran passe aussi au suivant tout seul.
-->
<template>
  <div class="scan-result" :class="tone" role="status">
    <component :is="TONE_ICONS[tone]" class="icon" aria-hidden="true" />

    <p class="title">
      {{ failed ? $t('evenement.scan.result.FAILED') : $t(`evenement.scan.result.${result?.status}`) }}
    </p>

    <template v-if="!failed && result?.commande">
      <p class="buyer">
        {{
          $t('evenement.scan.result.buyer', {
            name: `${result.commande.buyerFirstName} ${result.commande.buyerLastName}`,
          })
        }}
      </p>
      <p v-if="showsPlaces" class="places">
        {{ $t('evenement.scan.result.places', result.commande.quantity) }}
      </p>
      <p v-if="result.status === 'ALREADY_USED' && result.commande.scannedAt" class="detail">
        {{ $t('evenement.scan.result.entered-at', { time: formatTimeOfDay(result.commande.scannedAt) }) }}
      </p>
    </template>

    <p v-else-if="failed" class="detail">{{ $t('evenement.scan.result.failed-detail') }}</p>
    <p v-else-if="result?.status === 'UNKNOWN'" class="detail">{{ $t('evenement.scan.result.unknown-detail') }}</p>

    <button type="button" class="next" @click="$emit('next')">{{ $t('evenement.scan.result.next') }}</button>
  </div>
</template>

<script setup lang="ts">
import { CheckboxCircleIcon, CloseCircleIcon, ErrorWarningIcon } from '@remixicons/vue/line'
import type { Component } from 'vue'
import type { ScanResult } from '~/data/ticketing/domain/Scan'
import { formatTimeOfDay } from '~/utils/dateUtils'

type Tone = 'success' | 'warning' | 'danger'

const TONE_ICONS: Record<Tone, Component> = {
  success: CheckboxCircleIcon,
  warning: ErrorWarningIcon,
  danger: CloseCircleIcon,
}

const props = defineProps<{
  // Null quand la vérification a échoué (failed).
  result: ScanResult | null
  failed: boolean
}>()

defineEmits<{
  next: []
}>()

const tone = computed<Tone>(() => {
  if (props.failed) return 'danger'
  if (props.result?.status === 'VALIDATED') return 'success'
  if (props.result?.status === 'ALREADY_USED') return 'warning'
  return 'danger'
})

// Le nombre de places ne compte que pour une commande qui fait (ou a fait) entrer.
const showsPlaces = computed(
  () => props.result?.status === 'VALIDATED' || props.result?.status === 'ALREADY_USED',
)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.scan-result {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: $spacing-xs;
  padding: $spacing-l $spacing-m;
  border-radius: $radius-lg;
  color: $surface-white;
  text-align: center;

  &.success {
    background: $state-success;
  }

  &.warning {
    background: $state-warning;
  }

  &.danger {
    background: $state-danger;
  }

  .icon {
    width: 3.5rem;
    height: 3.5rem;
  }

  .title {
    margin: 0;
    font-size: 1.5rem;
    font-weight: 700;
  }

  .buyer {
    margin: 0;
    font-size: 1.125rem;
    font-weight: 600;
    overflow-wrap: anywhere;
  }

  .places {
    margin: 0;
    font-size: 1.125rem;
  }

  .detail {
    margin: 0;
    font-size: $font-size-sm;
  }

  .next {
    margin-top: $spacing-s;
    padding: $spacing-s $spacing-l;
    border: 2px solid $surface-white;
    border-radius: 9999px;
    background: transparent;
    color: $surface-white;
    font-family: 'Inter', sans-serif;
    font-size: $font-size-sm;
    font-weight: 600;
    cursor: pointer;
  }
}
</style>
