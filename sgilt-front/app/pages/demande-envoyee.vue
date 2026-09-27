<template>
  <DemandeEnvoyee v-if="confirmation" :summary="confirmation.summary" :email="confirmation.email" />
</template>

<script setup lang="ts">
import DemandeEnvoyee from '~/components/demande/DemandeEnvoyee.vue'
import { useDemande } from '~/composables/useDemande'

const { t } = useI18n()
useHead({ title: t('tunnel.envoyee.page-title') })

// La confirmation n'existe qu'en mémoire, juste après l'envoi : un rafraîchissement ou un
// accès direct n'a rien à afficher et renvoie à la landing.
const { confirmation } = useDemande()

if (!confirmation.value) {
  await navigateTo('/', { replace: true })
}

// En quittant la page, plus rien de la demande ne reste côté navigateur.
onUnmounted(() => {
  confirmation.value = null
})
</script>
