<!--
  Onglet Prestataire de la fiche d'une réservation, le même pour le client et le prestataire : le
  contenu de la fiche publique du prestataire, en lecture seule et sans son hero (celui de la fiche
  réservation suffit) — prestations, présentation, tarifs, témoignages, infos pratiques, FAQ. Le
  prestataire y voit donc sa fiche telle que le client la voit.
  La fiche publique n'existe que pour un prestataire publié : sinon, un message à la place.
  Charge la fiche dans l'état partagé usePrestataire (que lit PrestataireContent) ; la fiche publique
  et la page d'édition rechargent cet état à leur ouverture.
-->
<template>
  <div class="reservation-prestataire">
    <div v-if="loading" class="skeleton">
      <Sk v-for="n in 3" :key="n" height="6rem" radius="1rem" />
    </div>
    <PrestataireContent v-else-if="prestataire" display-mode="preview" without-sidebar />
    <p v-else class="unavailable">{{ $t('reservation.prestataire.unavailable') }}</p>
  </div>
</template>

<script setup lang="ts">
import Sk from '~/components/basics/Sk.vue'
import PrestataireContent from '~/components/prestataire/PrestataireContent.vue'
import { usePrestataire } from '~/data/prestataire/usePrestataire'

const props = defineProps<{
  // Slug du prestataire de la réservation.
  slug: string
}>()

const { prestataire, loading } = usePrestataire(props.slug)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.reservation-prestataire {
  .skeleton {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
  }

  .unavailable {
    margin: 0;
    padding: $spacing-m 0;
    font-size: $font-size-sm;
    font-style: italic;
    color: $text-secondary;
    text-align: center;
  }
}
</style>
