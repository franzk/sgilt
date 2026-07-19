<template>
  <PrestataireDetails
    v-if="prestataire"
    :prestataire="prestataire"
    display-mode="edit"
    @select="() => {}"
    @back="router.back()"
  >
    <template #tabs>
      <PageEditionTabs />
    </template>
    <template #main>
      <NuxtPage />
    </template>
  </PrestataireDetails>
</template>

<script setup lang="ts">
import PrestataireDetails from '~/components/prestataire/PrestataireDetails.vue'
import PageEditionTabs from '~/components/prestataire/PageEditionTabs.vue'

definePageMeta({ layout: 'default' })

const router = useRouter()
const route = useRoute()
const currentUser = useCurrentUser()
const { prestataire, loadMaFiche } = usePrestataire()

watch(
  () => currentUser.slug,
  (slug) => {
    if (slug) loadMaFiche()
  },
  { immediate: true },
)

// Aucune sous-route active (ex. arrivée directe sur /pro/page-edition) : l'onglet Édition
// est celui par défaut.
if (route.path === '/pro/page-edition') {
  await navigateTo('/pro/page-edition/edition', { replace: true })
}
</script>
