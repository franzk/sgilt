<template>
  <div v-if="noFlowWarning" class="no-flow">
    <p class="no-flow-title">{{ $t('tunnel.no-flow.title') }}</p>
    <p class="no-flow-message">{{ $t('tunnel.no-flow.message') }}</p>
    <NuxtLink to="/app">{{ $t('tunnel.no-flow.back-to-app') }}</NuxtLink>
  </div>

  <template v-else-if="prestataire">
    <DemandeDesktop
      v-if="!isMobile"
      :slug="slug"
      :prestataire-name="prestataire.name"
      :prestataire-image="prestataire.image"
    />
    <DemandeMobile
      v-else
      :prestataire-name="prestataire.name"
      :prestataire-image="prestataire.image"
      :slug="slug"
    />
  </template>

  <div v-else-if="!loading" class="not-found">
    <p>{{ $t('provider.not-found') }}</p>
    <NuxtLink to="/search">{{ $t('provider.back-to-search') }}</NuxtLink>
  </div>
</template>

<script setup lang="ts">
import DemandeDesktop from '~/components/demande/DemandeDesktop.vue'
import DemandeMobile from '~/components/demande/DemandeMobile.vue'
import { useDemande } from '~/composables/useDemande'
import { usePrestataire } from '~/data/prestataire/usePrestataire'

const route = useRoute()
const slug = route.params.slug as string

const { prestataire, loading } = usePrestataire(slug)

const { isMobile } = useDevice()

useHead({ title: 'Votre demande' })

const { etapeActuelle, state, goTo } = useDemande()
const { currentFlow } = useFlow()
const { isAuthenticated } = useKeycloak()

const noFlowWarning = ref(false)

onMounted(() => {
  if (currentFlow.value === null) {
    if (!isAuthenticated.value) {
      navigateTo(`/${slug}`)
    } else {
      noFlowWarning.value = true
    }
    return
  }
  if (etapeActuelle.value === 1 && state.eventType && state.eventType.toUpperCase() !== 'AUTRE') {
    goTo(2)
  }
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.no-flow {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 50vh;
  gap: $spacing-m;
  padding: $spacing-l;
  text-align: center;

  .no-flow-title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.6rem;
    font-weight: 500;
    color: $text-primary;
    margin: 0;
  }

  .no-flow-message {
    font-size: 0.95rem;
    color: $text-secondary;
    max-width: 400px;
    line-height: 1.6;
    margin: 0;
  }

  a {
    color: $color-primary;
    text-decoration: none;
    font-weight: 500;
  }
}

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
