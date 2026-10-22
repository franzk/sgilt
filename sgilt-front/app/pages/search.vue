<template>
  <PrestataireSearch @select="navigateTo(`/${$event.slug}`)" />
</template>

<script setup lang="ts">
import PrestataireSearch from '~/components/prestataire/PrestataireSearch.vue'

useHead({ title: 'Prestataires disponibles' })
</script>
