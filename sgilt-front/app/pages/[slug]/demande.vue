<template>
  <template v-if="prestataire">
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

watchEffect(() => {
  if (!prestataire.value) return
  useSeoMeta({ title: `Demande à ${prestataire.value.name} — Sgilt` })
})

const { etapeActuelle, state, goTo } = useDemande()

onMounted(() => {
  if (etapeActuelle.value === 1 && state.eventType && state.eventType.toUpperCase() !== 'AUTRE') {
    goTo(2)
  }
})
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
