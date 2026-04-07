<template>
  <PrestataireDetails
    v-if="prestataire"
    :prestataire="prestataire"
    :disableDate="disableDatePicker"
    @select="onSelect"
    @back="router.back()"
  />

  <div v-else-if="!loading" class="not-found">
    <p>{{ $t('provider.not-found') }}</p>
    <NuxtLink to="/search">{{ $t('provider.back-to-search') }}</NuxtLink>
  </div>

  <DemandeBottomSheet
    :is-open="showDemande"
    :prestataire-name="prestataire?.name ?? ''"
    @close="showDemande = false"
  />

  <FinalisationAddPrestataire
    v-if="prestataire"
    v-model:open="showFinalisation"
    :prestataire-name="prestataire.name"
  />
</template>

<script setup lang="ts">
import PrestataireDetails from '~/components/prestataire/PrestataireDetails.vue'
import DemandeBottomSheet from '~/components/demande/DemandeBottomSheet.vue'
import FinalisationAddPrestataire from '~/components/prestataire/FinalisationAddPrestataire.vue'
import { SearchMockService } from '~/services/search.mock'
import type { PrestataireDetail } from '~/types/prestataire'

const router = useRouter()
const route = useRoute()
const slug = route.params.slug as string

const prestataire = ref<PrestataireDetail | null>(null)
const loading = ref(true)

const { currentFlow } = useFlow()
const disableDatePicker = computed(() => currentFlow.value === 'add-prestataire')

onMounted(async () => {
  prestataire.value = (await SearchMockService.getBySlug(slug)) ?? null
  loading.value = false
})

const showDemande = ref(false)
const showFinalisation = ref(false)
const { isDesktop } = useDevice()

function onSelect(p: PrestataireDetail) {
  if (currentFlow.value === 'add-prestataire') {
    showFinalisation.value = true
    return
  }

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
