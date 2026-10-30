<!--
  URL inexistante sous /e/[slug]/ (ex. /e/[slug]/aasdfq) : jamais affichée, le middleware
  `ticketing` renvoie vers la fiche de l'événement.
-->
<template>
  <div />
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'ticketing' })
</script>
