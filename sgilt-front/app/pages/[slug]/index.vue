<template>
  <PrestataireDetails
    v-if="prestataire"
    :prestataire="prestataire"
    @select="onSelect"
    @back="router.back()"
  />

  <div v-else-if="!loading" class="not-found">
    <p>Ce prestataire est introuvable.</p>
    <NuxtLink to="/search">← Retour à la recherche</NuxtLink>
  </div>

  <DemandeBottomSheet
    :is-open="showDemande"
    :prestataire-name="prestataire?.name ?? ''"
    @close="showDemande = false"
  />
</template>

<script setup lang="ts">
import PrestataireDetails from '~/components/prestataire/PrestataireDetails.vue'
import DemandeBottomSheet from '~/components/demande/DemandeBottomSheet.vue'
import { SearchMockService } from '~/services/search.mock'
import type { PrestataireDetail } from '~/types/prestataire'

const router = useRouter()
const route = useRoute()
const slug = route.params.slug as string

const prestataire = ref<PrestataireDetail | null>(null)
const loading = ref(true)

onMounted(async () => {
  prestataire.value = (await SearchMockService.getBySlug(slug)) ?? null
  loading.value = false
})

/* watchEffect(() => {
  if (!prestataire.value) return
  useSeoMeta({
    title: `${prestataire.value.name} — Sgilt`,
    description: prestataire.value.baseline,
    ogImage: prestataire.value.heroImage,
  })
}) */

const showDemande = ref(false)
const { isDesktop } = useDevice()

function onSelect(p: PrestataireDetail) {
  if (isDesktop.value) {
    navigateTo(`/${p.slug}/demande`)
  } else {
    showDemande.value = true
  }
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 50vh;
  gap: 1rem;
  font-size: 1rem;
  color: $text-secondary;

  a {
    color: $color-primary;
    text-decoration: none;
    font-weight: 500;
  }
}
</style>
