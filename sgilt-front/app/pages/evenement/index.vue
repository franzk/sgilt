<template>
  <EventBoard
    :event-meta="localEvent"
    :rubriques="localEvent.rubriques"
    :cover-image="coverImage"
    @settings="navigateTo('/evenement/parametres')"
    @rubrique="(rubriqueKey) => navigateTo(`/evenement/${rubriqueKey}`)"
  />
</template>

<script setup lang="ts">
import EventBoard from '~/components/evenement/EventBoard.vue'
import { defaultCoverPath } from '~/utils/eventCovers'

definePageMeta({ layout: 'evenement' })

const { t } = useI18n()
useHead({ title: t('evenement.page-title') })

const { localEvent, initRubriques } = useLocalEvent()

// Le type d'événement se choisit sur /fete : sans type (accès direct), on y renvoie. Sinon, à la
// première arrivée, les rubriques du template sont copiées dans l'événement local. Côté client
// seulement : l'événement local vit dans le localStorage.
onMounted(() => {
  if (!localEvent.eventType) return navigateTo('/fete', { replace: true })
  initRubriques()
})

// Même banque d'images que l'event board /app (fallback par type, jusqu'à 'autre').
const { toUrl } = useImageUrl()
const coverImage = computed(() => toUrl(defaultCoverPath(localEvent.eventType)))
</script>
