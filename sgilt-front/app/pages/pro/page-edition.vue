<template>
  <PrestataireDetails
    v-if="prestataire"
    :prestataire="prestataire"
    display-mode="edit"
    @select="() => {}"
    @back="router.back()"
  />
</template>

<script setup lang="ts">
import PrestataireDetails from '~/components/prestataire/PrestataireDetails.vue'

definePageMeta({ layout: 'default' })

const router = useRouter()
const currentUser = useCurrentUser()
const { prestataire, loadMaFiche } = usePrestataire()

watch(
  () => currentUser.slug,
  (slug) => {
    if (slug) loadMaFiche()
  },
  { immediate: true },
)
</script>
