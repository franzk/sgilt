<!--
  Onglet « À suivre » de l'espace client (/app/a-suivre, mobile) : les réservations en attente
  de réponse ou en échange (ToFollowList). Sur desktop, le même bloc vit dans la colonne droite
  de /app : on y redirige.
-->
<template>
  <div class="to-follow-page">
    <ToFollowList />
  </div>
</template>

<script setup lang="ts">
import ToFollowList from '~/components/app/ToFollowList.vue'

definePageMeta({ layout: 'app' })
useHead({ title: 'À suivre' })

const { isDesktop } = useDevice()

// Sur desktop, /app affiche ce bloc dans sa colonne droite — on redirige
onMounted(() => {
  if (isDesktop.value) navigateTo('/app', { replace: true })
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.to-follow-page {
  min-height: 100%;
  padding: $spacing-m $spacing-m calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $spacing-m);
  background-color: $brand-background-alt;
}
</style>
