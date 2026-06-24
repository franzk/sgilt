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
const { prestataire, load } = usePrestataire()

watch(
  () => currentUser.slug,
  (slug) => { if (slug) load(slug) },
  { immediate: true },
)
</script>
