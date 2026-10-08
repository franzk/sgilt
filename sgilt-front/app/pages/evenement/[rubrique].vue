<template>
  <!-- Pas de slot #reservations : l'événement local n'a jamais de réservation (le parcours public
       s'arrête à la demande), la section garde son message « aucune réservation ». -->
  <EventRubriqueDetail
    v-if="rubriqueKey"
    :rubrique-key="rubriqueKey"
    @back="navigateTo('/evenement')"
    @search="navigateTo('/search')"
  />
</template>

<script setup lang="ts">
import EventRubriqueDetail from '~/components/evenement/EventRubriqueDetail.vue'

definePageMeta({ layout: 'evenement' })

const route = useRoute()
const { localEvent } = useLocalEvent()

// ── Rubrique ─────────────────────────────────────────────────────────────────
// Clé absente des rubriques de l'événement (URL tapée à la main) : retour à l'event board.
const rubriqueKey = computed(() => {
  const raw = route.params.rubrique
  return localEvent.rubriques.find((rubrique) => rubrique.key === raw)?.key ?? null
})

if (!rubriqueKey.value) {
  await navigateTo('/evenement', { replace: true })
}
</script>
