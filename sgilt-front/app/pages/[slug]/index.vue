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

  <FinalisationAddPrestataire
    v-if="prestataire"
    v-model:open="showFinalisation"
    :prestataire-name="prestataire.name"
    :prestataire-id="prestataire.id"
  />
</template>

<script setup lang="ts">
import PrestataireDetails from '~/components/prestataire/PrestataireDetails.vue'
import FinalisationAddPrestataire from '~/components/prestataire/FinalisationAddPrestataire.vue'
import { toISODate } from '~/utils/dateUtils'
import type { PrestataireDetail } from '~/data/prestataire/domain/PrestataireDetail'
import { usePrestataire } from '~/data/prestataire/usePrestataire'

const router = useRouter()
const route = useRoute()
const slug = route.params.slug as string

const { prestataire, loading } = usePrestataire(slug)

useHead(computed(() => ({ title: prestataire.value?.name ?? '' })))

const { currentFlow } = useFlow()
const disableDatePicker = computed(() => currentFlow.value === 'add-prestataire')

const showFinalisation = ref(false)
const { dateModel } = useSearchUi()

function onSelect(p: PrestataireDetail) {
  if (currentFlow.value === 'add-prestataire') {
    showFinalisation.value = true
    return
  }

  useDemande().setPrestataire(p.id, p.name, p.image)
  navigateTo({
    path: `/${p.slug}/demande`,
    query: dateModel.value ? { date: toISODate(dateModel.value) } : undefined,
  })
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
